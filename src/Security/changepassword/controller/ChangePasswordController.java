package Security.changepassword.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import notification.NotificationController;
import notification.NotificationDAO;
import database.DatabaseConnection;
import inventorymanagement.model.RegisterItemDAO;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class ChangePasswordController {

    DatabaseConnection dbConnectionInformation = new DatabaseConnection();
	
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
    private Button reportButton;

    @FXML
    private Button maintenanceButton;

    @FXML
    private Button searchButton;

    @FXML
    private Pane overlayPane;

    @FXML
    private Button adminButton;

    @FXML
    private Button staffButton;

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
    void changeAdmin(ActionEvent event) {
        changePassword("Admin");
    }

    @FXML
    void changeStaff(ActionEvent event) {
        changePassword("Staff");
    }

    private void changePassword(String userType) {
    	// List of security questions
        List<String> securityQuestions = Arrays.asList(
            "What is your favorite color?",
            "What was the name of your first pet?",
            "What is your mother's maiden name?",
            "What was the name of your first school?",
            "What is your favorite food?"
        );
    	
        // Randomly select a security question
        Random random = new Random();
        String securityQuestion = securityQuestions.get(random.nextInt(securityQuestions.size()));
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Security Question");
        dialog.setHeaderText(securityQuestion);
        dialog.setContentText("Answer:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && validateSecurityAnswer(securityQuestion, result.get())) {
            TextInputDialog reasonDialog = new TextInputDialog();
            reasonDialog.setTitle("Reason for Password Change");
            reasonDialog.setHeaderText("Please provide a reason for changing your password (max 200 characters):");
            Optional<String> reasonResult = reasonDialog.showAndWait();

            if (reasonResult.isPresent() && reasonResult.get().length() <= 200) {
                TextInputDialog newPasswordDialog = new TextInputDialog();
                newPasswordDialog.setTitle("New Password");
                newPasswordDialog.setHeaderText("Enter your new password that meets the following criteria:\n" +
                        "• 8-15 characters long\n" +
                        "• At least one lowercase letter\n" +
                        "• At least one uppercase letter\n" +
                        "• At least one number\n" +
                        "• No spaces\n" +
                        "• Should not contain the username");
                Optional<String> newPassword = newPasswordDialog.showAndWait();

                TextInputDialog confirmPasswordDialog = new TextInputDialog();
                confirmPasswordDialog.setTitle("Confirm Password");
                confirmPasswordDialog.setHeaderText("Confirm your new password:");
                Optional<String> confirmPassword = confirmPasswordDialog.showAndWait();

                if (newPassword.isPresent() && confirmPassword.isPresent() && newPassword.get().equals(confirmPassword.get())) {
                    if (validatePassword(newPassword.get(), userType)) {
                        String hashedPassword = hashPassword(newPassword.get());
                        if (updatePassword(userType, hashedPassword, reasonResult.get())) {
                            showAlert(Alert.AlertType.INFORMATION, "Success", "Password changed successfully!");
                        }
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Invalid Password", "Password does not meet the required criteria.");
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Password Mismatch", "Passwords do not match.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Invalid Reason", "Reason must be provided (up to 200 characters).");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Incorrect Answer", "Security answer is incorrect.");
        }
    }


    private boolean validateSecurityAnswer(String question, String answer) {
        // Implement security question validation logic here
        // For demonstration, we use simple hardcoded answers
        switch (question) {
            case "What is your favorite color?":
                return "blue".equalsIgnoreCase(answer);
            case "What was the name of your first pet?":
                return "fluffy".equalsIgnoreCase(answer);
            case "What is your mother's maiden name?":
                return "smith".equalsIgnoreCase(answer);
            case "What was the name of your first school?":
                return "greenwood".equalsIgnoreCase(answer);
            case "What is your favorite food?":
                return "pizza".equalsIgnoreCase(answer);
            default:
                return false;
        }
    }

    private boolean validatePassword(String password, String username) {
        return password.length() >= 8 && password.length() <= 15 &&
                password.matches(".*[a-z].*") &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[0-9].*") &&
                !password.contains(" ") &&
                !password.contains(username);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean updatePassword(String userType, String hashedPassword, String reason) {
        // Your existing updatePassword method implementation remains mostly unchanged
        // Modify your insertQuery to include reason column and adjust accordingly
        String insertQuery;
        String getCurrentIdQuery;
        String setStatusQuery;
        String setCurrentStatusQuery;

        // Determine whether updating admin or staff password
        if (userType.equals("Admin")) {
            insertQuery = "INSERT INTO admin (admin_username, admin_password, admin_password_status, admin_reason_of_change) " +
                    "VALUES ('admin', ?, 'current', ?)";
            getCurrentIdQuery = "SELECT admin_id FROM admin WHERE admin_password_status = 'current'";
            setStatusQuery = "UPDATE admin SET admin_password_status = 'old' WHERE admin_id = ?";
            setCurrentStatusQuery = "UPDATE admin SET admin_password_status = 'current' WHERE admin_password = ?";
        } else {
            insertQuery = "INSERT INTO staff (staff_username, staff_password, staff_password_status, staff_reason_of_change) " +
                    "VALUES ('staff', ?, 'current', ?)";
            getCurrentIdQuery = "SELECT staff_id FROM staff WHERE staff_password_status = 'current'";
            setStatusQuery = "UPDATE staff SET staff_password_status = 'old' WHERE staff_id = ?";
            setCurrentStatusQuery = "UPDATE staff SET staff_password_status = 'current' WHERE staff_password = ?";
        }

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
                    dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
            conn.setAutoCommit(false);

            // Get the current password's ID
            int currentId = -1;
            try (PreparedStatement getCurrentIdStatement = conn.prepareStatement(getCurrentIdQuery)) {
                var rs = getCurrentIdStatement.executeQuery();
                if (rs.next()) {
                    currentId = rs.getInt(1);
                }
            }

            // If there is a current password, update its status to 'old'
            if (currentId != -1) {
                try (PreparedStatement setStatusStatement = conn.prepareStatement(setStatusQuery)) {
                    setStatusStatement.setInt(1, currentId);
                    setStatusStatement.executeUpdate();
                }
            }

            // Insert the new password with status 'current' and reason
            try (PreparedStatement insertStatement = conn.prepareStatement(insertQuery)) {
                insertStatement.setString(1, hashedPassword);
                insertStatement.setString(2, reason);
                int insertResult = insertStatement.executeUpdate();

                // Set status of the new password to 'current'
                try (PreparedStatement setCurrentStatusStatement = conn.prepareStatement(setCurrentStatusQuery)) {
                    setCurrentStatusStatement.setString(1, hashedPassword);
                    int setCurrentStatusResult = setCurrentStatusStatement.executeUpdate();

                    conn.commit();
                    return insertResult > 0 && setCurrentStatusResult > 0;
                }
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackException) {
                    System.err.println("Error rolling back transaction: " + rollbackException.getMessage());
                }
            }
            showAlert(Alert.AlertType.ERROR, "Update Error", "Error updating password: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }


    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

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
            Scene scene = new Scene(root);
            Stage stage = (Stage) maintenanceButton.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
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
}