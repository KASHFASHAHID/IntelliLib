package repository;

import config.DatabaseConnection;
import model.Role;
import model.StaffAccount;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import java.time.Year;

public class StaffAccountRepository {

    public List<StaffAccount> findAllStaffAccounts() {

        List<StaffAccount> staffAccounts =
                new ArrayList<>();

        String sql = """
                SELECT user_id,
                       name,
                       email,
                       role,
                       account_status
                FROM users
                WHERE role IN ('ADMIN', 'LIBRARIAN')
                ORDER BY role, name
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet result =
                        statement.executeQuery()
        ) {

            while (result.next()) {

                StaffAccount staffAccount =
                        new StaffAccount(
                                result.getString("user_id"),
                                result.getString("name"),
                                result.getString("email"),
                                Role.valueOf(
                                        result.getString("role")
                                ),
                                result.getString("account_status")
                        );

                staffAccounts.add(staffAccount);
            }

        } catch (Exception exception) {

            System.err.println(
                    "Staff accounts could not be loaded: "
                            + exception.getMessage()
            );

            exception.printStackTrace();
        }

        return staffAccounts;
    }

    public StaffAccount findStaffAccountById(
            String userId
    ) {

        if (userId == null || userId.isBlank()) {
            return null;
        }

        String sql = """
                SELECT user_id,
                       name,
                       email,
                       role,
                       account_status
                FROM users
                WHERE user_id = ?
                  AND role IN ('ADMIN', 'LIBRARIAN')
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
                    ResultSet result =
                            statement.executeQuery()
            ) {

                if (result.next()) {

                    return new StaffAccount(
                            result.getString("user_id"),
                            result.getString("name"),
                            result.getString("email"),
                            Role.valueOf(
                                    result.getString("role")
                            ),
                            result.getString("account_status")
                    );
                }
            }

        } catch (Exception exception) {

            System.err.println(
                    "Staff account could not be found: "
                            + exception.getMessage()
            );

            exception.printStackTrace();
        }

        return null;
    }

    public boolean updateStaffAccountStatus(
            String userId,
            String newStatus
    ) {

        if (userId == null
                || userId.isBlank()
                || !isAllowedStatus(newStatus)) {

            return false;
        }

        String sql = """
                UPDATE users
                SET account_status = ?,
                    updated_at = CURRENT_TIMESTAMP
                WHERE user_id = ?
                  AND role IN ('ADMIN', 'LIBRARIAN')
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    newStatus.trim().toUpperCase()
            );

            statement.setString(
                    2,
                    userId.trim()
            );

            return statement.executeUpdate() == 1;

        } catch (Exception exception) {

            System.err.println(
                    "Staff status could not be updated: "
                            + exception.getMessage()
            );

            exception.printStackTrace();

            return false;
        }
    }

    public String createPendingStaffAccount(
        String name,
        String email,
        Role role
) {

    if (name == null
            || name.isBlank()
            || email == null
            || email.isBlank()
            || role == null
            || (
                role != Role.ADMIN
                && role != Role.LIBRARIAN
            )) {

        return null;
    }

    String cleanName = name.trim();
    String cleanEmail =
            email.trim().toLowerCase();

    if (cleanName.length() > 100
            || cleanEmail.length() > 100) {

        return null;
    }

    String prefix =
            role == Role.ADMIN
                    ? "ADM"
                    : "LIB";

    int year =
            Year.now().getValue();

    Connection connection = null;

    try {

        connection =
                DatabaseConnection.getConnection();

        connection.setAutoCommit(false);

        if (emailExists(
                connection,
                cleanEmail
        )) {

            connection.rollback();
            return null;
        }

        String userId =
                generateNextStaffUserId(
                        connection,
                        prefix,
                        year
                );

        String sql = """
                INSERT INTO users(
                    user_id,
                    password_hash,
                    name,
                    email,
                    phone,
                    role,
                    university,
                    department,
                    account_status,
                    must_change_password,
                    created_at,
                    updated_at
                )
                VALUES (
                    ?,
                    NULL,
                    ?,
                    ?,
                    NULL,
                    ?,
                    'Brainware University',
                    NULL,
                    'PENDING_ACTIVATION',
                    FALSE,
                    CURRENT_TIMESTAMP,
                    CURRENT_TIMESTAMP
                )
                """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    userId
            );

            statement.setString(
                    2,
                    cleanName
            );

            statement.setString(
                    3,
                    cleanEmail
            );

            statement.setString(
                    4,
                    role.name()
            );

            int inserted =
                    statement.executeUpdate();

            if (inserted != 1) {

                connection.rollback();
                return null;
            }
        }

        connection.commit();

        return userId;

    } catch (Exception exception) {

        if (connection != null) {

            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
        }

        System.err.println(
                "Pending staff account could not be created: "
                        + exception.getMessage()
        );

        exception.printStackTrace();

        return null;

    } finally {

        if (connection != null) {

            try {
                connection.setAutoCommit(true);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }

            try {
                connection.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }
}

private boolean emailExists(
        Connection connection,
        String email
) throws SQLException {

    String sql = """
            SELECT 1
            FROM users
            WHERE LOWER(TRIM(email)) =
                  LOWER(TRIM(?))
            LIMIT 1
            """;

    try (
            PreparedStatement statement =
                    connection.prepareStatement(sql)
    ) {

        statement.setString(
                1,
                email
        );

        try (
                ResultSet result =
                        statement.executeQuery()
        ) {

            return result.next();
        }
    }
}

private String generateNextStaffUserId(
        Connection connection,
        String prefix,
        int year
) throws SQLException {

    String pattern =
            prefix
                    + "-"
                    + year
                    + "-%";

    String sql = """
            SELECT COALESCE(
                       MAX(
                           CAST(
                               RIGHT(user_id, 4)
                               AS UNSIGNED
                           )
                       ),
                       0
                   ) AS highest_number
            FROM users
            WHERE user_id LIKE ?
            """;

    int nextNumber;

    try (
            PreparedStatement statement =
                    connection.prepareStatement(sql)
    ) {

        statement.setString(
                1,
                pattern
        );

        try (
                ResultSet result =
                        statement.executeQuery()
        ) {

            if (!result.next()) {
                nextNumber = 1;
            } else {
                nextNumber =
                        result.getInt(
                                "highest_number"
                        ) + 1;
            }
        }
    }

    return "%s-%d-%04d".formatted(
            prefix,
            year,
            nextNumber
    );
}

    private boolean isAllowedStatus(
            String status
    ) {

        if (status == null || status.isBlank()) {
            return false;
        }

        String normalizedStatus =
                status.trim().toUpperCase();

        return normalizedStatus.equals("ACTIVE")
                || normalizedStatus.equals("SUSPENDED")
                || normalizedStatus.equals("BLOCKED");
    }
}