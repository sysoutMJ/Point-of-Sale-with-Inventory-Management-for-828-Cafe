package maintenance.createmenu.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import notification.NotificationController;
import notification.NotificationDAO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import database.DatabaseConnection;
import inventorymanagement.model.RegisterItemDAO;

public class CreateMenuController {

	@FXML
	private Button homeButton;

	@FXML
	private Text Label1;

	@FXML
	private Text Label2;

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
	private FlowPane menuIngredients;

	@FXML
	private FlowPane MenuItemsPane;

	@FXML
	private Button AddMenuItemButton;

	@FXML
	private Button RestockButton;

	@FXML
	private VBox CategoryPane;

	@FXML
	private Button EditItemButton;

	@FXML
	private Button AddIngredientButton;

	@FXML
	private Button SetItemStatusButton;

	@FXML
	private Button CreateMenuButton;

	@FXML
	private Button goToBackupButton;

	@FXML
	private VBox inactiveCategoryPane;

	@FXML
	private VBox inactiveMenuItemPane;

	@FXML
	private Button RecycleBinButton;

	@FXML
	private Button AddCategoryButton;

	@FXML
	private Button SaveButton;

	@FXML
	private Pane container;

	DatabaseConnection dbConnectionInformation = new DatabaseConnection();

//	private static final String DB_URL = dbConnectionInformation.getUrl();
//	private static final String DB_USER = dbConnectionInformation.getUsername();
//	private static final String DB_PASSWORD = dbConnectionInformation.getPassword();

	private Button selectedCategoryButton = null; // Track currently selected category button
	private Button selectedMenuItemButton = null; // Track currently selected menu item button
	private String selectedMenuItemName = "";
	private boolean isRecycleBinActive = false; // Track recycle bin state

	@FXML
	private void handleLogoutButton(ActionEvent event) {
		System.out.println("Logout button pressed");
		// Implement your logout logic here
	}

	@FXML
	private void handleAddMenuItemButtonAction(ActionEvent event) {
		if (selectedCategoryButton == null) {
			System.out.println("Please select a category first.");
			return; // Exit method if no category is selected
		}

		TextInputDialog nameDialog = new TextInputDialog();
		nameDialog.setTitle("New Menu Item");
		nameDialog.setHeaderText("Add a new menu item");
		nameDialog.setContentText("Please enter the menu item name:");

		Optional<String> itemName = nameDialog.showAndWait();

		if (itemName.isPresent()) {
			TextInputDialog priceDialog = new TextInputDialog();
			priceDialog.setTitle("New Menu Item");
			priceDialog.setHeaderText("Add a new menu item");
			priceDialog.setContentText("Please enter the menu item price:");

			Optional<String> itemPrice = priceDialog.showAndWait();
			if (itemPrice.isPresent()) {
				addMenuItemToDatabase(itemName.get(), Double.parseDouble(itemPrice.get()));
			}
		}
	}

	private void addMenuItemToDatabase(String name, double price) {
		String categoryName = selectedCategoryButton.getText();
		String status = "active"; // Default status

		// Check if the menu item already exists
		if (menuItemExists(name, categoryName)) {
			// Show error dialog for duplicate menu item
			showErrorDialog("Menu Item Error", "Duplicate Menu Item",
					"A menu item with the same name already exists in this category.");
			return;
		}

		String sql = "INSERT INTO menu_item (menu_item_name, menu_item_price, menu_item_category_name, menu_item_status, menu_item_datetime_implemented_date) VALUES (?, ?, ?, ?, ?)";

		try (Connection conn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, name);
			pstmt.setDouble(2, price);
			pstmt.setString(3, categoryName);
			pstmt.setString(4, status);
			pstmt.setTimestamp(5, new java.sql.Timestamp(System.currentTimeMillis()));

			pstmt.executeUpdate();

			System.out.println("Menu item added successfully");

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		// Reload menu items for the selected category
		loadMenuItemsForCategory(categoryName);
	}

	@FXML
	private void handleRestockButtonAction(ActionEvent event) {

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/maintenance/view/RestockView.fxml"));
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
	private void handleEditItemButtonAction(ActionEvent event) {

		try {
			// Load Maintenance.fxml
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/maintenance/view/EditItemDetailsView.fxml"));
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
	private void handleSetItemStatusButtonAction(ActionEvent event) {

		try {
			// Load Maintenance.fxml
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/maintenance/view/SetItemStatusView.fxml"));
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
	void handleAddIngredientButtonAction(ActionEvent event) {

		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/maintenance/createmenu/view/AddIngredientsView.fxml"));
			AnchorPane dialogPane = loader.load();

			// Get the controller of the "Add Ingredients" page
			AddIngredientController controller = loader.getController();
			if (selectedMenuItemButton != null) {
				// Pass the selected menu item name to the controller
				controller.setMenuItemName(selectedMenuItemName);
			}

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Add Ingredients");
			dialogStage.initModality(Modality.APPLICATION_MODAL);
			dialogStage.setScene(new Scene(dialogPane));
			dialogStage.showAndWait();

			loadIngredientsForMenuItem(selectedMenuItemName);
		} catch (IOException e) {
			e.printStackTrace(); // Replace with appropriate error handling
		}
	}

	@FXML
	private void handleAddCategoryButtonAction(ActionEvent event) {

		System.out.println("Add Category button pressed");
		// Prompt user for category name and status
		TextInputDialog nameDialog = new TextInputDialog();
		nameDialog.setTitle("New Category");
		nameDialog.setHeaderText("Add a new category");
		nameDialog.setContentText("Please enter the category name:");

		Optional<String> categoryName = nameDialog.showAndWait();

		if (categoryName.isPresent()) {
			TextInputDialog statusDialog = new TextInputDialog("active");
			statusDialog.setTitle("New Category");
			statusDialog.setHeaderText("Add a new category");
			statusDialog.setContentText("Please enter the category status (default: active):");

			Optional<String> categoryStatus = statusDialog.showAndWait();
			String status = categoryStatus.orElse("active");

			addCategoryToDatabase(categoryName.get(), status);
		}
	}

	private void addCategoryToDatabase(String name, String status) {

		// Check if the category already exists
		if (categoryExists(name)) {
			// Show error dialog for duplicate category
			showErrorDialog("Category Error", "Duplicate Category", "A category with the same name already exists.");
			return;
		}

		String sql = "INSERT INTO menu_item_category (menu_item_category_name, menu_item_category_status, menu_item_implemented_date) VALUES (?, ?, ?)";

		try (Connection conn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, name);
			pstmt.setString(2, status);
			pstmt.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
			pstmt.executeUpdate();

			System.out.println("Category added successfully");

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		loadActiveCategories(); // Reload categories after adding new one
	}

	private void loadInactiveCategories() {
		inactiveCategoryPane.getChildren().clear(); // Clear existing nodes first

		String categorySql = "SELECT menu_item_category_name FROM menu_item_category WHERE menu_item_category_status = 'inactive'";

		try (Connection conn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
				PreparedStatement categoryPstmt = conn.prepareStatement(categorySql);
				ResultSet categoryRs = categoryPstmt.executeQuery()) {
			System.out.println("categ");
			while (categoryRs.next()) {
				String categoryName = categoryRs.getString("menu_item_category_name");
				Button categoryButton = createButton(categoryName);
				categoryButton.setStyle("-fx-font-size: 20px; -fx-background-color: #FAFAFA; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0, 0, 4);");

				// Add button to set category status to active
				Button setActiveButton = new Button("Set Active");
				setActiveButton.setStyle("-fx-background-color: #FAFAFA; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0, 0, 4);");
				setActiveButton.setOnAction(event -> handleSetActiveButtonAction(categoryName));
				HBox buttonBox = new HBox(setActiveButton);
				buttonBox.setAlignment(Pos.CENTER_RIGHT);
				buttonBox.setSpacing(5);
				VBox categButton = new VBox(categoryButton, buttonBox);

				// categButton.getChildren().add(buttonBox); // Add set active button to
				// category button
				inactiveCategoryPane.getChildren().add(categButton);

				categoryButton.setDisable(true);
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	private void loadInactiveMenuItems() {
		inactiveMenuItemPane.getChildren().clear(); // Clear existing nodes first

		String menuItemSql = "SELECT menu_item_name FROM menu_item WHERE menu_item_status = 'inactive'";

		try (Connection conn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
				PreparedStatement menuItemPstmt = conn.prepareStatement(menuItemSql);
				ResultSet menuItemRs = menuItemPstmt.executeQuery()) {
			System.out.println("menu");
			while (menuItemRs.next()) {
				String menuItemName = menuItemRs.getString("menu_item_name");
				Button menuItemButton = createButton(menuItemName);
				menuItemButton.setStyle("-fx-font-size: 20px; -fx-background-color: #FAFAFA; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0, 0, 4);");

				// Add button to set menu item status to active
				Button setActiveButton = new Button("Set Active");
				setActiveButton.setStyle("-fx-background-color: #FAFAFA; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0, 0, 4);");
				setActiveButton.setOnAction(event -> handleSetActiveMenuItemAction(menuItemName));
				HBox buttonBox = new HBox(setActiveButton);
				buttonBox.setAlignment(Pos.CENTER_RIGHT);
				buttonBox.setSpacing(5);
				VBox menuItemBox = new VBox(menuItemButton, buttonBox);

				// Add menu item box to the pane
				inactiveMenuItemPane.getChildren().add(menuItemBox);

				menuItemButton.setDisable(true);
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	private void handleSetActiveButtonAction(String categoryName) {
		Alert confirmation = new Alert(AlertType.CONFIRMATION);
		confirmation.setTitle("Activate Category/Menu Item");
		confirmation.setHeaderText("Confirm Activation");
		confirmation.setContentText("Are you sure you want to activate the category/menu item '" + categoryName + "'?");

		Optional<ButtonType> result = confirmation.showAndWait();
		result.ifPresent(buttonType -> {
			if (buttonType == ButtonType.OK) {
				// Update category/menu item status to active in the database
				updateCategoryStatus(categoryName, "active"); // Assuming categoryName can be both category and menu
																// item
			}
		});
		loadInactiveCategories();
	}

	private void handleSetActiveMenuItemAction(String menuItemName) {
		Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
		confirmation.setTitle("Activate Menu Item");
		confirmation.setHeaderText("Confirm Activation");
		confirmation.setContentText("Are you sure you want to activate the menu item '" + menuItemName + "'?");

		Optional<ButtonType> result = confirmation.showAndWait();
		result.ifPresent(buttonType -> {
			if (buttonType == ButtonType.OK) {
				// Update menu item status to active in the database
				updateMenuItemStatus(menuItemName, "active"); // Update the status specifically for menu items
			}
		});
		loadInactiveMenuItems();
	}

	private boolean menuItemExists(String itemName, String categoryName) {
		String sql = "SELECT COUNT(*) AS count FROM menu_item WHERE menu_item_name = ? AND menu_item_category_name = ?";

		try (Connection conn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, itemName);
			pstmt.setString(2, categoryName);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				int count = rs.getInt("count");
				return count > 0;
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		return false;
	}

	private boolean categoryExists(String categoryName) {
		String sql = "SELECT COUNT(*) AS count FROM menu_item_category WHERE menu_item_category_name = ?";

		try (Connection conn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, categoryName);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				int count = rs.getInt("count");
				return count > 0;
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		return false;
	}

	private void showErrorDialog(String title, String header, String content) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

	@FXML
	private void handleRecycleBinButtonAction(ActionEvent event) {
		Label1.setStyle("-fx-font-family: 'Barlow'; -fx-font-size: 20px");
		Label2.setStyle("-fx-font-family: 'Barlow'; -fx-font-size: 20px");
		System.out.println("Recycle Bin button pressed");
		isRecycleBinActive = !isRecycleBinActive;

		if (isRecycleBinActive) {
			Label1.setText("Inactive Categories");
			Label2.setText("Inactive Menu Items");
			Label1.setStyle("-fx-font-family: 'Barlow'; -fx-font-size: 20px");
			Label2.setStyle("-fx-font-family: 'Barlow'; -fx-font-size: 20px");
			Label2.setVisible(true);
			
			loadInactiveMenuItems();
			loadActiveCategories();
			loadInactiveCategories();
			MenuItemsPane.getChildren().clear();
			MenuItemsPane.setDisable(true);
			inactiveMenuItemPane.setDisable(false);
			inactiveCategoryPane.setDisable(false);
		} else {
			Label1.setStyle("-fx-font-family: 'Barlow'; -fx-font-size: 20px");
			Label2.setStyle("-fx-font-family: 'Barlow'; -fx-font-size: 20px");
			Label2.setVisible(false);
			Label1.setVisible(false);

			inactiveCategoryPane.getChildren().clear();
			inactiveMenuItemPane.getChildren().clear();
		}
	}

	private void loadActiveCategories() {
		// Load categories
		CategoryPane.getChildren().clear(); // Clear existing nodes first

		String categorySql = "SELECT menu_item_category_name FROM menu_item_category WHERE menu_item_category_status = 'active'";

		try (Connection conn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
				PreparedStatement categoryPstmt = conn.prepareStatement(categorySql);
				ResultSet categoryRs = categoryPstmt.executeQuery()) {

			while (categoryRs.next()) {
				String categoryName = categoryRs.getString("menu_item_category_name");
				VBox categoryButton = createCategoryButton(categoryName);
				CategoryPane.getChildren().add(categoryButton);
			}

			// Add Recycle Bin button
			Button recycleBinButton = createButton("Recycle Bin");
			recycleBinButton.setStyle("-fx-font-size: 20px;	-fx-background-color: '#FAFAFA';\r\n"
					+ "	-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0, 0, 4);");
			recycleBinButton.setOnAction(event -> handleRecycleBinButtonAction(event));
			CategoryPane.getChildren().add(recycleBinButton);

			// Add Add Category button
			Button addCategoryButton = createButton("+");
			addCategoryButton.setStyle("-fx-font-size: 20px; -fx-background-color: '#FAFAFA';\r\n"
					+ "	-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0, 0, 4);");
			addCategoryButton.setOnAction(event -> handleAddCategoryButtonAction(event));
			CategoryPane.getChildren().add(addCategoryButton);

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		// Load menu items for the initially selected category if there is one
		if (isRecycleBinActive) {
			selectedCategoryButton = null;
			if (selectedCategoryButton != null) {
				String categoryName = selectedCategoryButton.getText();
				selectedCategoryButton.setStyle("-fx-font-size: 20px; -fx-background-color: #FFBA68; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0, 0, 4);");
				loadMenuItemsForCategory(categoryName);
			} else {
				System.out.println("");
			}
		}
	}

	private Button createButton(String categoryName) {
		Button categoryButton = new Button(categoryName);
		categoryButton.getStyleClass().add("category-button"); // Apply default style
		categoryButton.setOnAction(event -> handleCategoryButtonAction(categoryButton));
		categoryButton.setPrefWidth(200); // Set preferred width
		categoryButton.setStyle("-fx-font-size: 20px; -fx-background-color: #FFBA68; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0, 0, 4);"); // Set font size
		VBox.setMargin(categoryButton, new Insets(5)); // Set margins
		return categoryButton;
	}

	private VBox createCategoryButton(String categoryName) {
		Button categoryButton = new Button(categoryName);
		categoryButton.getStyleClass().add("category-button"); // Apply default style
		categoryButton.setOnAction(event -> handleCategoryButtonAction(categoryButton));
		categoryButton.setPrefWidth(200); // Set preferred width
		categoryButton.setStyle("-fx-font-size: 20px; 	-fx-background-color: '#FAFAFA';\r\n"
				+ "	-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0, 0, 4);"); // Set font size
		VBox.setMargin(categoryButton, new Insets(5)); // Set margins

		// Add Edit button for category
		Button editButton = new Button("Edit");
		editButton.setStyle("	-fx-background-color: '#FAFAFA';\r\n"
				+ "	-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0, 0, 4);");
		editButton.setOnAction(event -> handleEditCategoryButtonAction(categoryButton));
		VBox.setMargin(editButton, new Insets(5));

		// Add Remove button for category
		Button removeButton = new Button("Remove");
		removeButton.setStyle("	-fx-background-color: '#FAFAFA';\r\n"
				+ "	-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0, 0, 4);");
		removeButton.setOnAction(event -> handleRemoveCategoryButtonAction(categoryButton));
		VBox.setMargin(removeButton, new Insets(5));

		// Place edit and remove buttons to the right of the category button
		HBox buttonBox = new HBox(editButton, removeButton);
		buttonBox.setAlignment(Pos.CENTER_RIGHT);
		buttonBox.setSpacing(5);
		VBox categoryBox = new VBox(categoryButton, buttonBox);

		return categoryBox;
	}

	private void handleEditCategoryButtonAction(Button categoryButton) {
		String oldCategoryName = categoryButton.getText();
		TextInputDialog dialog = new TextInputDialog(oldCategoryName);
		dialog.setTitle("Edit Category");
		dialog.setHeaderText("Edit Category Name");
		dialog.setContentText("Enter new category name:");

		Optional<String> result = dialog.showAndWait();
		result.ifPresent(newCategoryName -> {
			if (!newCategoryName.equals(oldCategoryName)) {
				if (categoryExists(newCategoryName)) {
					showErrorDialog("Category Error", "Duplicate Category",
							"A category with the same name already exists.");
					return;
				}
				// Update category name in database
				updateCategoryName(oldCategoryName, newCategoryName);
			}
		});
	}

	private void updateCategoryStatus(String categoryName, String status) {
		String sql = "UPDATE menu_item_category SET menu_item_category_status = ? WHERE menu_item_category_name = ?";

		try (Connection conn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, status);
			pstmt.setString(2, categoryName);
			pstmt.executeUpdate();

			System.out.println("Category status updated successfully");

			loadActiveCategories(); // Reload categories after update

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	private void updateCategoryName(String oldName, String newName) {
		String sql = "UPDATE menu_item_category SET menu_item_category_name = ? WHERE menu_item_category_name = ?";

		try (Connection conn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, newName);
			pstmt.setString(2, oldName);
			pstmt.executeUpdate();

			System.out.println("Category name updated successfully");

			loadActiveCategories(); // Reload categories after update

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	private void handleRemoveCategoryButtonAction(Button categoryButton) {
		String categoryName = categoryButton.getText();
		Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
		confirmation.setTitle("Remove Category");
		confirmation.setHeaderText("Confirm Category Removal");
		confirmation.setContentText("Are you sure you want to remove the category '" + categoryName + "'?");

		Optional<ButtonType> result = confirmation.showAndWait();
		result.ifPresent(buttonType -> {
			if (buttonType == ButtonType.OK) {
				// Set category status to inactive in the database
				updateCategoryStatus(categoryName, "inactive");
			}
		});

		if (isRecycleBinActive) {
			loadInactiveCategories();
		} else {
			System.out.print("s");
		}

		if (!isRecycleBinActive) {
			loadActiveCategories();
		}
	}

	private void handleCategoryButtonAction(Button selectedButton) {
		// Toggle selection
		if (selectedCategoryButton != null && selectedCategoryButton != selectedButton) {
			selectedCategoryButton.setStyle("-fx-font-size: 20px; -fx-background-color: #FAFAFA; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0, 0, 4);"); // Reset style of the previously selected button
		}
		selectedCategoryButton = selectedButton;
		selectedButton.setStyle(
				"-fx-font-size: 20px; -fx-background-color: #FFBA68; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0, 0, 4);");
		// Load menu items for the selected category
		String categoryName = selectedButton.getText();
		loadMenuItemsForCategory(categoryName);
		// Enable "Add Menu Item" button when a category is selected
		Label1.setStyle("-fx-font-family: 'Barlow'; -fx-font-size: 20px");
		Label2.setStyle("-fx-font-family: 'Barlow'; -fx-font-size: 20px");
		Label1.setText("Menu Items");
		Label2.setVisible(false);

		inactiveCategoryPane.getChildren().clear();
		inactiveMenuItemPane.getChildren().clear();

		menuIngredients.getChildren().clear();
	}

	private void loadMenuItemsForCategory(String categoryName) {
		MenuItemsPane.getChildren().clear(); // Clear existing nodes first

		String menuItemsSql = "SELECT menu_item_name, menu_item_price FROM menu_item "
				+ "WHERE menu_item_category_name = ? AND menu_item_status = 'active'";

		try (Connection conn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
				PreparedStatement menuItemsPstmt = conn.prepareStatement(menuItemsSql)) {

			menuItemsPstmt.setString(1, categoryName);
			ResultSet menuItemsRs = menuItemsPstmt.executeQuery();

			while (menuItemsRs.next()) {
				String itemName = menuItemsRs.getString("menu_item_name");
				double itemPrice = menuItemsRs.getDouble("menu_item_price");
				Button itemButton = new Button(itemName + " - ₱" + itemPrice);
				itemButton.setStyle("-fx-font-size: 15px;	-fx-background-color: '#FAFAFA';\r\n"
						+ "	-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0, 0, 4);");
				itemButton.setPrefWidth(200); // Set a preferred width for uniform size
				itemButton.setOnAction(event -> handleMenuItemButtonAction(itemButton, itemName, itemPrice));

				// Apply margins if needed
				VBox.setMargin(itemButton, new Insets(5));

				// Add Edit button for menu item
				Button editButton = new Button("Edit");
				editButton.setStyle("	-fx-background-color: '#FAFAFA';\r\n"
						+ "	-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0, 0, 4);");
				editButton.setOnAction(event -> handleEditMenuItemButtonAction(itemButton, itemName, itemPrice));
				VBox.setMargin(editButton, new Insets(5));

				// Add Remove button for menu item
				Button removeButton = new Button("Remove");
				removeButton.setStyle("	-fx-background-color: '#FAFAFA';\r\n"
						+ "	-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0, 0, 4);");
				removeButton.setOnAction(event -> handleRemoveMenuItemButtonAction(itemButton, itemName));
				VBox.setMargin(removeButton, new Insets(5));

				// Place edit and remove buttons to the right of the menu item button
				HBox buttonBox = new HBox(editButton, removeButton);
				buttonBox.setAlignment(Pos.CENTER_RIGHT);
				buttonBox.setSpacing(5);
				VBox menuItemBox = new VBox(itemButton, buttonBox);

				MenuItemsPane.getChildren().add(menuItemBox);
			}

			// Add Add Menu Item button
			Button addMenuItemButton = new Button("+ Add Menu Item");
			addMenuItemButton.setStyle("	-fx-font-size: 15px; -fx-background-color: '#FAFAFA';\r\n"
					+ "	-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0, 0, 4);");
			addMenuItemButton.setPrefWidth(200); // Set a preferred width for uniform size
			addMenuItemButton.setOnAction(event -> handleAddMenuItemButtonAction(event));
			VBox.setMargin(addMenuItemButton, new Insets(0));
			MenuItemsPane.getChildren().add(addMenuItemButton);

			MenuItemsPane.setDisable(false);
			inactiveMenuItemPane.setDisable(true);
			inactiveCategoryPane.setDisable(true);

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	private void handleEditMenuItemButtonAction(Button itemButton, String itemName, double itemPrice) {
		Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
		confirmation.setTitle("Edit Menu Item");
		confirmation.setHeaderText("Confirm Menu Item Edit");
		confirmation.setContentText("Are you sure you want to edit the menu item '" + itemName + "'?");

		Optional<ButtonType> result = confirmation.showAndWait();
		result.ifPresent(buttonType -> {
			if (buttonType == ButtonType.OK) {
				// Prompt user for new name and price
				TextInputDialog nameDialog = new TextInputDialog(itemName);
				nameDialog.setTitle("Edit Menu Item");
				nameDialog.setHeaderText("Edit Menu Item Name");
				nameDialog.setContentText("Enter new menu item name:");

				Optional<String> newName = nameDialog.showAndWait();
				newName.ifPresent(newItemName -> {
					TextInputDialog priceDialog = new TextInputDialog(String.valueOf(itemPrice));
					priceDialog.setTitle("Edit Menu Item");
					priceDialog.setHeaderText("Edit Menu Item Price");
					priceDialog.setContentText("Enter new menu item price:");

					Optional<String> newPrice = priceDialog.showAndWait();
					newPrice.ifPresent(newItemPrice -> {
						try {
							// Check if the update will cause a duplicate
							if (menuItemExists(newItemName, selectedCategoryButton.getText())
									&& !newItemName.equals(itemName)) {
								showErrorDialog("Menu Item Error", "Duplicate Menu Item",
										"A menu item with the same name already exists in this category.");
								return;
							}
							// Update menu item in database
							updateMenuItem(itemName, Double.parseDouble(newItemPrice), newItemName);
						} catch (NumberFormatException e) {
							showErrorDialog("Price Format Error", "Invalid Price",
									"Please enter a valid number for the price.");
						}
					});
				});
			}
		});
	}

	private void handleRemoveMenuItemButtonAction(Button itemButton, String itemName) {
		Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
		confirmation.setTitle("Remove Menu Item");
		confirmation.setHeaderText("Confirm Menu Item Removal");
		confirmation.setContentText("Are you sure you want to remove the menu item '" + itemName + "'?");

		Optional<ButtonType> result = confirmation.showAndWait();
		result.ifPresent(buttonType -> {
			if (buttonType == ButtonType.OK) {
				// Set menu item status to inactive in the database
				updateMenuItemStatus(itemName, "inactive");
			}
		});
	}

	private void updateMenuItem(String oldName, double newPrice, String newName) {
		String sql = "UPDATE menu_item SET menu_item_name = ?, menu_item_price = ? WHERE menu_item_name = ? AND menu_item_category_name = ?";

		try (Connection conn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, newName);
			pstmt.setDouble(2, newPrice);
			pstmt.setString(3, oldName);
			pstmt.setString(4, selectedCategoryButton.getText());
			pstmt.executeUpdate();

			System.out.println("Menu item updated successfully");

			loadMenuItemsForCategory(selectedCategoryButton.getText()); // Reload menu items after update

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	private void updateMenuItemStatus(String itemName, String status) {
		String sql = "UPDATE menu_item SET menu_item_status = ? WHERE menu_item_name = ?";

		try (Connection conn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, status);
			pstmt.setString(2, itemName);
			pstmt.executeUpdate();

			System.out.println("Menu item status updated successfully");

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	private void handleMenuItemButtonAction(Button selectedButton, String itemName, double itemPrice) {
		System.out.println("Menu item button " + itemName + " pressed. Price: ₱" + itemPrice);

		// Toggle selection
		if (selectedMenuItemButton != null && selectedMenuItemButton != selectedButton) {
			selectedMenuItemButton.setStyle("-fx-font-size: 15px; -fx-background-color: #FFBA68; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0, 0, 4);"); // Reset style of the previously selected button
		}
		selectedMenuItemButton = selectedButton;
		selectedButton.setStyle("-fx-font-size: 15px; -fx-background-color: #FFBA68; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0, 0, 4);");

		// Store the selected menu item name
		selectedMenuItemName = itemName;

		// Load ingredients for the selected menu item
		loadIngredientsForMenuItem(itemName);
	}

	public void loadIngredientsForMenuItem(String itemName) {
		menuIngredients.getChildren().clear(); // Clear existing nodes first

		String ingredientsSql = "SELECT menu_item_ingredient_id, ingredient_name, ingredient_quantity_needed, ingredient_unit_of_measurement FROM menu_item_ingredient WHERE menu_item_name = ? AND ingredient_status = 'active' ";

		try (Connection conn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
				PreparedStatement ingredientsPstmt = conn.prepareStatement(ingredientsSql)) {

			ingredientsPstmt.setString(1, itemName);
			ResultSet ingredientsRs = ingredientsPstmt.executeQuery();

			while (ingredientsRs.next()) {
				int ingredientId = ingredientsRs.getInt("menu_item_ingredient_id");
				String ingredientName = ingredientsRs.getString("ingredient_name");
				double quantityNeeded = ingredientsRs.getDouble("ingredient_quantity_needed");
				String measurementUnit = ingredientsRs.getString("ingredient_unit_of_measurement");

				Button ingredientButton = new Button(ingredientName + " - " + quantityNeeded + measurementUnit);
				ingredientButton.setStyle("-fx-font-size: 15px; -fx-background-color: #FAFAFA; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0, 0, 4);");
				ingredientButton.setPrefWidth(200); // Set a preferred width for uniform size

				// Apply margins if needed
				VBox.setMargin(ingredientButton, new Insets(5));

				// Add Remove button for ingredient
				Button removeButton = new Button("Remove");
				removeButton.setStyle("	-fx-background-color: '#FAFAFA';\r\n"
						+ "	-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0, 0, 4);");
				removeButton.setOnAction(event -> handleRemoveIngredientButtonAction(ingredientId));
				VBox.setMargin(removeButton, new Insets(5));

				// Place remove button to the right of the ingredient button
				HBox buttonBox = new HBox(removeButton);
				buttonBox.setAlignment(Pos.CENTER_RIGHT);
				buttonBox.setSpacing(5);

				// Create a container for the ingredient button and its remove button
				VBox ingredientBox = new VBox(ingredientButton, buttonBox);
				ingredientBox.setSpacing(5);

				menuIngredients.getChildren().add(ingredientBox);
			}

			// Add Add Ingredient button at the end (if needed)
			if (AddIngredientButton == null) {
				AddIngredientButton = new Button("Add Ingredient");
				AddIngredientButton.setStyle("	-fx-font-size: 20px; -fx-background-color: '#FAFAFA';\r\n"
						+ "	-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0, 0, 4);");
				AddIngredientButton.setPrefWidth(200); // Set preferred width
				AddIngredientButton.setOnAction(event -> handleAddIngredientButtonAction(event));
			}
			VBox.setMargin(AddIngredientButton, new Insets(5));
			menuIngredients.getChildren().add(AddIngredientButton);

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	private void handleRemoveIngredientButtonAction(int ingredientId) {
		String updateSql = "UPDATE menu_item_ingredient SET ingredient_status = 'inactive' WHERE menu_item_ingredient_id = ?";

		try (Connection conn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
				PreparedStatement updatePstmt = conn.prepareStatement(updateSql)) {

			updatePstmt.setInt(1, ingredientId);
			int rowsAffected = updatePstmt.executeUpdate();

			if (rowsAffected > 0) {
				System.out.println("Ingredient status set to inactive successfully.");
				// Optionally, you can refresh the list of ingredients or update UI as needed
				loadIngredientsForMenuItem(selectedMenuItemName); // Assuming currentMenuItemName is a class variable
			} else {
				System.err.println("Failed to set ingredient status to inactive.");
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	@FXML
	private void handleSaveButtonAction(ActionEvent event) {
		System.out.println("Save button pressed");
		// Implement your save/proceed logic here
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
	void goToRegisterItem(ActionEvent event) {

		try {
			// Load Maintenance.fxml
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/inventorymanagement/view/RegisterItemView.fxml"));
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
	void goToBackup(ActionEvent event) {

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/maintenance/backup/view/BackupView.fxml"));
			Parent root = loader.load();

			Stage stage = (Stage) goToBackupButton.getScene().getWindow();
			Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

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
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/maintenance/createmenu/view/CreateMenuView.fxml"));
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
		Label1.setStyle("-fx-font-family: 'Barlow'; -fx-font-size: 20px");
		Label2.setStyle("-fx-font-family: 'Barlow'; -fx-font-size: 20px");
		Label2.setVisible(false);
		loadActiveCategories();
	}

}
