package maintenance.restock.model;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import database.DatabaseConnection;
import inventorymanagement.model.RegisterItemDAO;

public class RestockDAO {

	// private final int generatedCodeLength = 15;
	private LocalDateTime dateTimeForUserLogs;
	private String itemStatus = "available";
	private String eventName = "Restocked Item";
	private String user = "Admin";

	private static String PREFIX = null;
	private static final int NUMERIC_LENGTH = 10; // Length of the numeric part

	// Method to fetch data from the database
	public List<RestockModel> fetchDataFromDatabase() {

		DatabaseConnection dbConnectionInformation = new DatabaseConnection();
		List<RestockModel> data = new ArrayList<>();

		String query = "SELECT * FROM 828cafe.inventory ORDER BY item_datetime_of_registration DESC";

		try (Connection myConn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
				PreparedStatement statement = myConn.prepareStatement(query);
				ResultSet resultSet = statement.executeQuery()) {

			while (resultSet.next()) {
				RestockModel item = new RestockModel();
//				item.setUnique_stock_report_id(resultSet.getString("unique_stock_report_id"));
				item.setUnique_item_id(resultSet.getString("unique_item_id"));
				item.setItem_name(resultSet.getString("item_name"));
				item.setItem_brand(resultSet.getString("item_brand"));
				item.setItem_supplier(resultSet.getString("item_supplier"));
				item.setItem_category(resultSet.getString("item_category"));
				item.setItem_unit_of_measurement(resultSet.getString("item_unit_of_measurement"));
				item.setItem_minimum_threshold(resultSet.getInt("item_minimum_threshold"));
				item.setItem_maximum_threshold(resultSet.getInt("item_maximum_threshold"));
//				item.setNew_item_batch_quantity(resultSet.getDouble("new_item_batch_quantity"));
//				item.setNew_item_batch_cost(resultSet.getBigDecimal("new_item_batch_cost"));
//				item.setPrevious_total_stock(resultSet.getDouble("previous_total_stock"));
//				item.setTotal_stock_after_restock(resultSet.getDouble("total_stock_after_restock"));
				item.setCurrent_stock(resultSet.getDouble("current_stock"));
				item.setItem_status(resultSet.getString("item_status"));

				Timestamp timestampOfRegistration = resultSet.getTimestamp("item_datetime_of_registration");
				if (timestampOfRegistration != null) {
					LocalDateTime dateTimeOfRegistration = timestampOfRegistration.toLocalDateTime();
					item.setDatetime_of_registration(dateTimeOfRegistration);
				} else {
					item.setDatetime_of_registration(null);
				}

				data.add(item);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			// Handle exceptions appropriately
		}

		return data;
	}

	public RestockModel getSelectedItem(String searchText, String brand, String supplier, String category) {
		RestockModel selectedItem = null;

		// Fetch all data from the database
		List<RestockModel> allItems = fetchDataFromDatabase();

		// Find the item that matches the criteria
		for (RestockModel item : allItems) {
			if (item.getItem_name().equalsIgnoreCase(searchText) && item.getItem_brand().equalsIgnoreCase(brand)
					&& item.getItem_supplier().equalsIgnoreCase(supplier)
					&& item.getItem_category().equalsIgnoreCase(category)) {

				// Found matching item, set it as selectedItem
				selectedItem = item;
				break;
			}
		}

		return selectedItem;
	}

	public List<RestockModel> searchItemName(String itemName) {

		DatabaseConnection dbConnectionInformation = new DatabaseConnection();
		List<RestockModel> data = new ArrayList<>();

		String query = "SELECT * FROM 828cafe.inventory WHERE item_name = ?";

		try (Connection myConn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
				PreparedStatement statement = myConn.prepareStatement(query)) {

			statement.setString(1, itemName);

			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					RestockModel item = new RestockModel();
//					item.setStock_id(resultSet.getInt("stock_id"));
//					item.setUnique_stock_report_id(resultSet.getString("unique_stock_report_id"));
					item.setUnique_item_id(resultSet.getString("unique_item_id"));
					item.setItem_name(resultSet.getString("item_name"));
					item.setItem_brand(resultSet.getString("item_brand"));
					item.setItem_supplier(resultSet.getString("item_supplier"));
					item.setItem_category(resultSet.getString("item_category"));
					item.setItem_unit_of_measurement(resultSet.getString("item_unit_of_measurement"));
					item.setItem_minimum_threshold(resultSet.getInt("item_minimum_threshold"));
					item.setItem_maximum_threshold(resultSet.getInt("item_maximum_threshold"));
//					item.setNew_item_batch_quantity(resultSet.getDouble("new_item_batch_quantity"));
//					item.setNew_item_batch_cost(resultSet.getBigDecimal("new_item_batch_cost"));
//					item.setPrevious_total_stock(resultSet.getDouble("previous_total_stock"));
//					item.setTotal_stock_after_restock(resultSet.getDouble("total_stock_after_restock"));
					item.setCurrent_stock(resultSet.getDouble("current_stock"));
					item.setItem_status(resultSet.getString("item_status"));

					Timestamp timestampOfRegistration = resultSet.getTimestamp("item_datetime_of_registration");
					if (timestampOfRegistration != null) {
						LocalDateTime dateTimeOfRegistration = timestampOfRegistration.toLocalDateTime();
						item.setDatetime_of_registration(dateTimeOfRegistration);
					} else {
						item.setDatetime_of_registration(null);
					}

					data.add(item);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return data;
	}

	// Method to fetch brands from the database
	public List<String> fetchBrands() {
		DatabaseConnection dbConnectionInformation = new DatabaseConnection();

		List<String> brands = new ArrayList<>();
		String query = "SELECT DISTINCT item_brand FROM 828cafe.inventory"; // Adjust query as per your database schema

		try (Connection myConn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
				PreparedStatement stmt = myConn.prepareStatement(query);
				ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String brand = rs.getString("item_brand");
				brands.add(brand);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return brands;
	}

	// Method to fetch suppliers from the database
	public List<String> fetchSuppliers() {
		DatabaseConnection dbConnectionInformation = new DatabaseConnection();
		List<String> suppliers = new ArrayList<>();
		String query = "SELECT DISTINCT item_supplier FROM 828cafe.inventory"; // Adjust query as per your database
																				// schema

		try (Connection myConn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
				PreparedStatement stmt = myConn.prepareStatement(query);
				ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String supplier = rs.getString("item_supplier");
				suppliers.add(supplier);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return suppliers;
	}

	// Method to fetch item categories from the database
	public List<String> fetchCategories() {
		DatabaseConnection dbConnectionInformation = new DatabaseConnection();
		List<String> categories = new ArrayList<>();
		String query = "SELECT DISTINCT item_category FROM 828cafe.inventory"; // Adjust query as per your database
																				// schema

		try (Connection myConn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
				PreparedStatement stmt = myConn.prepareStatement(query);
				ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String category = rs.getString("item_category");
				categories.add(category);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return categories;
	}

	public void updateStock(String itemName, String itemBrand, String itemSupplier, String itemCategory,
			double inputtedQuantityInput, Double costOfNewBatch, LocalDate expirationDateOfNewBatch,
			LocalDate dateOfPurchaseOfNewBatch) {
		DatabaseConnection dbConnectionInformation = new DatabaseConnection();

		String combinedQuery = "SELECT unique_item_id, current_stock, item_unit_of_measurement, item_minimum_threshold, item_maximum_threshold "
				+ "FROM 828cafe.inventory "
				+ "WHERE item_name = ? AND item_brand = ? AND item_supplier = ? AND item_category = ?";

		int currentStockFromDatabase = 0;
		String itemUnitOfMeasurement = "";
		int minimumThreshold = 0;
		int maximumThreshold = 0;
		String uniqueItemId = "";

		try (Connection myConn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
				PreparedStatement getQuantityStatement = myConn.prepareStatement(combinedQuery)) {

			getQuantityStatement.setString(1, itemName);
			getQuantityStatement.setString(2, itemBrand);
			getQuantityStatement.setString(3, itemSupplier);
			getQuantityStatement.setString(4, itemCategory);

// This gets the value from the current stock in the inventory table in the database.
			try (ResultSet resultSet = getQuantityStatement.executeQuery()) {
				if (resultSet.next()) {
					uniqueItemId = resultSet.getString("unique_item_id");
					currentStockFromDatabase = resultSet.getInt("current_stock");
					itemUnitOfMeasurement = resultSet.getString("item_unit_of_measurement");
					minimumThreshold = resultSet.getInt("item_minimum_threshold");
					maximumThreshold = resultSet.getInt("item_maximum_threshold");
				} else {
					System.out.println("Item not found in the database.");
					return;
				}
			}

// Formula for adding quantity
// This updates the inventory table in the database.
			double totalStockAfterRestock = currentStockFromDatabase + inputtedQuantityInput;
			String updateItemStatus = "available";
			String updateInventoryTableQuery = "UPDATE 828cafe.inventory SET current_stock = ?, item_status = ?"
					+ " WHERE item_name = ? AND item_brand = ? AND item_supplier = ? AND item_category = ?";
			try (PreparedStatement updateQuantityStatement = myConn.prepareStatement(updateInventoryTableQuery)) {
				updateQuantityStatement.setDouble(1, totalStockAfterRestock);
				updateQuantityStatement.setString(2, updateItemStatus);
				updateQuantityStatement.setString(3, itemName);
				updateQuantityStatement.setString(4, itemBrand);
				updateQuantityStatement.setString(5, itemSupplier);
				updateQuantityStatement.setString(6, itemCategory);

				updateQuantityStatement.executeUpdate();

				System.out.println("Quantity updated successfully in Inventory Table.");
			}

			String restockUniqueId = setGeneratedRandomCodeForUniqueID("restock");
			String insertRestockTableQuery = "INSERT INTO 828cafe.restock ("
					+ "unique_restock_id, item_reference_id, item_name, item_brand, item_supplier, item_category, "
					+ "item_unit_of_measurement, item_minimum_threshold, item_maximum_threshold, new_item_batch_quantity, "
					+ "new_item_batch_cost, previous_stock, current_stock, new_item_batch_expirationdate, date_of_purchase, "
					+ "datetime_of_restocking) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

			try (PreparedStatement insertRestockStatement = myConn.prepareStatement(insertRestockTableQuery)) {
				insertRestockStatement.setString(1, restockUniqueId);
				insertRestockStatement.setString(2, uniqueItemId);
				insertRestockStatement.setString(3, itemName);
				insertRestockStatement.setString(4, itemBrand);
				insertRestockStatement.setString(5, itemSupplier);
				insertRestockStatement.setString(6, itemCategory);
				insertRestockStatement.setString(7, itemUnitOfMeasurement);
				insertRestockStatement.setInt(8, minimumThreshold);
				insertRestockStatement.setInt(9, maximumThreshold);
				insertRestockStatement.setDouble(10, inputtedQuantityInput);
				insertRestockStatement.setDouble(11, costOfNewBatch);
				insertRestockStatement.setDouble(12, currentStockFromDatabase);
				insertRestockStatement.setDouble(13, totalStockAfterRestock);

// Set LocalDate for new_item_batch_expiration_date
				if (expirationDateOfNewBatch != null) {
					insertRestockStatement.setDate(14, java.sql.Date.valueOf(expirationDateOfNewBatch));
				} else {
					insertRestockStatement.setNull(14, java.sql.Types.DATE);
				}

// Set LocalDate for date_of_purchase
				if (dateOfPurchaseOfNewBatch != null) {
					insertRestockStatement.setDate(15, java.sql.Date.valueOf(dateOfPurchaseOfNewBatch));
				} else {
					insertRestockStatement.setNull(15, java.sql.Types.DATE);
				}

				LocalDateTime datetimeOfRestocking = LocalDateTime.now();
				insertRestockStatement.setTimestamp(16, Timestamp.valueOf(datetimeOfRestocking));

				insertRestockStatement.executeUpdate();

				System.out.println("New batch inserted successfully into restock table.");
			} catch (SQLException e) {
				e.printStackTrace();
// Handle exception as needed
			}

			String stockUniqueId = setGeneratedRandomCodeForUniqueID("StockReport");
			String insertStockReportTableQuery = "INSERT INTO 828cafe.stock_report ("
					+ "unique_stock_report_id, item_reference_id, item_name, item_brand, item_supplier, item_category, "
					+ "item_unit_of_measurement, item_minimum_threshold, item_maximum_threshold, previous_total_stock, "
					+ "change_in_stock, updated_stock, item_status, datetime_of_change) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

			try (PreparedStatement insertStockReportStatement = myConn.prepareStatement(insertStockReportTableQuery)) {
				insertStockReportStatement.setString(1, stockUniqueId);
				insertStockReportStatement.setString(2, uniqueItemId);
				insertStockReportStatement.setString(3, itemName);
				insertStockReportStatement.setString(4, itemBrand);
				insertStockReportStatement.setString(5, itemSupplier);
				insertStockReportStatement.setString(6, itemCategory);
				insertStockReportStatement.setString(7, itemUnitOfMeasurement);
				insertStockReportStatement.setInt(8, minimumThreshold);
				insertStockReportStatement.setInt(9, maximumThreshold);
				insertStockReportStatement.setDouble(10, currentStockFromDatabase);
				insertStockReportStatement.setDouble(11, inputtedQuantityInput);
				insertStockReportStatement.setDouble(12, totalStockAfterRestock);
				insertStockReportStatement.setString(13, updateItemStatus);

				LocalDateTime datetimeOfChange = LocalDateTime.now();
				insertStockReportStatement.setTimestamp(14, Timestamp.valueOf(datetimeOfChange));

				insertStockReportStatement.executeUpdate();

				System.out.println("Stock inserted successfully into Stock Report table.");
			} catch (SQLException e) {
				e.printStackTrace();
// Handle exception as needed
			}

			String userTrailReportUniqueId = setGeneratedRandomCodeForUniqueID("log");
			String userLogQuery = "INSERT INTO 828cafe.user_trail_report (unique_event_id, event_name, user, event_datetime) "
					+ "VALUES (?, ?, ?, ?)";

			try (PreparedStatement userTrailReportStatement = myConn.prepareStatement(userLogQuery)) {
				userTrailReportStatement.setString(1, userTrailReportUniqueId);
				userTrailReportStatement.setString(2, eventName);
				userTrailReportStatement.setString(3, user);
				userTrailReportStatement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

				userTrailReportStatement.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
// Handle exception as needed
			}

		} catch (SQLException e) {
			e.printStackTrace();
// Handle exception as needed
		}
	}

	String setGeneratedRandomCodeForUniqueID(String type) {

		String itemId = null;

		if (type == "StockReport") {
			PREFIX = "STR-";
		} else if (type == "log") {
			PREFIX = "LOG-";
		} else if (type == "restock") {
			PREFIX = "RST-";
		}
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
		if (type == "restock") {
			sql = "SELECT COUNT(*) AS count FROM restock WHERE unique_restock_id = ?";
		} else if (type == "log") {
			sql = "SELECT COUNT(*) AS count FROM user_trail_report WHERE unique_event_id = ?";
		} else if (type == "StockReport") {
			sql = "SELECT COUNT(*) AS count FROM stock_report WHERE unique_stock_report_id = ?";
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

	// Method for checking if an item with the same name, brand, supplier, and
	// category exists
	public boolean isItemExists(String itemName, String itemBrand, String itemSupplier, String itemCategory)
			throws SQLException {
		DatabaseConnection dbConnectionInformation = new DatabaseConnection();

		try (Connection myConn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());) {
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

		return false; // If there is no duplicated.
	}

	// For checking if a unique id exists already in the database.
	public boolean isUniqueIdExists(String uniqueItemId) throws SQLException {

		DatabaseConnection dbConnectionInformation = new DatabaseConnection();

		Connection myConn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());

		String query = "SELECT COUNT(*) FROM 828cafe.inventory WHERE unique_item_id = ?";

		try (PreparedStatement statement = myConn.prepareStatement(query)) {
			statement.setString(1, uniqueItemId);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					int count = resultSet.getInt(1);
					return count > 0;
				}
			}
		}

		return false; // If the produced generated unique id is not in the database, it will return
						// false.

	}

}
