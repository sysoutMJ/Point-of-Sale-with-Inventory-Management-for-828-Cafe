package notification;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;

public class NotificationController {
	
	public void displayNotifcationIcon(ToggleButton notificationToggleButton, ScrollPane notificationScrollPane) {
		NotificationDAO daoNotifcation = new NotificationDAO();

		boolean hasAlerts = daoNotifcation.hasStockAlerts(); // Check if there are stock alerts

		if (hasAlerts) {
			if (notificationToggleButton.isSelected()) {
				notificationToggleButton.setStyle("-fx-background-color: #F44B3E;");
				notificationScrollPane.setVisible(true);
				notificationToggleButton.setGraphic(new ImageView("/images/topmenubar/White_Back.png") {
					{
						setFitWidth(50);
						setFitHeight(50);
					}
				});
			} else {
				notificationToggleButton.setStyle("-fx-background-color: #F44B3E;");
				notificationScrollPane.setVisible(false);
				notificationToggleButton.setGraphic(new ImageView("/images/topmenubar/White_Notification.png") {
					{
						setFitWidth(50);
						setFitHeight(50);
					}
				});
			}
		} else {
			if (notificationToggleButton.isSelected()) {
				notificationToggleButton.setStyle("fx-background-color: #A2A2A2;");
				notificationScrollPane.setVisible(true);
				notificationToggleButton.setGraphic(new ImageView("/images/topmenubar/Back.png") {
					{
						setFitWidth(50);
						setFitHeight(50);
					}
				});
			} else {
				notificationScrollPane.setVisible(false);
				notificationToggleButton.setStyle("fx-background-color: #A2A2A2;");
				notificationToggleButton.setGraphic(new ImageView("/images/topmenubar/Notifications.png") {
					{
						setFitWidth(50);
						setFitHeight(50);
					}
				});
			}
		}

	}
}
