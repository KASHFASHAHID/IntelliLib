package repository;

import config.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MembershipRepository {

    public ResultSet getPendingRequests() {

        try {
            Connection connection = DatabaseConnection.getConnection();

            String sql = """
                    SELECT request_id, full_name, brainware_id, role_requested,
                           department, email, phone, status
                    FROM membership_requests
                    WHERE status = 'PENDING'
                    """;

            PreparedStatement statement = connection.prepareStatement(sql);
            return statement.executeQuery();

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch membership requests", e);
        }
    }
}