package org.example.cafe828rebuild;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainController {

    @FXML
    private VBox sideBarVBoxContainer;

    @FXML
    private StackPane mainStackPaneContainer;

    void hideSideBarVBoxContainer(){
        sideBarVBoxContainer.setVisible(false);
        sideBarVBoxContainer.setManaged(false);
    }

    public void initialize(){
        System.out.println("MainController initialized!");
        hideSideBarVBoxContainer();
    }

}
