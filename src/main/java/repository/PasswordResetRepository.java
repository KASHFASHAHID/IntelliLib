package repository;

import config.DatabaseConnection;
import model.PasswordResetOtp;
import model.Role;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class PasswordResetRepository {

    public User findActiveUser(
            String userId,
            String email
    ) {

        String sql = """
                SELECT user_id,
                       password_hash,
                       name,
                       email,
                       role
                FROM users
                WHERE user_id = ?
                  AND LOWER(email) = LOWER(?)
                  AND account_status IN ('ACTIVE', 'SUSPENDED')
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, userId.trim());
            statement.setString(2, email.trim());

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

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }

    public void invalidateExistingOtps(String userId) {

        String sql = """
                UPDATE password_reset_otps
                SET is_used = TRUE
                WHERE user_id = ?
                  AND is_used = FALSE
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, userId.trim());
            statement.executeUpdate();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public int saveOtp(
            String userId,
            String otpHash,
            LocalDateTime expiresAt
    ) {

        String sql = """
                INSERT INTO password_reset_otps(
                    user_id,
                    otp_hash,
                    expires_at,
                    attempt_count,
                    is_verified,
                    is_used
                )
                VALUES (?, ?, ?, 0, FALSE, FALSE)
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                sql,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {

            statement.setString(1, userId.trim());
            statement.setString(2, otpHash);

            statement.setTimestamp(
                    3,
                    Timestamp.valueOf(expiresAt)
            );

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 1) {

                try (ResultSet keys =
                             statement.getGeneratedKeys()) {

                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return -1;
    }

    public PasswordResetOtp findOtpById(int resetId) {

        String sql = """
                SELECT reset_id,
                       user_id,
                       otp_hash,
                       expires_at,
                       attempt_count,
                       is_verified,
                       is_used
                FROM password_reset_otps
                WHERE reset_id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, resetId);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {

                    Timestamp expiryTimestamp =
                            result.getTimestamp("expires_at");

                    LocalDateTime expiresAt =
                            expiryTimestamp == null
                                    ? null
                                    : expiryTimestamp.toLocalDateTime();

                    return new PasswordResetOtp(
                            result.getInt("reset_id"),
                            result.getString("user_id"),
                            result.getString("otp_hash"),
                            expiresAt,
                            result.getInt("attempt_count"),
                            result.getBoolean("is_verified"),
                            result.getBoolean("is_used")
                    );
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }

    public void incrementAttemptCount(int resetId) {

        String sql = """
                UPDATE password_reset_otps
                SET attempt_count = attempt_count + 1
                WHERE reset_id = ?
                  AND is_verified = FALSE
                  AND is_used = FALSE
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, resetId);
            statement.executeUpdate();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public boolean markOtpVerified(int resetId) {

        String sql = """
                UPDATE password_reset_otps
                SET is_verified = TRUE
                WHERE reset_id = ?
                  AND is_verified = FALSE
                  AND is_used = FALSE
                  AND expires_at > CURRENT_TIMESTAMP
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, resetId);

            return statement.executeUpdate() == 1;

        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public void markOtpUsed(int resetId) {

        String sql = """
                UPDATE password_reset_otps
                SET is_used = TRUE
                WHERE reset_id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, resetId);
            statement.executeUpdate();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public boolean resetPassword(
            int resetId,
            String userId,
            String newPasswordHash
    ) {

        String updatePasswordSql = """
                UPDATE users
                SET password_hash = ?,
                    must_change_password = FALSE
                WHERE user_id = ?
                  AND account_status IN ('ACTIVE', 'SUSPENDED')
                """;

        String useOtpSql = """
                UPDATE password_reset_otps
                SET is_used = TRUE
                WHERE reset_id = ?
                  AND user_id = ?
                  AND is_verified = TRUE
                  AND is_used = FALSE
                  AND expires_at > CURRENT_TIMESTAMP
                """;

        Connection connection = null;

        try {

            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            /*
             * First consume the verified OTP.
             * If it cannot be consumed, the password is not updated.
             */
            try (
                    PreparedStatement otpStatement =
                            connection.prepareStatement(useOtpSql);

                    PreparedStatement passwordStatement =
                            connection.prepareStatement(updatePasswordSql)
            ) {

                otpStatement.setInt(1, resetId);
                otpStatement.setString(2, userId.trim());

                int otpUpdated = otpStatement.executeUpdate();

                if (otpUpdated != 1) {
                    connection.rollback();
                    return false;
                }

                passwordStatement.setString(
                        1,
                        newPasswordHash
                );

                passwordStatement.setString(
                        2,
                        userId.trim()
                );

                int passwordUpdated =
                        passwordStatement.executeUpdate();

                if (passwordUpdated == 1) {

                    connection.commit();
                    return true;
                }

                connection.rollback();
                return false;
            }

        } catch (Exception exception) {

            if (connection != null) {

                try {
                    connection.rollback();
                } catch (Exception rollbackException) {
                    rollbackException.printStackTrace();
                }
            }

            exception.printStackTrace();
            return false;

        } finally {

            if (connection != null) {

                try {
                    connection.setAutoCommit(true);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                try {
                    connection.close();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    public void deleteOtp(int resetId) {

        String sql = """
                DELETE FROM password_reset_otps
                WHERE reset_id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, resetId);
            statement.executeUpdate();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}