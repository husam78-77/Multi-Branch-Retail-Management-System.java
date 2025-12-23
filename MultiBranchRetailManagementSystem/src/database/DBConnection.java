package database;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static Connection connection;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {

                connection = DriverManager.getConnection(
                        DBConfig.URL,
                        DBConfig.USER,
                        DBConfig.PASSWORD
                );

                System.out.println("✅ Connected to Supabase successfully");
            }
        } catch (Exception e) {
            System.out.println("❌ Database connection failed");
            e.printStackTrace();
        }
        return connection;
    }
}
