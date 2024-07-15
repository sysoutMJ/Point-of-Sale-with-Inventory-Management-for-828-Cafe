package report.controller;

import java.time.LocalDate;
import javafx.beans.property.*;

public class StockReportItem {

    private final StringProperty itemName;
    private final StringProperty itemBrand;
    private final StringProperty itemSupplier;
    private final IntegerProperty previousStock;
    private final IntegerProperty currentStock;
    private final IntegerProperty newItemBatchQuantity;
    private final DoubleProperty newItemBatchCost;
    private final StringProperty itemStatus;
    private final ObjectProperty<LocalDate> dateOfPurchase;
    private final ObjectProperty<LocalDate> newItemBatchExpirationDate;

    public StockReportItem(String itemName, String itemBrand, String itemSupplier, int previousStock, int currentStock,
                           int newItemBatchQuantity, double newItemBatchCost, String itemStatus,
                           LocalDate dateOfPurchase, LocalDate newItemBatchExpirationDate) {
        this.itemName = new SimpleStringProperty(itemName);
        this.itemBrand = new SimpleStringProperty(itemBrand);
        this.itemSupplier = new SimpleStringProperty(itemSupplier);
        this.previousStock = new SimpleIntegerProperty(previousStock);
        this.currentStock = new SimpleIntegerProperty(currentStock);
        this.newItemBatchQuantity = new SimpleIntegerProperty(newItemBatchQuantity);
        this.newItemBatchCost = new SimpleDoubleProperty(newItemBatchCost);
        this.itemStatus = new SimpleStringProperty(itemStatus);
        this.dateOfPurchase = new SimpleObjectProperty<>(dateOfPurchase);
        this.newItemBatchExpirationDate = new SimpleObjectProperty<>(newItemBatchExpirationDate);
    }

    public StringProperty itemNameProperty() {
        return itemName;
    }

    public StringProperty itemBrandProperty() {
        return itemBrand;
    }

    public StringProperty itemSupplierProperty() {
        return itemSupplier;
    }

    public IntegerProperty previousStockProperty() {
        return previousStock;
    }

    public IntegerProperty currentStockProperty() {
        return currentStock;
    }

    public IntegerProperty newItemBatchQuantityProperty() {
        return newItemBatchQuantity;
    }

    public DoubleProperty newItemBatchCostProperty() {
        return newItemBatchCost;
    }

    public StringProperty itemStatusProperty() {
        return itemStatus;
    }

    public ObjectProperty<LocalDate> dateOfPurchaseProperty() {
        return dateOfPurchase;
    }

    public ObjectProperty<LocalDate> newItemBatchExpirationDateProperty() {
        return newItemBatchExpirationDate;
    }
}
