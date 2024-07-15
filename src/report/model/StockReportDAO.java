package report.model;

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

import database.DatabaseConnection;

public class StockReportDAO {

	public List<StockReportModel> fetchDataFromDatabase() {

		DatabaseConnection dbConnectionInformation = new DatabaseConnection();
		List<StockReportModel> data = new ArrayList<>();

		String query = "SELECT * FROM 828cafe.stock_report ORDER BY datetime_of_change DESC";

		try (Connection myConn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
				PreparedStatement statement = myConn.prepareStatement(query);
				ResultSet resultSet = statement.executeQuery()) {

			while (resultSet.next()) {
				StockReportModel item = new StockReportModel();
				item.setUnique_stock_report_id(resultSet.getString("unique_stock_report_id"));
				item.setItem_name(resultSet.getString("item_name"));
				item.setItem_brand(resultSet.getString("item_brand"));
				item.setItem_supplier(resultSet.getString("item_supplier"));
				item.setItem_category(resultSet.getString("item_category"));
				item.setItem_unit_of_measurement(resultSet.getString("item_unit_of_measurement"));
				item.setItem_minimum_threshold(resultSet.getInt("item_minimum_threshold"));
				item.setItem_maximum_threshold(resultSet.getInt("item_maximum_threshold"));
				item.setPrevious_total_stock(resultSet.getDouble("previous_total_stock"));
				item.setChange_in_stock(resultSet.getDouble("change_in_stock"));
				item.setUpdated_stock(resultSet.getDouble("updated_stock"));
				item.setItem_status(resultSet.getString("item_status"));

				Timestamp timestampOfChange = resultSet.getTimestamp("datetime_of_change");
				if (timestampOfChange != null) {
					LocalDateTime dateTimeOfChange = timestampOfChange.toLocalDateTime();
					item.setDatetime_of_change(dateTimeOfChange);
				} else {
					item.setDatetime_of_change(null);
				}

				data.add(item);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			// Handle exceptions appropriately
		}

		return data;
	}
	
	// Method to get total expenses within a date range
    public double getTotalExpenses(LocalDate startDate, LocalDate endDate) {
        double totalExpenses = 0.0;
        String query = "SELECT SUM(new_item_batch_cost) AS total_expenses FROM restock WHERE datetime_of_restocking BETWEEN ? AND ?";
    	DatabaseConnection dbConnectionInformation = new DatabaseConnection();

        try (Connection connection = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
        		PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                totalExpenses = rs.getDouble("total_expenses");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return totalExpenses;
    }
	
	
	// Method to fetch data based on the date range
    public List<StockReportModel> getDataBasedOnDate(Date startDate, Date endDate) {
    	DatabaseConnection dbConnectionInformation = new DatabaseConnection();
        List<StockReportModel> stockReportList = new ArrayList<>();
        String query = "SELECT * FROM stock_report WHERE datetime_of_change BETWEEN ? AND ?";

        try (Connection myConn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
             PreparedStatement preparedStatement = myConn.prepareStatement(query)) {

            preparedStatement.setDate(1, startDate);
            preparedStatement.setDate(2, endDate);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                StockReportModel stockReport = new StockReportModel();
                // Populate stockReport with data from resultSet
                stockReport.setUnique_stock_report_id(resultSet.getString("unique_stock_report_id"));
                stockReport.setItem_name(resultSet.getString("item_name"));
                stockReport.setItem_brand(resultSet.getString("item_brand"));
                stockReport.setItem_supplier(resultSet.getString("item_supplier"));
                stockReport.setItem_category(resultSet.getString("item_category"));
                stockReport.setItem_minimum_threshold(resultSet.getInt("item_minimum_threshold"));
                stockReport.setItem_maximum_threshold(resultSet.getInt("item_maximum_threshold"));
                stockReport.setPrevious_total_stock(resultSet.getDouble("previous_total_stock"));
                stockReport.setChange_in_stock(resultSet.getDouble("change_in_stock"));
                stockReport.setUpdated_stock(resultSet.getDouble("updated_stock"));
                stockReport.setItem_status(resultSet.getString("item_status"));

				Timestamp timestampOfChange = resultSet.getTimestamp("datetime_of_change");
				if (timestampOfChange != null) {
					LocalDateTime dateTimeOfChange = timestampOfChange.toLocalDateTime();
					stockReport.setDatetime_of_change(dateTimeOfChange);
				} else {
					stockReport.setDatetime_of_change(null);
				}
                stockReportList.add(stockReport);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stockReportList;
    }
}
