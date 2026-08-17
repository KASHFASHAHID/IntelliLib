package repository;

import config.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import model.Reservation;
import java.util.ArrayList;
import java.util.List;

public class ReservationRepository {

    public boolean reserveBook(String userId, String isbn) {

    int queuePosition = getNextQueuePosition(isbn);

    String sql = """
            INSERT INTO reservations
            (user_id, isbn, status, reservation_date, queue_position)
            VALUES (?, ?, 'WAITING', ?, ?)
            """;

    try (
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
    ) {

        statement.setString(1, userId);
        statement.setString(2, isbn);
        statement.setDate(3, Date.valueOf(LocalDate.now()));
        statement.setInt(4, queuePosition);

        return statement.executeUpdate() > 0;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

    public boolean hasReservedBook(String userId, String isbn) {

    String sql = """
            SELECT COUNT(*)
            FROM reservations
            WHERE user_id = ?
              AND isbn = ?
              AND status = 'WAITING'
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

public int getNextQueuePosition(String isbn) {

    String sql = """
            SELECT COUNT(*)
            FROM reservations
            WHERE isbn = ?
              AND status = 'WAITING'
            """;

    try (
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
    ) {

        statement.setString(1, isbn);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return resultSet.getInt(1) + 1;
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return 1;
}

public List<Reservation> getReservationsByUser(String userId) {

    List<Reservation> reservations = new ArrayList<>();

    String sql = """
            SELECT r.isbn,
                   b.title,
                   GROUP_CONCAT(DISTINCT a.author_name SEPARATOR ', ') AS authors,
                   r.queue_position,
                   r.status,
                   r.reservation_date,
                   r.pickup_expiry_date
            FROM reservations r
            JOIN books b ON r.isbn = b.isbn
            LEFT JOIN book_authors ba ON b.isbn = ba.isbn
            LEFT JOIN authors a ON ba.author_id = a.author_id
            WHERE r.user_id = ?
            GROUP BY r.isbn, b.title, r.queue_position,
                     r.status, r.reservation_date, r.pickup_expiry_date
            ORDER BY r.reservation_date DESC
            """;

    try (
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
    ) {
        statement.setString(1, userId);

        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {

            Date pickupDate = resultSet.getDate("pickup_expiry_date");

            reservations.add(new Reservation(
                    resultSet.getString("title"),
                    resultSet.getString("authors"),
                    resultSet.getString("isbn"),
                    resultSet.getInt("queue_position"),
                    resultSet.getString("status"),
                    resultSet.getDate("reservation_date").toLocalDate(),
                    pickupDate == null ? null : pickupDate.toLocalDate()
            ));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return reservations;
}
}

