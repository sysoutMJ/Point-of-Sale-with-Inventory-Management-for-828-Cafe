package POS;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ReceiptController{

    @FXML
    private Label changeVal;

    @FXML
    private Label disVal;

    @FXML
    private Label gtotVal;

    @FXML
    private VBox itemBox;

    @FXML
    private Label payVal;

    @FXML
    private VBox qtyBox;

    @FXML
    private Label refVal;

    @FXML
    private VBox totBox;

    @FXML
    private Label totVal;

	private POS2Controller POS2Controller;

	private Order currentOrder;
    
    public void setData(POS2Controller POS2Controller, Order currentOrder) {
        this.POS2Controller = POS2Controller; // Set the reference to MainController
        this.currentOrder = currentOrder;
        
        populateItems();
    }
    
    private void populateItems() {
        itemBox.getChildren().clear();
        qtyBox.getChildren().clear();
        totBox.getChildren().clear();

        for (OrderItem item : currentOrder.getItems()) {
            // Create labels for item name, quantity, and total
            Label itemNameLabel = new Label(item.getName());
            Label qtyLabel = new Label(String.valueOf(item.getQuantity()));
            double total = item.getPrice() * item.getQuantity(); // Calculate total price
            Label totalLabel = new Label(String.format("₱%.2f", total));

            // Add labels to corresponding VBoxes
            itemBox.getChildren().add(itemNameLabel);
            qtyBox.getChildren().add(qtyLabel);
            totBox.getChildren().add(totalLabel);
        }
    }
    
    public void setChangeVal(double change) {
        changeVal.setText(String.format("₱%.2f", change));
    }

    public void setDisVal(Double value) {
        disVal.setText(String.format("₱%.2f", value));
    }

    public void setGtotVal(Double value) {
        gtotVal.setText(String.format("₱%.2f", value));
    }

    public void setPayVal(Double value) {
        payVal.setText(String.format("₱%.2f", value));
    }

    public void setRefVal(int value) {
        refVal.setText(String.format("%d", value));
    }

    public void setTotVal(Double value) {
        totVal.setText(String.format("₱%.2f", value));
    }
}
