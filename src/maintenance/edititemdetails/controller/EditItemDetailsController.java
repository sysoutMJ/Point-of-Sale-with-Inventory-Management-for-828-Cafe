package maintenance.edititemdetails.controller;

import java.io.IOException;
import java.util.List;

import inventorymanagement.model.RegisterItemDAO;
import javafx.concurrent.ScheduledService;
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
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import maintenance.edititemdetails.model.EditItemDetailsDAO;
import maintenance.edititemdetails.model.EditItemDetailsModel;
import notification.NotificationController;
import notification.NotificationDAO;

public class EditItemDetailsController {

	@FXML
	private Button homeButton;

	@FXML
	private Button notificationButton;

	@FXML
	private Button logoutButton;

	@FXML
	private Button securityButton;

	@FXML
	private Button helpButton;

	@FXML
	private Button aboutButton;

	@FXML
	private Button registerButton;

	@FXML
	private Button saveButton;

	@FXML
	private Button reportButton;

	@FXML
	private Button maintenanceButton;

	@FXML
	private Button searchButton;

	@FXML
	private Button checkButton;

	@FXML
	private TextField searchTextField;

	@FXML
	private TextField uniqueIDOfItem;

	@FXML
	private TextField newItemNameTextField;

	@FXML
	private MenuButton newItemUnitOfMeasurement;

	@FXML
	private MenuButton newItemCategory;

	@FXML
	private TextField newItemBrandTextField;

	@FXML
	private TextField newItemSupplierTextField;

	@FXML
	private TextField newMinimumThresholdCapacityTextField;

	@FXML
	private TextField newMaximumThresholdCapacityTextField;

	@FXML
	private TableView<EditItemDetailsModel> inventory_table;

	@FXML
	private TableColumn<EditItemDetailsModel, Integer> item_id;

	@FXML
	private TableColumn<EditItemDetailsModel, String> unique_item_id;

	@FXML
	private TableColumn<EditItemDetailsModel, String> item_name;

	@FXML
	private TableColumn<EditItemDetailsModel, Integer> current_stock;

	@FXML
	private TableColumn<EditItemDetailsModel, String> item_unit_of_measurement;

	@FXML
	private TableColumn<EditItemDetailsModel, Integer> item_minimum_threshold;

	@FXML
	private TableColumn<EditItemDetailsModel, Integer> item_maximum_threshold;

	@FXML
	private TableColumn<EditItemDetailsModel, String> item_category;

	@FXML
	private TableColumn<EditItemDetailsModel, String> item_brand;

	@FXML
	private TableColumn<EditItemDetailsModel, String> item_supplier;

	@FXML
	private TableColumn<EditItemDetailsModel, String> item_status;

	@FXML
	private TableColumn<EditItemDetailsModel, String> item_datetime_of_registration;

	@FXML
	private Pane overlayPane;

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
		setMenuItemsForUnitOfMeasurement(newItemUnitOfMeasurement);
		setMenuItemsForCategories(newItemCategory);
		adjustColumnWidths(inventory_table);

		// -----------------------------------------------------------------------------------------------------------------------
		
		enableDisableControllerInputFields(true);
	}

	// -----------------------------------------------------------------------------------------------------------------------

	// Method for populating the table.
	private void populateTable() {

		EditItemDetailsDAO daoEditItemDetails = new EditItemDetailsDAO();

		// Fetch data from database
		List<EditItemDetailsModel> items = daoEditItemDetails.fetchDataFromDatabase();

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

	// FOR POPULATING THE MENU BUTTONS.

	// -----------------------------------------------------------------------------------------------------------------------

	// Method for setting the menu items for the menu button, Unit of Measurements.
	void setMenuItemsForUnitOfMeasurement(MenuButton menuButton) {
		MenuItem L = new MenuItem("Liters (L)");
		MenuItem kg = new MenuItem("Kilograms (kg)");
		MenuItem pcs = new MenuItem("Pieces (pcs)");

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

	private String setSelectedUnit(MenuButton menuButton, String unit) {
		menuButton.setText(unit);
		System.out.println("Selected unit: " + unit);
		return unit;
	}

	// -----------------------------------------------------------------------------------------------------------------------
	
	private void setupTableSelectionListener() {
	    inventory_table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
	        if (newSelection != null) {
	            // Populate text fields with selected item data
	            EditItemDetailsModel selectedItem = newSelection;
	            uniqueIDOfItem.setText(selectedItem.getUnique_item_id());
	            newItemNameTextField.setText(selectedItem.getItem_name());
	            newItemUnitOfMeasurement.setText(selectedItem.getItem_unit_of_measurement());
	            newItemCategory.setText(selectedItem.getItem_category());
	            newMinimumThresholdCapacityTextField.setText(Integer.toString(selectedItem.getItem_minimum_threshold()));
	            newMaximumThresholdCapacityTextField.setText(Integer.toString(selectedItem.getItem_maximum_threshold()));
	            newItemBrandTextField.setText(selectedItem.getItem_brand());
	            newItemSupplierTextField.setText(selectedItem.getItem_supplier());
	            
	            enableDisableControllerInputFields(false);
	        } else {
	            // Clear text fields if no item is selected
	            clearTextFields();
	        }
	    });
	}

	
	@FXML
	void search_inventory_item(ActionEvent event) {
		EditItemDetailsDAO daoEditItemDetails = new EditItemDetailsDAO();
		String itemName = searchTextField.getText().trim();

		if (!itemName.isEmpty()) {
			List<EditItemDetailsModel> items = daoEditItemDetails.searchItemName(itemName);
			inventory_table.getItems().setAll(items);
			
			setupTableSelectionListener();

			if (items.isEmpty()) {
				enableDisableControllerInputFields(true);
				showItemNotFoundDialog();
				System.out.println("No items found");
			} else {
				showItemFoundDialog();
				//enableDisableControllerInputFields(false);
			}

		} else {
			inventory_table.getItems().clear();
			showItemNotFoundDialog();
			populateTable();
			enableDisableControllerInputFields(true);
		}
	}

	void enableDisableControllerInputFields(boolean bool) {

		// If true, will be disabled.
		// Else, it will be enabled.

		uniqueIDOfItem.setDisable(bool);
		newItemNameTextField.setDisable(bool);
		newItemUnitOfMeasurement.setDisable(bool);
		newItemCategory.setDisable(bool);
		newItemBrandTextField.setDisable(bool);
		newItemSupplierTextField.setDisable(bool);
		newMinimumThresholdCapacityTextField.setDisable(bool);
		newMaximumThresholdCapacityTextField.setDisable(bool);
	}
	
	private void clearTextFields() {
	    uniqueIDOfItem.clear();
	    newItemNameTextField.clear();
	    newItemUnitOfMeasurement.setText("");
	    newItemCategory.setText("");;
	    newMinimumThresholdCapacityTextField.clear();
	    newMaximumThresholdCapacityTextField.clear();
	    newItemBrandTextField.clear();
	    newItemSupplierTextField.clear();
	}

	// -----------------------------------------------------------------------------------------------------------------------

	// Dialogue Boxes

	// Show Item not found dialog.
	private void showItemNotFoundDialog() {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/maintenance/restock/view/dialogueboxes/ItemNotFoundView.fxml"));
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
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/maintenance/restock/view/dialogueboxes/ItemFoundView.fxml"));
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

	// Show blank text field dialog.
	private void showSearchFieldBlankDialog() {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/maintenance/restock/view/dialogueboxes/SearchFieldBlankView.fxml"));
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



	private void showConfirmSavingItemDetailsDialog() {
	    try {
	        FXMLLoader loader = new FXMLLoader(
	                getClass().getResource("/maintenance/edititemdetails/view/dialogueboxes/ConfirmSavingItemDetailsView.fxml"));
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

	        // Set overlay pane visibility to true and adjust opacity
	        overlayPane.setDisable(true);

	        // Show the dialog and wait for it to close
	        stage.showAndWait();

	        // Hide the overlay pane after the dialog is closed
	        overlayPane.setDisable(false);

	        String newItemNameParameter = newItemNameTextField.getText();
	        String newItemUnitOfMeasurementParameter = newItemUnitOfMeasurement.getText();
	        String newItemCategoryParameter = newItemCategory.getText();
	        String newItemBrandParameter = newItemBrandTextField.getText();
	        String newItemSupplierParameter = newItemSupplierTextField.getText();

	        String searchedNameParameter = searchTextField.getText();
	        String uniqueIDOfItemNameParameter = uniqueIDOfItem.getText();

	        // Add input validation for numeric fields
	        if (isNumeric(newMinimumThresholdCapacityTextField.getText()) && isNumeric(newMaximumThresholdCapacityTextField.getText())) {
	            int newMinimumThresholdParameter = Integer.parseInt(newMinimumThresholdCapacityTextField.getText());
	            int newMaximumThresholdParameter = Integer.parseInt(newMaximumThresholdCapacityTextField.getText());

	            if (confirmRegisterController.isConfirmed()) {
	                EditItemDetailsDAO daoEditItemDetails = new EditItemDetailsDAO();

	                daoEditItemDetails.updateItemDetails(newItemNameParameter,
	                        newItemUnitOfMeasurementParameter,
	                        newItemCategoryParameter,
	                        newItemBrandParameter,
	                        newMinimumThresholdParameter,
	                        newMaximumThresholdParameter,
	                        newItemSupplierParameter,
	                        searchedNameParameter,
	                        uniqueIDOfItemNameParameter
	                );

	                updateLookOfNotification();
	                showItemDetailsSuccessfullyUpdated();
	                searchTextField.setText("");
	                clearTextFields();
	                enableDisableControllerInputFields(true);
	                inventory_table.getItems().clear();
	                populateTable();
	            } else {
	                System.out.println("Restock canceled.");
	            }
	        } else {
	            showAlert("Invalid Input", "Minimum and maximum thresholds must be numbers.");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
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
	
	// Show Item successfully restocked dialog
		private void showItemDetailsSuccessfullyUpdated() {
			try {
				FXMLLoader loader = new FXMLLoader(getClass()
						.getResource("/maintenance/edititemdetails/view/dialogueboxes/ItemDetailsSuccessfullyUpdatedView.fxml"));
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

		
		@FXML
		void saveItem(ActionEvent event) {
			String itemName = searchTextField.getText().trim();

			if (!itemName.isEmpty()) {
				showConfirmSavingItemDetailsDialog();
			} else {
				showSearchFieldBlankDialog();
			}
		}
		
	// -----------------------------------------------------------------------------------------------------------------------

	// TOP MENU BAR

	@FXML
	void goToRestock(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/maintenance/restock/view/RestockView.fxml"));
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
	void goToEditItem(ActionEvent event) {
		try {
			// Load Maintenance.fxml
			FXMLLoader loader = new FXMLLoader(getClass().getResource("maintenance/ediitemdetails/view/EditItemDetailsView.fxml"));
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
	void goToSetItemStatus(ActionEvent event) {
		try {
			// Load Maintenance.fxml
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/maintenance/setitemstatus/view/SetItemStatusView.fxml"));
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
	void goToCreateMenu(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/maintenance/createmenu/view/CreateMenuView.fxml"));
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
	void goToBackup(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/maintenance/backup/view/BackupView.fxml"));
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
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/Admin/view/AdminMenuView.fxml"));
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
			// Load Maintenance.fxml
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/search/view/SearchInventoryView.fxml"));
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
			// Load Maintenance.fxml
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/Security/changepassword/view/ChangePassword.fxml"));
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
	void goToReport(ActionEvent event) {
		try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/report/view/ReportLandingPageView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	@FXML
    void goToHelp(ActionEvent event) {
    	try {
			// Load Maintenance.fxml
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/help/view/HelpLandingPageView.fxml"));
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
		}    }

    @FXML
    void goToAbout(ActionEvent event) {
    	
    	try {
			// Load Maintenance.fxml
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/about/view/AboutAdmin.fxml"));
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
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/Security/View/V_Login.fxml"));
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
