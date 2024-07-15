package maintenance.edititemdetails.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import database.DatabaseConnection;
import inventorymanagement.model.RegisterItemDAO;

public class EditItemDetailsDAO {

	private LocalDateTime dateTimeForUserLogs;
	private String itemStatus = "available";
	private String eventName = "Edited Item";
	private String user = "Admin";

	private static String PREFIX = null;
	private static final int NUMERIC_LENGTH = 10; // Length of the numeric part

	// Method to fetch data from the database
	public List<EditItemDetailsModel> fetchDataFromDatabase() {
		DatabaseConnection dbConnectionInformation = new DatabaseConnection();
		List<EditItemDetailsModel> data = new ArrayList<>();

		try (Connection myConn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword())) {

			String query = "SELECT * FROM 828cafe.inventory ORDER BY item_datetime_of_registration DESC";
			try (PreparedStatement statement = myConn.prepareStatement(query);
					ResultSet resultSet = statement.executeQuery()) {

				while (resultSet.next()) {
					EditItemDetailsModel item = new EditItemDetailsModel();
					item.setItem_id(resultSet.getInt("item_id"));
					item.setUnique_item_id(resultSet.getString("unique_item_id"));
					item.setItem_name(resultSet.getString("item_name"));
					item.setCurrent_stock(resultSet.getInt("current_stock"));
					item.setItem_unit_of_measurement(resultSet.getString("item_unit_of_measurement"));
					item.setItem_minimum_threshold(resultSet.getInt("item_minimum_threshold"));
					item.setItem_maximum_threshold(resultSet.getInt("item_maximum_threshold"));
					item.setItem_brand(resultSet.getString("item_brand"));
					item.setItem_supplier(resultSet.getString("item_supplier"));
					item.setItem_category(resultSet.getString("item_category"));
					item.setItem_status(resultSet.getString("item_status"));

					java.sql.Timestamp timestamp = resultSet.getTimestamp("item_datetime_of_registration");
					if (timestamp != null) {
						// Convert java.sql.Timestamp to java.time.LocalDateTime
						LocalDateTime dateTime = timestamp.toLocalDateTime();
						item.setItem_datetime_of_registration(dateTime);
					} else {
						// Handle null case, depending on your application's logic
						item.setItem_datetime_of_registration(null);
					}

					data.add(item);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return data;
	}

	public List<EditItemDetailsModel> searchItemName(String itemName) {
		DatabaseConnection dbConnectionInformation = new DatabaseConnection();
		List<EditItemDetailsModel> data = new ArrayList<>();

		try (Connection myConn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword())) {

			String query = "SELECT * FROM 828cafe.inventory WHERE item_name LIKE ? ORDER BY item_datetime_of_registration DESC";
			try (PreparedStatement statement = myConn.prepareStatement(query)) {
				statement.setString(1, "%" + itemName + "%");
				try (ResultSet resultSet = statement.executeQuery()) {
					while (resultSet.next()) {
						EditItemDetailsModel item = new EditItemDetailsModel();
						item.setItem_id(resultSet.getInt("item_id"));
						item.setUnique_item_id(resultSet.getString("unique_item_id"));
						item.setItem_name(resultSet.getString("item_name"));
						item.setCurrent_stock(resultSet.getInt("current_stock"));
						item.setItem_unit_of_measurement(resultSet.getString("item_unit_of_measurement"));
						item.setItem_minimum_threshold(resultSet.getInt("item_minimum_threshold"));
						item.setItem_maximum_threshold(resultSet.getInt("item_maximum_threshold"));
						item.setItem_brand(resultSet.getString("item_brand"));
						item.setItem_supplier(resultSet.getString("item_supplier"));
						item.setItem_category(resultSet.getString("item_category"));
						item.setItem_status(resultSet.getString("item_status"));

						java.sql.Timestamp timestamp = resultSet.getTimestamp("item_datetime_of_registration");
						if (timestamp != null) {
							// Convert java.sql.Timestamp to java.time.LocalDateTime
							LocalDateTime dateTime = timestamp.toLocalDateTime();
							item.setItem_datetime_of_registration(dateTime);
						} else {
							// Handle null case, depending on your application's logic
							item.setItem_datetime_of_registration(null);
						}

						data.add(item);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return data;
	}

	public void updateItemDetails(String newItemName, String newItemUnitOfMeasurement, String newItemCategory,
			String newItemBrand, int newMinimumThreshold, int newMaximumThreshold, String newItemSupplier,
			String searchedItemName, String uniqueIDOfItemName) {
		DatabaseConnection dbConnectionInformation = new DatabaseConnection();
		
		LocalDateTime currentDateTime = LocalDateTime.now();

		String updateQueryForInventoryTable = "UPDATE 828cafe.inventory "
				+ "SET item_name = ?, item_unit_of_measurement = ?, "
				+ "item_minimum_threshold = ?, item_maximum_threshold = ?, "
				+ "item_category = ?, item_brand = ?, item_supplier = ? "
				+ "WHERE item_name = ? AND unique_item_id = ?";

		String updateQueryForStockReportTable = "UPDATE 828cafe.stock_report "
				+ "SET item_name = ?, item_unit_of_measurement = ?, "
				+ "item_minimum_threshold = ?, item_maximum_threshold = ?, "
				+ "item_category = ?, item_brand = ?, item_supplier = ? "
				+ "WHERE item_name = ? AND item_reference_id = ?";
		
		String updateQueryForRestockTable = "UPDATE 828cafe.restock	 "
		+ "SET item_name = ?, item_unit_of_measurement = ?, "
		+ "item_minimum_threshold = ?, item_maximum_threshold = ?, "
		+ "item_category = ?, item_brand = ?, item_supplier = ? "
		+ "WHERE item_name = ? AND item_reference_id = ?";

		String userTrailReportUniqueId = setGeneratedRandomCodeForUniqueID("edit");
		String userLogQuery = "INSERT INTO 828cafe.user_trail_report (unique_event_id, event_name, user, event_datetime) "
				+ "VALUES (?, ?, ?, ?)";

		try (Connection myConn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
				PreparedStatement updateInventoryStatement = myConn.prepareStatement(updateQueryForInventoryTable);
				PreparedStatement updateStockReportStatement = myConn.prepareStatement(updateQueryForStockReportTable);
				PreparedStatement updateRestockStatement = myConn.prepareStatement(updateQueryForRestockTable);
				PreparedStatement userTrailReportStatement = myConn.prepareStatement(userLogQuery)) {

			// Update inventory table
			updateInventoryStatement.setString(1, newItemName);
			updateInventoryStatement.setString(2, newItemUnitOfMeasurement);
			updateInventoryStatement.setInt(3, newMinimumThreshold);
			updateInventoryStatement.setInt(4, newMaximumThreshold);
			updateInventoryStatement.setString(5, newItemCategory);
			updateInventoryStatement.setString(6, newItemBrand);
			updateInventoryStatement.setString(7, newItemSupplier);
			updateInventoryStatement.setString(8, searchedItemName);
			updateInventoryStatement.setString(9, uniqueIDOfItemName);

			updateInventoryStatement.executeUpdate();

			// Update stock report table
			updateStockReportStatement.setString(1, newItemName);
			updateStockReportStatement.setString(2, newItemUnitOfMeasurement);
			updateStockReportStatement.setInt(3, newMinimumThreshold);
			updateStockReportStatement.setInt(4, newMaximumThreshold);
			updateStockReportStatement.setString(5, newItemCategory);
			updateStockReportStatement.setString(6, newItemBrand);
			updateStockReportStatement.setString(7, newItemSupplier);
			updateStockReportStatement.setString(8, searchedItemName);
			updateStockReportStatement.setString(9, uniqueIDOfItemName);

			updateStockReportStatement.executeUpdate();
			
			// Update stock report table
			updateRestockStatement.setString(1, newItemName);
			updateRestockStatement.setString(2, newItemUnitOfMeasurement);
			updateRestockStatement.setInt(3, newMinimumThreshold);
			updateRestockStatement.setInt(4, newMaximumThreshold);
			updateRestockStatement.setString(5, newItemCategory);
			updateRestockStatement.setString(6, newItemBrand);
			updateRestockStatement.setString(7, newItemSupplier);
			updateRestockStatement.setString(8, searchedItemName);
			updateRestockStatement.setString(9, uniqueIDOfItemName);

			updateRestockStatement.executeUpdate();

			// Insert user trail report
			userTrailReportStatement.setString(1, userTrailReportUniqueId);
			userTrailReportStatement.setString(2, eventName);
			userTrailReportStatement.setString(3, user);
			userTrailReportStatement.setTimestamp(4, Timestamp.valueOf(currentDateTime));

			userTrailReportStatement.executeUpdate();

			System.out.println("Details updated successfully.");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	String setGeneratedRandomCodeForUniqueID(String type) {

		String itemId = null;
			PREFIX = "LOG-";
		do {
			// Generate a random numeric part
			String numericPart = generateRandomNumericPart(NUMERIC_LENGTH);

			// Combine prefix with numeric part
			itemId = PREFIX + numericPart;
		} while (isStockIDExist(itemId, type));
		return itemId;

	}

	private boolean isStockIDExist(String ID, String type) {
		String sql = null;
		if (type == "edit") {
			sql = "SELECT COUNT(*) AS count FROM user_trail_report WHERE unique_event_id = ?";
		}
		DatabaseConnection dbConnectionInformation = new DatabaseConnection();

		try (Connection conn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, ID); // Set the integer reference code as parameter
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				int count = rs.getInt("count");
				return count > 0;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false; // Default to false if any exception occurs or if no records found
	}

	private static String generateRandomNumericPart(int length) {
		Random random = new Random();
		StringBuilder numericPart = new StringBuilder();

		for (int i = 0; i < length; i++) {
			numericPart.append(random.nextInt(10)); // Append random digits (0-9)
		}

		return numericPart.toString();
	}

	// Generate a random code for stock report id
	public String setGeneratedRandomCodeForStockReportId(int length) {
		RegisterItemDAO daoRegisterItem = new RegisterItemDAO();

		String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuilder code = new StringBuilder(length);

		boolean uniqueIdFound = false;

		while (!uniqueIdFound) {
			code.setLength(0);
			for (int i = 0; i < length; i++) {
				code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
			}

			try {
				if (!daoRegisterItem.isUniqueIdExists(code.toString())) {
					uniqueIdFound = true;
					System.out.println("Generated ID is unique.");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return code.toString();
	}

	// Method for checking if an item with the same name, brand, supplier, and
	// category exists
	public boolean isItemExists(String itemName, String itemBrand, String itemSupplier, String itemCategory)
			throws SQLException {
		DatabaseConnection dbConnectionInformation = new DatabaseConnection();

		try (Connection myConn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword())) {

			String query = "SELECT COUNT(*) FROM 828cafe.inventory WHERE item_name = ? AND item_brand = ? AND item_supplier = ? AND item_category = ?";
			try (PreparedStatement statement = myConn.prepareStatement(query)) {
				statement.setString(1, itemName);
				statement.setString(2, itemBrand);
				statement.setString(3, itemSupplier);
				statement.setString(4, itemCategory);
				try (ResultSet resultSet = statement.executeQuery()) {
					if (resultSet.next()) {
						int count = resultSet.getInt(1);
						return count > 0;
					}
				}
			}
		}

		return false;
	}

//	// Check if a unique id exists in the database
//	public boolean isUniqueIdExists(String uniqueItemId) throws SQLException {
//		DatabaseConnection dbConnectionInformation = new DatabaseConnection();
//
//		try (Connection myConn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
//				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword())) {
//
//			String query = "SELECT COUNT(*) FROM 828cafe.inventory WHERE unique_item_id = ?";
//			try (PreparedStatement statement = myConn.prepareStatement(query)) {
//				statement.setString(1, uniqueItemId);
//				try (ResultSet resultSet = statement.executeQuery()) {
//					if (resultSet.next()) {
//						int count = resultSet.getInt(1);
//						return count > 0;
//					}
//				}
//			}
//		}
//
//		return false;
//	}
}
