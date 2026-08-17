package repository;

import config.DatabaseConnection;
import model.Role;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserRepository {

    public User findActiveUserById(String userId) {

        String sql = """
                SELECT user_id,
                       password_hash,
                       name,
                       email,
                       role
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

            statement.setString(1, userId.trim());

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {

                    return new User(
                            result.getString("user_id"),
                            result.getString("password_hash"),
                            result.getString("name"),
                            result.getString("email"),
                            Role.valueOf(
                                    result.getString("role")
                            )
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean updatePasswordHash(
            String userId,
            String passwordHash
    ) {

        String sql = """
                UPDATE users
                SET password_hash = ?
                WHERE user_id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, passwordHash);
            statement.setString(2, userId.trim());

            return statement.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public User findLoginUserById(
        String userId
) {

    if (userId == null || userId.isBlank()) {
        return null;
    }

    String sql = """
            SELECT user_id,
                   password_hash,
                   name,
                   email,
                   role,
                   account_status
            FROM users
            WHERE user_id = ?
              AND account_status IN (
                  'ACTIVE',
                  'SUSPENDED'
              )
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

                return new User(
                        resultSet.getString(
                                "user_id"
                        ),
                        resultSet.getString(
                                "password_hash"
                        ),
                        resultSet.getString(
                                "name"
                        ),
                        resultSet.getString(
                                "email"
                        ),
                        Role.valueOf(
                                resultSet.getString(
                                        "role"
                                )
                        ),
                        resultSet.getString(
                                "account_status"
                        )
                );
            }
        }

    } catch (Exception exception) {

        System.err.println(
                "Login user could not be loaded: "
                        + exception.getMessage()
        );

        exception.printStackTrace();
    }

    return null;
}
}