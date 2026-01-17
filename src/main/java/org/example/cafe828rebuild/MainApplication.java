package org.example.cafe828rebuild;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        Font.loadFont(Launcher.class.getResourceAsStream("/org/example/cafe828rebuild/fonts/Angkor-Regular.ttf"), 12);
        Font.loadFont(Launcher.class.getResourceAsStream("/org/example/cafe828rebuild/fonts/Barlow-Bold.ttf"), 12);
        Font.loadFont(Launcher.class.getResourceAsStream("/org/example/cafe828rebuild/fonts/Barlow-Regular.ttf"), 12);
        Font.loadFont(Launcher.class.getResourceAsStream("/org/example/cafe828rebuild/fonts/NotoSans-Bold.ttf"), 12);

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-application.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setTitle("828Cafe");

        stage.setScene(scene);
        stage.show();
    }
}
