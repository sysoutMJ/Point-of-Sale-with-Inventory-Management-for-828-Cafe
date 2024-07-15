package maintenance.backup.controller;

import java.io.File;
import java.io.IOException;

import inventorymanagement.model.RegisterItemDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import notification.NotificationController;
import notification.NotificationDAO;

public class BackupController {

	@FXML
	private Button homeButton;

	@FXML
	private Button notificationButton;

	@FXML
	private Button logoutButton;

	@FXML
	private Button securityButton;

	@FXML
	private Button helpButton;

	@FXML
	private Button aboutButton;

	@FXML
	private Button registerButton;

	@FXML
	private Button restockButton;

	@FXML
	private Button reportButton;

	@FXML
	private Button maintenanceButton;

	@FXML
	private Button searchButton;

	@FXML
	private Button checkButton;
	
	@FXML
	private Button CreateMenuButton;
	
    @FXML
    private Button backupButton;

	@FXML
	private Pane overlayPane;

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

	// -----------------------------------------------------------------------------------------------------------------------

	

	// -----------------------------------------------------------------------------------------------------------------------


	
	// -----------------------------------------------------------------------------------------------------------------------
	
	@FXML
    void backupAction(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Database Backup");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL Files", "*.sql"));
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            String backupFilePath = file.getAbsolutePath();

            try {
                // Execute mysqldump command to backup the database
                String[] command = new String[]{
                        "C:\\Program Files\\MySQL\\MySQL Server 8.4\\bin\\mysqldump",
                        "-u", "root", // Replace with your MySQL username
                        "-pjavauser", // Replace with your MySQL password
                        "828cafe", // Replace with your database name
                        "-r", backupFilePath
                };

                Process process = Runtime.getRuntime().exec(command);
                int exitCode = process.waitFor();

                if (exitCode == 0) {
                    System.out.println("Backup created successfully.");
                } else {
                    System.out.println("Error: Backup creation failed.");
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            
            System.out.print("ss");
        }
    }
	
	@FXML
    void restoreAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Restore Database Backup");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL Files", "*.sql"));
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            String restoreFilePath = file.getAbsolutePath();

            // Warn user about overwriting current data
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirm Restore");
            alert.setHeaderText("Warning: Restoring this backup will overwrite your current database.");
            alert.setContentText("Are you sure you want to proceed?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        // Execute mysql command to restore the database
                        String[] command = new String[]{
                                "C:\\Program Files\\MySQL\\MySQL Server 8.4\\bin\\mysql",
                                "-u", "root", // Replace with your MySQL username
                                "-pjavauser", // Replace with your MySQL password
                                "828cafe", // Replace with your database name
                                "-e", "source " + restoreFilePath
                        };

                        Process process = Runtime.getRuntime().exec(command);
                        int exitCode = process.waitFor();

                        if (exitCode == 0) {
                            showAlert(AlertType.INFORMATION, "Restore Successful", "Database restore completed.");
                        } else {
                            showAlert(AlertType.ERROR, "Restore Failed", "Error: Database restore failed.");
                        }
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void showAlert(AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

	// TOP MENU BAR

	@FXML
	void goToRestock(ActionEvent event) {
		updateLookOfNotification();
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/maintenance/restock/view/RestockView.fxml"));
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
	void goToEditItem(ActionEvent event) {
		updateLookOfNotification();
		try {
			// Load Maintenance.fxml
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/maintenance/edititemdetails/view/EditItemDetailsView.fxml"));
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
	void goToSetItemStatus(ActionEvent event) {
		updateLookOfNotification();
		try {
			// Load Maintenance.fxml
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/maintenance/setitemstatus/view/SetItemStatusView.fxml"));
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
	void goToCreateMenu(ActionEvent event) {
		updateLookOfNotification();
		try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/maintenance/createmenu/view/CreateMenuView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) CreateMenuButton.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	@FXML
	void goToBackup(ActionEvent event) {

	}

	// -----------------------------------------------------------------------------------------------------------------------

	// SIDE BAR MENU METHODS

	@FXML
	void goToHome(ActionEvent event) {
		updateLookOfNotification();
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
		updateLookOfNotification();
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/search/view/SearchInventoryView.fxml"));
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
		updateLookOfNotification();
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
		updateLookOfNotification();
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
		updateLookOfNotification();
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
		updateLookOfNotification();
		try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/report/view/ReportLandingPageView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	@FXML
    void goToHelp(ActionEvent event) {
		updateLookOfNotification();
    	try {
			// Load Maintenance.fxml
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/help/view/HelpLandingPageView.fxml"));
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
		}    }

    @FXML
    void goToAbout(ActionEvent event) {
    	updateLookOfNotification();
    	
    	try {
			// Load Maintenance.fxml
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/about/view/AboutAdmin.fxml"));
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
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/Security/View/V_Login.fxml"));
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

	// -----------------------------------------------------------------------------------------------------------------------

}
