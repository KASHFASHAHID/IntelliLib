package repository;

import config.DatabaseConnection;
import model.BorrowedBook;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class BorrowedBooksRepository {

    public List<BorrowedBook> getBorrowedBooks(
            String userId
    ) {

        List<BorrowedBook> borrowedBooks =
                new ArrayList<>();

        if (userId == null || userId.isBlank()) {
            return borrowedBooks;
        }
        updateOverdueLoans(
        userId.trim()
);

        String sql = """
                SELECT bl.loan_id,
                       b.title,
                       GROUP_CONCAT(
                           DISTINCT a.author_name
                           SEPARATOR ', '
                       ) AS authors,
                       bl.copy_number,
                       bl.issue_date,
                       bl.due_date,
                       bl.status,
                       bl.renewal_count,
                       bl.last_renewed_at
                FROM book_loans bl
                JOIN book_copies bc
                    ON bl.copy_number = bc.copy_number
                JOIN books b
                    ON bc.isbn = b.isbn
                LEFT JOIN book_authors ba
                    ON b.isbn = ba.isbn
                LEFT JOIN authors a
                    ON ba.author_id = a.author_id
                WHERE bl.user_id = ?
                  AND bl.return_date IS NULL
                  AND bl.status IN ('ISSUED', 'OVERDUE')
                GROUP BY bl.loan_id,
                         b.title,
                         bl.copy_number,
                         bl.issue_date,
                         bl.due_date,
                         bl.status,
                         bl.renewal_count,
                         bl.last_renewed_at
                ORDER BY bl.due_date
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    userId.trim()
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                while (resultSet.next()) {

                    LocalDate issueDate =
                            resultSet
                                    .getDate("issue_date")
                                    .toLocalDate();

                    LocalDate dueDate =
                            resultSet
                                    .getDate("due_date")
                                    .toLocalDate();

                    long daysLeft =
                            ChronoUnit.DAYS.between(
                                    LocalDate.now(),
                                    dueDate
                            );

                    Timestamp renewedTimestamp =
                            resultSet.getTimestamp(
                                    "last_renewed_at"
                            );

                    LocalDateTime lastRenewedAt =
                            renewedTimestamp == null
                                    ? null
                                    : renewedTimestamp
                                            .toLocalDateTime();

                    borrowedBooks.add(
                            new BorrowedBook(
                                    resultSet.getInt("loan_id"),
                                    resultSet.getString("title"),
                                    resultSet.getString("authors"),
                                    resultSet.getString("copy_number"),
                                    issueDate,
                                    dueDate,
                                    resultSet.getString("status"),
                                    daysLeft,
                                    resultSet.getInt("renewal_count"),
                                    lastRenewedAt
                            )
                    );
                }
            }

        } catch (Exception exception) {

            System.err.println(
                    "Borrowed books could not be loaded: "
                            + exception.getMessage()
            );

            exception.printStackTrace();
        }

        return borrowedBooks;
    }

    public String getRenewalBlockReason(
            int loanId,
            String userId
    ) {

        if (loanId <= 0
                || userId == null
                || userId.isBlank()) {

            return "Invalid loan information.";
        }

        String sql = """
                SELECT bl.due_date,
                       bl.return_date,
                       bl.status,
                       bl.renewal_count,
                       u.account_status,
                       bc.isbn,
                       EXISTS (
                           SELECT 1
                           FROM reservations r
                           WHERE r.isbn = bc.isbn
                             AND r.user_id <> bl.user_id
                             AND r.status IN (
                                 'WAITING',
                                 'READY_FOR_PICKUP'
                             )
                       ) AS another_member_waiting
                FROM book_loans bl
                JOIN book_copies bc
                    ON bl.copy_number = bc.copy_number
                JOIN users u
                    ON bl.user_id = u.user_id
                WHERE bl.loan_id = ?
                  AND bl.user_id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    loanId
            );

            statement.setString(
                    2,
                    userId.trim()
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (!resultSet.next()) {
                    return "The selected loan could not be found.";
                }

                if (resultSet.getDate("return_date") != null) {
                    return "This book has already been returned.";
                }

                String accountStatus =
                        resultSet.getString(
                                "account_status"
                        );

                if (!"ACTIVE".equalsIgnoreCase(
                        accountStatus
                )) {

                    return "Your library account must be ACTIVE "
                            + "before a loan can be renewed.";
                }

                String loanStatus =
                        resultSet.getString("status");

                LocalDate dueDate =
                        resultSet
                                .getDate("due_date")
                                .toLocalDate();

                if ("OVERDUE".equalsIgnoreCase(
                        loanStatus
                ) || dueDate.isBefore(LocalDate.now())) {

                    return "Overdue books cannot be renewed. "
                            + "Please return the book first.";
                }

                int renewalCount =
                        resultSet.getInt(
                                "renewal_count"
                        );

                if (renewalCount >= 1) {

                    return "The maximum renewal limit "
                            + "for this loan has already been reached.";
                }

                boolean anotherMemberWaiting =
                        resultSet.getBoolean(
                                "another_member_waiting"
                        );

                if (anotherMemberWaiting) {

                    return "This book cannot be renewed because "
                            + "another member is waiting for it.";
                }

                return null;
            }

        } catch (Exception exception) {

            System.err.println(
                    "Loan renewal eligibility could not be checked: "
                            + exception.getMessage()
            );

            exception.printStackTrace();

            return "The renewal eligibility check failed.";
        }
    }

    public LocalDate renewLoan(
            int loanId,
            String userId,
            int renewalDays
    ) {

        if (loanId <= 0
                || userId == null
                || userId.isBlank()
                || renewalDays <= 0) {

            return null;
        }

        String findLoanSql = """
                SELECT bl.due_date,
                       bl.renewal_count,
                       bc.isbn
                FROM book_loans bl
                JOIN book_copies bc
                    ON bl.copy_number = bc.copy_number
                JOIN users u
                    ON bl.user_id = u.user_id
                WHERE bl.loan_id = ?
                  AND bl.user_id = ?
                  AND bl.return_date IS NULL
                  AND bl.status = 'ISSUED'
                  AND bl.due_date >= CURRENT_DATE
                  AND bl.renewal_count < 1
                  AND u.account_status = 'ACTIVE'
                  AND NOT EXISTS (
                      SELECT 1
                      FROM reservations r
                      WHERE r.isbn = bc.isbn
                        AND r.user_id <> bl.user_id
                        AND r.status IN (
                            'WAITING',
                            'READY_FOR_PICKUP'
                        )
                  )
                FOR UPDATE
                """;

        String renewLoanSql = """
                UPDATE book_loans
                SET due_date = ?,
                    renewal_count = renewal_count + 1,
                    last_renewed_at = CURRENT_TIMESTAMP
                WHERE loan_id = ?
                  AND user_id = ?
                  AND return_date IS NULL
                  AND status = 'ISSUED'
                  AND renewal_count < 1
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection()
        ) {

            connection.setAutoCommit(false);

            try (
                    PreparedStatement findLoanStatement =
                            connection.prepareStatement(
                                    findLoanSql
                            );

                    PreparedStatement renewLoanStatement =
                            connection.prepareStatement(
                                    renewLoanSql
                            )
            ) {

                findLoanStatement.setInt(
                        1,
                        loanId
                );

                findLoanStatement.setString(
                        2,
                        userId.trim()
                );

                LocalDate currentDueDate;

                try (
                        ResultSet resultSet =
                                findLoanStatement.executeQuery()
                ) {

                    if (!resultSet.next()) {

                        connection.rollback();
                        return null;
                    }

                    currentDueDate =
                            resultSet
                                    .getDate("due_date")
                                    .toLocalDate();
                }

                LocalDate newDueDate =
                        currentDueDate.plusDays(
                                renewalDays
                        );

                renewLoanStatement.setDate(
                        1,
                        Date.valueOf(newDueDate)
                );

                renewLoanStatement.setInt(
                        2,
                        loanId
                );

                renewLoanStatement.setString(
                        3,
                        userId.trim()
                );

                if (renewLoanStatement.executeUpdate() != 1) {

                    connection.rollback();
                    return null;
                }

                connection.commit();

                return newDueDate;

            } catch (Exception exception) {

                connection.rollback();

                System.err.println(
                        "Loan could not be renewed: "
                                + exception.getMessage()
                );

                exception.printStackTrace();

                return null;
            }

        } catch (Exception exception) {

            System.err.println(
                    "Loan renewal transaction failed: "
                            + exception.getMessage()
            );

            exception.printStackTrace();

            return null;
        }
    }

    private void updateOverdueLoans(
        String userId
) {

    String sql = """
            UPDATE book_loans
            SET status = 'OVERDUE'
            WHERE user_id = ?
              AND return_date IS NULL
              AND status = 'ISSUED'
              AND due_date < CURRENT_DATE
            """;

    try (
            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
    ) {

        statement.setString(
                1,
                userId
        );

        statement.executeUpdate();

    } catch (Exception exception) {

        System.err.println(
                "Overdue loan statuses could not be updated: "
                        + exception.getMessage()
        );

        exception.printStackTrace();
    }
}
}