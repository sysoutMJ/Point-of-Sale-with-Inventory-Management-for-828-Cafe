package search.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
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
import search.model.SearchInventoryDAO;
import search.model.SearchInventoryModel;

public class SearchInventoryController {

	@FXML
	private TableView<SearchInventoryModel> inventory_table;

	@FXML
	private TableColumn<SearchInventoryModel, Integer> item_id;

	@FXML
	private TableColumn<SearchInventoryModel, String> unique_item_id;

	@FXML
	private TableColumn<SearchInventoryModel, String> item_name;

	@FXML
	private TableColumn<SearchInventoryModel, Integer> current_stock;

	@FXML
	private TableColumn<SearchInventoryModel, String> item_unit_of_measurement;

	@FXML
	private TableColumn<SearchInventoryModel, Integer> item_minimum_threshold;

	@FXML
	private TableColumn<SearchInventoryModel, Integer> item_maximum_threshold;

	@FXML
	private TableColumn<SearchInventoryModel, String> item_category;

	@FXML
	private TableColumn<SearchInventoryModel, String> item_brand;

	@FXML
	private TableColumn<SearchInventoryModel, String> item_supplier;

	@FXML
	private TableColumn<SearchInventoryModel, String> item_status;

	@FXML
	private TableColumn<SearchInventoryModel, String> item_datetime_of_registration;

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

		item_id.setCellValueFactory(new PropertyValueFactory<>("item_id"));
		unique_item_id.setCellValueFactory(new PropertyValueFactory<>("unique_item_id"));
		item_name.setCellValueFactory(new PropertyValueFactory<>("item_name"));
		current_stock.setCellValueFactory(new PropertyValueFactory<>("current_stock"));
		item_unit_of_measurement.setCellValueFactory(new PropertyValueFactory<>("item_unit_of_measurement"));
		item_minimum_threshold.setCellValueFactory(new PropertyValueFactory<>("item_minimum_threshold"));
		item_maximum_threshold.setCellValueFactory(new PropertyValueFactory<>("item_maximum_threshold"));
		item_category.setCellValueFactory(new PropertyValueFactory<>("item_category"));
		item_brand.setCellValueFactory(new PropertyValueFactory<>("item_brand"));
		item_supplier.setCellValueFactory(new PropertyValueFactory<>("item_supplier"));
		item_status.setCellValueFactory(new PropertyValueFactory<>("item_status"));
		item_datetime_of_registration.setCellValueFactory(new PropertyValueFactory<>("item_datetime_of_registration"));

		// -----------------------------------------------------------------------------------------------------------------------

		populateTable();
		adjustColumnWidths(inventory_table);

		setMenuItemsForSearchBy(searchByMenuButton);
		setMenuItemsForSortBy(sortByMenuButton);

		if (searchByMenuButton.getText().equals("Date")) {
			searchTextField.setVisible(false);
			datePicker.setVisible(true);
		} else {
			searchTextField.setVisible(true);
			datePicker.setVisible(false);
		}

		// -----------------------------------------------------------------------------------------------------------------------

	}

	// -----------------------------------------------------------------------------------------------------------------------

	// Method for populating the table.
	private void populateTable() {

		SearchInventoryDAO daoSearchInventory = new SearchInventoryDAO();

		// Fetch data from database
		List<SearchInventoryModel> items = daoSearchInventory.fetchDataFromDatabase();

		// Add data to TableView
		inventory_table.getItems().addAll(items);
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
		MenuItem itemName = new MenuItem("Item Name");
		MenuItem itemBrand = new MenuItem("Brand");
		MenuItem itemSupplier = new MenuItem("Supplier");
		MenuItem itemRegistrationDate = new MenuItem("Registration Date");
		MenuItem itemCategory = new MenuItem("Category");

		itemName.setOnAction(e -> setSelectedUnitSearchBy(menuButton, "Name"));
		itemBrand.setOnAction(e -> setSelectedUnitSearchBy(menuButton, "Brand"));
		itemSupplier.setOnAction(e -> setSelectedUnitSearchBy(menuButton, "Supplier"));
		itemRegistrationDate.setOnAction(e -> setSelectedUnitSearchBy(menuButton, "Registration Date"));
		itemCategory.setOnAction(e -> setSelectedUnitSearchBy(menuButton, "Category"));

		// Adds the menu items to the menu button.
		menuButton.getItems().addAll(itemName, itemBrand, itemSupplier, itemRegistrationDate, itemCategory);
	}

	void setMenuItemsForSortBy(MenuButton menuButton) {
		MenuItem itemName = new MenuItem("Item Name");
		MenuItem itemBrand = new MenuItem("Brand");
		MenuItem itemSupplier = new MenuItem("Supplier");
		MenuItem itemRegistrationDate = new MenuItem("Registration Date");
		MenuItem itemCategory = new MenuItem("Category");

		itemName.setOnAction(e -> sortByMenuButton.setText("Item Name"));
		itemBrand.setOnAction(e -> sortByMenuButton.setText("Brand"));
		itemSupplier.setOnAction(e -> sortByMenuButton.setText("Supplier"));
		itemRegistrationDate.setOnAction(e -> sortByMenuButton.setText("Registration Date"));
		itemCategory.setOnAction(e -> sortByMenuButton.setText("Category"));

		// Adds the menu items to the menu button.
		menuButton.getItems().addAll(itemName, itemBrand, itemSupplier, itemRegistrationDate, itemCategory);
	}

	// Method for displaying the chosen option for both of the menu buttons in the
	// UI.
	private void setSelectedUnitSearchBy(MenuButton menuButton, String unit) {
		menuButton.setText(unit);
		System.out.println("Selected unit: " + unit);

		// Handle visibility of searchTextField and datePicker
		if (unit.equals("Registration Date")) {
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
	private void showItemNotFoundDialog() {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/search/view/dialogueboxes/ItemNotFoundView.fxml"));
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
	private void showItemFoundDialog() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/search/view/dialogueboxes/ItemFoundView.fxml"));
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
	void search_inventory_item(ActionEvent event) {
		SearchInventoryDAO daoSearchInventory = new SearchInventoryDAO();
		String searchTerm = searchTextField.getText().trim();
		LocalDate selectedDate = datePicker.getValue();

		if (!searchTerm.isEmpty() || selectedDate != null) {
			String searchType = searchByMenuButton.getText();

			switch (searchType) {
			case "Name":
				List<SearchInventoryModel> itemsByName = daoSearchInventory.searchItemName(searchTerm);
				if (!itemsByName.isEmpty()) {
					showItemFoundDialog();
					updateTableView(itemsByName); // Update table view with filtered results
					sortBy();
				} else {
					showItemNotFoundDialog();
					clearFieldsAndResetTable();
				}

				break;
			case "Brand":
				List<SearchInventoryModel> itemsByBrand = daoSearchInventory.searchItemByBrand(searchTerm);
				if (!itemsByBrand.isEmpty()) {
					showItemFoundDialog();
					updateTableView(itemsByBrand); // Update table view with filtered results
					sortBy();
				} else {
					showItemNotFoundDialog();
					clearFieldsAndResetTable();
				}
				break;
			case "Supplier":
				List<SearchInventoryModel> itemsBySupplier = daoSearchInventory.searchItemBySupplier(searchTerm);
				if (!itemsBySupplier.isEmpty()) {
					showItemFoundDialog();
					updateTableView(itemsBySupplier); // Update table view with filtered results
					sortBy();
				} else {
					showItemNotFoundDialog();
					clearFieldsAndResetTable();
				}
				break;
			case "Registration Date":
				if (selectedDate != null) {
					List<SearchInventoryModel> itemsByDate = daoSearchInventory
							.searchItemByRegistrationDate(selectedDate);
					if (!itemsByDate.isEmpty()) {
						showItemFoundDialog();
						updateTableView(itemsByDate); // Update table view with filtered results
						sortBy();
					} else {
						showItemNotFoundDialog();
						clearFieldsAndResetTable();
					}
				} else {
					showBlankDatePickerDetectedDialog();
					clearFieldsAndResetTable();
				}
				break;
			case "Category":
				List<SearchInventoryModel> itemsByCategory = daoSearchInventory.searchItemByCategory(searchTerm);
				if (!itemsByCategory.isEmpty()) {
					showItemFoundDialog();
					updateTableView(itemsByCategory); // Update table view with filtered results
					sortBy();
				} else {
					showItemNotFoundDialog();
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

	private void updateTableView(List<SearchInventoryModel> items) {
		ObservableList<SearchInventoryModel> observableList = FXCollections.observableArrayList(items);
		inventory_table.setItems(observableList);
	}
	
	private void sortBy() {
		if (sortByMenuButton.getText() == "Item Name") {
			inventory_table.getSortOrder().clear();
			inventory_table.getSortOrder().add(item_name);
		}

		if (sortByMenuButton.getText() == "Brand") {
			inventory_table.getSortOrder().clear();
			inventory_table.getSortOrder().add(item_brand);
		}

		if (sortByMenuButton.getText() == "Supplier") {
			inventory_table.getSortOrder().clear();
			inventory_table.getSortOrder().add(item_supplier);
		}

		if (sortByMenuButton.getText() == "Registration Date") {
			inventory_table.getSortOrder().clear();
			inventory_table.getSortOrder().add(item_datetime_of_registration);
		}

		if (sortByMenuButton.getText() == "Category") {
			inventory_table.getSortOrder().clear();
			inventory_table.getSortOrder().add(item_category);
		}
	}

	private void clearFieldsAndResetTable() {
		searchTextField.setText("");
		searchByMenuButton.setText("");
		sortByMenuButton.setText("");
		datePicker.setValue(null);
		datePicker.setVisible(false);
		searchTextField.setVisible(true);
		inventory_table.getItems().clear();
		populateTable();
	}

	// -----------------------------------------------------------------------------------------------------------------------

	@FXML
	void goToSearchInventory() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/search/view/SearchInventoryView.fxml"));
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
	void goToSearchLogs() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/search/view/SearchLogsView.fxml"));
			Parent root = loader.load();

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
