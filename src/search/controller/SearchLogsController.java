package search.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import inventorymanagement.model.RegisterItemDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import notification.NotificationController;
import notification.NotificationDAO;
import search.model.SearchLogsDAO;
import search.model.SearchLogsModel;

public class SearchLogsController {

	@FXML
	private TableView<SearchLogsModel> logs_table;

	@FXML
	private TableColumn<SearchLogsModel, Integer> event_id;

	@FXML
	private TableColumn<SearchLogsModel, String> unique_event_id;

	@FXML
	private TableColumn<SearchLogsModel, String> event_name;

	@FXML
	private TableColumn<SearchLogsModel, String> user;

	@FXML
	private TableColumn<SearchLogsModel, LocalDateTime> event_datetime;

	@FXML
	private Button goToSearchInventoryButton;

	@FXML
	private Button goToSearchLogsButton;

	@FXML
	private TextField searchTextField;

	@FXML
	private DatePicker datePicker;

	@FXML
	private MenuButton searchByMenuButton;

	@FXML
	private MenuButton sortByMenuButton;

	@FXML
	private Button checkButton;

	// -------------------------------
	// Side menu bar

	@FXML
	private Button registerButton;

	@FXML
	private Button homeButton;

	@FXML
	private Button searchButton;

	@FXML
	private Button maintenanceButton;

	@FXML
	private Button securityButton;

	@FXML
	private Button registerItemButton;

	@FXML
	private Button reportButton;

	@FXML
	private Button helpButton;

	@FXML
	private Button aboutButton;

	@FXML
	private Button logoutButton;

	@FXML
	private ScrollPane tableScrollPane;

	@FXML
	private Pane overlayPane;

	@FXML
	private VBox notificationContainer;

	// ---------------------------------------------------

	// The final length for unique item code of inventory items.
	final int uniqueItemIdLength = 15;

	// ------------------------------------------------------------------------------------------------------------------------------------
	// For Notification button located in the right side. Variables are putted here
	// for easy modifying and distribution to other modules

	@FXML
	private ToggleButton notificationToggleButton;

	@FXML
	private ScrollPane notificationScrollPane;

	@FXML
	private Label notificationLabel;

	NotificationController controllerNotification = new NotificationController();

	void updateLookOfNotification() {
		controllerNotification.displayNotifcationIcon(notificationToggleButton, notificationScrollPane);
		;
	}

	@FXML
	void getNotification(ActionEvent event) {
		updateLookOfNotification();
	}

	@FXML
	public void initialize() {

		updateLookOfNotification();
		notificationScrollPane.setVisible(false);
		notificationLabel.setText("");

		NotificationDAO daoNotification = new NotificationDAO();
		StringBuilder sb = new StringBuilder();

		sb.append(daoNotification.getStockAlertMessages());

		// Update the notification label
		notificationLabel.setText(sb.toString());

		notificationScrollPane.setVisible(false);

		// END FOR NOTIF
		// -------------------------------------------------------------------------------------------------------------------

		// Sets up the columns to display the values from the database.
		//
		// It uses the property names from the class that was provided, which is the
		// Model,
		// to fetch and display the corresponding values in the table columns.
		//

		event_id.setCellValueFactory(new PropertyValueFactory<>("eventId"));
		unique_event_id.setCellValueFactory(new PropertyValueFactory<>("uniqueEventId"));
		event_name.setCellValueFactory(new PropertyValueFactory<>("eventName"));
		user.setCellValueFactory(new PropertyValueFactory<>("user"));
		event_datetime.setCellValueFactory(new PropertyValueFactory<>("eventDatetime"));

		// -----------------------------------------------------------------------------------------------------------------------

		populateTable();
		adjustColumnWidths(logs_table);

		setMenuItemsForSearchBy(searchByMenuButton);
		setMenuItemsForSortBy(sortByMenuButton);

		if (searchByMenuButton.getText().equals("Date")) {
			searchTextField.setVisible(false);
			datePicker.setVisible(true);
		} else {
			searchTextField.setVisible(true);
			datePicker.setVisible(false);
		}

	}

	// -----------------------------------------------------------------------------------------------------------------------

	// Method for populating the table.
	private void populateTable() {

		SearchLogsDAO daoSearchLogs = new SearchLogsDAO();

		// Fetch data from database
		List<SearchLogsModel> items = daoSearchLogs.fetchDataFromDatabase();

		// Add data to TableView
		logs_table.getItems().addAll(items);
	}

	// -----------------------------------------------------------------------------------------------------------------------

	// Method for adjusting the column widths based on its data.
	private void adjustColumnWidths(TableView<?> table) {
		// Set the right policy
		table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		table.getColumns().stream().forEach((column) -> {
			// Minimal width = columnheader
			Text t = new Text(column.getText());
			double max = t.getLayoutBounds().getWidth();
			for (int i = 0; i < table.getItems().size(); i++) {
				// cell must not be empty
				if (column.getCellData(i) != null) {
					t = new Text(column.getCellData(i).toString());
					double calcwidth = t.getLayoutBounds().getWidth();
					// remember new max-width
					if (calcwidth > max) {
						max = calcwidth;
					}
				}
			}
			// set the new max-widht with some extra space
			column.setPrefWidth(max + 10.0d);
		});
	}

	// -----------------------------------------------------------------------------------------------------------------------

	// Method for setting the menu items for the menu button, Unit of Measurements.
	void setMenuItemsForSearchBy(MenuButton menuButton) {
		MenuItem date = new MenuItem("Date");
		MenuItem user = new MenuItem("User");
		MenuItem action = new MenuItem("Action");

		date.setOnAction(e -> setSelectedUnitSearchBy(menuButton, "Date"));
		user.setOnAction(e -> setSelectedUnitSearchBy(menuButton, "User"));
		action.setOnAction(e -> setSelectedUnitSearchBy(menuButton, "Action"));

		// Adds the menu items to the menu button.
		menuButton.getItems().addAll(date, user, action);
	}

	void setMenuItemsForSortBy(MenuButton menuButton) {
		MenuItem date = new MenuItem("Date");
		MenuItem user = new MenuItem("User");

		date.setOnAction(e -> sortByMenuButton.setText("Date"));
		user.setOnAction(e -> sortByMenuButton.setText("User"));

		// Adds the menu items to the menu button.
		menuButton.getItems().addAll(date, user);
	}

	// Method for displaying the chosen option for both of the menu buttons in the
	// UI.
	private void setSelectedUnitSearchBy(MenuButton menuButton, String unit) {
		menuButton.setText(unit);
		System.out.println("Selected unit: " + unit);

		// Handle visibility of searchTextField and datePicker
		if (unit.equals("Date")) {
			searchTextField.setVisible(false);
			datePicker.setVisible(true);
		} else {
			searchTextField.setVisible(true);
			datePicker.setVisible(false);
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------

	// Dialogue Boxes

	// Show Item not found dialog.
	private void showLogNotFoundDialog() {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/search/view/dialogueboxes/LogNotFoundView.fxml"));
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

			// Show the dialog and wait for it to close
			stage.showAndWait();

			// Hide the overlay pane after the dialog is closed
			overlayPane.setDisable(false);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void showSelectSearchByDialog() {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/search/view/dialogueboxes/SelectSearchByView.fxml"));
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

			// Show the dialog and wait for it to close
			stage.showAndWait();

			// Hide the overlay pane after the dialog is closed
			overlayPane.setDisable(false);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Show Item found
	private void showLogFoundDialog() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/search/view/dialogueboxes/LogFoundView.fxml"));
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

			// Show the dialog and wait for it to close
			stage.showAndWait();

			// Hide the overlay pane after the dialog is closed
			overlayPane.setDisable(false);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void showBlankDatePickerDetectedDialog() {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/search/view/dialogueboxes/SearchFieldBlankView.fxml"));
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

			overlayPane.setDisable(true);

			// Show the dialog and wait for it to close
			stage.showAndWait();

			overlayPane.setDisable(false);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Shows the blank text field detected error dialog.
	private void showBlankTextFieldsDialog() {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/search/view/dialogueboxes/SearchFieldBlankView.fxml"));
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

			overlayPane.setDisable(true);

			// Show the dialog and wait for it to close
			stage.showAndWait();

			overlayPane.setDisable(false);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------

	@FXML
	void search_logs(ActionEvent event) {
		SearchLogsDAO daoSearchLogs = new SearchLogsDAO();
		String searchTerm = searchTextField.getText().trim();
		LocalDate selectedDate = datePicker.getValue();

		if (!searchTerm.isEmpty() || selectedDate != null) {
			String searchType = searchByMenuButton.getText();

			if (sortByMenuButton.getText() == "Date") {
				logs_table.getSortOrder().clear();
				logs_table.getSortOrder().add(event_datetime);
			}

			if (sortByMenuButton.getText() == "User") {
				logs_table.getSortOrder().clear();
				logs_table.getSortOrder().add(user);
			}

			switch (searchType) {
			case "Date":
				if (selectedDate != null) {
					List<SearchLogsModel> itemsByDate = daoSearchLogs.searchLogsByDate(selectedDate);
					if (!itemsByDate.isEmpty()) {
						showLogFoundDialog();
						updateTableView(itemsByDate); // Update table view with filtered results
					} else {
						showLogNotFoundDialog();
						clearFieldsAndResetTable();
					}
				} else {
					showBlankDatePickerDetectedDialog();
					clearFieldsAndResetTable();
				}
				break;
			case "User":
				List<SearchLogsModel> itemsByUser = daoSearchLogs.searchLogsByUser(searchTerm);
				if (!itemsByUser.isEmpty()) {
					showLogFoundDialog();
					updateTableView(itemsByUser); // Update table view with filtered results
				} else {
					showLogNotFoundDialog();
					clearFieldsAndResetTable();
				}
				break;
			case "Action":
				List<SearchLogsModel> itemsByAction = daoSearchLogs.searchLogsByAction(searchTerm);
				if (!itemsByAction.isEmpty()) {
					showLogFoundDialog();
					updateTableView(itemsByAction); // Update table view with filtered results
				} else {
					showLogNotFoundDialog();
					clearFieldsAndResetTable();
				}
				break;
			default:
				showSelectSearchByDialog();
				clearFieldsAndResetTable();
				break;
			}
		} else {
			showBlankTextFieldsDialog();
			clearFieldsAndResetTable();
		}
	}

	private void updateTableView(List<SearchLogsModel> items) {
		ObservableList<SearchLogsModel> observableList = FXCollections.observableArrayList(items);
		logs_table.setItems(observableList);
	}

	private void clearFieldsAndResetTable() {
		searchTextField.setText("");
		searchByMenuButton.setText("");
		sortByMenuButton.setText("");
		datePicker.setValue(null);
		datePicker.setVisible(false);
		searchTextField.setVisible(true);
		logs_table.getItems().clear();
		populateTable();
	}

	// -----------------------------------------------------------------------------------------------------------------------

	@FXML
	void goToSearchInventory() {
		try {
			// Load Maintenance.fxml
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/search/view/SearchInventoryView.fxml"));
			Parent root = loader.load();

			// Get the controller for Maintenance.fxml if needed
			// C_Maintenance maintenanceController = loader.getController();

			// Access the current scene and set the new root
			Stage stage = (Stage) maintenanceButton.getScene().getWindow();
			Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void goToSearchLogs() {
		try {
			// Load Maintenance.fxml
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/search/view/SearchLogsView.fxml"));
			Parent root = loader.load();

			// Get the controller for Maintenance.fxml if needed
			// C_Maintenance maintenanceController = loader.getController();

			// Access the current scene and set the new root
			Stage stage = (Stage) maintenanceButton.getScene().getWindow();
			Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------

	// SIDE BAR MENU METHODS

	@FXML
	void goToHome(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/Admin/view/AdminMenuView.fxml"));
			Parent root = loader.load();

			Stage stage = (Stage) maintenanceButton.getScene().getWindow();
			Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@FXML
	void goToSearch(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/search/view/SearchLandingPageView.fxml"));
			Parent root = loader.load();

			Stage stage = (Stage) maintenanceButton.getScene().getWindow();
			Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void goToRegisterItem(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/inventorymanagement/view/RegisterItemView.fxml"));
			Parent root = loader.load();

			Stage stage = (Stage) maintenanceButton.getScene().getWindow();
			Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void goToMaintenance(ActionEvent event) {
		try {
			// Load Maintenance.fxml
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/maintenance/landingpage/view/LandingPageView.fxml"));
			Parent root = loader.load();

			// Get the controller for Maintenance.fxml if needed
			// C_Maintenance maintenanceController = loader.getController();

			// Access the current scene and set the new root
			Stage stage = (Stage) maintenanceButton.getScene().getWindow();
			Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void goToSecurity(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/Security/changepassword/view/ChangePassword.fxml"));
			Parent root = loader.load();

			Stage stage = (Stage) maintenanceButton.getScene().getWindow();
			Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@FXML
	void goToReport(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/report/view/StockReportView.fxml"));
			Parent root = loader.load();

			Stage stage = (Stage) maintenanceButton.getScene().getWindow();
			Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@FXML
	void goToHelp(ActionEvent event) {
		try {
			// Load Maintenance.fxml
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/help/view/HelpLandingPageView.fxml"));
			Parent root = loader.load();

			// Get the controller for Maintenance.fxml if needed
			// C_Maintenance maintenanceController = loader.getController();

			// Access the current scene and set the new root
			Stage stage = (Stage) maintenanceButton.getScene().getWindow();
			Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void goToAbout(ActionEvent event) {

		try {
			// Load Maintenance.fxml
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/about/view/AboutAdmin.fxml"));
			Parent root = loader.load();

			// Get the controller for Maintenance.fxml if needed
			// C_Maintenance maintenanceController = loader.getController();

			// Access the current scene and set the new root
			Stage stage = (Stage) maintenanceButton.getScene().getWindow();
			Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void goToLogout(ActionEvent event) {
		RegisterItemDAO daoRegisterItem = new RegisterItemDAO();
		try {
			// Load Maintenance.fxml
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/Security/View/V_Login.fxml"));
			Parent root = loader.load();

			// Get the controller for Maintenance.fxml if needed
			// C_Maintenance maintenanceController = loader.getController();
			daoRegisterItem.logUserTrail("Admin", "Logout");
			// Access the current scene and set the new root
			Stage stage = (Stage) maintenanceButton.getScene().getWindow();
			Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------

}
