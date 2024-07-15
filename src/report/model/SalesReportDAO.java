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

public class SalesReportDAO {
	
	public List<SalesReportModel> fetchDataFromDatabase() {

		DatabaseConnection dbConnectionInformation = new DatabaseConnection();
		List<SalesReportModel> data = new ArrayList<>();

		String query = "SELECT * FROM 828cafe.sales_report ORDER BY sold_datetime DESC";

		try (Connection myConn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
				PreparedStatement statement = myConn.prepareStatement(query);
				ResultSet resultSet = statement.executeQuery()) {

			while (resultSet.next()) {
				SalesReportModel item = new SalesReportModel();
			    item.setSales_report_id(resultSet.getInt("sales_report_id"));
			    item.setUnique_sales_report_id(resultSet.getString("unique_sales_report_id"));
			    item.setMenu_item_name(resultSet.getString("menu_item_name"));
			    item.setMenu_item_price(resultSet.getDouble("menu_item_price"));
			    item.setMenu_item_quantity_sold(resultSet.getDouble("menu_item_quantity_sold"));
			    
			    // Handle null value for sold_datetime
			    Timestamp timestamp = resultSet.getTimestamp("sold_datetime");
			    if (timestamp != null) {
			        item.setSold_datetime(timestamp.toLocalDateTime());
			    } else {
			        item.setSold_datetime(null); // or handle accordingly
			    }

				data.add(item);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			// Handle exceptions appropriately
		}

		return data;
	}
	
	// Method to fetch data based on the date range
    public List<SalesReportModel> getDataBasedOnDate(Date startDate, Date endDate) {
    	DatabaseConnection dbConnectionInformation = new DatabaseConnection();
        List<SalesReportModel> stockReportList = new ArrayList<>();
        String query = "SELECT * FROM 828cafe.sales_report WHERE sold_datetime BETWEEN ? AND ?";

        try (Connection myConn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
             PreparedStatement preparedStatement = myConn.prepareStatement(query)) {

            preparedStatement.setDate(1, startDate);
            preparedStatement.setDate(2, endDate);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
				SalesReportModel item = new SalesReportModel();
			    item.setSales_report_id(resultSet.getInt("sales_report_id"));
			    item.setUnique_sales_report_id(resultSet.getString("unique_sales_report_id"));
			    item.setMenu_item_name(resultSet.getString("menu_item_name"));
			    item.setMenu_item_price(resultSet.getDouble("menu_item_price"));
			    item.setMenu_item_quantity_sold(resultSet.getDouble("menu_item_quantity_sold"));

			 // Handle null value for sold_datetime
			    Timestamp timestamp = resultSet.getTimestamp("sold_datetime");
			    if (timestamp != null) {
			        item.setSold_datetime(timestamp.toLocalDateTime());
			    } else {
			        item.setSold_datetime(null); // or handle accordingly
			    }

                stockReportList.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stockReportList;
    }
    
    // Method to get total sales within a date range
    public double getTotalSales(LocalDate startDate, LocalDate endDate) {
        double totalSales = 0.0;
        String query = "SELECT SUM(order_grand_total) AS total_sales FROM orders_table WHERE order_datetime BETWEEN ? AND ?";
    	DatabaseConnection dbConnectionInformation = new DatabaseConnection();

        try (Connection connection = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
        		PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                totalSales = rs.getDouble("total_sales");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return totalSales;
    }
    
    
}
