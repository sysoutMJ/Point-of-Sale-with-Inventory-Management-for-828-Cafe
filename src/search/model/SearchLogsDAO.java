package search.model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import database.DatabaseConnection;

public class SearchLogsDAO {

    final int generatedCodeLength = 15;

    // Method to fetch data from the database
    public List<SearchLogsModel> fetchDataFromDatabase() {
        DatabaseConnection dbConnectionInformation = new DatabaseConnection();
        List<SearchLogsModel> data = new ArrayList<>();

        try (Connection myConn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
                dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword())) {

            String query = "SELECT * FROM 828cafe.user_trail_report ORDER BY event_datetime DESC";
            try (PreparedStatement statement = myConn.prepareStatement(query);
                    ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    SearchLogsModel item = mapResultSetToSearchLogsModel(resultSet);
                    data.add(item);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    // Method to search logs by date
    public List<SearchLogsModel> searchLogsByDate(LocalDate eventDate) {
        DatabaseConnection dbConnectionInformation = new DatabaseConnection();
        List<SearchLogsModel> data = new ArrayList<>();

        try (Connection myConn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
                dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword())) {

            String query = "SELECT * FROM user_trail_report WHERE DATE(event_datetime) = ? ORDER BY event_datetime DESC";
            try (PreparedStatement statement = myConn.prepareStatement(query)) {
                statement.setDate(1, Date.valueOf(eventDate));
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        SearchLogsModel item = mapResultSetToSearchLogsModel(resultSet);
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

    // Method to search logs by user
    public List<SearchLogsModel> searchLogsByUser(String user) {
        DatabaseConnection dbConnectionInformation = new DatabaseConnection();
        List<SearchLogsModel> data = new ArrayList<>();

        try (Connection myConn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
                dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword())) {

            String query = "SELECT * FROM user_trail_report WHERE user = ? ORDER BY event_datetime DESC";
            try (PreparedStatement statement = myConn.prepareStatement(query)) {
                statement.setString(1, user);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        SearchLogsModel item = mapResultSetToSearchLogsModel(resultSet);
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

    // Method to search logs by action (event name)
    public List<SearchLogsModel> searchLogsByAction(String eventName) {
        DatabaseConnection dbConnectionInformation = new DatabaseConnection();
        List<SearchLogsModel> data = new ArrayList<>();

        try (Connection myConn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
                dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword())) {

            String query = "SELECT * FROM user_trail_report WHERE event_name = ? ORDER BY event_datetime DESC";
            try (PreparedStatement statement = myConn.prepareStatement(query)) {
                statement.setString(1, eventName);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        SearchLogsModel item = mapResultSetToSearchLogsModel(resultSet);
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

 // Method to map ResultSet to SearchLogsModel
    private SearchLogsModel mapResultSetToSearchLogsModel(ResultSet resultSet) throws SQLException {
        SearchLogsModel item = new SearchLogsModel();
        item.setEventId(resultSet.getInt("event_id"));
        item.setUniqueEventId(resultSet.getString("unique_event_id"));
        item.setEventName(resultSet.getString("event_name"));
        item.setUser(resultSet.getString("user"));
        
        Timestamp eventTimestamp = resultSet.getTimestamp("event_datetime");
        if (eventTimestamp != null) {
            item.setEventDatetime(eventTimestamp.toLocalDateTime());
        } else {
            item.setEventDatetime(null); // or handle default value if needed
        }
        
        // Set other fields as necessary
        return item;
    }


    public void insertToUserLog(SearchLogsModel SearchLogsModelAttributes) throws SQLException {
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

                if (SearchLogsModelAttributes.getEventDatetime() != null) {
                    userTrailReportStatement.setTimestamp(4,
                            Timestamp.valueOf(SearchLogsModelAttributes.getEventDatetime()));
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
                SearchLogsDAO dao = new SearchLogsDAO();
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
