package repository;

import config.DatabaseConnection;
import model.DashboardStatistics;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DashboardRepository {

    public DashboardStatistics getStatistics() {

        DashboardStatistics statistics =
                new DashboardStatistics();

        String sql = """
                SELECT
                    (SELECT COUNT(*)
                     FROM books)
                     AS total_book_titles,

                    (SELECT COUNT(*)
                     FROM book_copies)
                     AS total_book_copies,

                    (SELECT COUNT(*)
                     FROM book_copies
                     WHERE status = 'AVAILABLE')
                     AS available_copies,

                    (SELECT COUNT(*)
                     FROM book_loans
                     WHERE return_date IS NULL
                       AND status IN ('ISSUED', 'OVERDUE'))
                     AS issued_copies,

                    (SELECT COUNT(*)
                     FROM book_loans
                     WHERE return_date IS NULL
                       AND status IN ('ISSUED', 'OVERDUE')
                       AND due_date < CURRENT_DATE)
                     AS overdue_loans,

                    (SELECT COUNT(*)
                     FROM users
                     WHERE role IN ('STUDENT', 'TEACHER')
                       AND account_status = 'ACTIVE')
                     AS active_members,

                    (SELECT COUNT(*)
                     FROM membership_requests
                     WHERE status = 'PENDING')
                     AS pending_membership_requests,

                    (SELECT COUNT(*)
                     FROM reservations
                     WHERE status IN (
                         'WAITING',
                         'READY_FOR_PICKUP'
                     ))
                     AS active_reservations,

                    (SELECT COALESCE(SUM(amount), 0)
                     FROM fines
                     WHERE status = 'PENDING')
                     AS pending_fine_amount
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

                statistics.setTotalBookTitles(
                        resultSet.getInt(
                                "total_book_titles"
                        )
                );

                statistics.setTotalBookCopies(
                        resultSet.getInt(
                                "total_book_copies"
                        )
                );

                statistics.setAvailableCopies(
                        resultSet.getInt(
                                "available_copies"
                        )
                );

                statistics.setIssuedCopies(
                        resultSet.getInt(
                                "issued_copies"
                        )
                );

                statistics.setOverdueLoans(
                        resultSet.getInt(
                                "overdue_loans"
                        )
                );

                statistics.setActiveMembers(
                        resultSet.getInt(
                                "active_members"
                        )
                );

                statistics.setPendingMembershipRequests(
                        resultSet.getInt(
                                "pending_membership_requests"
                        )
                );

                statistics.setActiveReservations(
                        resultSet.getInt(
                                "active_reservations"
                        )
                );

                BigDecimal pendingFineAmount =
                        resultSet.getBigDecimal(
                                "pending_fine_amount"
                        );

                statistics.setPendingFineAmount(
                        pendingFineAmount
                );
            }

        } catch (Exception exception) {

            System.err.println(
                    "Dashboard statistics could not be loaded: "
                            + exception.getMessage()
            );

            exception.printStackTrace();
        }

        return statistics;
    }
}