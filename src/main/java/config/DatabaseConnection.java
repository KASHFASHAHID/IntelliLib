package config;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://mysql-3ebc8e0e-shahidkashfa-05da.a.aivencloud.com:10408/brainware_smart_library?sslMode=REQUIRED";

    private static final String USER =  "avnadmin";

    private static final String PASSWORD =
        System.getenv("INTELLILIB_DB_PASSWORD");

    public static Connection getConnection() {

        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            throw new RuntimeException("Database Connection Failed!", e);
        }
    }
}
