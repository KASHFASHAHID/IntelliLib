package repository;

import config.DatabaseConnection;
import model.ActivityLog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogRepository {

    public boolean saveLog(
            String userId,
            String action,
            String details
    ) {

        String sql = """
                INSERT INTO activity_logs(
                    user_id,
                    action,
                    details
                )
                VALUES (?, ?, ?)
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, userId);
            statement.setString(2, action);
            statement.setString(3, details);

            return statement.executeUpdate() == 1;

        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public List<ActivityLog> findAllLogs() {

        List<ActivityLog> logs = new ArrayList<>();

        String sql = """
                SELECT log_id,
                       user_id,
                       action,
                       details,
                       created_at
                FROM activity_logs
                ORDER BY created_at DESC,
                         log_id DESC
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

                Timestamp createdTimestamp =
                        resultSet.getTimestamp("created_at");

                LocalDateTime createdAt =
                        createdTimestamp == null
                                ? null
                                : createdTimestamp.toLocalDateTime();

                logs.add(
                        new ActivityLog(
                                resultSet.getInt("log_id"),
                                resultSet.getString("user_id"),
                                resultSet.getString("action"),
                                resultSet.getString("details"),
                                createdAt
                        )
                );
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return logs;
    }
}