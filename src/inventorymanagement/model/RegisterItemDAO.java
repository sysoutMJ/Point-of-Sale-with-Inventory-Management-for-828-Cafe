package inventorymanagement.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.math.BigDecimal;

import database.DatabaseConnection;

public class RegisterItemDAO {

	final int generatedCodeLength = 15;
	private static String PREFIX = null;
    private static final int NUMERIC_LENGTH = 10; // Length of the numeric part

	// Method to fetch data from the database
	public List<RegisterItemInventoryModel> fetchDataFromDatabase() {

		DatabaseConnection dbConnectionInformation = new DatabaseConnection();
		List<RegisterItemInventoryModel> data = new ArrayList<>();

		try {
			Connection myConn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
					dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());

			String query = "SELECT * FROM 828cafe.inventory ORDER BY item_datetime_of_registration DESC";

			PreparedStatement statement = myConn.prepareStatement(query);
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {

				RegisterItemInventoryModel item = new RegisterItemInventoryModel();
				item.setItem_id(resultSet.getInt("item_id"));
				item.setUnique_item_id(resultSet.getString("unique_item_id"));
				item.setItem_name(resultSet.getString("item_name"));
				item.setCurrent_stock(resultSet.getDouble("current_stock"));
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

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return data;
	}

	public void insertItem(RegisterItemInventoryModel registerItemAttributes) throws SQLException {

		// Debug
		// If item does not exist, insert.
		if (isItemExists(registerItemAttributes.getItem_name(), registerItemAttributes.getItem_brand(),
				registerItemAttributes.getItem_supplier(), registerItemAttributes.getItem_category())) {
			System.out.println("Item already exists. Not inserting.");
			return;
		}

		DatabaseConnection dbConnectionInformation = new DatabaseConnection();

		Connection myConn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());

		// Utilized PreparedStatement to avoid SQL injection attacks
		// Insert into inventory table.
		String inventoryQuery = "INSERT INTO 828cafe.inventory (unique_item_id, item_name, current_stock, item_unit_of_measurement, "
				+ "item_minimum_threshold, item_maximum_threshold, item_category, item_brand, item_supplier, item_status, item_datetime_of_registration) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement inventoryStatement = myConn.prepareStatement(inventoryQuery)) {
			inventoryStatement.setString(1, registerItemAttributes.getUnique_item_id());
			inventoryStatement.setString(2, registerItemAttributes.getItem_name());
			inventoryStatement.setDouble(3, registerItemAttributes.getCurrent_stock());
			inventoryStatement.setString(4, registerItemAttributes.getItem_unit_of_measurement());
			inventoryStatement.setInt(5, registerItemAttributes.getItem_minimum_threshold());
			inventoryStatement.setInt(6, registerItemAttributes.getItem_maximum_threshold());
			inventoryStatement.setString(7, registerItemAttributes.getItem_category());
			inventoryStatement.setString(8, registerItemAttributes.getItem_brand());
			inventoryStatement.setString(9, registerItemAttributes.getItem_supplier());
			inventoryStatement.setString(10, registerItemAttributes.getItem_status());

			if (registerItemAttributes.getItem_datetime_of_registration() != null) {
				inventoryStatement.setTimestamp(11,
						Timestamp.valueOf(registerItemAttributes.getItem_datetime_of_registration()));
			} else {
				inventoryStatement.setNull(4, java.sql.Types.TIMESTAMP);
			}

			inventoryStatement.executeUpdate();
		}
		
//		String stockUniqueId = setGeneratedRandomCodeForStockReportId(generatedCodeLength);
//
//		// Insert into stock_report table
//		String stockReportQuery = "INSERT INTO 828cafe.stock_report (unique_stock_report_id, item_name, item_brand, item_supplier, item_category, "
//		        + "item_unit_of_measurement, item_minimum_threshold, item_maximum_threshold, new_item_batch_quantity, new_item_batch_cost, "
//		        + "previous_total_stock, total_stock_after_restock, current_stock, item_status, new_item_batch_expiration_date, date_of_purchase, datetime_of_restocking) "
//		        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//		try (PreparedStatement stockReportStatement = myConn.prepareStatement(stockReportQuery)) {
//
//		    // Generates a unique ID to set for unique_stock_report_id of stock_report in the database.
//		    stockReportStatement.setString(1, stockUniqueId);
//
//		    stockReportStatement.setString(2, registerItemAttributes.getItem_name());
//		    stockReportStatement.setString(3, registerItemAttributes.getItem_brand());
//		    stockReportStatement.setString(4, registerItemAttributes.getItem_supplier());
//		    stockReportStatement.setString(5, registerItemAttributes.getItem_category());
//		    stockReportStatement.setString(6, registerItemAttributes.getItem_unit_of_measurement());
//		    stockReportStatement.setInt(7, registerItemAttributes.getItem_minimum_threshold());
//		    stockReportStatement.setInt(8, registerItemAttributes.getItem_maximum_threshold());
//
//		    // Set to zero as this can be updated once the user has re-stocked an item.
//		    stockReportStatement.setDouble(9, 0); // new_item_batch_quantity
//		    stockReportStatement.setBigDecimal(10, BigDecimal.valueOf(0.0));// new_item_batch_cost
//		    stockReportStatement.setDouble(11, 0); // previous_total_stock
//		    stockReportStatement.setDouble(12, 0); // total_stock_after_restock
//
//		    stockReportStatement.setDouble(13, registerItemAttributes.getCurrent_stock());
//		    stockReportStatement.setString(14, registerItemAttributes.getItem_status());
//
//		    // Set to null and can be updated once the user has re-stocked an item.
//		    stockReportStatement.setObject(15, null); // Assuming LocalDate
//		    stockReportStatement.setObject(16, null); // Assuming LocalDate
//		    stockReportStatement.setObject(17, null); // Assuming LocalDateTime
//
//		    stockReportStatement.executeUpdate();
//		}

		String userTrailReportUniqueId = setGeneratedRandomCodeForUniqueID("reg");
		String eventName = "Registered Item";
		String user = "Admin";
		String userLogQuery = "INSERT INTO 828cafe.user_trail_report (unique_event_id, event_name, user, event_datetime)"
				+ "VALUES (?, ?, ?, ?)";

		try (PreparedStatement userTrailReportStatement = myConn.prepareStatement(userLogQuery)) {
			userTrailReportStatement.setString(1, userTrailReportUniqueId);
			userTrailReportStatement.setString(2, eventName);
			userTrailReportStatement.setString(3, user);
			userTrailReportStatement.setTimestamp(4, Timestamp.valueOf(registerItemAttributes.getItem_datetime_of_registration()));
			userTrailReportStatement.executeUpdate();

		}

	}

	// Generate a random string of code for unique_stock_report_id in stock_report
	// in the database.
String setGeneratedRandomCodeForUniqueID(String type) {
		
		String itemId = null;
		
		if(type == "logout") {
			PREFIX = "LOG-";
		}else if(type == "reg") {
			PREFIX = "LOG-";
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
	if(type == "edit") {
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

	public void logUserTrail(String username, String eventName) {
		DatabaseConnection dbConnectionInformation = new DatabaseConnection();
		String insertQuery = "INSERT INTO user_trail_report (unique_event_id, event_name, user, event_datetime) "
				+ "VALUES (?, ?, ?, ?)";

		try (Connection conn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
				PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

			String uniqueEventId = setGeneratedRandomCodeForUniqueID("logout");
			LocalDateTime currentDateTime = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String formattedDateTime = currentDateTime.format(formatter);

			stmt.setString(1, uniqueEventId);
			stmt.setString(2, eventName);
			stmt.setString(3, username);
			stmt.setString(4, formattedDateTime);

			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Generate a random string of code for unique_stock_report_id in stock_report
	// in the database.
	public String setGeneratedRandomCodeForEventId(int length) {
		RegisterItemDAO daoRegisterItem = new RegisterItemDAO();

		String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuilder code = new StringBuilder(length);

		boolean uniqueIdFound = false;

		while (!uniqueIdFound) { // While false,
			// Generate random code
			code.setLength(0); // Clear previous contents
			for (int i = 0; i < length; i++) {
				code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
			}

			try {

				// -----------------------------------------------------------------------
				// Checks if there is already a unique code in the database.

				if (!daoRegisterItem.isUniqueIdExists(code.toString())) {

					// If isUniqueIdExists returned false, then it is indeed unique.
					uniqueIdFound = true;
					System.out.println("Generated ID is unique.");
				}

				// -----------------------------------------------------------------------
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return code.toString();
	}

	// Method for checking if an item with the same name, brand, supplier, and
	// category exists in the inventory table in the database.
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

		return false; // If there is no duplicate.
	}

	// This method checks it the generated random string of code already exists in
	// the database. If it does not exists, it will return false.
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

		return false;

	}


}
