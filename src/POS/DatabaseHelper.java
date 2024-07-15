package POS;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.DatabaseConnection;

public class DatabaseHelper {
	
	private static DatabaseConnection db = new DatabaseConnection();
	
    private static final String URL = db.getUrl();
    private static final String USER = db.getUsername();
    private static final String PASSWORD = db.getPassword();

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        System.out.println("Connected to the database");
        return connection;
    }

    public static boolean isReferenceCodeExistsInDatabase(String referenceCode) {
        String sql = "SELECT COUNT(*) FROM orders_table WHERE order_reference_code = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, referenceCode);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking reference code in database:");
            e.printStackTrace();
        }
        return false; // Default to false if an exception occurs
    }
}
