package repository;

import config.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BorrowRepository {

    public boolean borrowBook(String userId, String isbn, int borrowDays) {
        


        String findCopySql = """
                SELECT copy_number
                FROM book_copies
                WHERE isbn = ?
                  AND status = 'AVAILABLE'
                LIMIT 1
                """;

        String updateCopySql = """
                UPDATE book_copies
                SET status = 'ISSUED'
                WHERE copy_number = ?
                """;

        String insertLoanSql = """
                INSERT INTO book_loans
                (user_id, copy_number, issued_by, issue_date, due_date, return_date, status)
                VALUES (?, ?, NULL, ?, ?, NULL, 'ISSUED')
                """;

        try (Connection connection = DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            String copyNumber = null;

            try (
                    PreparedStatement findCopyStmt = connection.prepareStatement(findCopySql);
                    PreparedStatement updateCopyStmt = connection.prepareStatement(updateCopySql);
                    PreparedStatement insertLoanStmt = connection.prepareStatement(insertLoanSql)
            ) {
                findCopyStmt.setString(1, isbn);

                ResultSet resultSet = findCopyStmt.executeQuery();

                if (resultSet.next()) {
                    copyNumber = resultSet.getString("copy_number");
                } else {
                    connection.rollback();
                    return false;
                }

                updateCopyStmt.setString(1, copyNumber);
                updateCopyStmt.executeUpdate();

                LocalDate issueDate = LocalDate.now();
                LocalDate dueDate = issueDate.plusDays(borrowDays);

                insertLoanStmt.setString(1, userId);
                insertLoanStmt.setString(2, copyNumber);
                insertLoanStmt.setDate(3, Date.valueOf(issueDate));
                insertLoanStmt.setDate(4, Date.valueOf(dueDate));
                insertLoanStmt.executeUpdate();

                connection.commit();
                return true;

            } catch (Exception e) {
                connection.rollback();
                e.printStackTrace();
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasBorrowedBook(String userId, String isbn) {

    String sql = """
            SELECT COUNT(*)
            FROM book_loans bl
            JOIN book_copies bc
                 ON bl.copy_number = bc.copy_number
            WHERE bl.user_id = ?
              AND bc.isbn = ?
              AND bl.status = 'ISSUED'
            """;

    try (
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
    ) {

        statement.setString(1, userId);
        statement.setString(2, isbn);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return resultSet.getInt(1) > 0;
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return false;
}
public int getActiveBorrowCount(String userId) {

    String sql = """
            SELECT COUNT(*)
            FROM book_loans
            WHERE user_id = ?
              AND status = 'ISSUED'
            """;

    try (
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
    ) {

        statement.setString(1, userId);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return resultSet.getInt(1);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return 0;
}

public boolean borrowBooks(
        String userId,
        String isbn,
        int borrowDays,
        int quantity
) {

    if (quantity <= 0) {
        return false;
    }

    String findCopiesSql = """
            SELECT copy_number
            FROM book_copies
            WHERE isbn = ?
              AND status = 'AVAILABLE'
            ORDER BY copy_number
            LIMIT ?
            """;

    String updateCopySql = """
            UPDATE book_copies
            SET status = 'ISSUED'
            WHERE copy_number = ?
            """;

    String insertLoanSql = """
            INSERT INTO book_loans
            (user_id, copy_number, issued_by, issue_date, due_date, return_date, status)
            VALUES (?, ?, NULL, ?, ?, NULL, 'ISSUED')
            """;

    try (Connection connection = DatabaseConnection.getConnection()) {

        connection.setAutoCommit(false);

        try (
                PreparedStatement findStmt = connection.prepareStatement(findCopiesSql);
                PreparedStatement updateStmt = connection.prepareStatement(updateCopySql);
                PreparedStatement loanStmt = connection.prepareStatement(insertLoanSql)
        ) {

            findStmt.setString(1, isbn);
            findStmt.setInt(2, quantity);

            ResultSet rs = findStmt.executeQuery();

            java.util.List<String> copies = new java.util.ArrayList<>();

            while (rs.next()) {
                copies.add(rs.getString("copy_number"));
            }

            if (copies.size() < quantity) {
                connection.rollback();
                return false;
            }

            LocalDate issueDate = LocalDate.now();
            LocalDate dueDate = issueDate.plusDays(borrowDays);

            for (String copyNumber : copies) {

                updateStmt.setString(1, copyNumber);
                updateStmt.executeUpdate();

                loanStmt.setString(1, userId);
                loanStmt.setString(2, copyNumber);
                loanStmt.setDate(3, Date.valueOf(issueDate));
                loanStmt.setDate(4, Date.valueOf(dueDate));
                loanStmt.executeUpdate();
            }

            connection.commit();
            return true;

        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
            return false;
        }

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}
public boolean hasOverdueActiveLoans(
        String userId
) {

    if (userId == null || userId.isBlank()) {
        return false;
    }

    String sql = """
            SELECT COUNT(*)
            FROM book_loans
            WHERE user_id = ?
              AND return_date IS NULL
              AND due_date < CURRENT_DATE
              AND status IN ('ISSUED', 'OVERDUE')
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

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        }

    } catch (Exception exception) {

        System.err.println(
                "Overdue loan check failed: "
                        + exception.getMessage()
        );

        exception.printStackTrace();
    }

    return false;
}
}