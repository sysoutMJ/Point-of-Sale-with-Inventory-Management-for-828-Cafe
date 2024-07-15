package Security.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ChooseAccountForgotPassDialogueBoxController {
	
    @FXML
    private Button cancelButton;
    
    @FXML
    private Button adminButton;
    
    @FXML
    private Button staffButton;

    private boolean confirmed = false;
    private String option;

    @FXML
    void cancel(ActionEvent event) {
        closeDialog();
    }

    @FXML
    void selectAdmin(ActionEvent event) {
        option = "admin";
        confirmed = true;
        closeDialog();
    }

    @FXML
    void selectStaff(ActionEvent event) {
        option = "staff";
        confirmed = true;
        closeDialog();
    }

    public String getOption() {
        return option;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    private void closeDialog() {
        // Get the stage from any of the buttons
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
