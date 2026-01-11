package cafe828.desktop.about;

import java.io.IOException;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import cafe828.desktop.notification.NotificationController;
import cafe828.desktop.database.RegisterItemDAO;
import cafe828.desktop.database.NotificationDAO;

public class AboutAdminController {

    @FXML
    private Pane overlayPane;

    @FXML
    private AnchorPane mainFrameBg;

    @FXML
    private VBox menuBar;

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
    private Label registerItemLabel;

    @FXML
    private VBox vBoxContent;

    @FXML
    private HBox hBoxTeam;

    @FXML
    private Label aboutSoftwareLabel;

    @FXML
    private VBox vBoxSoftwareInfo;

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

    // Define methods for handling actions if needed

    @FXML
	void goToHome(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/cafe828/desktop/admin/AdminMenuView.fxml"));
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
	void goToSearch(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/cafe828/desktop/search/SearchLandingPageView.fxml"));
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
					getClass().getResource("/cafe828/desktop/inventory/RegisterItemView.fxml"));
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
					getClass().getResource("/cafe828/desktop/maintenance/landingpage/LandingPageView.fxml"));
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
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/cafe828/desktop/security/changepassword/ChangePassword.fxml"));
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
	void goToReport(ActionEvent event) {
		try {
			// Load Maintenance.fxml
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/cafe828/desktop/report/ReportLandingPageView.fxml"));
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
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/cafe828/desktop/help/HelpLandingPageView.fxml"));
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

	}

	@FXML
    void goToLogout(ActionEvent event) {
    	RegisterItemDAO daoRegisterItem = new RegisterItemDAO();
    	try {
			// Load Maintenance.fxml
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/cafe828/desktop/security/V_Login.fxml"));
			Parent root = loader.load();
			
			// Get the controller for Maintenance.fxml if needed
			// C_Maintenance maintenanceController = loader.getController();
			daoRegisterItem.logUserTrail("Admin", "Logout");
			// Access the current scene and set the new root
			Stage stage = (Stage) logoutButton.getScene().getWindow();
			Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
    }

}
