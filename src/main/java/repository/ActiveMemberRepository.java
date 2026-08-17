package repository;

import config.DatabaseConnection;
import model.ActiveMember;
import model.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ActiveMemberRepository {

    /*
     * Used when we need only ACTIVE students and teachers,
     * such as the Active Members dashboard count/details.
     */
    public List<ActiveMember> findAllActiveMembers() {

        return findMembersByStatuses(
                List.of("ACTIVE")
        );
    }

    /*
     * Used by the member-management page.
     * It includes active, suspended, and blocked members,
     * allowing the Admin to reactivate restricted accounts.
     */
    public List<ActiveMember> findAllManageableMembers() {

        return findMembersByStatuses(
                List.of(
                        "ACTIVE",
                        "SUSPENDED",
                        "BLOCKED"
                )
        );
    }

    private List<ActiveMember> findMembersByStatuses(
            List<String> statuses
    ) {

        List<ActiveMember> members =
                new ArrayList<>();

        if (statuses == null || statuses.isEmpty()) {
            return members;
        }

        String placeholders =
                String.join(
                        ", ",
                        java.util.Collections.nCopies(
                                statuses.size(),
                                "?"
                        )
                );

        String sql = """
                SELECT user_id,
                       name,
                       email,
                       phone,
                       role,
                       department,
                       account_status
                FROM users
                WHERE role IN ('STUDENT', 'TEACHER')
                  AND account_status IN (%s)
                ORDER BY
                    CASE account_status
                        WHEN 'ACTIVE' THEN 1
                        WHEN 'SUSPENDED' THEN 2
                        WHEN 'BLOCKED' THEN 3
                        ELSE 4
                    END,
                    role,
                    name
                """.formatted(placeholders);

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            for (int index = 0;
                 index < statuses.size();
                 index++) {

                statement.setString(
                        index + 1,
                        statuses.get(index)
                );
            }

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                while (resultSet.next()) {

                    String roleValue =
                            resultSet.getString("role");

                    if (roleValue == null
                            || roleValue.isBlank()) {

                        continue;
                    }

                    Role role = Role.valueOf(
                            roleValue
                                    .trim()
                                    .toUpperCase()
                    );

                    ActiveMember member =
                            new ActiveMember(
                                    resultSet.getString(
                                            "user_id"
                                    ),
                                    resultSet.getString(
                                            "name"
                                    ),
                                    resultSet.getString(
                                            "email"
                                    ),
                                    resultSet.getString(
                                            "phone"
                                    ),
                                    role,
                                    resultSet.getString(
                                            "department"
                                    ),
                                    resultSet.getString(
                                            "account_status"
                                    )
                            );

                    members.add(member);
                }
            }

        } catch (Exception exception) {

            System.err.println(
                    "Members could not be loaded: "
                            + exception.getMessage()
            );

            exception.printStackTrace();
        }

        return members;
    }

    /*
     * Changes the status only when the selected account
     * belongs to a STUDENT or TEACHER.
     *
     * This prevents an Admin from modifying staff accounts.
     */
    public boolean updateAccountStatus(
            String userId,
            String newStatus
    ) {

        if (userId == null || userId.isBlank()
                || newStatus == null
                || newStatus.isBlank()) {

            return false;
        }

        String normalizedStatus =
                newStatus
                        .trim()
                        .toUpperCase();

        if (!isAllowedStatus(normalizedStatus)) {
            return false;
        }

        String sql = """
                UPDATE users
                SET account_status = ?
                WHERE user_id = ?
                  AND role IN ('STUDENT', 'TEACHER')
                  AND account_status IN (
                      'ACTIVE',
                      'SUSPENDED',
                      'BLOCKED'
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
                    normalizedStatus
            );

            statement.setString(
                    2,
                    userId.trim()
            );

            return statement.executeUpdate() == 1;

        } catch (Exception exception) {

            System.err.println(
                    "Member account status could not be updated: "
                            + exception.getMessage()
            );

            exception.printStackTrace();
            return false;
        }
    }

    /*
     * Loads one manageable member before an account action.
     * The service will use this for validation, logging,
     * notifications, and email.
     */
    public ActiveMember findManageableMemberById(
            String userId
    ) {

        if (userId == null || userId.isBlank()) {
            return null;
        }

        String sql = """
                SELECT user_id,
                       name,
                       email,
                       phone,
                       role,
                       department,
                       account_status
                FROM users
                WHERE user_id = ?
                  AND role IN ('STUDENT', 'TEACHER')
                  AND account_status IN (
                      'ACTIVE',
                      'SUSPENDED',
                      'BLOCKED'
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

                if (!resultSet.next()) {
                    return null;
                }

                String roleValue =
                        resultSet.getString("role");

                if (roleValue == null
                        || roleValue.isBlank()) {

                    return null;
                }

                Role role = Role.valueOf(
                        roleValue
                                .trim()
                                .toUpperCase()
                );

                return new ActiveMember(
                        resultSet.getString(
                                "user_id"
                        ),
                        resultSet.getString(
                                "name"
                        ),
                        resultSet.getString(
                                "email"
                        ),
                        resultSet.getString(
                                "phone"
                        ),
                        role,
                        resultSet.getString(
                                "department"
                        ),
                        resultSet.getString(
                                "account_status"
                        )
                );
            }

        } catch (Exception exception) {

            System.err.println(
                    "Member account could not be loaded: "
                            + exception.getMessage()
            );

            exception.printStackTrace();
            return null;
        }
    }

    private boolean isAllowedStatus(
            String status
    ) {

        return "ACTIVE".equals(status)
                || "SUSPENDED".equals(status)
                || "BLOCKED".equals(status);
    }
}