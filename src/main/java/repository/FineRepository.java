package repository;

import config.DatabaseConnection;
import model.Fine;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.OverdueFineNotice;

import java.sql.Date;
import java.time.LocalDate;

public class FineRepository {

    public List<Fine> getFinesByUser(
            String userId
    ) {

        List<Fine> fines =
                new ArrayList<>();

        if (userId == null || userId.isBlank()) {
            return fines;
        }

        String sql = """
                SELECT
                    f.fine_id,
                    f.loan_id,
                    b.title AS book_title,
                    bl.copy_number,
                    f.amount,
                    f.status,
                    f.created_at,
                    f.paid_at
                FROM fines f
                JOIN book_loans bl
                    ON f.loan_id = bl.loan_id
                JOIN book_copies bc
                    ON bl.copy_number = bc.copy_number
                JOIN books b
                    ON bc.isbn = b.isbn
                WHERE f.user_id = ?
                ORDER BY f.created_at DESC
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

                while (resultSet.next()) {

                    Timestamp created =
                            resultSet.getTimestamp(
                                    "created_at"
                            );

                    Timestamp paid =
                            resultSet.getTimestamp(
                                    "paid_at"
                            );

                    fines.add(
                            new Fine(
                                    resultSet.getInt(
                                            "fine_id"
                                    ),
                                    resultSet.getInt(
                                            "loan_id"
                                    ),
                                    resultSet.getString(
                                            "book_title"
                                    ),
                                    resultSet.getString(
                                            "copy_number"
                                    ),
                                    resultSet.getBigDecimal(
                                            "amount"
                                    ),
                                    resultSet.getString(
                                            "status"
                                    ),
                                    created == null
                                            ? null
                                            : created.toLocalDateTime(),
                                    paid == null
                                            ? null
                                            : paid.toLocalDateTime()
                            )
                    );
                }
            }

        } catch (Exception exception) {

            System.err.println(
                    "Fines could not be loaded: "
                            + exception.getMessage()
            );

            exception.printStackTrace();
        }

        return fines;
    }

    public BigDecimal getOutstandingFineAmount(
            String userId
    ) {

        if (userId == null || userId.isBlank()) {
            return BigDecimal.ZERO;
        }

        String sql = """
                SELECT COALESCE(
                    SUM(amount),
                    0
                ) AS outstanding_amount
                FROM fines
                WHERE user_id = ?
                  AND status = 'PENDING'
                  AND paid_at IS NULL
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

                    BigDecimal amount =
                            resultSet.getBigDecimal(
                                    "outstanding_amount"
                            );

                    return amount == null
                            ? BigDecimal.ZERO
                            : amount;
                }
            }

        } catch (Exception exception) {

            System.err.println(
                    "Outstanding fine amount could not be loaded: "
                            + exception.getMessage()
            );

            exception.printStackTrace();
        }

        return BigDecimal.ZERO;
    }

    public boolean markFineAsPaid(
            int fineId,
            String memberUserId,
            String paymentMethod,
            String paymentReference,
            String paidByUserId
    ) {

        if (fineId <= 0
                || memberUserId == null
                || memberUserId.isBlank()
                || paymentMethod == null
                || paymentMethod.isBlank()
                || paidByUserId == null
                || paidByUserId.isBlank()) {

            return false;
        }

        String sql = """
                UPDATE fines
                SET status = 'PAID',
                    paid_at = CURRENT_TIMESTAMP,
                    payment_method = ?,
                    payment_reference = ?,
                    paid_by = ?
                WHERE fine_id = ?
  AND user_id = ?
  AND status = 'PENDING'
  AND paid_at IS NULL
  AND finalized_at IS NOT NULL
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    paymentMethod.trim().toUpperCase()
            );

            if (paymentReference == null
                    || paymentReference.isBlank()) {

                statement.setNull(
                        2,
                        java.sql.Types.VARCHAR
                );

            } else {

                statement.setString(
                        2,
                        paymentReference.trim()
                );
            }

            statement.setString(
                    3,
                    paidByUserId.trim()
            );

            statement.setInt(
                    4,
                    fineId
            );

            statement.setString(
                    5,
                    memberUserId.trim()
            );

            return statement.executeUpdate() == 1;

        } catch (Exception exception) {

            System.err.println(
                    "Fine payment could not be recorded: "
                            + exception.getMessage()
            );

            exception.printStackTrace();

            return false;
        }
    }

    public int synchronizeRunningOverdueFines(
        BigDecimal finePerDay
) {

    if (finePerDay == null
            || finePerDay.compareTo(BigDecimal.ZERO) <= 0) {

        return 0;
    }

    String insertMissingFinesSql = """
            INSERT INTO fines
            (
                user_id,
                loan_id,
                amount,
                status,
                created_at
            )
            SELECT
                bl.user_id,
                bl.loan_id,
                DATEDIFF(
                    CURRENT_DATE,
                    bl.due_date
                ) * ?,
                'PENDING',
                CURRENT_TIMESTAMP
            FROM book_loans bl
            WHERE bl.return_date IS NULL
              AND bl.status = 'OVERDUE'
              AND bl.due_date < CURRENT_DATE
              AND NOT EXISTS (
                  SELECT 1
                  FROM fines f
                  WHERE f.loan_id = bl.loan_id
              )
            """;

    String updateRunningFinesSql = """
            UPDATE fines f
            JOIN book_loans bl
                ON f.loan_id = bl.loan_id
            SET f.amount =
                    DATEDIFF(
                        CURRENT_DATE,
                        bl.due_date
                    ) * ?
            WHERE bl.return_date IS NULL
              AND bl.status = 'OVERDUE'
              AND bl.due_date < CURRENT_DATE
              AND f.status = 'PENDING'
              AND f.paid_at IS NULL
              AND f.finalized_at IS NULL
            """;

    try (
            Connection connection =
                    DatabaseConnection.getConnection()
    ) {

        connection.setAutoCommit(false);

        try (
                PreparedStatement insertStatement =
                        connection.prepareStatement(
                                insertMissingFinesSql
                        );

                PreparedStatement updateStatement =
                        connection.prepareStatement(
                                updateRunningFinesSql
                        )
        ) {

            insertStatement.setBigDecimal(
                    1,
                    finePerDay
            );

            int insertedRows =
                    insertStatement.executeUpdate();

            updateStatement.setBigDecimal(
                    1,
                    finePerDay
            );

            int updatedRows =
                    updateStatement.executeUpdate();

            connection.commit();

            return insertedRows + updatedRows;

        } catch (Exception exception) {

            connection.rollback();

            System.err.println(
                    "Running overdue fines could not be synchronized: "
                            + exception.getMessage()
            );

            exception.printStackTrace();

            return 0;
        }

    } catch (Exception exception) {

        System.err.println(
                "Running overdue fine transaction failed: "
                        + exception.getMessage()
        );

        exception.printStackTrace();

        return 0;
    }
}
public List<OverdueFineNotice>
getOverdueFinesNeedingNotification() {

    List<OverdueFineNotice> notices =
            new ArrayList<>();

    String sql = """
            SELECT
                f.fine_id,
                f.loan_id,
                f.user_id,
                u.name AS member_name,
                u.email AS member_email,
                b.title AS book_title,
                bl.due_date,
                DATEDIFF(
                    CURRENT_DATE,
                    bl.due_date
                ) AS overdue_days,
                f.amount
            FROM fines f
            JOIN book_loans bl
                ON f.loan_id = bl.loan_id
            JOIN users u
                ON f.user_id = u.user_id
            JOIN book_copies bc
                ON bl.copy_number = bc.copy_number
            JOIN books b
                ON bc.isbn = b.isbn
            WHERE f.status = 'PENDING'
              AND f.paid_at IS NULL
              AND f.finalized_at IS NULL
              AND bl.return_date IS NULL
              AND bl.status = 'OVERDUE'
              AND bl.due_date < CURRENT_DATE
              AND (
                    f.last_notified_date IS NULL
                    OR f.last_notified_date < CURRENT_DATE
              )
            ORDER BY bl.due_date
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

            LocalDate dueDate =
                    resultSet
                            .getDate("due_date")
                            .toLocalDate();

            notices.add(
                    new OverdueFineNotice(
                            resultSet.getInt(
                                    "fine_id"
                            ),
                            resultSet.getInt(
                                    "loan_id"
                            ),
                            resultSet.getString(
                                    "user_id"
                            ),
                            resultSet.getString(
                                    "member_name"
                            ),
                            resultSet.getString(
                                    "member_email"
                            ),
                            resultSet.getString(
                                    "book_title"
                            ),
                            dueDate,
                            resultSet.getLong(
                                    "overdue_days"
                            ),
                            resultSet.getBigDecimal(
                                    "amount"
                            )
                    )
            );
        }

    } catch (Exception exception) {

        System.err.println(
                "Overdue fine notices could not be loaded: "
                        + exception.getMessage()
        );

        exception.printStackTrace();
    }

    return notices;
}

public boolean markFineNotificationSent(
        int fineId,
        LocalDate notificationDate
) {

    if (fineId <= 0 || notificationDate == null) {
        return false;
    }

    String sql = """
            UPDATE fines
            SET last_notified_date = ?
            WHERE fine_id = ?
              AND status = 'PENDING'
              AND paid_at IS NULL
              AND finalized_at IS NULL
            """;

    try (
            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
    ) {

        statement.setDate(
                1,
                Date.valueOf(notificationDate)
        );

        statement.setInt(
                2,
                fineId
        );

        return statement.executeUpdate() == 1;

    } catch (Exception exception) {

        System.err.println(
                "Fine notification date could not be updated: "
                        + exception.getMessage()
        );

        exception.printStackTrace();

        return false;
    }
}
public boolean isFineFinalized(
        int fineId,
        String memberUserId
) {

    if (fineId <= 0
            || memberUserId == null
            || memberUserId.isBlank()) {

        return false;
    }

    String sql = """
            SELECT finalized_at
            FROM fines
            WHERE fine_id = ?
              AND user_id = ?
              AND status = 'PENDING'
              AND paid_at IS NULL
            """;

    try (
            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
    ) {

        statement.setInt(
                1,
                fineId
        );

        statement.setString(
                2,
                memberUserId.trim()
        );

        try (
                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            if (!resultSet.next()) {
                return false;
            }

            return resultSet.getTimestamp(
                    "finalized_at"
            ) != null;
        }

    } catch (Exception exception) {

        System.err.println(
                "Fine finalization status could not be checked: "
                        + exception.getMessage()
        );

        exception.printStackTrace();

        return false;
    }
}

public boolean hasPendingFine(String userId) {

    if (userId == null || userId.isBlank()) {
        return false;
    }

    String sql = """
            SELECT COUNT(*)
            FROM fines
            WHERE user_id = ?
              AND status = 'PENDING'
              AND paid_at IS NULL
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
                return resultSet.getInt(1) > 0;
            }
        }

    } catch (Exception exception) {

        System.err.println(
                "Pending fine check failed: "
                        + exception.getMessage()
        );

        exception.printStackTrace();
    }

    return false;
}
}