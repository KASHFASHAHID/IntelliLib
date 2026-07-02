package app;

import config.DatabaseConnection;
import java.sql.Connection;

public class DatabaseTest {

    public static void main(String[] args) {

        try (Connection connection = DatabaseConnection.getConnection()) {

            if (connection != null) {
                System.out.println("✅ Connected to MySQL Successfully!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}