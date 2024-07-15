package report;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class lala extends Application {
	public void start(Stage primaryStage) {
		try {
			
			Font.loadFont(getClass().getResourceAsStream("/Fonts/Angkor-Regular.ttf"), 14);
			Font.loadFont(getClass().getResourceAsStream("/Fonts/Barlow-Bold.ttf"), 14);
			Font.loadFont(getClass().getResourceAsStream("/Fonts/Barlow-Regular.ttf"), 14);
			Font.loadFont(getClass().getResourceAsStream("/Fonts/NotoSans-Bold.ttf"), 14);
			
			Parent root = FXMLLoader.load(getClass().getResource("/report/view/ReportLandingPageView.fxml"));
			Scene scene = new Scene(root);
			
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
