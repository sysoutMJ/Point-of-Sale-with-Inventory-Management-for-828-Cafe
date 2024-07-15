package maintenance.setitemstatus.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class DialogueBoxesController {
	
    @FXML
    private Button cancelButton;

    @FXML
    private Button confirmButton;

    private boolean confirmed = false;
    
    @FXML
    void cancel(ActionEvent event) {
    	closeDialog();
    }

    @FXML
    void confirm(ActionEvent event) {
        confirmed = true;
        closeDialog();
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public void closeDialog() {
        // Get the stage from any of the buttons
        Stage stage = (Stage) confirmButton.getScene().getWindow();
        stage.close();
    }
    
}
