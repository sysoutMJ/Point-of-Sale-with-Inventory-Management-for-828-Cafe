package report.controller;

import java.io.File;
import java.io.IOException;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.DatabaseConnection;
import inventorymanagement.model.RegisterItemDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import notification.NotificationController;
import notification.NotificationDAO;
//import net.sf.jasperreports.engine.JRException;
//import net.sf.jasperreports.engine.JasperCompileManager;
//import net.sf.jasperreports.engine.JasperExportManager;
//import net.sf.jasperreports.engine.JasperFillManager;
//import net.sf.jasperreports.engine.JasperPrint;
//import net.sf.jasperreports.engine.JasperReport;
//import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
//import net.sf.jasperreports.view.JasperViewer;
import report.model.StockReportDAO;
import report.model.StockReportModel;

public class StockReportController {

	@FXML
	private Pane overlayPane;

	@FXML
	private TableView<StockReportModel> stock_report;

	@FXML
	private TableColumn<StockReportModel, String> unique_stock_report_id;

	@FXML
	private TableColumn<StockReportModel, String> item_name;

	@FXML
	private TableColumn<StockReportModel, String> item_brand;

	@FXML
	private TableColumn<StockReportModel, String> item_supplier;

	@FXML
	private TableColumn<StockReportModel, String> item_category;
	
	@FXML
	private TableColumn<StockReportModel, String> item_unit_of_measurement;
	
	@FXML
	private TableColumn<StockReportModel, String> item_minimum_threshold;
	
	@FXML
	private TableColumn<StockReportModel, String> item_maximum_threshold;

	@FXML
	private TableColumn<StockReportModel, Integer> previous_total_stock;

	@FXML
	private TableColumn<StockReportModel, Integer> updated_stock;

	@FXML
	private TableColumn<StockReportModel, Integer> change_in_stock;
	
	@FXML
	private TableColumn<StockReportModel, Integer> item_status;
	
	@FXML
	private TableColumn<StockReportModel, String> datetime_of_change;

	@FXML
	private Button notificationButton;

	@FXML
	private Button homeButton;

	@FXML
	private Button searchButton;

	@FXML
	private Button registerItemButton;

	@FXML
	private Button maintenanceButton;

	@FXML
	private Button securityButton;

	@FXML
	private Button reportButton;

	@FXML
	private Button helpButton;

	@FXML
	private Button aboutButton;

	@FXML
	private Button logoutButton;

	@FXML
	private Button generatePDFButton;

	@FXML
	private Button goToStockReportButton;

	@FXML
	private Button goToSalesReportButton;

	@FXML
	private DatePicker startDatePicker;

	@FXML
	private DatePicker endDatePicker;

	// ------------------------------------------------------------------------------------------------------------------------------------
	// For Notification button located in the right side. Variables are putted here
	// for easy modifying and distribution to other modules

	@FXML
	private ToggleButton notificationToggleButton;

	@FXML
	private ScrollPane notificationScrollPane;

	@FXML
	private Label notificationLabel;

	NotificationController controllerNotification = new NotificationController();

	void updateLookOfNotification() {
		controllerNotification.displayNotifcationIcon(notificationToggleButton, notificationScrollPane);
		;
	}

	@FXML
	void getNotification(ActionEvent event) {
		updateLookOfNotification();
	}

	@FXML
	public void initialize() {

		updateLookOfNotification();
		notificationScrollPane.setVisible(false);
		notificationLabel.setText("");

		NotificationDAO daoNotification = new NotificationDAO();
		StringBuilder sb = new StringBuilder();

		sb.append(daoNotification.getStockAlertMessages());

		// Update the notification label
		notificationLabel.setText(sb.toString());

		notificationScrollPane.setVisible(false);

		// END FOR NOTIF
		// -------------------------------------------------------------------------------------------------------------------

//		stock_id.setCellValueFactory(new PropertyValueFactory<>("stock_id"));
		unique_stock_report_id.setCellValueFactory(new PropertyValueFactory<>("unique_stock_report_id"));
		item_name.setCellValueFactory(new PropertyValueFactory<>("item_name"));
		item_brand.setCellValueFactory(new PropertyValueFactory<>("item_brand"));
		item_supplier.setCellValueFactory(new PropertyValueFactory<>("item_supplier"));
		item_category.setCellValueFactory(new PropertyValueFactory<>("item_category"));
		item_unit_of_measurement.setCellValueFactory(new PropertyValueFactory<>("item_unit_of_measurement"));
		item_minimum_threshold.setCellValueFactory(new PropertyValueFactory<>("item_minimum_threshold"));
		item_maximum_threshold.setCellValueFactory(new PropertyValueFactory<>("item_maximum_threshold"));//		new_item_batch_quantity.setCellValueFactory(new PropertyValueFactory<>("new_item_batch_quantity"));
//		new_item_batch_cost.setCellValueFactory(new PropertyValueFactory<>("new_item_batch_cost"));
		previous_total_stock.setCellValueFactory(new PropertyValueFactory<>("previous_total_stock"));
		change_in_stock.setCellValueFactory(new PropertyValueFactory<>("change_in_stock"));
		updated_stock.setCellValueFactory(new PropertyValueFactory<>("updated_stock"));
//		new_item_batch_expiration_date.setCellValueFactory(new PropertyValueFactory<>("new_item_batch_expiration_date"));
//		date_of_purchase.setCellValueFactory(new PropertyValueFactory<>("date_of_purchase"));
		item_status.setCellValueFactory(new PropertyValueFactory<>("item_status"));
		datetime_of_change.setCellValueFactory(new PropertyValueFactory<>("datetime_of_change"));

		// -----------------------------------------------------------------------------------------------------------------------

		adjustColumnWidths(stock_report);
		populateTable();

	}

	// -----------------------------------------------------------------------------------------------------------------------

	// Method for adjusting the column widths based on its data.
	private void adjustColumnWidths(TableView<?> table) {
		// Set the right policy
		table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		table.getColumns().stream().forEach((column) -> {
			// Minimal width = columnheader
			Text t = new Text(column.getText());
			double max = t.getLayoutBounds().getWidth();
			for (int i = 0; i < table.getItems().size(); i++) {
				// cell must not be empty
				if (column.getCellData(i) != null) {
					t = new Text(column.getCellData(i).toString());
					double calcwidth = t.getLayoutBounds().getWidth();
					// remember new max-width
					if (calcwidth > max) {
						max = calcwidth;
					}
				}
			}
			// set the new max-widht with some extra space
			column.setPrefWidth(max + 10.0d);
		});
	}

	// Method for populating the table.
	private void populateTable() {

		StockReportDAO daoStockReport = new StockReportDAO();

		// Fetch data from database
		List<StockReportModel> items = daoStockReport.fetchDataFromDatabase();

		// Add data to TableView
		stock_report.getItems().addAll(items);
	}

	@FXML
	private void generatePDF(ActionEvent event) throws JRException, SQLException {
		// Database connection
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/828cafe", "root", "javauser");

		// Query to fetch data
		String query = "SELECT unique_stock_report_id, item_name, item_brand, item_supplier, item_category, previous_total_stock, change_in_stock, updated_stock, datetime_of_change FROM stock_report ORDER BY datetime_of_change DESC";
		PreparedStatement statement = conn.prepareStatement(query);
		ResultSet resultSet = statement.executeQuery();

		// Populate data list
		List<Map<String, Object>> data = new ArrayList<>();
		while (resultSet.next()) {
			Map<String, Object> row = new HashMap<>();
			row.put("unique_stock_report_id", resultSet.getString("unique_stock_report_id"));
			row.put("item_name", resultSet.getString("item_name"));
			row.put("item_brand", resultSet.getString("item_brand"));
			row.put("item_supplier", resultSet.getString("item_supplier"));
			row.put("item_category", resultSet.getString("item_category"));
			row.put("previous_total_stock", resultSet.getDouble("previous_total_stock"));
			row.put("change_in_stock", resultSet.getDouble("change_in_stock"));
			row.put("updated_stock", resultSet.getDouble("updated_stock"));
			
			
			// Retrieve the datetime_of_change as a Timestamp
		    Timestamp timestamp = resultSet.getTimestamp("datetime_of_change");
		    LocalDateTime datetimeOfChange = timestamp.toLocalDateTime();
		    row.put("datetime_of_change", datetimeOfChange);
		    
			data.add(row);
		}

		// Compile and fill the report
        JasperReport jasperReport = JasperCompileManager.compileReport("C:\\Users\\Danielle\\Desktop\\NEW\\MAAAAAAARCUUUUUUUUUS\\SE 2\\src\\report\\view\\stock_report.jrxml");
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), dataSource);

        // View the report
        JasperViewer.viewReport(jasperPrint, false);

		// Close resources
		resultSet.close();
		statement.close();
		conn.close();
	}


	@FXML
	void search() {
		StockReportDAO daoStockReport = new StockReportDAO();
		LocalDate startDate = startDatePicker.getValue();
		LocalDate endDate = endDatePicker.getValue();

		if (startDate != null && endDate != null) {
			// Convert LocalDate to Date
			Date sqlStartDate = Date.valueOf(startDate);
			Date sqlEndDate = Date.valueOf(endDate);

			List<StockReportModel> items = daoStockReport.getDataBasedOnDate(sqlStartDate, sqlEndDate);

			if (items.isEmpty()) {
				showNoDataFoundDialog();
				endDatePicker.setValue(null);
				startDatePicker.setValue(null);
			} else {
				showDataFoundDialog();
				stock_report.getItems().setAll(items);
			}
		} else {
			showNoDataFoundDialog();
			endDatePicker.setValue(null);
			startDatePicker.setValue(null);
		}
	}

	private void showDataFoundDialog() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/report/view/DataFoundView.fxml"));
			Parent root = loader.load();

			Stage stage = new Stage();
			stage.initStyle(StageStyle.UNDECORATED);
			stage.initModality(Modality.APPLICATION_MODAL); // Ensures it blocks input to other windows
			stage.initOwner(overlayPane.getScene().getWindow()); // Set the main window as the owner

			Scene scene = new Scene(root);
			stage.setScene(scene);

			// Set the dialogue box in the middle of the parent container.
			// -------------------------------

			// Calculate the center position relative to the main window
			double centerXPosition = overlayPane.getScene().getWindow().getX()
					+ overlayPane.getScene().getWindow().getWidth() / 2;
			double centerYPosition = overlayPane.getScene().getWindow().getY()
					+ overlayPane.getScene().getWindow().getHeight() / 2;

			// Set the dialog position to be centered
			stage.setX(centerXPosition - root.prefWidth(-1) / 2);
			stage.setY(centerYPosition - root.prefHeight(-1) / 2);

			// -------------------------------------------------------------------------------------------

			// Set overlay pane visibility to true and adjust opacity
			overlayPane.setDisable(true);

			// Show the dialog and wait for it to close
			stage.showAndWait();

			// Hide the overlay pane after the dialog is closed
			overlayPane.setDisable(false);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void showNoDataFoundDialog() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/report/view/NoDataFoundView.fxml"));
			Parent root = loader.load();

			Stage stage = new Stage();
			stage.initStyle(StageStyle.UNDECORATED);
			stage.initModality(Modality.APPLICATION_MODAL); // Ensures it blocks input to other windows
			stage.initOwner(overlayPane.getScene().getWindow()); // Set the main window as the owner

			Scene scene = new Scene(root);
			stage.setScene(scene);

			// Set the dialogue box in the middle of the parent container.
			// -------------------------------

			// Calculate the center position relative to the main window
			double centerXPosition = overlayPane.getScene().getWindow().getX()
					+ overlayPane.getScene().getWindow().getWidth() / 2;
			double centerYPosition = overlayPane.getScene().getWindow().getY()
					+ overlayPane.getScene().getWindow().getHeight() / 2;

			// Set the dialog position to be centered
			stage.setX(centerXPosition - root.prefWidth(-1) / 2);
			stage.setY(centerYPosition - root.prefHeight(-1) / 2);

			// -------------------------------------------------------------------------------------------

			// Set overlay pane visibility to true and adjust opacity
			overlayPane.setDisable(true);

			// Show the dialog and wait for it to close
			stage.showAndWait();

			// Hide the overlay pane after the dialog is closed
			overlayPane.setDisable(false);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------

	@FXML
	void goToStockReport() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/report/view/StockReportView.fxml"));
			Parent root = loader.load();

			Stage stage = (Stage) maintenanceButton.getScene().getWindow();
			Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void goToSalesReport() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/report/view/SalesReportView.fxml"));
			Parent root = loader.load();

			Stage stage = (Stage) maintenanceButton.getScene().getWindow();
			Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------

	// -----------------------------------------------------------------------------------------------------------------------

	// SIDE BAR MENU METHODS

	@FXML
	void goToHome(ActionEvent event) {
		try {
			// Load Maintenance.fxml
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/Admin/view/AdminMenuView.fxml"));
			Parent root = loader.load();

			// Get the controller for Maintenance.fxml if needed
			// C_Maintenance maintenanceController = loader.getController();

			// Access the current scene and set the new root
			Stage stage = (Stage) maintenanceButton.getScene().getWindow();
			Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void goToSearch(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/search/view/SearchLandingPageView.fxml"));
			Parent root = loader.load();

			Stage stage = (Stage) maintenanceButton.getScene().getWindow();
			Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void goToRegisterItem(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/inventorymanagement/view/RegisterItemView.fxml"));
			Parent root = loader.load();

			Stage stage = (Stage) maintenanceButton.getScene().getWindow();
			Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void goToMaintenance(ActionEvent event) {
		try {
			// Load Maintenance.fxml
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/maintenance/landingpage/view/LandingPageView.fxml"));
			Parent root = loader.load();

			// Get the controller for Maintenance.fxml if needed
			// C_Maintenance maintenanceController = loader.getController();

			// Access the current scene and set the new root
			Stage stage = (Stage) maintenanceButton.getScene().getWindow();
			Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void goToSecurity(ActionEvent event) {
		try {
			// Load Maintenance.fxml
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/Security/changepassword/view/ChangePassword.fxml"));
			Parent root = loader.load();

			// Get the controller for Maintenance.fxml if needed
			// C_Maintenance maintenanceController = loader.getController();

			// Access the current scene and set the new root
			Stage stage = (Stage) maintenanceButton.getScene().getWindow();
			Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void goToReport(ActionEvent event) {
		try {
			// Load Maintenance.fxml
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/report/view/ReportLandingPageView.fxml"));
			Parent root = loader.load();

			// Get the controller for Maintenance.fxml if needed
			// C_Maintenance maintenanceController = loader.getController();

			// Access the current scene and set the new root
			Stage stage = (Stage) maintenanceButton.getScene().getWindow();
			Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void goToHelp(ActionEvent event) {
		try {
			// Load Maintenance.fxml
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/help/view/HelpLandingPageView.fxml"));
			Parent root = loader.load();

			// Get the controller for Maintenance.fxml if needed
			// C_Maintenance maintenanceController = loader.getController();

			// Access the current scene and set the new root
			Stage stage = (Stage) maintenanceButton.getScene().getWindow();
			Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void goToAbout(ActionEvent event) {

		try {
			// Load Maintenance.fxml
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/about/view/AboutAdmin.fxml"));
			Parent root = loader.load();

			// Get the controller for Maintenance.fxml if needed
			// C_Maintenance maintenanceController = loader.getController();

			// Access the current scene and set the new root
			Stage stage = (Stage) maintenanceButton.getScene().getWindow();
			Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void goToLogout(ActionEvent event) {
		RegisterItemDAO daoRegisterItem = new RegisterItemDAO();
		try {
			// Load Maintenance.fxml
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/Security/View/V_Login.fxml"));
			Parent root = loader.load();

			// Get the controller for Maintenance.fxml if needed
			// C_Maintenance maintenanceController = loader.getController();
			daoRegisterItem.logUserTrail("Admin", "Logout");
			// Access the current scene and set the new root
			Stage stage = (Stage) maintenanceButton.getScene().getWindow();
			Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}