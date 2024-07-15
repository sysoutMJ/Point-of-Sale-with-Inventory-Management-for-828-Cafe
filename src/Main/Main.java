package Main;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;

public class Main extends Application {
	
	private static boolean isLoggedIn = false;
	@Override
	public void start(Stage primaryStage) {
		try {
			
			Font.loadFont(getClass().getResourceAsStream("/Fonts/Angkor-Regular.ttf"), 14);
			Font.loadFont(getClass().getResourceAsStream("/Fonts/Barlow-Bold.ttf"), 14);
			Font.loadFont(getClass().getResourceAsStream("/Fonts/Barlow-Regular.ttf"), 14);
			Font.loadFont(getClass().getResourceAsStream("/Fonts/NotoSans-Bold.ttf"), 14);
			
			Parent root = FXMLLoader.load(getClass().getResource("/Security/View/V_Login.fxml"));
			Scene scene = new Scene(root);
			
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (isLoggedIn) {
                try {
                    backupDatabase();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }));
    }
	
	private void backupDatabase() throws IOException, InterruptedException {
        // Set the backup file path with a timestamp
        String backupDirectory = "C:/Backup"; // Change to your backup directory
        String databaseName = "828cafe";
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String backupFilePath = backupDirectory + "/" + databaseName + "_backup_" + timestamp + ".sql";

        // Execute mysqldump command to backup the database
        String[] command = new String[]{
        		 "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump.exe", // Adjust the path to your mysqldump
                "-u", "828Cafe", // Replace with your MySQL username
                "-p828Cafe!", // Replace with your MySQL password
                databaseName,
                "-r", backupFilePath
        };

        Process process = Runtime.getRuntime().exec(command);
        int exitCode = process.waitFor();

        if (exitCode == 0) {
            System.out.println("Backup created successfully.");
        } else {
            System.out.println("Error: Backup creation failed.");
        }
    }
	
	public static void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

	public static void main(String[] args) {
		launch(args);
		System.out.println("Running");
	}
}
