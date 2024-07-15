package notification.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class LowStockNotificationController {

    @FXML
    private Label itemDetailsLabel;

    public void initData(String itemName, String itemCategory, String itemBrand, String itemSupplier) {
        String itemDetails = itemName + " - " + itemCategory + " - " + itemBrand + " - " + itemSupplier;
        itemDetailsLabel.setText(itemDetails);
    }
}
