package service;

import config.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class OverdueLoanService {

    public int updateAllOverdueLoans() {

        String sql = """
                UPDATE book_loans
                SET status = 'OVERDUE'
                WHERE return_date IS NULL
                  AND status = 'ISSUED'
                  AND due_date < CURRENT_DATE
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            return statement.executeUpdate();

        } catch (Exception exception) {

            System.err.println(
                    "Global overdue loan update failed: "
                            + exception.getMessage()
            );

            exception.printStackTrace();

            return 0;
        }
    }
}