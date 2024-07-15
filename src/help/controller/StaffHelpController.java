package help.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import inventorymanagement.model.RegisterItemDAO;

public class StaffHelpController implements Initializable {

    @FXML
    private Pane overlayPane;

    @FXML
    private VBox menuBar;

    @FXML
    private Button userManual;
    @FXML
    private Button logoutButton;
    @FXML
    private Button POSButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize any necessary components or data here
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
			Stage stage = (Stage) logoutButton.getScene().getWindow();
			Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	@FXML
	void goToPOS(ActionEvent event) {
		try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/POS/POS.fxml"));
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
    private void userMan() {
    	// Replace with your actual PDF file path
        String pdfFilePath = "C:\\Users\\Danielle\\Desktop\\NEW\\MAAAAAAARCUUUUUUUUUS\\SE 2\\src\\help\\controller\\Staff Manual.pdf";
        
        try {
            File file = new File(pdfFilePath);
            if (file.exists()) {
                Desktop.getDesktop().open(file);
            } else {
                System.out.println("File does not exist.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
