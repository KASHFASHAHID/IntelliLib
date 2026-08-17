package repository;

import config.DatabaseConnection;
import model.ReadyForPickup;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReadyForPickupRepository {

    public List<ReadyForPickup> getReadyForPickupReservations() {

        List<ReadyForPickup> reservations = new ArrayList<>();

        String sql = """
                SELECT
                    r.reservation_id,
                    r.user_id,
                    u.name AS member_name,
                    r.isbn,
                    b.title AS book_title,
                    r.queue_position,
                    r.pickup_expiry_date
                FROM reservations r
                JOIN users u
                    ON r.user_id = u.user_id
                JOIN books b
                    ON r.isbn = b.isbn
                WHERE r.status = 'READY_FOR_PICKUP'
                ORDER BY r.pickup_expiry_date,
                         r.queue_position,
                         r.reservation_id
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {

                Date expiryDate =
                        resultSet.getDate("pickup_expiry_date");

                LocalDate pickupExpiryDate =
                        expiryDate == null
                                ? null
                                : expiryDate.toLocalDate();

                reservations.add(
                        new ReadyForPickup(
                                resultSet.getInt("reservation_id"),
                                resultSet.getString("user_id"),
                                resultSet.getString("member_name"),
                                resultSet.getString("isbn"),
                                resultSet.getString("book_title"),
                                resultSet.getInt("queue_position"),
                                pickupExpiryDate
                        )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return reservations;
    }
    public boolean issueReservedBook(
        int reservationId,
        String userId,
        String isbn,
        int borrowDays
) {

    String findReservedCopySql = """
            SELECT copy_number
            FROM book_copies
            WHERE isbn = ?
              AND status = 'RESERVED'
            LIMIT 1
            FOR UPDATE
            """;

    String updateCopySql = """
            UPDATE book_copies
            SET status = 'ISSUED'
            WHERE copy_number = ?
              AND status = 'RESERVED'
            """;

    String insertLoanSql = """
            INSERT INTO book_loans
            (
                user_id,
                copy_number,
                issued_by,
                issue_date,
                due_date,
                return_date,
                status
            )
            VALUES (?, ?, NULL, ?, ?, NULL, 'ISSUED')
            """;

    String completeReservationSql = """
            UPDATE reservations
            SET status = 'COMPLETED'
            WHERE reservation_id = ?
              AND user_id = ?
              AND isbn = ?
              AND status = 'READY_FOR_PICKUP'
            """;

    String notificationSql = """
            INSERT INTO notifications
            (user_id, title, message, is_read)
            VALUES (?, ?, ?, FALSE)
            """;

    try (Connection connection = DatabaseConnection.getConnection()) {

        connection.setAutoCommit(false);

        try (
                PreparedStatement findCopyStmt =
                        connection.prepareStatement(findReservedCopySql);

                PreparedStatement updateCopyStmt =
                        connection.prepareStatement(updateCopySql);

                PreparedStatement insertLoanStmt =
                        connection.prepareStatement(insertLoanSql);

                PreparedStatement completeReservationStmt =
                        connection.prepareStatement(completeReservationSql);

                PreparedStatement notificationStmt =
                        connection.prepareStatement(notificationSql)
        ) {

            findCopyStmt.setString(1, isbn);

            String copyNumber;

            try (ResultSet resultSet = findCopyStmt.executeQuery()) {

                if (!resultSet.next()) {
                    connection.rollback();
                    return false;
                }

                copyNumber = resultSet.getString("copy_number");
            }

            LocalDate issueDate = LocalDate.now();
            LocalDate dueDate = issueDate.plusDays(borrowDays);

            updateCopyStmt.setString(1, copyNumber);

            if (updateCopyStmt.executeUpdate() != 1) {
                connection.rollback();
                return false;
            }

            insertLoanStmt.setString(1, userId);
            insertLoanStmt.setString(2, copyNumber);
            insertLoanStmt.setDate(3, Date.valueOf(issueDate));
            insertLoanStmt.setDate(4, Date.valueOf(dueDate));
            insertLoanStmt.executeUpdate();

            completeReservationStmt.setInt(1, reservationId);
            completeReservationStmt.setString(2, userId);
            completeReservationStmt.setString(3, isbn);

            if (completeReservationStmt.executeUpdate() != 1) {
                connection.rollback();
                return false;
            }

            String title = "Reserved Book Issued Successfully";

            String message =
                    "Your reserved book has been issued successfully. " +
                    "Please return it on or before " + dueDate + ".";

            notificationStmt.setString(1, userId);
            notificationStmt.setString(2, title);
            notificationStmt.setString(3, message);
            notificationStmt.executeUpdate();

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

    
}
