package repository;

import config.DatabaseConnection;
import model.BorrowRecord;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BorrowRecordRepository {

    public List<BorrowRecord> getAllBorrowRecords(
            String keyword
    ) {

        return getBorrowRecords(
                keyword,
                "ALL"
        );
    }

    public List<BorrowRecord> getBorrowRecords(
            String keyword,
            String filterMode
    ) {

        List<BorrowRecord> records =
                new ArrayList<>();

        String filterCondition =
                getFilterCondition(filterMode);

        String sql = """
                SELECT
                    bl.loan_id,
                    bl.user_id,
                    u.name AS member_name,
                    u.role,
                    b.title,

                    GROUP_CONCAT(
                        DISTINCT a.author_name
                        ORDER BY a.author_name
                        SEPARATOR ', '
                    ) AS authors,

                    bl.copy_number,
                    bl.issue_date,
                    bl.due_date,
                    bl.return_date,

                    CASE
                        WHEN bl.return_date IS NULL
                             AND bl.due_date < CURRENT_DATE
                             AND bl.status IN ('ISSUED', 'OVERDUE')
                        THEN 'OVERDUE'

                        ELSE bl.status
                    END AS display_status

                FROM book_loans bl

                INNER JOIN users u
                    ON bl.user_id = u.user_id

                INNER JOIN book_copies bc
                    ON bl.copy_number = bc.copy_number

                INNER JOIN books b
                    ON bc.isbn = b.isbn

                LEFT JOIN book_authors ba
                    ON b.isbn = ba.isbn

                LEFT JOIN authors a
                    ON ba.author_id = a.author_id

                WHERE (
                    bl.user_id LIKE ?
                    OR u.name LIKE ?
                    OR b.title LIKE ?
                    OR bl.copy_number LIKE ?
                    OR bl.status LIKE ?

                    OR EXISTS (
                        SELECT 1
                        FROM book_authors search_ba

                        INNER JOIN authors search_author
                            ON search_ba.author_id =
                               search_author.author_id

                        WHERE search_ba.isbn = b.isbn
                          AND search_author.author_name LIKE ?
                    )
                )

                %s

                GROUP BY
                    bl.loan_id,
                    bl.user_id,
                    u.name,
                    u.role,
                    b.title,
                    bl.copy_number,
                    bl.issue_date,
                    bl.due_date,
                    bl.return_date,
                    bl.status

                ORDER BY
                    CASE
                        WHEN bl.return_date IS NULL
                             AND bl.due_date < CURRENT_DATE
                             AND bl.status IN ('ISSUED', 'OVERDUE')
                        THEN 1

                        ELSE 2
                    END,

                    bl.issue_date DESC,
                    bl.loan_id DESC
                """.formatted(filterCondition);

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            String safeKeyword =
                    keyword == null
                            ? ""
                            : keyword.trim();

            String searchText =
                    "%" + safeKeyword + "%";

            statement.setString(1, searchText);
            statement.setString(2, searchText);
            statement.setString(3, searchText);
            statement.setString(4, searchText);
            statement.setString(5, searchText);
            statement.setString(6, searchText);

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                while (resultSet.next()) {

                    Date issueSqlDate =
                            resultSet.getDate(
                                    "issue_date"
                            );

                    Date dueSqlDate =
                            resultSet.getDate(
                                    "due_date"
                            );

                    Date returnSqlDate =
                            resultSet.getDate(
                                    "return_date"
                            );

                    LocalDate issueDate =
                            issueSqlDate == null
                                    ? null
                                    : issueSqlDate.toLocalDate();

                    LocalDate dueDate =
                            dueSqlDate == null
                                    ? null
                                    : dueSqlDate.toLocalDate();

                    LocalDate returnDate =
                            returnSqlDate == null
                                    ? null
                                    : returnSqlDate.toLocalDate();

                    BorrowRecord record =
                            new BorrowRecord(
                                    resultSet.getInt(
                                            "loan_id"
                                    ),
                                    resultSet.getString(
                                            "user_id"
                                    ),
                                    resultSet.getString(
                                            "member_name"
                                    ),
                                    resultSet.getString(
                                            "role"
                                    ),
                                    resultSet.getString(
                                            "title"
                                    ),
                                    resultSet.getString(
                                            "authors"
                                    ),
                                    resultSet.getString(
                                            "copy_number"
                                    ),
                                    issueDate,
                                    dueDate,
                                    returnDate,
                                    resultSet.getString(
                                            "display_status"
                                    )
                            );

                    records.add(record);
                }
            }

        } catch (Exception exception) {

            System.err.println(
                    "Borrow records could not be loaded: "
                            + exception.getMessage()
            );

            exception.printStackTrace();
        }

        return records;
    }

    private String getFilterCondition(
            String filterMode
    ) {

        String normalizedFilter =
                filterMode == null
                        ? "ALL"
                        : filterMode
                                .trim()
                                .toUpperCase();

        return switch (normalizedFilter) {

            case "ISSUED" -> """
                    AND bl.return_date IS NULL
                    AND bl.status IN ('ISSUED', 'OVERDUE')
                    """;

            case "OVERDUE" -> """
                    AND bl.return_date IS NULL
                    AND bl.status IN ('ISSUED', 'OVERDUE')
                    AND bl.due_date < CURRENT_DATE
                    """;

            default -> "";
        };
    }
}