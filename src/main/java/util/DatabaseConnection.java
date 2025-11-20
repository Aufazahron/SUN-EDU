package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class untuk mengelola koneksi database
 */
public class DatabaseConnection {
    private static final String DB_URL = "jdbc:mariadb://localhost:3306/edu-sun";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    
    private static Connection connection;
    
    /**
     * Mendapatkan koneksi database
     * @return Connection object
     * @throws SQLException jika terjadi error saat koneksi
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("Koneksi database berhasil!");
            } catch (SQLException e) {
                System.err.println("Error koneksi database: " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }
    
    /**
     * Menutup koneksi database
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Koneksi database ditutup.");
            }
        } catch (SQLException e) {
            System.err.println("Error menutup koneksi: " + e.getMessage());
        }
    }
    
    /**
     * Test koneksi database
     * @return true jika koneksi berhasil
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}

