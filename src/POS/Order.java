package POS;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private String orderReferenceCode;
    private double orderTotalAmount;
    private double orderDiscount;
    private double orderGrandTotal;
    private LocalDateTime orderDateTime;
    private String paymentType;
    private String GCashReferenceNumber;
    private double change;
    private List<OrderItem> items;

    public Order() {
        items = new ArrayList<>();
    }

    public String getOrderReferenceCode() {
        return orderReferenceCode;
    }

    public void setOrderReferenceCode(String orderReferenceCode) {
        this.orderReferenceCode = orderReferenceCode;
    }

    public double getOrderTotalAmount() {
        return orderTotalAmount;
    }

    public void setOrderTotalAmount(double orderTotalAmount) {
        this.orderTotalAmount = orderTotalAmount;
    }

    public double getOrderDiscount() {
        return orderDiscount;
    }

    public void setOrderDiscount(double orderDiscount) {
        this.orderDiscount = orderDiscount;
    }

    public double getOrderGrandTotal() {
        return orderGrandTotal;
    }

    public void setOrderGrandTotal(double orderGrandTotal) {
        this.orderGrandTotal = orderGrandTotal;
    }

    public LocalDateTime getOrderDateTime() {
        return orderDateTime;
    }

    public void setOrderDateTime(LocalDateTime orderDateTime) {
        this.orderDateTime = orderDateTime;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getGCashReferenceNumber() {
        return GCashReferenceNumber;
    }

    public void setGCashReferenceNumber(String GCashReferenceNumber) {
        this.GCashReferenceNumber = GCashReferenceNumber;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void addItem(OrderItem item) {
        items.add(item);
    }

    public void removeItem(OrderItem item) {
        items.remove(item);
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderReferenceCode='" + orderReferenceCode + '\'' +
                ", orderTotalAmount=" + orderTotalAmount +
                ", orderDiscount=" + orderDiscount +
                ", orderGrandTotal=" + orderGrandTotal +
                ", orderDateTime=" + orderDateTime +
                ", paymentType='" + paymentType + '\'' +
                ", GCashReferenceNumber='" + GCashReferenceNumber + '\'' +
                ", change=" + change +
                ", items=" + items +
                '}';
    }
}
