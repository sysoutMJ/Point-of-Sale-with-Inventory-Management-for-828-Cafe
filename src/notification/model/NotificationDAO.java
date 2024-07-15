package notification.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.DatabaseConnection;
import javafx.scene.layout.VBox;

public class NotificationDAO {
	
public void checkStock(VBox vbox) throws SQLException {
		
		DatabaseConnection dbConnectionInformation = new DatabaseConnection();

		String selectAllQuery = "SELECT item_name, item_brand, item_supplier, item_category, current_stock, item_minimum_threshold, item_maximum_threshold "
				+ "FROM 828cafe.inventory";

		try (Connection myConn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
				PreparedStatement getQuantityStatement = myConn.prepareStatement(selectAllQuery);
				ResultSet resultSet = getQuantityStatement.executeQuery()) {

			while (resultSet.next()) {
				String itemName = resultSet.getString("item_name");
				String itemBrand = resultSet.getString("item_brand");
				String itemSupplier = resultSet.getString("item_supplier");
				String itemCategory = resultSet.getString("item_category");
				
				String itemDetails = itemName + "-" + itemBrand + "-" + itemSupplier + "-" + itemCategory + "" ;
				String belowMinimumText = "Low Stock: ";
				String aboveMaximumText = "Over Stock: ";
				
				int currentStock = resultSet.getInt("current_stock");
				int minimumThreshold = resultSet.getInt("item_minimum_threshold");
				int maximumThreshold = resultSet.getInt("item_maximum_threshold");

				// Check if current stock is below minimum or above maximum thresholds
				if (currentStock < minimumThreshold) {
					System.out.println(belowMinimumText + itemDetails);
//					addLowStockNotification(itemName, itemCategory, itemBrand, itemSupplier, vbox);
					// You could trigger notifications or other actions here
				}
				if (currentStock > maximumThreshold) {
					System.out.println(aboveMaximumText + itemDetails);
//					addNotification("Over Stock:", itemName, itemCategory, itemBrand, itemSupplier, "out-of-stock-notification-hbox", vbox);
					// You could trigger notifications or other actions here
				}
			}
		} catch (SQLException e) {
			e.printStackTrace(); // Handle or log the exception appropriately
		}

	}
	
//	public void addLowStockNotification(String itemName, String itemCategory, String itemBrand, String itemSupplier, VBox vbox) {
//        try {
//            // Load notification HBox from FXML
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/notification/view/LowStockNotificationView.fxml"));
//            HBox notificationBox = loader.load();
//
//            // Access controller of the loaded FXML
//            LowStockNotificationController controller = loader.getController();
//
//            // Initialize notification details
//            controller.initData(itemName, itemCategory, itemBrand, itemSupplier);
//
//            // Add notificationBox to notificationContainer
//            vbox.getChildren().add(notificationBox);
//
//        } catch (Exception e) {
//            e.printStackTrace(); // Handle or log the exception appropriately
//        }
//    }
	
//	public void addLowStockNotification(String itemName, String itemCategory, String itemBrand, String itemSupplier, VBox vbox) {
//        try {
//            // Create HBox for low stock notification
//            HBox notificationBox = new HBox();
//            notificationBox.setId("low-stock-notification-hbox"); // Set CSS ID for styling
//
//            // ImageView for icon
//            ImageView iconImageView = new ImageView(new Image(getClass().getResourceAsStream("/images/notifications/LowStockWarning.png")));
//            iconImageView.setFitHeight(60.0);
//            iconImageView.setFitWidth(60.0);
//
//            // Label for "Low Stock:"
//            Label lowStockLabel = new Label("Low Stock:");
//            lowStockLabel.getStyleClass().add("warning-label"); // Apply CSS style class
//            lowStockLabel.setFont(new Font(40.0));
//
//            // Label for item details
//            Label itemDetailsLabel = new Label(itemName + "-" + itemCategory + "-" + itemBrand + "-" + itemSupplier);
//            itemDetailsLabel.getStyleClass().add("item-details"); // Apply CSS style class
//            itemDetailsLabel.setFont(new Font(40.0));
//
//            // Button for close
//            Button closeButton = new Button();
//            closeButton.getStyleClass().add("close-button-low-stock-notification"); // Apply CSS style class
//            ImageView closeIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/notifications/CloseButton.png")));
//            closeIcon.setFitHeight(60.0);
//            closeIcon.setFitWidth(60.0);
//            closeButton.setGraphic(closeIcon);
//            closeButton.setOnAction(event -> {
//                // Handle close button action if needed
//            	vbox.getChildren().remove(notificationBox);
//            });
//
//            // Add children to notificationBox
//            notificationBox.getChildren().addAll(iconImageView, lowStockLabel, itemDetailsLabel, closeButton);
//
//            // Add notificationBox to notificationContainer
//            vbox.getChildren().add(notificationBox);
//
//        } catch (Exception e) {
//            e.printStackTrace(); // Handle or log the exception appropriately
//        }
//    }
}
