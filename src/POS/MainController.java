package POS;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import database.DatabaseConnection;
import inventorymanagement.model.RegisterItemDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainController {

    @FXML
    private FlowPane order;

    private Order currentOrder;
    private double subtotal;
    private double discount;
    private double grandTotal;
    private boolean isPWDDiscountApplied;
    private boolean isSeniorDiscountApplied;
    private Button selectedCategoryButton; // Field to store the currently selected category button

    @FXML
    private AnchorPane mainAnchorPane;

    @FXML
    private Pane sidePane;

    @FXML
    private Pane topPane;

    @FXML
    private Pane rightPane;

    @FXML
    private FlowPane MenuItemsPane;

    @FXML
    private FlowPane categoryPane;

    @FXML
    private Button posButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Button userManualButton;

    @FXML
    private Button PWDdiscountButton;

    @FXML
    private Button SeniorDiscountButton;

    @FXML
    private Text subtotalText;
    @FXML
    private Text discountText;
    @FXML
    private Text grandTotalText;

    @FXML
    private Button proceedButton;

    private List<String> categories;

    @FXML
    private void initialize() {
        currentOrder = new Order();
        subtotal = 0.0;
        discount = 0.0;
        grandTotal = 0.0;
        isPWDDiscountApplied = false;
        isSeniorDiscountApplied = false;
        loadCategories();
        loadAllMenuItems();
        updateTotals();
    }

    private void loadAllMenuItems() {
        loadMenuItems(null);
    }
    
    public static String formatDouble(double value) {
        // Create DecimalFormat object with two decimal places pattern
        DecimalFormat df = new DecimalFormat("#0.00");
        
        // Format the double value
        String formatted = df.format(value);
        
        return formatted;
    }

    private void loadMenuItems(String categoryFilter) {
        MenuItemsPane.getChildren().clear();

        String query;
        if (categoryFilter == null) {
            query = "SELECT * FROM menu_item WHERE menu_item_status = 'active'";
        } else {
            query = "SELECT * FROM menu_item WHERE menu_item_status = 'active' AND menu_item_category_name = ?";
        }

        try (Connection connection = DatabaseHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            if (categoryFilter != null) {
                statement.setString(1, categoryFilter);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("menu_item_id");
                    String name = resultSet.getString("menu_item_name");
                    double price = resultSet.getDouble("menu_item_price");
                    String category = resultSet.getString("menu_item_category_name");
                    
                    if (categories == null) {
                        categories = new ArrayList<>();
                    }
                    if (!categories.contains(category)) {
                        categories.add(category);
                    }

                    // Check if the menu item has ingredients
                    boolean hasIngredients = hasIngredients(name);
                    
                    String formattedPrice = formatDouble(price);

                    Button menuItemButton = new Button(name + "\n₱" + formattedPrice);
                    menuItemButton.setStyle("-fx-font-size: 25px; -fx-background-color: #FAFAFA; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0, 0, 4);"); // Set font size to 25
                    
                    // Check ingredient stock sufficiency
                    if (isIngredientStockSufficientWithUnitComparison(name)) {
                        menuItemButton.setOnAction(e -> addToOrder(name, price));
                    } else {
                        menuItemButton.setDisable(true);
                        
                    }

                    if (hasIngredients) {
                        menuItemButton.setOnAction(e -> addToOrder(name, price));
                    } else {
                        menuItemButton.setDisable(true);
                    }

                    FlowPane.setMargin(menuItemButton, new Insets(10));
                    MenuItemsPane.getChildren().add(menuItemButton);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    private boolean hasIngredients(String menuItemName) {
        String query = "SELECT COUNT(*) AS count FROM menu_item_ingredient WHERE menu_item_name = ?";

        try (Connection connection = DatabaseHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, menuItemName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt("count");
                    return count > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // Default to false if there's an error or no ingredients found
    }

    private boolean isIngredientStockSufficientWithUnitComparison(String menuItemName) {
        String ingredientQuery = "SELECT ingredient_name, ingredient_quantity_needed, ingredient_unit_of_measurement FROM menu_item_ingredient WHERE menu_item_name = ?";
        String stockQuery = "SELECT current_stock, item_unit_of_measurement FROM inventory WHERE item_name = ?";

        try (Connection connection = DatabaseHelper.getConnection();
             PreparedStatement ingredientStmt = connection.prepareStatement(ingredientQuery)) {

            ingredientStmt.setString(1, menuItemName);
            try (ResultSet ingredientRs = ingredientStmt.executeQuery()) {
                while (ingredientRs.next()) {
                    String ingredientName = ingredientRs.getString("ingredient_name");
                    int quantityNeeded = ingredientRs.getInt("ingredient_quantity_needed");
                    String ingredientUnit = ingredientRs.getString("ingredient_unit_of_measurement");

                    try (PreparedStatement stockStmt = connection.prepareStatement(stockQuery)) {
                        stockStmt.setString(1, ingredientName);
                        try (ResultSet stockRs = stockStmt.executeQuery()) {
                            if (stockRs.next()) {
                                int currentStock = stockRs.getInt("current_stock");
                                String originalUnit = stockRs.getString("item_unit_of_measurement");

                                // Check if units match, or convert if necessary
                                if (ingredientUnit.equals(originalUnit)) {
                                    if (currentStock < quantityNeeded) {
                                        return false; // Stock is insufficient
                                    }
                                } else {
                                    // Convert quantity needed to match stock unit of measurement
                                    double convertedQuantityNeeded = convertQuantityToStockUnit(ingredientUnit, quantityNeeded, originalUnit);
                                    System.out.print(convertedQuantityNeeded);
                                    if (currentStock < convertedQuantityNeeded) {
                                        return false; // Stock is insufficient after conversion
                                    }
                                }
                            } else {
                                return false; // Ingredient not found in inventory
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true; // All ingredients have sufficient stock
    }

    private void loadCategories() {
        categoryPane.getChildren().clear();

        String query = "SELECT * FROM menu_item_category WHERE menu_item_category_status = 'active'";

        try (Connection connection = DatabaseHelper.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String categoryName = resultSet.getString("menu_item_category_name");

                Button categoryButton = new Button(categoryName);
                categoryButton.setStyle("-fx-font-size: 20px;	-fx-background-color: '#FAFAFA';\r\n"
                		+ "	-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0, 0, 4);");
                categoryButton.setOnAction(e -> {
                    updateCategorySelection(categoryButton);
                    loadMenuItems(categoryName);
                });
                FlowPane.setMargin(categoryButton, new Insets(5));
                categoryPane.getChildren().add(categoryButton);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateOrder(Order updatedOrder) {
        this.currentOrder = updatedOrder;
        refreshOrder();
        calculateTotals();
        updateTotals();
    }

    public void setDiscounts(boolean isPWDDiscountApplied , boolean isSeniorDiscountApplied) {
        this.isSeniorDiscountApplied = isSeniorDiscountApplied;
        this.isPWDDiscountApplied = isPWDDiscountApplied;
        updateButtons(isPWDDiscountApplied ,isSeniorDiscountApplied);
        calculateTotals();
    }
    
    private boolean canAddToOrder(String itemName) {
        String ingredientQuery = "SELECT ingredient_name, ingredient_quantity_needed, ingredient_unit_of_measurement FROM menu_item_ingredient WHERE menu_item_name = ?";
        String stockQuery = "SELECT current_stock, item_unit_of_measurement FROM inventory WHERE item_name = ?";
        
        try (Connection connection = DatabaseHelper.getConnection();
             PreparedStatement ingredientStmt = connection.prepareStatement(ingredientQuery)) {

            ingredientStmt.setString(1, itemName);
            try (ResultSet ingredientRs = ingredientStmt.executeQuery()) {
                while (ingredientRs.next()) {
                    String ingredientName = ingredientRs.getString("ingredient_name");
                    int quantityNeeded = ingredientRs.getInt("ingredient_quantity_needed");
                    String ingredientUnit = ingredientRs.getString("ingredient_unit_of_measurement");
                    int totalRequired = quantityNeeded;
                    
                    OrderItem existingItem = findOrderItemByName(itemName);
                    if (existingItem != null) {
                        totalRequired += existingItem.getQuantity() * quantityNeeded;
                    }

                    try (PreparedStatement stockStmt = connection.prepareStatement(stockQuery)) {
                        stockStmt.setString(1, ingredientName);
                        try (ResultSet stockRs = stockStmt.executeQuery()) {
                            if (stockRs.next()) {
                                int currentStock = stockRs.getInt("current_stock");
                                String originalUnit = stockRs.getString("item_unit_of_measurement");

                                // Check if units match, or convert if necessary
                                if (ingredientUnit.equals(originalUnit)) {
                                    if (currentStock < totalRequired) {
                                        return false; // Stock is insufficient
                                    }
                                } else {
                                    // Convert quantity needed to match stock unit of measurement
                                    double convertedQuantityNeeded = convertQuantityToStockUnit(ingredientUnit, totalRequired, originalUnit);
                                    if (currentStock < convertedQuantityNeeded) {
                                        return false; // Stock is insufficient after conversion
                                    }
                                }
                            } else {
                                return false; // Ingredient not found in inventory
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true; // All ingredients have sufficient stock
    }

    private double convertQuantityToStockUnit(String ingredientUnit, int quantityNeeded, String originalUnit) {
        switch (ingredientUnit) {
            case "Grams (g)":
                    return gramsToKilograms(quantityNeeded);
            case "Kilograms (kg)":
                    return quantityNeeded; // Already in kilograms
            case "Table Spoon (tbsp)":
                if (originalUnit.equals("Liter (L)")) {
                    return tbspToLiters(quantityNeeded);
                } else {
                    return tbspToKilograms(quantityNeeded);
                }
            case "Tea Spoon (tsp)":
                if (originalUnit.equals("Liter (L)")) {
                    return tspToLiters(quantityNeeded);
                } else {
                    return tspToKilograms(quantityNeeded);
                }
            case "Milliliters (ml)":
                    return mlToLiters(quantityNeeded);
            default:
                return quantityNeeded; // Default to original quantity needed
        }
    }


    private double gramsToKilograms(int grams) {
        return grams / 1000.0;
    }

    private double tbspToLiters(int tbsp) {
        return tbsp * 0.015;
    }

    private double tspToLiters(int tsp) {
        return tsp * 0.005;
    }

    private double mlToLiters(int ml) {
        return ml / 1000.0;
    }
    
    private double tbspToKilograms(double tbsp) {
	    return tbsp * 0.015;
	}

	private double tspToKilograms(double tsp) {
	    return tsp * 0.005;
	}



    private void addToOrder(String itemName, double itemPrice) {
        if (canAddToOrder(itemName)) {
            OrderItem existingItem = findOrderItemByName(itemName);
            if (existingItem != null) {
                existingItem.setQuantity(existingItem.getQuantity() + 1);
            } else {
                OrderItem newItem = new OrderItem(itemName, itemPrice, 1);
                currentOrder.addItem(newItem);
            }
            calculateTotals();
            updateTotals();
            refreshOrder();
        } else {
            // Show error dialog if the ingredient stock is insufficient
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Insufficient Stock");
            alert.setHeaderText("Cannot add item to order");
            alert.setContentText("The stock for the required ingredients is insufficient.");
            alert.showAndWait();
        }
    }


    private OrderItem findOrderItemByName(String itemName) {
        for (OrderItem item : currentOrder.getItems()) {
            if (item.getName().equals(itemName)) {
                return item;
            }
        }
        return null;
    }

    private Pane createOrderItemPane(OrderItem item) {
        Pane itemPane = new Pane();
        itemPane.setStyle("-fx-background-color: #ffffff; -fx-padding: 5px;");

        Text itemName = new Text(item.getName());
        itemName.setStyle("-fx-font-family: 'Barlow'; -fx-font-size: 17px;");
        itemName.setLayoutX(10);
        itemName.setLayoutY(20);
        
        Double price = item.getPrice();
        String formattedPrice = formatDouble(price);
        
        Text itemPrice = new Text("₱" + formattedPrice);
        itemPrice.setStyle("-fx-font-family: 'Barlow'; -fx-font-size: 17px;");
        itemPrice.setLayoutX(150);
        itemPrice.setLayoutY(20);

        // Label to display quantity
        Text quantityText = new Text(String.valueOf(item.getQuantity()));
        quantityText.setStyle("-fx-font-family: 'Barlow'; -fx-font-size: 17px;");
        quantityText.setLayoutX(250);
        quantityText.setLayoutY(20);

        // Buttons to increase and decrease quantity
        Button increaseButton = new Button("+");
        increaseButton.setStyle("-fx-background-color: #D4FFC1; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0, 0, 4);");
        increaseButton.setLayoutX(270);
        increaseButton.setLayoutY(20);
        increaseButton.setOnAction(e -> {
            if (canAddToOrder(item.getName())) {
                item.setQuantity(item.getQuantity() + 1);
                quantityText.setText(String.valueOf(item.getQuantity()));
                refreshOrder();
            } else {
                // Show error dialog if the ingredient stock is insufficient
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Insufficient Stock");
                alert.setHeaderText("Cannot increase quantity");
                alert.setContentText("The stock for the required ingredients is insufficient.");
                alert.showAndWait();
            }
        });

        Button decreaseButton = new Button("-");
        decreaseButton.setStyle("-fx-background-color: #FFC0C0; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0, 0, 4);");
        decreaseButton.setLayoutX(300);
        decreaseButton.setLayoutY(20);
        decreaseButton.setOnAction(e -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                quantityText.setText(String.valueOf(item.getQuantity()));
                refreshOrder();
            }
        });

        Button removeButton = new Button("Remove");
        removeButton.setStyle("-fx-background-color: #FAFAFA; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0, 0, 4);");
        removeButton.setLayoutX(330);
        removeButton.setLayoutY(15);
        removeButton.setOnAction(e -> {
            currentOrder.removeItem(item);
            calculateTotals();
            updateTotals();
            refreshOrder();
        });

        itemPane.getChildren().addAll(itemName, itemPrice, quantityText, increaseButton, decreaseButton, removeButton);
        return itemPane;
    }

    private void calculateTotals() {
        subtotal = currentOrder.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        if (isPWDDiscountApplied) {
            discount = subtotal * 0.20;
        } else if (isSeniorDiscountApplied) {
            discount = subtotal * 0.10;
        } else {
            discount = 0.0;
        }
        grandTotal = subtotal - discount;
        updateTotals();
    }

    private void refreshOrder() {
        order.getChildren().clear();
        for (OrderItem item : currentOrder.getItems()) {
            Pane itemPane = createOrderItemPane(item);
            order.getChildren().add(itemPane);
        }
        calculateTotals();
    }

    private void updateTotals() {
        subtotalText.setText("₱" + String.format("%.2f", subtotal));
        discountText.setText("₱" + String.format("%.2f", discount));
        grandTotalText.setText("₱" + String.format("%.2f", grandTotal));
    }

    private void updateButtons(boolean isPWDDiscountApplied, boolean isSeniorDiscountApplied) {
        if (isPWDDiscountApplied) {
            PWDdiscountButton.setStyle("-fx-background-color: #FFBA68;");
            isSeniorDiscountApplied = false;
            SeniorDiscountButton.setStyle("");
        } 
        else if(isSeniorDiscountApplied) {
            PWDdiscountButton.setStyle("-fx-background-color: #FFBA68;");
            isSeniorDiscountApplied = false;
            SeniorDiscountButton.setStyle("");
        }
        calculateTotals();
    }

    @FXML
    private void handlePOSButtonAction() {
        // Handle Point of Sale button action
    }

    @FXML
    private void handlePWDdiscountButton() {
        isPWDDiscountApplied = !isPWDDiscountApplied;
        if (isPWDDiscountApplied) {
            PWDdiscountButton.setStyle("-fx-background-color: #FFBA68;");
            isSeniorDiscountApplied = false;
            SeniorDiscountButton.setStyle("");
        } else {
            PWDdiscountButton.setStyle("");
        }
        calculateTotals();
    }

    @FXML
    private void handleSeniorDiscountButton() {
        isSeniorDiscountApplied = !isSeniorDiscountApplied;
        if (isSeniorDiscountApplied) {
            SeniorDiscountButton.setStyle("-fx-background-color: #FFBA68;");
            isPWDDiscountApplied = false;
            PWDdiscountButton.setStyle("");
        } else {
            SeniorDiscountButton.setStyle("");
        }
        calculateTotals();
    }
    
    @FXML
    private void goToHome() {
    	try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/help/view/HelpStaffLandingPageView.fxml"));
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
			Stage stage = (Stage) logoutButton.getScene().getWindow();
			Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    private void updateCategorySelection(Button selectedButton) {
        if (selectedCategoryButton != null && selectedCategoryButton != selectedButton) {
            selectedCategoryButton.setStyle("-fx-background-color: #FAFAFA; -fx-font-size: 20px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0, 0, 4);"); // Reset style of the previously selected button
        }
        selectedCategoryButton = selectedButton;
        selectedButton.setStyle("-fx-background-color: #FFBA68; -fx-font-size: 20px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0, 0, 4);" );
    }

    @FXML
    private void handleProceedButtonAction() {
        if (currentOrder.getItems().isEmpty()) {
            // Order is empty, show error dialog
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Empty Order");
            alert.setContentText("Please add items to the order before proceeding.");
            alert.showAndWait();
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("POS2.fxml"));
                Parent root = loader.load();
                POS2Controller pos2Controller = loader.getController();
                pos2Controller.setData(this, currentOrder, subtotal, discount, grandTotal, isPWDDiscountApplied, isSeniorDiscountApplied); // Pass the main controller and order data
                
                Stage stage = (Stage) proceedButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
