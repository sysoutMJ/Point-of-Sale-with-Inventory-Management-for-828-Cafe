package POS;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import database.DatabaseConnection;

public class OrderItem {
    private String name;
    private double price;
    private int quantity;

    // Constructor
    public OrderItem() {
        // Default constructor
    }

    public OrderItem(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }
    
 // New method to get the ingredient quantity needed for this order item
    public int getIngredientQuantityNeeded(String ingredientName) {
        String ingredientQuery = "SELECT ingredient_quantity_needed FROM menu_item_ingredient WHERE menu_item_name = ? AND ingredient_name = ?";

		DatabaseConnection dbConnectionInformation = new DatabaseConnection();

        
        try (Connection connection = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
             PreparedStatement ingredientStmt = connection.prepareStatement(ingredientQuery)) {

            ingredientStmt.setString(1, this.name);
            ingredientStmt.setString(2, ingredientName);
            try (ResultSet ingredientRs = ingredientStmt.executeQuery()) {
                if (ingredientRs.next()) {
                    return ingredientRs.getInt("ingredient_quantity_needed");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0; // Default to 0 if not found
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Override toString() if needed for debugging or logging
    @Override
    public String toString() {
        return "OrderItem{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
