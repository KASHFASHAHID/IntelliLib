package repository;

import config.DatabaseConnection;
import model.Notification;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class NotificationRepository {

    public List<Notification> getNotificationsByUser(
            String userId
    ) {

        List<Notification> notifications =
                new ArrayList<>();

        if (userId == null || userId.isBlank()) {
            return notifications;
        }

        String sql = """
                SELECT title,
                       message,
                       is_read,
                       created_at
                FROM notifications
                WHERE user_id = ?
                ORDER BY created_at DESC
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

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                while (resultSet.next()) {

                    Timestamp createdAt =
                            resultSet.getTimestamp(
                                    "created_at"
                            );

                    notifications.add(
                            new Notification(
                                    resultSet.getString(
                                            "title"
                                    ),
                                    resultSet.getString(
                                            "message"
                                    ),
                                    resultSet.getBoolean(
                                            "is_read"
                                    ),
                                    createdAt == null
                                            ? null
                                            : createdAt.toLocalDateTime()
                            )
                    );
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return notifications;
    }

    public boolean createNotification(
            String userId,
            String title,
            String message
    ) {

        if (userId == null
                || userId.isBlank()
                || title == null
                || title.isBlank()
                || message == null
                || message.isBlank()) {

            return false;
        }

        String sql = """
                INSERT INTO notifications
                (
                    user_id,
                    title,
                    message,
                    is_read
                )
                VALUES (?, ?, ?, FALSE)
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

            statement.setString(
                    2,
                    title
            );

            statement.setString(
                    3,
                    message
            );

            return statement.executeUpdate() == 1;

        } catch (Exception exception) {

            exception.printStackTrace();
            return false;
        }
    }

    public boolean markAllAsRead(
            String userId
    ) {

        if (userId == null || userId.isBlank()) {
            return false;
        }

        String sql = """
                UPDATE notifications
                SET is_read = TRUE
                WHERE user_id = ?
                  AND is_read = FALSE
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

            /*
             * This is successful even when there are no
             * unread notifications to update.
             */
            return true;

        } catch (Exception exception) {

            exception.printStackTrace();
            return false;
        }
    }
}