package Admin.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;
import notification.NotificationController;
import notification.NotificationDAO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import database.DatabaseConnection;
import inventorymanagement.model.RegisterItemDAO;

public class AdminMenuController {

	@FXML
	private Button register_item;

	@FXML
	private Button maintenance_button;

	@FXML
	private Button report_module;

	@FXML
	private Button search_module;

	@FXML
	private Button help_module;

	@FXML
	private Button about_module;

	@FXML
	private Button logout_module;

	@FXML
	private Button changePass;

	@FXML
	private Button chg_psswrd;

	@FXML
	private Button edt_scrty_qstn;

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
	}

	@FXML
	void goToEditSecurityQuestion(ActionEvent event) {
		// Implement navigation to Edit Security Question page
	}

	private void logUserTrail(String username, String eventName) {
		DatabaseConnection dbConnectionInformation = new DatabaseConnection();
		String insertQuery = "INSERT INTO user_trail_report (unique_event_id, event_name, user, event_datetime, login, logout) "
				+ "VALUES (?, ?, ?, ?, ?, NULL)";

		try (Connection conn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
				PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

			String uniqueEventId = setGeneratedRandomCodeForStockReportId(15);
			LocalDateTime currentDateTime = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String formattedDateTime = currentDateTime.format(formatter);

			stmt.setString(1, uniqueEventId);
			stmt.setString(2, eventName);
			stmt.setString(3, username);
			stmt.setString(4, formattedDateTime);
			stmt.setString(5, formattedDateTime);

			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Generate a random string of code for unique_stock_report_id in stock_report
	// in the database.
	public String setGeneratedRandomCodeForStockReportId(int length) {
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

	// This method checks it the generated random string of code already exists in
	// the database. If it does not exists, it will return false.
	public boolean isUniqueIdExists(String uniqueItemId) throws SQLException {

		DatabaseConnection dbConnectionInformation = new DatabaseConnection();

		Connection myConn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());

		String query = "SELECT COUNT(*) FROM 828cafe.user_trail_report WHERE unique_event_id = ?";

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

	@FXML
	void change_pass(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/Security/changepassword/view/ChangePassword.fxml"));
			Parent root = loader.load();

			Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
			stage.getScene().setRoot(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void goToRegisterItem(ActionEvent event) {
		try {
			// Load Maintenance.fxml
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/inventorymanagement/view/RegisterItemView.fxml"));
			Parent root = loader.load();

			// Get the controller for Maintenance.fxml if needed
			// C_Maintenance maintenanceController = loader.getController();

			// Access the current scene and set the new root
			Stage stage = (Stage) logout_module.getScene().getWindow();
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
			Stage stage = (Stage) logout_module.getScene().getWindow();
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
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/report/view/StockReportView.fxml"));
			Parent root = loader.load();

			// Get the controller for Maintenance.fxml if needed
			// C_Maintenance maintenanceController = loader.getController();

			// Access the current scene and set the new root
			Stage stage = (Stage) logout_module.getScene().getWindow();
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
			// Load Maintenance.fxml
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/search/view/SearchLandingPageView.fxml"));
			Parent root = loader.load();

			// Get the controller for Maintenance.fxml if needed
			// C_Maintenance maintenanceController = loader.getController();

			// Access the current scene and set the new root
			Stage stage = (Stage) logout_module.getScene().getWindow();
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
			Stage stage = (Stage) logout_module.getScene().getWindow();
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
			Stage stage = (Stage) logout_module.getScene().getWindow();
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
			Stage stage = (Stage) logout_module.getScene().getWindow();
			Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
