package io.github.technocrats.capstone.models;

public class OrderItem {

    private String orderId;
    private String productName;
    private float quantity;
    private float unitCost;

    public OrderItem(String orderId, String productName, float quantity, float unitCost) {
        this.orderId = orderId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitCost = unitCost;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public float getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(float unitCost) {
        this.unitCost = unitCost;
    }
}
