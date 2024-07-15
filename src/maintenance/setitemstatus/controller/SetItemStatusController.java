package maintenance.setitemstatus.controller;

import java.io.IOException;
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
import maintenance.restock.model.RestockModel;
import maintenance.setitemstatus.model.SetItemStatusDAO;
import maintenance.setitemstatus.model.SetItemStatusModel;
import notification.NotificationController;
import notification.NotificationDAO;

public class SetItemStatusController {

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
	private ToggleButton statusToggleButton;

	@FXML
	private TextField searchTextField;

	@FXML
	private TextField currentStatusTextField;

	@FXML
	private MenuButton brandOfItemMenuButton;

	@FXML
	private MenuButton supplierOfItemMenuButton;

	@FXML
	private MenuButton categoryOfItemMenuButton;

	@FXML
	private TableView<SetItemStatusModel> inventory_table;

	@FXML
	private TableColumn<SetItemStatusModel, Integer> item_id;

	@FXML
	private TableColumn<SetItemStatusModel, String> unique_item_id;

	@FXML
	private TableColumn<SetItemStatusModel, String> item_name;

	@FXML
	private TableColumn<SetItemStatusModel, Integer> current_stock;

	@FXML
	private TableColumn<SetItemStatusModel, String> item_unit_of_measurement;

	@FXML
	private TableColumn<SetItemStatusModel, Integer> item_minimum_threshold;

	@FXML
	private TableColumn<SetItemStatusModel, Integer> item_maximum_threshold;

	@FXML
	private TableColumn<SetItemStatusModel, String> item_category;

	@FXML
	private TableColumn<SetItemStatusModel, String> item_brand;

	@FXML
	private TableColumn<SetItemStatusModel, String> item_supplier;

	@FXML
	private TableColumn<SetItemStatusModel, String> item_status;

	@FXML
	private TableColumn<SetItemStatusModel, String> item_datetime_of_registration;

	@FXML
	private Pane overlayPane;

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

		// Populate menu buttons
		fetchBrandsFromDatabase();
		fetchSuppliersFromDatabase();
		fetchCategoriesFromDatabase();

		// -----------------------------------------------------------------------------------------------------------------------

		populateTable();
		adjustColumnWidths(inventory_table);

		// -----------------------------------------------------------------------------------------------------------------------

		enableDisableControllerInputFields(true);
	}

	// -----------------------------------------------------------------------------------------------------------------------

	// Method for populating the table.
	private void populateTable() {

		SetItemStatusDAO daoSetItemStatus = new SetItemStatusDAO();

		// Fetch data from database
		List<SetItemStatusModel> items = daoSetItemStatus.fetchDataFromDatabase();

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

	// Method to populate Brand Menu Button
	private void populateBrandMenu(List<SetItemStatusModel> items) {
		// Clear existing items
		brandOfItemMenuButton.getItems().clear();

		// Extract brands from searched items
		List<String> brands = items.stream().map(SetItemStatusModel::getItem_brand).distinct()
				.collect(Collectors.toList());

		// Add fetched brands to the menu button
		for (String brand : brands) {
			MenuItem item = new MenuItem(brand);
			item.setOnAction(e -> {
				brandOfItemMenuButton.setText(brand);
			});
			brandOfItemMenuButton.getItems().add(item);
		}
	}

	// Method to populate Supplier Menu Button
	private void populateSupplierMenu(List<SetItemStatusModel> items) {
		// Clear existing items
		supplierOfItemMenuButton.getItems().clear();

		// Extract suppliers from searched items
		List<String> suppliers = items.stream().map(SetItemStatusModel::getItem_supplier).distinct()
				.collect(Collectors.toList());

		// Add fetched suppliers to the menu button
		for (String supplier : suppliers) {
			MenuItem item = new MenuItem(supplier);
			item.setOnAction(e -> {
				supplierOfItemMenuButton.setText(supplier);
			});
			supplierOfItemMenuButton.getItems().add(item);
		}
	}

	// Method to populate Item Category Menu Button
	private void populateCategoryMenu(List<SetItemStatusModel> items) {
		// Clear existing items
		categoryOfItemMenuButton.getItems().clear();

		// Extract categories from searched items
		List<String> categories = items.stream().map(SetItemStatusModel::getItem_category).distinct()
				.collect(Collectors.toList());

		// Add fetched categories to the menu button
		for (String category : categories) {
			MenuItem item = new MenuItem(category);
			item.setOnAction(e -> {
				categoryOfItemMenuButton.setText(category);
			});
			categoryOfItemMenuButton.getItems().add(item);
		}
	}

	private List<String> fetchBrandsFromDatabase() {

		SetItemStatusDAO daoSetItemStatus = new SetItemStatusDAO();
		return daoSetItemStatus.fetchBrands();
	}

	private List<String> fetchSuppliersFromDatabase() {
		SetItemStatusDAO daoSetItemStatus = new SetItemStatusDAO();
		return daoSetItemStatus.fetchSuppliers();
	}

	private List<String> fetchCategoriesFromDatabase() {

		SetItemStatusDAO daoSetItemStatus = new SetItemStatusDAO();
		return daoSetItemStatus.fetchCategories(); // Assuming fetchCategories() returns List<String>
	}

	// -----------------------------------------------------------------------------------------------------------------------

	@FXML
	void search_inventory_item(ActionEvent event) {
		SetItemStatusDAO daoSetItemStatus = new SetItemStatusDAO();
		String itemName = searchTextField.getText().trim();

		if (!itemName.isEmpty()) {
			List<SetItemStatusModel> items = daoSetItemStatus.searchItemName(itemName);
			inventory_table.getItems().setAll(items);

			if (items.isEmpty()) {
				showItemNotFoundDialog();
				enableDisableControllerInputFields(true);
				System.out.println("No items found");
			} else {
				showItemFoundDialog();
				enableDisableControllerInputFields(false);
				populateBrandMenu(items); // Populate menu buttons based on search results
				populateSupplierMenu(items);
				populateCategoryMenu(items);

				// Get the status of the first item in the list and set it to the text field
				String status = daoSetItemStatus.getItemStatus(items.get(0).getItem_name());
				currentStatusTextField.setText(status);

				if ("available".equalsIgnoreCase(status)) {
					statusToggleButton.setText("Deactivate");
					;
					currentStatusTextField.setStyle("-fx-text-fill: #6DD640;");
					statusToggleButton.setStyle("-fx-background-color: #FFC0C0;");
				}

				if ("unavailable".equalsIgnoreCase(status)) {
					statusToggleButton.setText("Reactivate");
					currentStatusTextField.setStyle("-fx-text-fill: #D64040;");
					statusToggleButton.setStyle("-fx-background-color: #D3FFC1;");
				}
			}

		} else {
			inventory_table.getItems().clear();
			showItemNotFoundDialog();
			currentStatusTextField.setText("");
			statusToggleButton.setText("");
			populateTable();
			enableDisableControllerInputFields(true);
		}
	}

	void enableDisableControllerInputFields(Boolean bool) {
		brandOfItemMenuButton.setDisable(bool);
		supplierOfItemMenuButton.setDisable(bool);
		currentStatusTextField.setDisable(bool);
		categoryOfItemMenuButton.setDisable(bool);
		statusToggleButton.setDisable(bool);
	}

	// -----------------------------------------------------------------------------------------------------------------------

	// Dialogue Boxes

	// Show blank fields detected dialog.
	private void showBlankFieldsDetectedDialog() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass()
					.getResource("/maintenance/setitemstatus/view/dialogueboxes/BlankTextFieldsDetectedView.fxml"));
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

	// Show Item not found dialog.
	private void showItemNotFoundDialog() {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/maintenance/setitemstatus/view/dialogueboxes/ItemNotFoundView.fxml"));
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
					getClass().getResource("/maintenance/setitemstatus/view/dialogueboxes/ItemFoundView.fxml"));
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

	private void showConfirmActivatingItemDialog(String itemName, String itemBrand, String itemSupplier,
			String itemCategory, String newStatus) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass()
					.getResource("/maintenance/setitemstatus/view/dialogueboxes/ConfirmActivatingItemView.fxml"));
			Parent root = loader.load();

			DialogueBoxesController confirmRegisterController = loader.getController();

			Stage stage = new Stage();
			stage.initStyle(StageStyle.UNDECORATED);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initOwner(overlayPane.getScene().getWindow());

			Scene scene = new Scene(root);
			stage.setScene(scene);

			double centerXPosition = overlayPane.getScene().getWindow().getX()
					+ overlayPane.getScene().getWindow().getWidth() / 2;
			double centerYPosition = overlayPane.getScene().getWindow().getY()
					+ overlayPane.getScene().getWindow().getHeight() / 2;

			stage.setX(centerXPosition - root.prefWidth(-1) / 2);
			stage.setY(centerYPosition - root.prefHeight(-1) / 2);

			overlayPane.setDisable(true);
			stage.showAndWait();
			overlayPane.setDisable(false);

			if (confirmRegisterController.isConfirmed()) {
				SetItemStatusDAO daoSetItemStatus = new SetItemStatusDAO();
				boolean updateSuccessful = daoSetItemStatus.updateItemStatus(itemName, itemBrand, itemSupplier,
						itemCategory, newStatus);

				if (updateSuccessful) {
					updateLookOfNotification();
					showItemSuccessfullySetToAvailableDialog();
					searchTextField.setText("");
					brandOfItemMenuButton.setText("");
					supplierOfItemMenuButton.setText("");
					categoryOfItemMenuButton.setText("");
					currentStatusTextField.setText("");
					statusToggleButton.setText("");
					enableDisableControllerInputFields(true);
					inventory_table.getItems().clear();
					populateTable();
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void showConfirmDeactivatingItemDialog(String itemName, String itemBrand, String itemSupplier,
			String itemCategory, String newStatus) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass()
					.getResource("/maintenance/setitemstatus/view/dialogueboxes/ConfirmDeactivatingItemView.fxml"));
			Parent root = loader.load();

			DialogueBoxesController confirmRegisterController = loader.getController();

			Stage stage = new Stage();
			stage.initStyle(StageStyle.UNDECORATED);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initOwner(overlayPane.getScene().getWindow());

			Scene scene = new Scene(root);
			stage.setScene(scene);

			double centerXPosition = overlayPane.getScene().getWindow().getX()
					+ overlayPane.getScene().getWindow().getWidth() / 2;
			double centerYPosition = overlayPane.getScene().getWindow().getY()
					+ overlayPane.getScene().getWindow().getHeight() / 2;

			stage.setX(centerXPosition - root.prefWidth(-1) / 2);
			stage.setY(centerYPosition - root.prefHeight(-1) / 2);

			overlayPane.setDisable(true);
			stage.showAndWait();
			overlayPane.setDisable(false);

			if (confirmRegisterController.isConfirmed()) {
				SetItemStatusDAO daoSetItemStatus = new SetItemStatusDAO();
				boolean updateSuccessful = daoSetItemStatus.updateItemStatus(itemName, itemBrand, itemSupplier,
						itemCategory, newStatus);

				if (updateSuccessful) {
					updateLookOfNotification();
					showItemSuccessfullySetToUnavailableDialog();
					searchTextField.setText("");
					brandOfItemMenuButton.setText("");
					supplierOfItemMenuButton.setText("");
					categoryOfItemMenuButton.setText("");
					currentStatusTextField.setText("");
					statusToggleButton.setText("");
					enableDisableControllerInputFields(true);
					inventory_table.getItems().clear();
					populateTable();
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Show Item Successfully Set To Available Dialog.
	private void showItemSuccessfullySetToAvailableDialog() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(
					"/maintenance/setitemstatus/view/dialogueboxes/ItemSuccessfullySetToAvailableView.fxml"));
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

	// Show Item Successfully Set To Unavailable Dialog.
	private void showItemSuccessfullySetToUnavailableDialog() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(
					"/maintenance/setitemstatus/view/dialogueboxes/ItemSuccessfullySetToUnavailableView.fxml"));
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

	// For menu button

	@FXML
	void changeStatus(ActionEvent event) {
		SetItemStatusDAO daoSetItemStatus = new SetItemStatusDAO();
		SetItemStatusModel selectedItem = inventory_table.getSelectionModel().getSelectedItem();
		String searchedItemName = searchTextField.getText().trim();

		String selectedBrand = brandOfItemMenuButton.getText();
		String selectedCategory = categoryOfItemMenuButton.getText();
		String selectedSupplier = supplierOfItemMenuButton.getText();

		if (!searchedItemName.isEmpty()) {
			enableDisableControllerInputFields(false);

			if (selectedBrand != null && !selectedBrand.isEmpty() && selectedCategory != null
					&& !selectedCategory.isEmpty() && selectedSupplier != null && !selectedSupplier.isEmpty()) {

				enableDisableControllerInputFields(false);

				String itemName = searchTextField.getText().trim();
				String currentStatus = daoSetItemStatus.getItemStatus(itemName);
				String newStatus = "available".equalsIgnoreCase(currentStatus) ? "unavailable" : "available";

				if ("available".equalsIgnoreCase(currentStatus)) {
					showConfirmDeactivatingItemDialog(itemName, selectedBrand, selectedSupplier, selectedCategory,
							newStatus);
				} else {
					showConfirmActivatingItemDialog(itemName, selectedBrand, selectedSupplier, selectedCategory,
							newStatus);
				}

			} else {

				showBlankFieldsDetectedDialog();
				enableDisableControllerInputFields(false);
			}
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
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/maintenance/edititemdetails/view/EditItemDetailsView.fxml"));
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
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/maintenance/setitemstatus/view/SetItemStatusView.fxml"));
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
			// Load Maintenance.fxml
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/maintenance/createmenu/view/CreateMenuView.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/search/view/SearchLandingPageView.fxml"));
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
