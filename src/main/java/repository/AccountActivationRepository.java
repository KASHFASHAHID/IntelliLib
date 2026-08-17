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

public class AccountActivationRepository {

    public User findPendingUser(
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
                WHERE TRIM(user_id) = TRIM(?)
                  AND LOWER(TRIM(email)) = LOWER(TRIM(?))
                  AND account_status = 'PENDING_ACTIVATION'
                  AND password_hash IS NULL
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, userId);
            statement.setString(2, email);

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (!resultSet.next()) {

                    System.err.println(
                            "Pending activation account not found for User ID: "
                                    + userId
                    );

                    return null;
                }

                String roleValue =
                        resultSet.getString("role");

                if (roleValue == null
                        || roleValue.isBlank()) {

                    System.err.println(
                            "Role is missing for User ID: "
                                    + userId
                    );

                    return null;
                }

                Role role = Role.valueOf(
                        roleValue.trim().toUpperCase()
                );

                User user = new User();

                user.setUserId(
                        resultSet.getString("user_id")
                );

                user.setPassword(
                        resultSet.getString("password_hash")
                );

                user.setName(
                        resultSet.getString("name")
                );

                user.setEmail(
                        resultSet.getString("email")
                );

                user.setRole(role);

                System.out.println(
                        "Pending activation account found: "
                                + user.getUserId()
                );

                return user;
            }

        } catch (Exception exception) {

            System.err.println(
                    "Pending activation account lookup failed: "
                            + exception.getMessage()
            );

            exception.printStackTrace();
            return null;
        }
    }

    public void invalidateExistingOtps(
            String userId
    ) {

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

            statement.setString(
                    1,
                    userId.trim()
            );

            statement.executeUpdate();

        } catch (Exception exception) {

            System.err.println(
                    "Existing activation OTPs could not be invalidated: "
                            + exception.getMessage()
            );

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

            statement.setString(
                    1,
                    userId.trim()
            );

            statement.setString(
                    2,
                    otpHash
            );

            statement.setTimestamp(
                    3,
                    Timestamp.valueOf(expiresAt)
            );

            int affectedRows =
                    statement.executeUpdate();

            if (affectedRows != 1) {
                return -1;
            }

            try (
                    ResultSet generatedKeys =
                            statement.getGeneratedKeys()
            ) {

                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

        } catch (Exception exception) {

            System.err.println(
                    "Activation OTP could not be saved: "
                            + exception.getMessage()
            );

            exception.printStackTrace();
        }

        return -1;
    }

    public PasswordResetOtp findOtpById(
            int resetId
    ) {

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

            statement.setInt(
                    1,
                    resetId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (!resultSet.next()) {
                    return null;
                }

                Timestamp expiryTimestamp =
                        resultSet.getTimestamp(
                                "expires_at"
                        );

                LocalDateTime expiresAt =
                        expiryTimestamp == null
                                ? null
                                : expiryTimestamp.toLocalDateTime();

                return new PasswordResetOtp(
                        resultSet.getInt("reset_id"),
                        resultSet.getString("user_id"),
                        resultSet.getString("otp_hash"),
                        expiresAt,
                        resultSet.getInt("attempt_count"),
                        resultSet.getBoolean("is_verified"),
                        resultSet.getBoolean("is_used")
                );
            }

        } catch (Exception exception) {

            System.err.println(
                    "Activation OTP could not be loaded: "
                            + exception.getMessage()
            );

            exception.printStackTrace();
            return null;
        }
    }

    public void incrementAttemptCount(
            int resetId
    ) {

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

            statement.setInt(
                    1,
                    resetId
            );

            statement.executeUpdate();

        } catch (Exception exception) {

            System.err.println(
                    "OTP attempt count could not be updated: "
                            + exception.getMessage()
            );

            exception.printStackTrace();
        }
    }

    public boolean markOtpVerified(
            int resetId
    ) {

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

            statement.setInt(
                    1,
                    resetId
            );

            return statement.executeUpdate() == 1;

        } catch (Exception exception) {

            System.err.println(
                    "Activation OTP could not be verified: "
                            + exception.getMessage()
            );

            exception.printStackTrace();
            return false;
        }
    }

    public void markOtpUsed(
            int resetId
    ) {

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

            statement.setInt(
                    1,
                    resetId
            );

            statement.executeUpdate();

        } catch (Exception exception) {

            System.err.println(
                    "Activation OTP could not be marked as used: "
                            + exception.getMessage()
            );

            exception.printStackTrace();
        }
    }

    public boolean activateAccount(
            int resetId,
            String userId,
            String passwordHash
    ) {

        String consumeOtpSql = """
                UPDATE password_reset_otps
                SET is_used = TRUE
                WHERE reset_id = ?
                  AND user_id = ?
                  AND is_verified = TRUE
                  AND is_used = FALSE
                  AND expires_at > CURRENT_TIMESTAMP
                """;

        String activateUserSql = """
                UPDATE users
                SET password_hash = ?,
                    account_status = 'ACTIVE',
                    must_change_password = FALSE
                WHERE user_id = ?
                  AND account_status = 'PENDING_ACTIVATION'
                  AND password_hash IS NULL
                """;

        Connection connection = null;

        try {

            connection =
                    DatabaseConnection.getConnection();

            connection.setAutoCommit(false);

            try (
                    PreparedStatement otpStatement =
                            connection.prepareStatement(
                                    consumeOtpSql
                            );

                    PreparedStatement userStatement =
                            connection.prepareStatement(
                                    activateUserSql
                            )
            ) {

                otpStatement.setInt(
                        1,
                        resetId
                );

                otpStatement.setString(
                        2,
                        userId.trim()
                );

                int otpUpdated =
                        otpStatement.executeUpdate();

                if (otpUpdated != 1) {

                    connection.rollback();
                    return false;
                }

                userStatement.setString(
                        1,
                        passwordHash
                );

                userStatement.setString(
                        2,
                        userId.trim()
                );

                int userUpdated =
                        userStatement.executeUpdate();

                if (userUpdated != 1) {

                    connection.rollback();
                    return false;
                }

                connection.commit();
                return true;
            }

        } catch (Exception exception) {

            if (connection != null) {

                try {
                    connection.rollback();
                } catch (Exception rollbackException) {
                    rollbackException.printStackTrace();
                }
            }

            System.err.println(
                    "Account could not be activated: "
                            + exception.getMessage()
            );

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

    public void deleteOtp(
            int resetId
    ) {

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

            statement.setInt(
                    1,
                    resetId
            );

            statement.executeUpdate();

        } catch (Exception exception) {

            System.err.println(
                    "Activation OTP could not be deleted: "
                            + exception.getMessage()
            );

            exception.printStackTrace();
        }
    }
}