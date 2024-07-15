package notification;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.DatabaseConnection;
import javafx.scene.layout.VBox;

public class NotificationDAO {

	// --------------------------------------------------------------------------------------------------------------------------------------------
	// For alerts

	public boolean hasStockAlerts() {
	    DatabaseConnection dbConnectionInformation = new DatabaseConnection();
	    boolean hasAlerts = false;

	    try (Connection conn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
	            dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword())) {
	        String sql = "SELECT current_stock, item_minimum_threshold, item_maximum_threshold FROM inventory";

	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            ResultSet rs = stmt.executeQuery();
	            while (rs.next()) {
	                int currentStock = rs.getInt("current_stock");
	                int itemMinimumThreshold = rs.getInt("item_minimum_threshold");
	                int itemMaximumThreshold = rs.getInt("item_maximum_threshold");

	                if (currentStock < itemMinimumThreshold || currentStock == 0 || currentStock > itemMaximumThreshold) {
	                    hasAlerts = true;
	                    break; // No need to check further if one alert is found
	                }
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return hasAlerts;
	}

	public String getStockAlertMessages() {
	    StringBuilder sb = new StringBuilder();
	    DatabaseConnection dbConnectionInformation = new DatabaseConnection();

	    try (Connection conn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
	            dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword())) {
	        String sql = "SELECT item_name, item_category, item_brand, item_supplier, " +
	                     "current_stock, item_minimum_threshold, item_maximum_threshold " +
	                     "FROM inventory";

	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            ResultSet rs = stmt.executeQuery();
	            while (rs.next()) {
	                String itemName = rs.getString("item_name");
	                String itemCategory = rs.getString("item_category");
	                String itemBrand = rs.getString("item_brand");
	                String itemSupplier = rs.getString("item_supplier");
	                int currentStock = rs.getInt("current_stock");
	                int itemMinimumThreshold = rs.getInt("item_minimum_threshold");
	                int itemMaximumThreshold = rs.getInt("item_maximum_threshold");

	                String alertType = null;
	                if (currentStock == 0) {
	                    alertType = "Out of stock alert";
	                } 
	                if (currentStock < itemMinimumThreshold) {
	                    alertType = "Low stock alert";
	                } if (currentStock > itemMaximumThreshold) {
	                    alertType = "Overstock alert";
	                }

	                if (alertType != null) {
	                    String logMessage = formatLogMessage(itemName, itemCategory, itemBrand, itemSupplier);
	                    String alertMessage = String.format("%s - %s: Current stock: %d%n", alertType, logMessage, currentStock);
	                    sb.append(alertMessage);
	                }
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return sb.toString();
	}



	private String formatLogMessage(String itemName, String itemCategory, String itemBrand, String itemSupplier) {
		return itemName + "-" + itemCategory + "-" + itemBrand + "-" + itemSupplier;
	}
}
