package notification.controller;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ShowNotifiactionView extends Application {
	
	private ToggleButton notificationToggleButton;
    private ScrollPane notificationScrollPane;
    private VBox notificationContainer;

    @Override
    public void start(Stage primaryStage) {
        // Initialize the ToggleButton
        notificationToggleButton = new ToggleButton();
        notificationToggleButton.setLayoutX(1814.0);
        notificationToggleButton.setLayoutY(56.0);
        notificationToggleButton.setPrefHeight(75.0);
        notificationToggleButton.setPrefWidth(75.0);
        notificationToggleButton.setOnAction(event -> getNotification());

        // Set the graphic for the ToggleButton
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("../../images/topmenubar/Notifications.png")));
        imageView.setFitHeight(75.0);
        imageView.setFitWidth(50.0);
        imageView.setPickOnBounds(true);
        imageView.setPreserveRatio(true);
        notificationToggleButton.setGraphic(imageView);

        // Apply stylesheets to the ToggleButton
        notificationToggleButton.getStylesheets().add(getClass().getResource("../../top-menu-buttons.css").toExternalForm());

        // Initialize the ScrollPane
        notificationScrollPane = new ScrollPane();
        notificationScrollPane.setLayoutX(272.0);
        notificationScrollPane.setLayoutY(56.0);
        notificationScrollPane.setPrefHeight(945.0);
        notificationScrollPane.setPrefWidth(1541.0);

        // Initialize the VBox
        notificationContainer = new VBox();
        notificationContainer.setLayoutX(272.0);
        notificationContainer.setLayoutY(56.0);
        notificationContainer.setPrefHeight(945.0);
        notificationContainer.setPrefWidth(1526.0);
        notificationContainer.setSpacing(30.0);
        notificationContainer.setPadding(new Insets(40.0));

        // Apply stylesheets to the VBox
        notificationContainer.getStylesheets().add(getClass().getResource("../../notification.css").toExternalForm());

        // Set the VBox as the content of the ScrollPane
        notificationScrollPane.setContent(notificationContainer);

        // Create the root layout and add the ToggleButton and ScrollPane to it
        VBox root = new VBox();
        root.getChildren().addAll(notificationToggleButton, notificationScrollPane);

        // Create the Scene and set it on the primary Stage
        Scene scene = new Scene(root, 1920, 1080);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Notification Page");
        primaryStage.show();
    }
    
    // Method to handle the notification toggle button action
    private void getNotification() {
        // Insert logic to handle notifications here
        // For example, check for low stock and add children to notificationContainer
    }
    
}
