package maintenance.setitemstatus.model;

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

public class SetItemStatusDAO {

	private final int generatedCodeLength = 15;
	private LocalDateTime dateTimeForUserLogs;
	private String itemStatus = "available";
	private String eventName = "Updated Item Status";
	private String user = "Admin";

	// Method to fetch data from the database
	public List<SetItemStatusModel> fetchDataFromDatabase() {
		DatabaseConnection dbConnectionInformation = new DatabaseConnection();
		List<SetItemStatusModel> data = new ArrayList<>();

		try (Connection myConn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword())) {

			String query = "SELECT * FROM 828cafe.inventory ORDER BY item_datetime_of_registration DESC";
			try (PreparedStatement statement = myConn.prepareStatement(query);
					ResultSet resultSet = statement.executeQuery()) {

				while (resultSet.next()) {
					SetItemStatusModel item = new SetItemStatusModel();
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

	public List<SetItemStatusModel> searchItemName(String itemName) {
		DatabaseConnection dbConnectionInformation = new DatabaseConnection();
		List<SetItemStatusModel> data = new ArrayList<>();

		try (Connection myConn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword())) {

			String query = "SELECT * FROM 828cafe.inventory WHERE item_name LIKE ? ORDER BY item_datetime_of_registration DESC";
			try (PreparedStatement statement = myConn.prepareStatement(query)) {
				statement.setString(1, "%" + itemName + "%");
				try (ResultSet resultSet = statement.executeQuery()) {
					while (resultSet.next()) {
						SetItemStatusModel item = new SetItemStatusModel();
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
	
	public String getItemStatus(String itemName) {
	    DatabaseConnection dbConnectionInformation = new DatabaseConnection();
	    String status = null;

	    try (Connection myConn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
	            dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword())) {

	        String query = "SELECT item_status FROM 828cafe.inventory WHERE item_name = ?";
	        try (PreparedStatement statement = myConn.prepareStatement(query)) {
	            statement.setString(1, itemName);
	            try (ResultSet resultSet = statement.executeQuery()) {
	                if (resultSet.next()) {
	                    status = resultSet.getString("item_status");
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return status;
	}

	// -----------------------------------------------------------------------------------------------------------------------------

	public boolean updateItemStatus(String itemName, String itemBrand, String itemSupplier, String itemCategory, String newStatus) {
	    DatabaseConnection dbConnectionInformation = new DatabaseConnection();
	    String query = "UPDATE 828cafe.inventory SET item_status = ? WHERE item_name = ? AND item_brand = ? AND item_supplier = ? AND item_category = ?";

	    try (Connection myConn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
	            dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
	         PreparedStatement statement = myConn.prepareStatement(query)) {

	        statement.setString(1, newStatus);
	        statement.setString(2, itemName);
	        statement.setString(3, itemBrand);
	        statement.setString(4, itemSupplier);
	        statement.setString(5, itemCategory);

	        int rowsUpdated = statement.executeUpdate();
	        return rowsUpdated > 0;

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	// -----------------------------------------------------------------------------------------------------------------------------


}
