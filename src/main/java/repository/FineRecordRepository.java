package repository;

import config.DatabaseConnection;
import model.FineRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class FineRecordRepository {

    public List<FineRecord> getAllFineRecords(
            String keyword
    ) {

        List<FineRecord> records =
                new ArrayList<>();

        String sql = """
                SELECT
                    f.fine_id,
                    f.user_id,
                    u.name AS member_name,
                    u.role,
                    b.title AS book_title,
                    bl.copy_number,
                    f.amount,
                    f.status,
                    f.created_at,
                    f.paid_at
                FROM fines f
                JOIN users u
                    ON f.user_id = u.user_id
                JOIN book_loans bl
                    ON f.loan_id = bl.loan_id
                JOIN book_copies bc
                    ON bl.copy_number = bc.copy_number
                JOIN books b
                    ON bc.isbn = b.isbn
                WHERE f.user_id LIKE ?
                   OR u.name LIKE ?
                   OR b.title LIKE ?
                   OR f.status LIKE ?
                ORDER BY
                    f.created_at DESC
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            String searchText =
                    "%" + (keyword == null ? "" : keyword.trim()) + "%";

            statement.setString(1, searchText);
            statement.setString(2, searchText);
            statement.setString(3, searchText);
            statement.setString(4, searchText);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {

                    Timestamp createdTimestamp =
                            resultSet.getTimestamp("created_at");

                    Timestamp paidTimestamp =
                            resultSet.getTimestamp("paid_at");

                    FineRecord record =
                            new FineRecord(
                                    resultSet.getInt("fine_id"),
                                    resultSet.getString("user_id"),
                                    resultSet.getString("member_name"),
                                    resultSet.getString("role"),
                                    resultSet.getString("book_title"),
                                    resultSet.getString("copy_number"),
                                    resultSet.getBigDecimal("amount"),
                                    resultSet.getString("status"),
                                    createdTimestamp == null
                                            ? null
                                            : createdTimestamp.toLocalDateTime(),
                                    paidTimestamp == null
                                            ? null
                                            : paidTimestamp.toLocalDateTime()
                            );

                    records.add(record);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return records;
    }
}