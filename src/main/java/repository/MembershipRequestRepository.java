package repository;

import config.DatabaseConnection;
import model.MembershipRequest;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MembershipRequestRepository {

    public List<MembershipRequest> getAllPendingRequests() {

        List<MembershipRequest> requests = new ArrayList<>();

        String sql = """
                SELECT request_id,
                       full_name,
                       brainware_id,
                       university,
                       role_requested,
                       course_or_designation,
                       department,
                       semester,
                       email,
                       phone,
                       reason,
                       status
                FROM membership_requests
                WHERE status = 'PENDING'
                ORDER BY request_id
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

                requests.add(
                        new MembershipRequest(
                                resultSet.getInt("request_id"),
                                resultSet.getString("full_name"),
                                resultSet.getString("brainware_id"),
                                resultSet.getString("role_requested"),
                                resultSet.getString("department"),
                                resultSet.getString("email"),
                                resultSet.getString("phone"),
                                resultSet.getString("status"),
                                resultSet.getString("university"),
                                resultSet.getString(
                                        "course_or_designation"
                                ),
                                (Integer) resultSet.getObject(
                                        "semester"
                                ),
                                resultSet.getString("reason")
                        )
                );
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return requests;
    }

    public String[] approveRequest(
            MembershipRequest request,
            String reviewedBy
    ) {

        if (request == null
                || reviewedBy == null
                || reviewedBy.isBlank()) {

            return null;
        }

        String role = request.getRoleRequested();

        if (!"STUDENT".equals(role)
                && !"TEACHER".equals(role)) {

            return null;
        }

        String prefix =
                "TEACHER".equals(role)
                        ? "TEA"
                        : "STU";

        String newUserId =
                prefix
                        + "-2026-"
                        + String.format(
                                "%04d",
                                request.getRequestId()
                        );

        String cardNumber =
                "CARD-2026-"
                        + String.format(
                                "%04d",
                                request.getRequestId()
                        );

        String insertUserSql = """
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
                    must_change_password
                )
                VALUES (
                    ?,
                    NULL,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    'PENDING_ACTIVATION',
                    TRUE
                )
                """;

        String insertCardSql = """
                INSERT INTO library_cards(
                    card_number,
                    user_id,
                    issue_date,
                    expiry_date,
                    status
                )
                VALUES (?, ?, ?, ?, 'ACTIVE')
                """;

        String updateRequestSql = """
                UPDATE membership_requests
                SET status = 'APPROVED',
                    reviewed_by = ?,
                    reviewed_at = CURRENT_TIMESTAMP,
                    remarks = ?
                WHERE request_id = ?
                  AND status = 'PENDING'
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection()
        ) {

            connection.setAutoCommit(false);

            try (
                    PreparedStatement userStatement =
                            connection.prepareStatement(
                                    insertUserSql
                            );

                    PreparedStatement cardStatement =
                            connection.prepareStatement(
                                    insertCardSql
                            );

                    PreparedStatement requestStatement =
                            connection.prepareStatement(
                                    updateRequestSql
                            )
            ) {

                userStatement.setString(
                        1,
                        newUserId
                );

                userStatement.setString(
                        2,
                        request.getFullName()
                );

                userStatement.setString(
                        3,
                        request.getEmail()
                );

                userStatement.setString(
                        4,
                        request.getPhone()
                );

                userStatement.setString(
                        5,
                        role
                );

                userStatement.setString(
                        6,
                        request.getUniversity()
                );

                userStatement.setString(
                        7,
                        request.getDepartment()
                );

                if (userStatement.executeUpdate() != 1) {
                    connection.rollback();
                    return null;
                }

                LocalDate issueDate = LocalDate.now();
                LocalDate expiryDate =
                        issueDate.plusYears(1);

                cardStatement.setString(
                        1,
                        cardNumber
                );

                cardStatement.setString(
                        2,
                        newUserId
                );

                cardStatement.setDate(
                        3,
                        Date.valueOf(issueDate)
                );

                cardStatement.setDate(
                        4,
                        Date.valueOf(expiryDate)
                );

                if (cardStatement.executeUpdate() != 1) {
                    connection.rollback();
                    return null;
                }

                requestStatement.setString(
                        1,
                        reviewedBy.trim()
                );

                requestStatement.setString(
                        2,
                        "Membership approved. "
                                + "Account activation required."
                );

                requestStatement.setInt(
                        3,
                        request.getRequestId()
                );

                if (requestStatement.executeUpdate() != 1) {
                    connection.rollback();
                    return null;
                }

                connection.commit();

                return new String[]{
                        newUserId,
                        cardNumber
                };

            } catch (Exception exception) {

                connection.rollback();
                exception.printStackTrace();
                return null;

            } finally {

                connection.setAutoCommit(true);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public boolean rejectRequest(
            MembershipRequest request,
            String reviewedBy,
            String rejectionReason
    ) {

        if (request == null
                || reviewedBy == null
                || reviewedBy.isBlank()
                || rejectionReason == null
                || rejectionReason.isBlank()) {

            return false;
        }

        String sql = """
                UPDATE membership_requests
                SET status = 'REJECTED',
                    reviewed_by = ?,
                    reviewed_at = CURRENT_TIMESTAMP,
                    rejection_reason = ?
                WHERE request_id = ?
                  AND status = 'PENDING'
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    reviewedBy.trim()
            );

            statement.setString(
                    2,
                    rejectionReason.trim()
            );

            statement.setInt(
                    3,
                    request.getRequestId()
            );

            return statement.executeUpdate() == 1;

        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public boolean submitRequest(
            MembershipRequest request
    ) {

        if (request == null) {
            return false;
        }

        String sql = """
                INSERT INTO membership_requests(
                    full_name,
                    brainware_id,
                    university,
                    role_requested,
                    course_or_designation,
                    department,
                    semester,
                    email,
                    phone,
                    reason,
                    status
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'PENDING')
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    request.getFullName()
            );

            statement.setString(
                    2,
                    request.getBrainwareId()
            );

            statement.setString(
                    3,
                    request.getUniversity()
            );

            statement.setString(
                    4,
                    request.getRoleRequested()
            );

            statement.setString(
                    5,
                    request.getCourseOrDesignation()
            );

            statement.setString(
                    6,
                    request.getDepartment()
            );

            if (request.getSemester() != null) {

                statement.setInt(
                        7,
                        request.getSemester()
                );

            } else {

                statement.setNull(
                        7,
                        java.sql.Types.INTEGER
                );
            }

            statement.setString(
                    8,
                    request.getEmail()
            );

            statement.setString(
                    9,
                    request.getPhone()
            );

            statement.setString(
                    10,
                    request.getReason()
            );

            return statement.executeUpdate() == 1;

        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }
}