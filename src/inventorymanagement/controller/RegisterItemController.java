package inventorymanagement.controller;

import inventorymanagement.model.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

import database.DatabaseConnection;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import notification.NotificationController;
import notification.NotificationDAO;

public class RegisterItemController {

	@FXML
	private TableView<RegisterItemInventoryModel> inventory_table;

	@FXML
	private TableColumn<RegisterItemInventoryModel, Integer> item_id;

	@FXML
	private TableColumn<RegisterItemInventoryModel, String> unique_item_id;

	@FXML
	private TableColumn<RegisterItemInventoryModel, String> item_name;

	@FXML
	private TableColumn<RegisterItemInventoryModel, Double> current_stock;

	@FXML
	private TableColumn<RegisterItemInventoryModel, String> item_unit_of_measurement;

	@FXML
	private TableColumn<RegisterItemInventoryModel, Integer> item_minimum_threshold;

	@FXML
	private TableColumn<RegisterItemInventoryModel, Integer> item_maximum_threshold;

	@FXML
	private TableColumn<RegisterItemInventoryModel, String> item_category;

	@FXML
	private TableColumn<RegisterItemInventoryModel, String> item_brand;

	@FXML
	private TableColumn<RegisterItemInventoryModel, String> item_supplier;

	@FXML
	private TableColumn<RegisterItemInventoryModel, String> item_status;

	@FXML
	private TableColumn<RegisterItemInventoryModel, String> item_datetime_of_registration;

	@FXML
	private TextField autoGenerateItemIDTextField;

	@FXML
	private TextField itemNameTextField;

	@FXML
	private TextField itemBrandTextField;

	@FXML
	private TextField itemSupplierTextField;

	@FXML
	private TextField minimumThresholdCapacityTextField;

	@FXML
	private TextField maximumThresholdCapacityTextField;

	@FXML
	private MenuButton itemCategoryMenuButton;

	@FXML
	private MenuButton unitOfMeasurementMenuButton;

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

	private static String PREFIX = null;
	private static final int NUMERIC_LENGTH = 10; // Length of the numeric part

	// The final length for unique item code of inventory items.
	final int uniqueItemIdLength = 15;

	private ScheduledService<Void> tableRefreshService;

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

		// --------------------------------------------------------------------

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
		setMenuItemsForUnitOfMeasurement(unitOfMeasurementMenuButton);
		setMenuItemsForCategories(itemCategoryMenuButton);
		setGeneratedRandomCodeToAutoGenerateItemIDTextfield(uniqueItemIdLength);
		adjustColumnWidths(inventory_table);

		// -----------------------------------------------------------------------------------------------------------------------

		// Initialize scheduled service to refresh table periodically
		tableRefreshService = new ScheduledService<>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<>() {
					@Override
					protected Void call() throws Exception {
						Platform.runLater(() -> {
							refreshTable();
						});
						return null;
					}
				};
			}
		};

		tableRefreshService.setPeriod(Duration.seconds(5)); // Set refresh period (e.g., every minute)
		tableRefreshService.start();

		// -----------------------------------------------------------------------------------------------------------------------
	}

	// -----------------------------------------------------------------------------------------------------------------------

	// Method to refresh table data
	private void refreshTable() {
		inventory_table.getItems().clear(); // Clear existing items
		populateTable(); // Reload data from the database
	}

	// -----------------------------------------------------------------------------------------------------------------------

	// Method for populating the table.
	private void populateTable() {

		RegisterItemDAO daoRegisterItem = new RegisterItemDAO();

		// Fetch data from database
		List<RegisterItemInventoryModel> items = daoRegisterItem.fetchDataFromDatabase();

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
	void setMenuItemsForUnitOfMeasurement(MenuButton menuButton) {
//		MenuItem ml = new MenuItem("Milliliters (ml)");
		MenuItem L = new MenuItem("Liters (L)");
//		MenuItem floz = new MenuItem("Fluid Ounces (fl oz)");
//		MenuItem cups = new MenuItem("Cups");
//		MenuItem pints = new MenuItem("Pints");
//		MenuItem quarts = new MenuItem("Quarts");
//		MenuItem gallons = new MenuItem("Gallons");

//		MenuItem g = new MenuItem("Grams (g)");
		MenuItem kg = new MenuItem("Kilograms (kg)");

		MenuItem pcs = new MenuItem("Pieces (pcs)");
//		MenuItem packets = new MenuItem("Packets");
//		MenuItem slices = new MenuItem("Slices");
//		MenuItem dozens = new MenuItem("Dozens");

//		ml.setOnAction(e -> setSelectedUnit(menuButton, "Milliliters (ml)"));
		L.setOnAction(e -> setSelectedUnit(menuButton, "Liters (L)"));
//		floz.setOnAction(e -> setSelectedUnit(menuButton, "Fluid Ounces (fl oz)"));
//		cups.setOnAction(e -> setSelectedUnit(menuButton, "Cups"));
//		pints.setOnAction(e -> setSelectedUnit(menuButton, "Pints"));
//		quarts.setOnAction(e -> setSelectedUnit(menuButton, "Quarts"));
//		gallons.setOnAction(e -> setSelectedUnit(menuButton, "Gallons"));

//		g.setOnAction(e -> setSelectedUnit(menuButton, "Grams (g)"));
		kg.setOnAction(e -> setSelectedUnit(menuButton, "Kilograms (kg)"));
//		oz.setOnAction(e -> setSelectedUnit(menuButton, "Ounces (oz)"));
//		lb.setOnAction(e -> setSelectedUnit(menuButton, "Pounds (lb)"));

		pcs.setOnAction(e -> setSelectedUnit(menuButton, "Pieces (pcs)"));
//		packets.setOnAction(e -> setSelectedUnit(menuButton, "Packets"));
//		slices.setOnAction(e -> setSelectedUnit(menuButton, "Slices"));
//		dozens.setOnAction(e -> setSelectedUnit(menuButton, "Dozens"));

		// Adds the menu items to the menu button.
		menuButton.getItems().addAll(L, kg, pcs);
	}

	// -----------------------------------------------------------------------------------------------------------------------

	// Method for setting the categories for the menu button, Item Category.
	void setMenuItemsForCategories(MenuButton menuButton) {
		MenuItem bakeryIngredients = new MenuItem("Bakery Ingredients");
		MenuItem coffee = new MenuItem("Coffee");
		MenuItem cereals = new MenuItem("Cereals");
		MenuItem dairy = new MenuItem("Dairy");
		MenuItem eggs = new MenuItem("Eggs");
		MenuItem fish = new MenuItem("Fish");
		MenuItem freshFruits = new MenuItem("Fresh Fruits");
		MenuItem grains = new MenuItem("Grains");
		MenuItem meat = new MenuItem("Meat");
		MenuItem nonDairy = new MenuItem("Non-Dairy");
		MenuItem oils = new MenuItem("Oils");
		MenuItem spices = new MenuItem("Spices");
		MenuItem syrups = new MenuItem("Syrups");
		MenuItem sweeteners = new MenuItem("Sweeteners");
		MenuItem tea = new MenuItem("Tea");
		MenuItem vegetables = new MenuItem("Vegetables");
		MenuItem vinegars = new MenuItem("Vinegars");

		// Add event handlers to set the selected category
		bakeryIngredients.setOnAction(e -> setSelectedUnit(menuButton, "Bakery Ingredients"));
		coffee.setOnAction(e -> setSelectedUnit(menuButton, "Coffee"));
		cereals.setOnAction(e -> setSelectedUnit(menuButton, "Cereals"));
		dairy.setOnAction(e -> setSelectedUnit(menuButton, "Dairy"));
		eggs.setOnAction(e -> setSelectedUnit(menuButton, "Eggs"));
		fish.setOnAction(e -> setSelectedUnit(menuButton, "Fish"));
		freshFruits.setOnAction(e -> setSelectedUnit(menuButton, "Fresh Fruits"));
		grains.setOnAction(e -> setSelectedUnit(menuButton, "Grains"));
		meat.setOnAction(e -> setSelectedUnit(menuButton, "Meat"));
		nonDairy.setOnAction(e -> setSelectedUnit(menuButton, "Non-Dairy"));
		oils.setOnAction(e -> setSelectedUnit(menuButton, "Oils"));
		spices.setOnAction(e -> setSelectedUnit(menuButton, "Spices"));
		syrups.setOnAction(e -> setSelectedUnit(menuButton, "Syrups"));
		sweeteners.setOnAction(e -> setSelectedUnit(menuButton, "Sweeteners"));
		tea.setOnAction(e -> setSelectedUnit(menuButton, "Tea"));
		vegetables.setOnAction(e -> setSelectedUnit(menuButton, "Vegetables"));
		vinegars.setOnAction(e -> setSelectedUnit(menuButton, "Vinegars"));

		// Add menu items to the MenuButton
		menuButton.getItems().addAll(bakeryIngredients, coffee, cereals, dairy, eggs, fish, freshFruits, grains, meat,
				nonDairy, oils, spices, syrups, sweeteners, tea, vegetables, vinegars);

	}

	// -----------------------------------------------------------------------------------------------------------------------

	// Method for displaying the chosen option for both of the menu buttons in the
	// UI.
	private String setSelectedUnit(MenuButton menuButton, String unit) {
		menuButton.setText(unit);
		System.out.println("Selected unit: " + unit);
		return unit;
	}

	// -----------------------------------------------------------------------------------------------------------------------

	// Method that checks if the fields are empty.
	private boolean areTextFieldNotEmpty() {
		return !autoGenerateItemIDTextField.getText().isEmpty() && !itemNameTextField.getText().isEmpty()
				&& !itemBrandTextField.getText().isEmpty() && !itemSupplierTextField.getText().isEmpty()
				&& !itemCategoryMenuButton.getText().isEmpty() && !unitOfMeasurementMenuButton.getText().isEmpty();
	}

	// -----------------------------------------------------------------------------------------------------------------------

	// Method for clearing all the textfields in the UI.
	private void clearFields() {
		autoGenerateItemIDTextField.setText("");
		itemNameTextField.setText("");
		unitOfMeasurementMenuButton.setText("");
		itemCategoryMenuButton.setText("");
		itemBrandTextField.setText("");
		itemSupplierTextField.setText("");
		minimumThresholdCapacityTextField.setText("");
		maximumThresholdCapacityTextField.setText("");
	}

	// -----------------------------------------------------------------------------------------------------------------------

	// Dialogue Boxes

	// Shows the confirm register dialog.
	private void showConfirmRegisterItemDialog() {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/inventorymanagement/view/dialogueboxes/ConfirmRegisterItemView.fxml"));
			Parent root = loader.load();

			DialogueBoxesController confirmRegisterController = loader.getController();

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

			if (confirmRegisterController.isConfirmed()) {
				registerItemToDatabase();

			} else {
				System.out.println("Registration canceled.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// Shows the blank text field detected error dialog.
	private void showBlankTextFieldsDialog() {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/inventorymanagement/view/dialogueboxes/BlankTextFieldDetectedView.fxml"));
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

	// Shows the item succesffully registered dialog.
	private void showItemSuccessFullyRegisteredDialog() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass()
					.getResource("/inventorymanagement/view/dialogueboxes/ItemSuccessfullyRegisteredView.fxml"));
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
			overlayPane.setStyle("-fx-opacity: 0.5;");
			overlayPane.setVisible(true);

			// Show the dialog and wait for it to close
			stage.showAndWait();

			// Hide the overlay pane after the dialog is closed
			overlayPane.setStyle("-fx-opacity: 1;");
			overlayPane.setVisible(true);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Shows the Duplicated detected dialog.
	private void showDuplicateDetectedDialog() {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/inventorymanagement/view/dialogueboxes/DuplicateDetectedView.fxml"));
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

	// -----------------------------------------------------------------------------------------------------------------------

	// Method that checks if there is duplicate in the database
	private boolean isDuplicateItem() {
		RegisterItemDAO daoRegisterItem = new RegisterItemDAO();

		String itemName = itemNameTextField.getText();
		String itemBrand = itemBrandTextField.getText();
		String itemSupplier = itemSupplierTextField.getText();
		String itemCategory = itemCategoryMenuButton.getText();

		try {
			return daoRegisterItem.isItemExists(itemName, itemBrand, itemSupplier, itemCategory);
		} catch (SQLException e) {
			e.printStackTrace();
			return false; // Handle exception accordingly
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------

	// The method for registering the item in to the DATABASE.
	@FXML
	void registerItem(ActionEvent event) {
		updateLookOfNotification();

		// If true, show blank textfied dialog. Else proceed.
		if (areTextFieldNotEmpty()) {

			// If true, show duplicate detected. Else process inserting.
			if (isDuplicateItem()) {
				showDuplicateDetectedDialog();
			} else {
				showConfirmRegisterItemDialog();
			}

		} else {
			showBlankTextFieldsDialog();
		}
	}

	void registerItemToDatabase() {
		RegisterItemDAO daoRegisterItem = new RegisterItemDAO();
		RegisterItemInventoryModel newItem = new RegisterItemInventoryModel();

		// -------------------------------------------------------------------------

		// Get the data from the text fields.
		
		if (isNumeric(minimumThresholdCapacityTextField.getText()) && isNumeric(maximumThresholdCapacityTextField.getText())) {

		newItem.setUnique_item_id(autoGenerateItemIDTextField.getText()); // Auto Generated.
		newItem.setItem_name(itemNameTextField.getText());

		// Automatically set to 0.
		// This will be updated later on in the re-stock if the user wishes to re-stock
		// immediately.
		
		newItem.setCurrent_stock(0.00);

		newItem.setItem_unit_of_measurement(unitOfMeasurementMenuButton.getText());
		newItem.setItem_minimum_threshold(Integer.parseInt(minimumThresholdCapacityTextField.getText()));
		newItem.setItem_maximum_threshold(Integer.parseInt(maximumThresholdCapacityTextField.getText()));
		newItem.setItem_category(itemCategoryMenuButton.getText());
		newItem.setItem_brand(itemBrandTextField.getText());
		newItem.setItem_supplier(itemSupplierTextField.getText());

		// Set status to out of stock.
		// Can be changed once the user has re-stocked an item.

		newItem.setItem_status("out of stock");

		LocalDateTime now = LocalDateTime.now();
		newItem.setItem_datetime_of_registration(now);

		// -------------------------------------------------------------------------

		// Call DAO method to insert the item into the database
		try {
			// Pass the data into the DAO.
			daoRegisterItem.insertItem(newItem);

			// After successful insertion, refresh the table.
			inventory_table.getItems().clear();
			populateTable();
			clearFields();

			// Generate another unique item id for the next item that is going to be
			// registered.
			setGeneratedRandomCodeToAutoGenerateItemIDTextfield(uniqueItemIdLength);

			// For testing purposes
			System.out.println("Item registered successfully");
			showItemSuccessFullyRegisteredDialog();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		} else {
            showAlert("Invalid Input", "Minimum and maximum thresholds must be numbers.");
	}
	}

	// Method to show an alert
	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	// Utility method to check if a string is numeric
	private boolean isNumeric(String str) {
		if (str == null || str.isEmpty()) {
			return false;
		}
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------

	// The method that generates an auto-generated code for Unique_item_id in the
	// inventory table of the database.
	// This scans the whole inventory table from the database. If a duplicate is
	// found, it regenerates a random code.

	String setGeneratedRandomCodeToAutoGenerateItemIDTextfield(int length) {

		String itemId = null;
		do {
			// Generate a random numeric part
			String numericPart = generateRandomNumericPart(NUMERIC_LENGTH);
			PREFIX = "INV-";
			// Combine prefix with numeric part
			itemId = PREFIX + numericPart;
		} while (isIDExist(itemId));
		autoGenerateItemIDTextField.setText(itemId);
		return itemId;

	}

	private boolean isIDExist(String ID) {
		String sql = "SELECT COUNT(*) AS count FROM inventory WHERE unique_item_id = ?";

		DatabaseConnection dbConnectionInformation = new DatabaseConnection();
		try (Connection conn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, ID); // Set the integer reference code as parameter
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				int count = rs.getInt("count");
				return count > 0;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false; // Default to false if any exception occurs or if no records found
	}

	private static String generateRandomNumericPart(int length) {
		Random random = new Random();
		StringBuilder numericPart = new StringBuilder();

		for (int i = 0; i < length; i++) {
			numericPart.append(random.nextInt(10)); // Append random digits (0-9)
		}

		return numericPart.toString();
	}

	// -----------------------------------------------------------------------------------------------------------------------

	// SIDE BAR MENU METHODS

	@FXML
	void goToHome(ActionEvent event) {
		updateLookOfNotification();
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
		updateLookOfNotification();
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
	void goToRegisterItem(ActionEvent event) {
		updateLookOfNotification();
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
		updateLookOfNotification();
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
		updateLookOfNotification();
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
		updateLookOfNotification();
		try {
			// Load Maintenance.fxml
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/report/view/ReportLandingPageView.fxml"));
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
	void goToHelp(ActionEvent event) {
		updateLookOfNotification();
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
		updateLookOfNotification();

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
