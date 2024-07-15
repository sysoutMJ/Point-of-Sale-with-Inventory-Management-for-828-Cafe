package maintenance.restock.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import inventorymanagement.model.RegisterItemDAO;
import javafx.concurrent.ScheduledService;
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
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import maintenance.restock.model.RestockDAO;
import maintenance.restock.model.RestockModel;
import notification.NotificationController;
import notification.NotificationDAO;

public class RestockController {

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
	private Button restockButton;

	@FXML
	private Button reportButton;

	@FXML
	private Button maintenanceButton;

	@FXML
	private Button searchButton;

	@FXML
	private Button checkButton;
	
	@FXML
	private Button CreateMenuButton;

	@FXML
	private TextField searchTextField;

	@FXML
	private TextField quantityTextField;

	@FXML
	private TextField costOfNewBatchTextField;

	@FXML
	private DatePicker expirationDateOfNewBatchDatePicker;

	@FXML
	private DatePicker dateOfPurchaseDatePicker;

	@FXML
	private MenuButton brandOfNewBatchMenuButton;

	@FXML
	private MenuButton itemCategoryMenuButton;

	@FXML
	private MenuButton supplierOfNewBatchMenuButton;
	
	@FXML
    private MenuButton unitOfMeasurementOfNewBatchOfItem;

	@FXML
	private TableView<RestockModel> stock_report;

	@FXML
	private TableColumn<RestockModel, String> unique_item_id;

	@FXML
	private TableColumn<RestockModel, String> item_name;

	@FXML
	private TableColumn<RestockModel, String> item_brand;

	@FXML
	private TableColumn<RestockModel, String> item_supplier;

	@FXML
	private TableColumn<RestockModel, String> item_category;

	@FXML
	private TableColumn<RestockModel, String> item_unit;

	@FXML
	private TableColumn<RestockModel, Integer> item_minimum_threshold;

	@FXML
	private TableColumn<RestockModel, Integer> item_maximum_threshold;

	@FXML
	private TableColumn<RestockModel, String> date_of_registration;

	@FXML
	private TableColumn<RestockModel, Double> current_stock;

	@FXML
	private TableColumn<RestockModel, String> item_status;

	@FXML
	private Pane overlayPane;

	private ScheduledService<Void> tableRefreshService;
	
	private double gramsToKilograms(double grams) {
	    return grams / 1000;
	}

	private double tbspToKilograms(double tbsp) {
	    return tbsp * 0.015;
	}

	private double tspToKilograms(double tsp) {
	    return tsp * 0.005;
	}

	private double mlToLiters(double ml) {
	    return ml / 1000;
	}

	private double tbspToLiters(double tbsp) {
	    return tbsp * 0.015;
	}

	private double tspToLiters(double tsp) {
	    return tsp * 0.005;
	}

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

		unique_item_id.setCellValueFactory(new PropertyValueFactory<>("unique_item_id"));
		item_name.setCellValueFactory(new PropertyValueFactory<>("item_name"));
		item_brand.setCellValueFactory(new PropertyValueFactory<>("item_brand"));
		item_supplier.setCellValueFactory(new PropertyValueFactory<>("item_supplier"));
		item_category.setCellValueFactory(new PropertyValueFactory<>("item_category"));
		item_unit.setCellValueFactory(new PropertyValueFactory<>("item_unit_of_measurement"));
		item_minimum_threshold.setCellValueFactory(new PropertyValueFactory<>("item_minimum_threshold"));
		item_maximum_threshold.setCellValueFactory(new PropertyValueFactory<>("item_maximum_threshold"));
		item_status.setCellValueFactory(new PropertyValueFactory<>("item_status"));
		current_stock.setCellValueFactory(new PropertyValueFactory<>("current_stock"));
		date_of_registration.setCellValueFactory(new PropertyValueFactory<>("item_datetime_of_registration"));

		// Populate menu buttons
		fetchBrandsFromDatabase();
		fetchSuppliersFromDatabase();
		fetchCategoriesFromDatabase();

		// -----------------------------------------------------------------------------------------------------------------------

		populateTable();
		adjustColumnWidths(stock_report);
		enableDisableControllerInputFields(true);

		// -----------------------------------------------------------------------------------------------------------------------

		// Initialize scheduled service to refresh table periodically
//        tableRefreshService = new ScheduledService<>() {
//            @Override
//            protected Task<Void> createTask() {
//                return new Task<>() {
//                    @Override
//                    protected Void call() throws Exception {
//                        Platform.runLater(() -> {
//                            refreshTable();
//                        });
//                        return null;
//                    }
//                };
//            }
//        };
//        
//        tableRefreshService.setPeriod(Duration.seconds(5)); // Set refresh period (e.g., every minute)
//        tableRefreshService.start();

	}

	// -----------------------------------------------------------------------------------------------------------------------

//    // Method to refresh table data
//    private void refreshTable() {
//        stock_report.getItems().clear(); // Clear existing items
//        populateTable(); // Reload data from the database
//    }

	// -----------------------------------------------------------------------------------------------------------------------

	// Method for populating the table.
	private void populateTable() {

		RestockDAO daoRestock = new RestockDAO();

		// Fetch data from database
		List<RestockModel> items = daoRestock.fetchDataFromDatabase();

		// Add data to TableView
		stock_report.getItems().addAll(items);
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

	// Method to populate Brand Menu Button
	private void populateBrandMenu(List<RestockModel> items) {
		// Clear existing items
		brandOfNewBatchMenuButton.getItems().clear();

		// Extract brands from searched items
		List<String> brands = items.stream().map(RestockModel::getItem_brand).distinct().collect(Collectors.toList());

		// Add fetched brands to the menu button
		for (String brand : brands) {
			MenuItem item = new MenuItem(brand);
			item.setOnAction(e -> {
				brandOfNewBatchMenuButton.setText(brand);
			});
			brandOfNewBatchMenuButton.getItems().add(item);
		}
	}

	// Method to populate Supplier Menu Button
	private void populateSupplierMenu(List<RestockModel> items) {
		// Clear existing items
		supplierOfNewBatchMenuButton.getItems().clear();

		// Extract suppliers from searched items
		List<String> suppliers = items.stream().map(RestockModel::getItem_supplier).distinct()
				.collect(Collectors.toList());
		
		// Extract the unit of measurement from the first item (assuming all items have the same unit of measurement)
	    String unitOfMeasurement = items.get(0).getItem_unit_of_measurement();
	    populateUnitOfMeasurementMenu(unitOfMeasurement);

		// Add fetched suppliers to the menu button
		for (String supplier : suppliers) {
			MenuItem item = new MenuItem(supplier);
			item.setOnAction(e -> {
				supplierOfNewBatchMenuButton.setText(supplier);
			});
			supplierOfNewBatchMenuButton.getItems().add(item);
		}
	}

	// Method to populate Item Category Menu Button
	private void populateCategoryMenu(List<RestockModel> items) {
		// Clear existing items
		itemCategoryMenuButton.getItems().clear();

		// Extract categories from searched items
		List<String> categories = items.stream().map(RestockModel::getItem_category).distinct()
				.collect(Collectors.toList());

		// Add fetched categories to the menu button
		for (String category : categories) {
			MenuItem item = new MenuItem(category);
			item.setOnAction(e -> {
				itemCategoryMenuButton.setText(category);
			});
			itemCategoryMenuButton.getItems().add(item);
		}
	}
	
	private void populateUnitOfMeasurementMenu(String unitOfMeasurement) {
	    // Clear existing items
	    unitOfMeasurementOfNewBatchOfItem.getItems().clear();

	    // Populate menu items based on the unit of measurement
	    if (unitOfMeasurement.equals("Kilograms (kg)")) {
	        unitOfMeasurementOfNewBatchOfItem.getItems().addAll(
	                new MenuItem("Kilograms (kg)"),
	                new MenuItem("Grams (g)"),
	                new MenuItem("Table Spoon (tbsp)"),
	                new MenuItem("Tea Spoon (tsp)")
	                
	        );
	    } else if (unitOfMeasurement.equals("Liters (L)")) {
	        unitOfMeasurementOfNewBatchOfItem.getItems().addAll(
	                new MenuItem("Milliliters (ml)"),
	                new MenuItem("Liters (L)"),
	                new MenuItem("Table Spoon (tbsp)"),
	                new MenuItem("Tea Spoon (tsp)")
	        );
	    } else if (unitOfMeasurement.equals("Pieces (pcs)")) {
	        unitOfMeasurementOfNewBatchOfItem.getItems().addAll(
	                new MenuItem("Slices"),
	                new MenuItem("Packets"),
	                new MenuItem("Pieces (pcs)")
	        );
	    }

	    // Add action handlers for menu items
	    for (MenuItem item : unitOfMeasurementOfNewBatchOfItem.getItems()) {
	        item.setOnAction(e -> unitOfMeasurementOfNewBatchOfItem.setText(item.getText()));
	    }
	}


	private List<String> fetchBrandsFromDatabase() {

		RestockDAO daoRestock = new RestockDAO();
		return daoRestock.fetchBrands();
	}

	private List<String> fetchSuppliersFromDatabase() {
		RestockDAO daoRestock = new RestockDAO();
		return daoRestock.fetchSuppliers();
	}

	private List<String> fetchCategoriesFromDatabase() {

		RestockDAO daoRestock = new RestockDAO();
		return daoRestock.fetchCategories(); // Assuming fetchCategories() returns List<String>
	}

	// -----------------------------------------------------------------------------------------------------------------------

	@FXML
	void search_inventory_item(ActionEvent event) {
	    RestockDAO daoRestock = new RestockDAO();
	    String itemName = searchTextField.getText().trim();

	    if (!itemName.isEmpty()) {
	        List<RestockModel> items = daoRestock.searchItemName(itemName);
	        stock_report.getItems().setAll(items);

	        if (items.isEmpty()) {
	            enableDisableControllerInputFields(true);
	            showItemNotFoundDialog();
	            System.out.println("No items found");
	        } else {
	            showItemFoundDialog();
	            enableDisableControllerInputFields(false);
	            populateBrandMenu(items); // Populate menu buttons based on search results
	            populateSupplierMenu(items);
	            populateCategoryMenu(items);
	        }

	    } else {
	        stock_report.getItems().clear();
	        showItemNotFoundDialog();
	        populateTable();
	        enableDisableControllerInputFields(true);
	    }
	}


	void enableDisableControllerInputFields(boolean bool) {

		// If true, will be disabled.
		// Else, it will be enabled.

		brandOfNewBatchMenuButton.setDisable(bool);
		supplierOfNewBatchMenuButton.setDisable(bool);
		unitOfMeasurementOfNewBatchOfItem.setDisable(bool);
		itemCategoryMenuButton.setDisable(bool);
		quantityTextField.setDisable(bool);
		costOfNewBatchTextField.setDisable(bool);
		expirationDateOfNewBatchDatePicker.setDisable(bool);
		dateOfPurchaseDatePicker.setDisable(bool);
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
	private void showSearchBlankTextField() {
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

	// Show Item successfully restocked dialog
	private void showItemSuccessfullyRestocked() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass()
					.getResource("/maintenance/restock/view/dialogueboxes/ItemSuccessfullyRestockedView.fxml"));
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
	
	// Show negative value not allowed
	private void showNegativeValueNotAllowed() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass()
					.getResource("/maintenance/restock/view/dialogueboxes/NegativeValueNotAllowed.fxml"));
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

	private void showConfirmRestockItemDialog() {
	    try {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/maintenance/restock/view/dialogueboxes/ConfirmRestockItemView.fxml"));
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
	        double centerXPosition = overlayPane.getScene().getWindow().getX() + overlayPane.getScene().getWindow().getWidth() / 2;
	        double centerYPosition = overlayPane.getScene().getWindow().getY() + overlayPane.getScene().getWindow().getHeight() / 2;

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

	        String searchText = searchTextField.getText();
	        String brand = brandOfNewBatchMenuButton.getText();
	        String supplier = supplierOfNewBatchMenuButton.getText();
	        String category = itemCategoryMenuButton.getText();
	        String unit = unitOfMeasurementOfNewBatchOfItem.getText();
	        Double quantity = Double.parseDouble(quantityTextField.getText());
	        Double cost = Double.parseDouble(costOfNewBatchTextField.getText());
	        LocalDate expirationDate = expirationDateOfNewBatchDatePicker.getValue();
	        LocalDate dateOfPurchase = dateOfPurchaseDatePicker.getValue();

	        
	        RestockDAO dao = new RestockDAO();
	        // Get the original unit of measurement
	        RestockModel selectedItem = dao.getSelectedItem(searchText, brand, supplier, category);
	        String originalUnit = selectedItem.getItem_unit_of_measurement();

	        // Perform unit conversion if necessary
	        if (!unit.equals(originalUnit)) {
	            if (originalUnit.equals("Kilograms (kg)") && unit.equals("Grams (g)")) {
	                quantity = gramsToKilograms(quantity);
	            } else if (originalUnit.equals("Kilograms (kg)") && unit.equals("Table Spoon (tbsp)")) {
	                quantity = tbspToKilograms(quantity);
	            } else if (originalUnit.equals("Kilograms (kg)") && unit.equals("Tea Spoon (tsp)")) {
	                quantity = tspToKilograms(quantity);
	            } else if (originalUnit.equals("Liters (L)") && unit.equals("Milliliters (ml)")) {
	                quantity = mlToLiters(quantity);
	            } else if (originalUnit.equals("Liters (L)") && unit.equals("Table Spoon (tbsp)")) {
	                quantity = tbspToLiters(quantity);
	            } else if (originalUnit.equals("Liters (L)") && unit.equals("Tea Spoon (tsp)")) {
	                quantity = tspToLiters(quantity);
	            }
	        }

	        if (confirmRegisterController.isConfirmed()) {
	            RestockDAO daoRestock = new RestockDAO();

	            daoRestock.updateStock(searchText, brand, supplier, category, quantity, cost, expirationDate, dateOfPurchase);
	            
	            updateLookOfNotification();
	            showItemSuccessfullyRestocked();
	            searchTextField.setText("");
	            clearTextField();
	            enableDisableControllerInputFields(true);
	            stock_report.getItems().clear();
	            populateTable();
	        } else {
	            System.out.println("Restock canceled.");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}


	// -----------------------------------------------------------------------------------------------------------------------

	void clearTextField() {

		brandOfNewBatchMenuButton.setText("");
		supplierOfNewBatchMenuButton.setText("");
		itemCategoryMenuButton.setText("");
		quantityTextField.setText("");
		costOfNewBatchTextField.setText("");
		expirationDateOfNewBatchDatePicker.setValue(null);
		dateOfPurchaseDatePicker.setValue(null);
	}

	// -----------------------------------------------------------------------------------------------------------------------

	@FXML
	void restockItem(ActionEvent event) {
		String itemName = searchTextField.getText().trim();
		String inputStock = quantityTextField.getText().trim();

		if (!itemName.isEmpty()) {
		    try {
		        Double stockQuantity = Double.parseDouble(inputStock);
		        
		        if (stockQuantity < 0) {
		            // Show error message for negative stock quantity
		            showNegativeValueNotAllowed();
		        } else {
		            // Valid stock quantity, proceed with showing confirmation dialog
		            showConfirmRestockItemDialog();
		        }
		    } catch (NumberFormatException e) {
		        // Show error message for invalid number format
		    	showNegativeValueNotAllowed();
		    }
		} else {
		    // Show error message for empty item name
		    showSearchBlankTextField();
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
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/maintenance/edititemdetails/view/EditItemDetailsView.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/maintenance/createmenu/view/CreateMenuView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) CreateMenuButton.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	@FXML
	void goToBackup(ActionEvent event) {
		try {
			// Load Maintenance.fxml
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/maintenance/backup/view/BackupView.fxml"));
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
			// Load Maintenance.fxml
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/Admin/view/AdminMenuView.fxml"));
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
	void goToSearch(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/search/view/SearchLandingPageView.fxml"));
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
