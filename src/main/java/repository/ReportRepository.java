package repository;

import config.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReportRepository {

    private int getCount(String sql) {

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getTotalMembers() {

        String sql = """
                SELECT COUNT(*)
                FROM users
                WHERE role IN ('STUDENT', 'TEACHER')
                  AND account_status = 'ACTIVE'
                """;

        return getCount(sql);
    }

    public int getTotalBooks() {

        String sql = """
                SELECT COUNT(*)
                FROM books
                """;

        return getCount(sql);
    }

    public int getTotalCopies() {

        String sql = """
                SELECT COUNT(*)
                FROM book_copies
                """;

        return getCount(sql);
    }

    public int getAvailableCopies() {

        String sql = """
                SELECT COUNT(*)
                FROM book_copies
                WHERE status = 'AVAILABLE'
                """;

        return getCount(sql);
    }

    public int getIssuedBooks() {

        String sql = """
                SELECT COUNT(*)
                FROM book_loans
                WHERE status = 'ISSUED'
                """;

        return getCount(sql);
    }

    public int getReturnedBooks() {

        String sql = """
                SELECT COUNT(*)
                FROM book_loans
                WHERE status = 'RETURNED'
                """;

        return getCount(sql);
    }

    public int getOverdueBooks() {

        String sql = """
                SELECT COUNT(*)
                FROM book_loans
                WHERE status = 'ISSUED'
                  AND due_date < CURDATE()
                """;

        return getCount(sql);
    }

    public int getActiveReservations() {

        String sql = """
                SELECT COUNT(*)
                FROM reservations
                WHERE status IN (
                    'WAITING',
                    'READY_FOR_PICKUP'
                )
                """;

        return getCount(sql);
    }

    public int getPendingFinesCount() {

        String sql = """
                SELECT COUNT(*)
                FROM fines
                WHERE status = 'PENDING'
                """;

        return getCount(sql);
    }

    public BigDecimal getPendingFineAmount() {

        String sql = """
                SELECT COALESCE(SUM(amount), 0)
                FROM fines
                WHERE status = 'PENDING'
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            if (resultSet.next()) {

                BigDecimal amount =
                        resultSet.getBigDecimal(1);

                return amount == null
                        ? BigDecimal.ZERO
                        : amount;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return BigDecimal.ZERO;
    }

    public BigDecimal getCollectedFineAmount() {

        String sql = """
                SELECT COALESCE(SUM(amount), 0)
                FROM fines
                WHERE status = 'PAID'
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            if (resultSet.next()) {

                BigDecimal amount =
                        resultSet.getBigDecimal(1);

                return amount == null
                        ? BigDecimal.ZERO
                        : amount;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return BigDecimal.ZERO;
    }
}