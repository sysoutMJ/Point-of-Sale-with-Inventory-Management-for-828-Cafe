package maintenance.createmenu.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;

import database.DatabaseConnection;

public class AddIngredientController implements Initializable {

    @FXML
    private VBox itemCategoryPane;

    @FXML
    private VBox ingredientsPane;

    @FXML
    private Button confirmButton;
    
    @FXML
    private MenuButton unitofMeasurement;
    
    DatabaseConnection dbConnectionInformation = new DatabaseConnection();

    @FXML
    private TextField quantityNeeded; // Assuming this is the TextField for quantity input

    private String menuItemName;
    private int ingredientQuantity; // Variable to hold ingredient quantity

    private boolean selectedIngredient;

    private String unitOfMeasurement;
    private String ingredientBrand;
    private String ingredientSupplier;
    private int currentStock;

    private Button selectedCategoryButton;
    private Button selectedItemButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadCategoriesFromDatabase();
    }

    public void setMenuItemName(String menuItemName) {
        this.menuItemName = menuItemName;
    }

    private void loadCategoriesFromDatabase() {
    	

        try (Connection conn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT item_category_name FROM category")) {

            itemCategoryPane.getChildren().clear();

            while (rs.next()) {
                String categoryName = rs.getString("item_category_name");
                Button categoryButton = new Button(categoryName);
                categoryButton.setStyle("-fx-font-size: 18px; -fx-background-color: #FAFAFA; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0, 0, 4);");
                categoryButton.setOnAction(event -> handleCategoryButton(categoryButton, categoryName));
                itemCategoryPane.getChildren().add(categoryButton);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleCategoryButton(Button categoryButton, String categoryName) {
        if (selectedCategoryButton != null) {
            selectedCategoryButton.setStyle("-fx-font-size: 18px; -fx-background-color: #FAFAFA; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0, 0, 4);"); // Reset style of previously selected category button
        }
        selectedCategoryButton = categoryButton;
        selectedCategoryButton.setStyle("-fx-font-size: 18px; -fx-background-color: #FFBA68; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0, 0, 4);");

        loadItemsFromCategory(categoryName);
    }

    private void loadItemsFromCategory(String categoryName) {


        String query = "SELECT item_name, item_unit_of_measurement, item_brand, item_supplier, current_stock FROM inventory WHERE item_category = ?";

        try (Connection conn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, categoryName);
            try (ResultSet rs = stmt.executeQuery()) {

                ingredientsPane.getChildren().clear();

                while (rs.next()) {
                    String itemName = rs.getString("item_name");
                    unitOfMeasurement = rs.getString("item_unit_of_measurement");
                    ingredientBrand = rs.getString("item_brand");
                    ingredientSupplier = rs.getString("item_supplier");
                    currentStock = rs.getInt("current_stock");

                    Button itemButton = new Button(currentStock + " " + unitOfMeasurement + " - " + itemName + " - " + ingredientBrand + " - " + ingredientSupplier);
                    itemButton.setStyle("-fx-font-size: 18px; -fx-background-color: #FAFAFA; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0, 0, 4);");
                    itemButton.setOnAction(event -> handleItemButton(itemButton, itemName));
                    ingredientsPane.getChildren().add(itemButton);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleItemButton(Button itemButton, String itemName) {
        if (selectedItemButton != null) {
            selectedItemButton.setStyle("-fx-font-size: 18px; -fx-background-color: #FAFAFA; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0, 0, 4);"); // Reset style of previously selected item button
        }
        selectedItemButton = itemButton;
        selectedItemButton.setStyle("-fx-font-size: 18px; -fx-background-color: #FFBA68; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0, 0, 4);");
        
        populateUnitOfMeasurementMenu(selectedItemButton);
    }

    private void insertIngredientData(String itemName) {

        String quantityText = quantityNeeded.getText();
        if (!quantityText.isEmpty()) {
            ingredientQuantity = Integer.parseInt(quantityText);
        } else {
            showAlert(AlertType.ERROR, "Error", "Ingredient quantity is empty.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword())) {

            String checkQuery = "SELECT ingredient_status, ingredient_quantity_needed FROM menu_item_ingredient WHERE menu_item_name = ? AND ingredient_name = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, menuItemName);
                checkStmt.setString(2, itemName);

                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        String status = rs.getString("ingredient_status");
                        int existingQuantity = rs.getInt("ingredient_quantity_needed");
                        if ("inactive".equalsIgnoreCase(status)) {
                            Optional<ButtonType> result = showConfirmation("Confirmation", "Ingredient is inactive. Set it back to active?");
                            if (result.isPresent() && result.get() == ButtonType.OK) {
                                setIngredientActive(conn, itemName);
                            }
                        } else {
                            Optional<ButtonType> updateQuantity = showConfirmation("Confirmation", "Ingredient already exists and is active. Do you want to update the quantity?");
                            if (updateQuantity.isPresent() && updateQuantity.get() == ButtonType.OK) {
                                updateIngredientQuantity(conn, itemName);
                            }
                        }
                        return;
                    }
                }
            }

            Optional<ButtonType> confirmation = showConfirmation("Confirmation", "Are you sure you want to add this ingredient?");
            if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
                String insertQuery = "INSERT INTO menu_item_ingredient (menu_item_name, ingredient_name, ingredient_unit_of_measurement, ingredient_brand, ingredient_supplier, ingredient_quantity_needed, ingredient_status, ingredient_datetime_of_added) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                    insertStmt.setString(1, menuItemName);
                    insertStmt.setString(2, itemName);
                    insertStmt.setString(3, unitofMeasurement.getText());
                    insertStmt.setString(4, ingredientBrand);
                    insertStmt.setString(5, ingredientSupplier);
                    insertStmt.setInt(6, ingredientQuantity);
                    insertStmt.setString(7, "active");
                    insertStmt.setTimestamp(8, new Timestamp(System.currentTimeMillis()));

                    int rowsAffected = insertStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        showAlert(AlertType.INFORMATION, "Success", "Ingredient data inserted successfully.");
                        closeDialog();
                    } else {
                        showAlert(AlertType.ERROR, "Error", "Failed to insert ingredient data.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void populateUnitOfMeasurementMenu(Button selectedItemButton) {
        unitofMeasurement.getItems().clear(); // Clear existing items
        
        String[] parts = selectedItemButton.getText().split(" ");
        String originalUnitOfMeasurement = parts[1] + " " + parts[2];
        
        // Populate menu items based on the unit of measurement
        if (originalUnitOfMeasurement.equals("Kilograms (kg)")) {
            unitofMeasurement.getItems().addAll(
                    new MenuItem("Kilograms (kg)"),
                    new MenuItem("Grams (g)"),
                    new MenuItem("Table Spoon (tbsp)"),
                    new MenuItem("Tea Spoon (tsp)")
            );
        } else if (originalUnitOfMeasurement.equals("Liters (L)")) {
            unitofMeasurement.getItems().addAll(
                    new MenuItem("Milliliters (ml)"),
                    new MenuItem("Liters (L)"),
                    new MenuItem("Table Spoon (tbsp)"),
                    new MenuItem("Tea Spoon (tsp)")
            );
        } else if (originalUnitOfMeasurement.equals("Pieces (pcs)")) {
            unitofMeasurement.getItems().addAll(
                    new MenuItem("Slices"),
                    new MenuItem("Packets"),
                    new MenuItem("Pieces (pcs)")
            );
        }

        
     // Add action handlers for menu items
	    for (MenuItem item : unitofMeasurement.getItems()) {
	        item.setOnAction(e -> unitofMeasurement.setText(item.getText()));
	    }
        
        // Optionally, set a default selection
        // unitofMeasurement.setText("Default Unit"); // Set default text if needed
    }


    private void setIngredientActive(Connection conn, String itemName) throws SQLException {
        String updateQuery = "UPDATE menu_item_ingredient SET ingredient_status = 'active' WHERE menu_item_name = ? AND ingredient_name = ?";
        try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
            updateStmt.setString(1, menuItemName);
            updateStmt.setString(2, itemName);
            int rowsAffected = updateStmt.executeUpdate();
            if (rowsAffected > 0) {
                showAlert(AlertType.INFORMATION, "Success", "Ingredient set to active successfully.");
                closeDialog();
            } else {
                showAlert(AlertType.ERROR, "Error", "Failed to set ingredient to active.");
            }
        }
    }

    private void updateIngredientQuantity(Connection conn, String itemName) throws SQLException {
        String updateQuery = "UPDATE menu_item_ingredient SET ingredient_quantity_needed = ? WHERE menu_item_name = ? AND ingredient_name = ?";
        try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
            updateStmt.setInt(1, ingredientQuantity);
            updateStmt.setString(2, menuItemName);
            updateStmt.setString(3, itemName);
            int rowsAffected = updateStmt.executeUpdate();
            if (rowsAffected > 0) {
                showAlert(AlertType.INFORMATION, "Success", "Ingredient quantity updated successfully.");
                closeDialog();
            } else {
                showAlert(AlertType.ERROR, "Error", "Failed to update ingredient quantity.");
            }
        }
    }

    @FXML
    void confirmAction(ActionEvent event) {
        if (selectedItemButton != null) {
            String itemName = selectedItemButton.getText().split(" - ")[1]; // Assuming the item name is the second part of the button text
            insertIngredientData(itemName);
        } else {
            showAlert(AlertType.ERROR, "Error", "No item selected.");
        }
    }

    private Optional<ButtonType> showConfirmation(String title, String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeDialog() {
        Stage stage = (Stage) confirmButton.getScene().getWindow();
        stage.close();
    }
}
