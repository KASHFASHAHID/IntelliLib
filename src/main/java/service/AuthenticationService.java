package service;

import config.DatabaseConnection;
import model.Role;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthenticationService {

    public User login(String userId, String password) {

        String sql = """
                SELECT *
                FROM users
                WHERE user_id = ?
                AND password_hash = ?
                AND account_status = 'ACTIVE'
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, userId.trim());
            statement.setString(2, password.trim());

            ResultSet result = statement.executeQuery();

            if (result.next()) {

                return new User(
                        result.getString("user_id"),
                        result.getString("password_hash"),
                        result.getString("name"),
                        result.getString("email"),
                        Role.valueOf(result.getString("role"))
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}