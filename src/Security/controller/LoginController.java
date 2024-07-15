package Security.controller;

import javafx.event.ActionEvent;
import Main.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import database.DatabaseConnection;
import inventorymanagement.controller.DialogueBoxesController;
import inventorymanagement.model.RegisterItemDAO;

public class LoginController {

	@FXML 
	private Pane overlayPane;
	
    @FXML
    private Button confirm;
    
    @FXML
    private ImageView logo;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button direct;

    @FXML
    private Button forgotPassButton;

    @FXML
    private Button admin;

    @FXML
    private Button staff;

    @FXML
    private Button cancel;

    @FXML
    private Button backtoLogin;

    @FXML
    private Button saveAnswer;
    
    @FXML
    void initialize() {
//    	usernameField.setText("staff");
//    	passwordField.setText("828Cafee");
    	
        usernameField.setOnKeyPressed(this::handleEnterKey);
        passwordField.setOnKeyPressed(this::handleEnterKey);
    }
    
    private static String PREFIX = null;
	private static final int NUMERIC_LENGTH = 10; // Length of the numeric part

    private void handleEnterKey(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            logins(new ActionEvent());
        }
    }
    
    @FXML
    void forgotPass(ActionEvent event) {
        showUserTypeDialog();
    }

    private void showUserTypeDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Security/View/ChooseAccountForgotPassView.fxml"));
            Parent root = loader.load();

            ChooseAccountForgotPassDialogueBoxController controller = loader.getController();

            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL); // Ensures it blocks input to other windows
            stage.initOwner(overlayPane.getScene().getWindow()); // Set the main window as the owner

            Scene scene = new Scene(root);
            stage.setScene(scene);

            // Calculate the center position relative to the main window
            double centerXPosition = overlayPane.getScene().getWindow().getX() + overlayPane.getScene().getWindow().getWidth() / 2;
            double centerYPosition = overlayPane.getScene().getWindow().getY() + overlayPane.getScene().getWindow().getHeight() / 2;

            // Set the dialog position to be centered
            stage.setX(centerXPosition - root.prefWidth(-1) / 2);
            stage.setY(centerYPosition - root.prefHeight(-1) / 2);

			overlayPane.setDisable(true);
			logo.setOpacity(0.5);

			// Show the dialog and wait for it to close
			stage.showAndWait();

			// Hide the overlay pane after the dialog is closed
			overlayPane.setDisable(false);
			logo.setOpacity(1);

            String option = controller.getOption();
            if ("admin".equals(option)) {
                changePassword("Admin");
            } else if ("staff".equals(option)) {
                changePassword("Staff");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
//    private void showUserTypeDialog() {
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.setTitle("Forgot Password");
//        alert.setHeaderText("Select User Type");
//        alert.setContentText("Please choose the user type for password recovery:");
//
//        ButtonType buttonTypeAdmin = new ButtonType("Admin");
//        ButtonType buttonTypeStaff = new ButtonType("Staff");
//        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
//
//        alert.getButtonTypes().setAll(buttonTypeAdmin, buttonTypeStaff, buttonTypeCancel);
//
//        Optional<ButtonType> result = alert.showAndWait();
//        if (result.isPresent()) {
//            if (result.get() == buttonTypeAdmin) {
//                changePassword("Admin");
//            } else if (result.get() == buttonTypeStaff) {
//                changePassword("Staff");
//            }
//        }
//    }

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

        // Show the security question dialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Security Question"); // title of the 
        dialog.setHeaderText(securityQuestion);
        dialog.setContentText("Answer:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && validateSecurityAnswer(securityQuestion, result.get())) {
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
                    if (updatePassword(userType, hashedPassword)) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Password changed successfully!");
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Invalid Password", "Password does not meet the required criteria.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Password Mismatch", "Passwords do not match.");
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

    private boolean updatePassword(String userType, String hashedPassword) {
        DatabaseConnection dbConnectionInformation = new DatabaseConnection();
        String insertQuery;
        String getCurrentIdQuery;
        String setStatusQuery;
        String setCurrentStatusQuery;

        // Determine whether updating admin or staff password
        if (userType.equals("Admin")) {
            insertQuery = "INSERT INTO admin (admin_username, admin_password, admin_password_status) VALUES ('admin', ?, 'current')";
            getCurrentIdQuery = "SELECT admin_id FROM admin WHERE admin_password_status = 'current'";
            setStatusQuery = "UPDATE admin SET admin_password_status = 'old' WHERE admin_id = ?";
            setCurrentStatusQuery = "UPDATE admin SET admin_password_status = 'current' WHERE admin_password = ?";
        } else {
            insertQuery = "INSERT INTO staff (staff_username, staff_password, staff_password_status) VALUES ('staff', ?, 'current')";
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
                ResultSet rs = getCurrentIdStatement.executeQuery();
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

            // Insert the new password with status 'current'
            try (PreparedStatement insertStatement = conn.prepareStatement(insertQuery)) {
                insertStatement.setString(1, hashedPassword);
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
    
    private void logUserTrail(String username, String eventName) {
        DatabaseConnection dbConnectionInformation = new DatabaseConnection();
        String insertQuery = "INSERT INTO 828cafe.user_trail_report (unique_event_id, event_name, user, event_datetime)"
				+ "VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
                dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            String uniqueEventId = setGeneratedRandomCodeForUniqueID();
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = currentDateTime.format(formatter);

            stmt.setString(1, uniqueEventId);
            stmt.setString(2, eventName);
            stmt.setString(3, username);
            stmt.setString(4, formattedDateTime);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    String setGeneratedRandomCodeForUniqueID() {

		String itemId = null;
			PREFIX = "LOG-";
		do {
			// Generate a random numeric part
			String numericPart = generateRandomNumericPart(NUMERIC_LENGTH);

			// Combine prefix with numeric part
			itemId = PREFIX + numericPart;
		} while (isStockIDExist(itemId));
		return itemId;

	}
    
    private boolean isStockIDExist(String ID) {
		String sql = null;
		sql = "SELECT COUNT(*) AS count FROM user_trail_report WHERE unique_event_id = ?";
		
		 DatabaseConnection dbConnectionInformation = new DatabaseConnection();

        try (Connection conn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
	            dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ID); // Set the integer reference code as parameter
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt("count");
                return count > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // Default to false if any exception occurs or if no records found
    }

	private static String generateRandomNumericPart(int length) {
        Random random = new Random();
        StringBuilder numericPart = new StringBuilder();

        for (int i = 0; i < length; i++) {
            numericPart.append(random.nextInt(10)); // Append random digits (0-9)
        }

        return numericPart.toString();
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

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void logins(ActionEvent event) {
        String username = this.usernameField.getText();
        String password = this.passwordField.getText();

        // Determine user role
        String role = determineUserRole(username);
        
        if (role != null) {
            boolean isLoggedIn = authenticate(username, password, role);

            if (isLoggedIn) {
            	Main.setLoggedIn(true);
                if ("admin".equals(role)) {
                    logUserTrail(username, "Login");
                    loadMainFXML("/Admin/view/AdminMenuView.fxml");
                } else if ("staff".equals(role)) {
                    logUserTrail(username, "Login");
                    loadMainFXML("/POS/POS.fxml");
                } else {
                    // Handle other roles if needed
                }
            } else {
            	showwrongUsernameOrPasswordDialog();
                usernameField.setText("");
                passwordField.setText("");
            }
        } else {
            // Handle case where username doesn't match admin or staff
        	showwrongUsernameOrPasswordDialog();
            usernameField.setText("");
            passwordField.setText("");
        }
    }

    private String determineUserRole(String username) {
        // Check if username matches admin or staff
        if ("admin".equals(username)) {
            return "admin";
        } else if ("staff".equals(username)) {
            return "staff";
        }
        return null; // Return null if username doesn't match admin or staff
    }

    private boolean authenticate(String username, String password, String role) {
        DatabaseConnection dbConnectionInformation = new DatabaseConnection();
        String query;
        if ("admin".equals(role)) {
            query = "SELECT admin_password FROM admin WHERE admin_password_status = 'current'";
        } else if ("staff".equals(role)) {
            query = "SELECT staff_password FROM staff WHERE staff_password_status = 'current'";
        } else {
            throw new IllegalArgumentException("Unknown role: " + role);
        }

        String hashedPassword = hashPassword(password);

        try (Connection conn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
                dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            String storedHash;
            if (rs.next()) {
                if ("admin".equals(role)) {
                    storedHash = rs.getString("admin_password");

                } else {
                    storedHash = rs.getString("staff_password");

                }
                return storedHash.equals(hashedPassword);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void loadMainFXML(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) confirm.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showwrongUsernameOrPasswordDialog() {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/Security/View/WrongUsernameOrPasswordView.fxml"));
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
			logo.setOpacity(0.5);

			// Show the dialog and wait for it to close
			stage.showAndWait();

			// Hide the overlay pane after the dialog is closed
			overlayPane.setDisable(false);
			logo.setOpacity(1);

		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}