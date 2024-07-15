package search.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import database.DatabaseConnection;

public class SearchInventoryDAO {

	final int generatedCodeLength = 15;

	// Method to fetch data from the database
	public List<SearchInventoryModel> fetchDataFromDatabase() {
		DatabaseConnection dbConnectionInformation = new DatabaseConnection();
		List<SearchInventoryModel> data = new ArrayList<>();

		try (Connection myConn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword())) {

			String query = "SELECT * FROM 828cafe.inventory ORDER BY item_datetime_of_registration DESC";
			try (PreparedStatement statement = myConn.prepareStatement(query);
					ResultSet resultSet = statement.executeQuery()) {

				while (resultSet.next()) {
					SearchInventoryModel item = mapResultSetToInventoryModel(resultSet);
					data.add(item);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return data;
	}

	public List<SearchInventoryModel> searchItemName(String itemName) {
		DatabaseConnection dbConnectionInformation = new DatabaseConnection();
		List<SearchInventoryModel> data = new ArrayList<>();

		try (Connection myConn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword())) {

			String query = "SELECT * FROM 828cafe.inventory WHERE item_name = ?";
			try (PreparedStatement statement = myConn.prepareStatement(query)) {
				statement.setString(1, itemName);
				try (ResultSet resultSet = statement.executeQuery()) {
					while (resultSet.next()) {
						SearchInventoryModel item = mapResultSetToInventoryModel(resultSet);
						data.add(item);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Check if data list is empty
		if (data.isEmpty()) {
			return Collections.emptyList(); // Return empty list if no items found
		}

		return data;
	}

	public List<SearchInventoryModel> searchItemByBrand(String brand) {
		DatabaseConnection dbConnectionInformation = new DatabaseConnection();
		List<SearchInventoryModel> data = new ArrayList<>();

		try (Connection myConn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword())) {

			String query = "SELECT * FROM 828cafe.inventory WHERE item_brand = ?";
			try (PreparedStatement statement = myConn.prepareStatement(query)) {
				statement.setString(1, brand);
				try (ResultSet resultSet = statement.executeQuery()) {
					while (resultSet.next()) {
						SearchInventoryModel item = mapResultSetToInventoryModel(resultSet);
						data.add(item);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Check if data list is empty
		if (data.isEmpty()) {
			return Collections.emptyList(); // Return empty list if no items found
		}

		return data;
	}

	public List<SearchInventoryModel> searchItemBySupplier(String supplier) {
		DatabaseConnection dbConnectionInformation = new DatabaseConnection();
		List<SearchInventoryModel> data = new ArrayList<>();

		try (Connection myConn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword())) {

			String query = "SELECT * FROM 828cafe.inventory WHERE item_supplier = ?";
			try (PreparedStatement statement = myConn.prepareStatement(query)) {
				statement.setString(1, supplier);
				try (ResultSet resultSet = statement.executeQuery()) {
					while (resultSet.next()) {
						SearchInventoryModel item = mapResultSetToInventoryModel(resultSet);
						data.add(item);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Check if data list is empty
		if (data.isEmpty()) {
			return Collections.emptyList(); // Return empty list if no items found
		}

		return data;
	}

	public List<SearchInventoryModel> searchItemByRegistrationDate(LocalDate registrationDate) {
	    DatabaseConnection dbConnectionInformation = new DatabaseConnection();
	    List<SearchInventoryModel> data = new ArrayList<>();

	    try (Connection myConn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
	            dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword())) {

	        String query = "SELECT * FROM 828cafe.inventory WHERE item_datetime_of_registration LIKE ?";
	        try (PreparedStatement statement = myConn.prepareStatement(query)) {
	        	
	        	System.out.print(registrationDate);
	            // Convert LocalDate to LocalDateTime at the start of the day
	        	System.out.print("%" + registrationDate + "%");
	            // Convert LocalDateTime to Timestamp
	            statement.setString(1, "%" + registrationDate + "%");
	            try (ResultSet resultSet = statement.executeQuery()) {
	                while (resultSet.next()) {
	                    SearchInventoryModel item = mapResultSetToInventoryModel(resultSet);
	                    data.add(item);
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    // Return the list of items found, or an empty list if no items found
	    return data.isEmpty() ? Collections.emptyList() : data;
	}


	public List<SearchInventoryModel> searchItemByCategory(String category) {
		DatabaseConnection dbConnectionInformation = new DatabaseConnection();
		List<SearchInventoryModel> data = new ArrayList<>();

		try (Connection myConn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword())) {

			String query = "SELECT * FROM 828cafe.inventory WHERE item_category = ?";
			try (PreparedStatement statement = myConn.prepareStatement(query)) {
				statement.setString(1, category);
				try (ResultSet resultSet = statement.executeQuery()) {
					while (resultSet.next()) {
						SearchInventoryModel item = mapResultSetToInventoryModel(resultSet);
						data.add(item);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Check if data list is empty
		if (data.isEmpty()) {
			return Collections.emptyList(); // Return empty list if no items found
		}

		return data;
	}

	private SearchInventoryModel mapResultSetToInventoryModel(ResultSet resultSet) throws SQLException {
		SearchInventoryModel item = new SearchInventoryModel();
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
		return item;
	}

	public void insertToUserLog(SearchInventoryModel searchInventoryModelAttributes) throws SQLException {
		DatabaseConnection dbConnectionInformation = new DatabaseConnection();

		try (Connection myConn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword())) {

			String userTrailReportUniqueId = generateRandomCode(generatedCodeLength);
			String eventName = "Searched Item";
			String user = "Admin";
			String userLogQuery = "INSERT INTO 828cafe.user_trail_report (unique_event_id, event_name, user, event_datetime)"
					+ "VALUES (?, ?, ?, ?)";

			try (PreparedStatement userTrailReportStatement = myConn.prepareStatement(userLogQuery)) {
				userTrailReportStatement.setString(1, userTrailReportUniqueId);
				userTrailReportStatement.setString(2, eventName);
				userTrailReportStatement.setString(3, user);

				if (searchInventoryModelAttributes.getItem_datetime_of_registration() != null) {
					userTrailReportStatement.setTimestamp(4,
							Timestamp.valueOf(searchInventoryModelAttributes.getItem_datetime_of_registration()));
				} else {
					userTrailReportStatement.setNull(4, java.sql.Types.TIMESTAMP);
				}

				userTrailReportStatement.executeUpdate();
			}

		}
	}

	public String generateRandomCode(int length) {
		String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuilder code = new StringBuilder(length);

		boolean uniqueIdFound = false;

		while (!uniqueIdFound) {
			code.setLength(0); // Clear previous contents
			for (int i = 0; i < length; i++) {
				code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
			}

			try {
				SearchInventoryDAO dao = new SearchInventoryDAO();
				if (!dao.isUniqueIdExists(code.toString())) {
					uniqueIdFound = true;
					System.out.println("Generated ID is unique.");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return code.toString();
	}

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

	public boolean isUniqueIdExists(String uniqueItemId) throws SQLException {
		DatabaseConnection dbConnectionInformation = new DatabaseConnection();

		try (Connection myConn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword())) {

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
		}

		return false;
	}
}
