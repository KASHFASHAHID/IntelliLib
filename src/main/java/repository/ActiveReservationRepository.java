package repository;

import config.DatabaseConnection;
import model.ActiveReservation;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ActiveReservationRepository {

    public List<ActiveReservation> findAllActiveReservations() {

        List<ActiveReservation> reservations =
                new ArrayList<>();

        String sql = """
                SELECT r.reservation_id,
                       r.user_id,
                       u.name AS member_name,
                       r.isbn,
                       b.title AS book_title,
                       r.status,
                       r.reservation_date,
                       r.queue_position,
                       r.pickup_expiry_date,
                       r.notification_sent,
                       r.created_at
                FROM reservations r
                INNER JOIN users u
                        ON u.user_id = r.user_id
                INNER JOIN books b
                        ON b.isbn = r.isbn
                WHERE r.status IN (
                    'WAITING',
                    'READY_FOR_PICKUP'
                )
                ORDER BY
                    CASE
                        WHEN r.status = 'READY_FOR_PICKUP'
                        THEN 1
                        ELSE 2
                    END,
                    r.reservation_date,
                    r.queue_position
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            while (resultSet.next()) {

                Date reservationDateValue =
                        resultSet.getDate(
                                "reservation_date"
                        );

                Date pickupExpiryValue =
                        resultSet.getDate(
                                "pickup_expiry_date"
                        );

                Timestamp createdAtValue =
                        resultSet.getTimestamp(
                                "created_at"
                        );

                ActiveReservation reservation =
                        new ActiveReservation(
                                resultSet.getInt(
                                        "reservation_id"
                                ),
                                resultSet.getString(
                                        "user_id"
                                ),
                                resultSet.getString(
                                        "member_name"
                                ),
                                resultSet.getString(
                                        "isbn"
                                ),
                                resultSet.getString(
                                        "book_title"
                                ),
                                resultSet.getString(
                                        "status"
                                ),
                                reservationDateValue == null
                                        ? null
                                        : reservationDateValue
                                                .toLocalDate(),
                                resultSet.getInt(
                                        "queue_position"
                                ),
                                pickupExpiryValue == null
                                        ? null
                                        : pickupExpiryValue
                                                .toLocalDate(),
                                resultSet.getBoolean(
                                        "notification_sent"
                                ),
                                createdAtValue == null
                                        ? null
                                        : createdAtValue
                                                .toLocalDateTime()
                        );

                reservations.add(reservation);
            }

        } catch (Exception exception) {

            System.err.println(
                    "Active reservations could not be loaded: "
                            + exception.getMessage()
            );

            exception.printStackTrace();
        }

        return reservations;
    }
}