package repository;

import config.DatabaseConnection;
import model.Profile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ProfileRepository {

    public Profile getProfileByUserId(
            String userId
    ) {

        String sql = """
                SELECT user_id,
                       name,
                       email,
                       phone,
                       role,
                       university,
                       department,
                       account_status
                FROM users
                WHERE user_id = ?
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

                    return new Profile(
                            resultSet.getString("user_id"),
                            resultSet.getString("name"),
                            resultSet.getString("email"),
                            resultSet.getString("phone"),
                            resultSet.getString("role"),
                            resultSet.getString("university"),
                            resultSet.getString("department"),
                            resultSet.getString("account_status")
                    );
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }

    public boolean isEmailUsedByAnotherUser(
            String email,
            String currentUserId
    ) {

        String sql = """
                SELECT 1
                FROM users
                WHERE LOWER(email) = LOWER(?)
                  AND user_id <> ?
                LIMIT 1
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, email);
            statement.setString(2, currentUserId);

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                return resultSet.next();
            }

        } catch (Exception exception) {

            exception.printStackTrace();

            /*
             * Treat a database-check failure as unavailable,
             * so the update is not performed unsafely.
             */
            return true;
        }
    }

    public boolean updateContactDetails(
            String userId,
            String email,
            String phone
    ) {

        String sql = """
                UPDATE users
                SET email = ?,
                    phone = ?
                WHERE user_id = ?
                  AND account_status IN ('ACTIVE', 'SUSPENDED')
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, email);
            statement.setString(2, phone);
            statement.setString(3, userId);

            return statement.executeUpdate() == 1;

        } catch (Exception exception) {

            exception.printStackTrace();
            return false;
        }
    }

    public String getPasswordHashByUserId(
            String userId
    ) {

        String sql = """
                SELECT password_hash
                FROM users
                WHERE user_id = ?
                  AND account_status = 'ACTIVE'
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

                    return resultSet.getString(
                            "password_hash"
                    );
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }

    public boolean updatePasswordHash(
            String userId,
            String newPasswordHash
    ) {

        String sql = """
                UPDATE users
                SET password_hash = ?
                WHERE user_id = ?
                  AND account_status = 'ACTIVE'
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    newPasswordHash
            );

            statement.setString(
                    2,
                    userId.trim()
            );

            return statement.executeUpdate() == 1;

        } catch (Exception exception) {

            exception.printStackTrace();
            return false;
        }
    }
}