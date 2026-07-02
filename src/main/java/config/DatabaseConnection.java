package config;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/brainware_smart_library";

    private static final String USER = "root";

    private static final String PASSWORD = "WJ28@krhps";

    public static Connection getConnection() {

        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            throw new RuntimeException("Database Connection Failed!", e);
        }
    }
}
