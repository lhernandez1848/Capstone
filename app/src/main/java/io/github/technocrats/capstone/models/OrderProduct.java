package io.github.technocrats.capstone.models;

public class OrderProduct {
    private int orderItemId;
    private String orderId;
    private String productName;
    private float unitCost;
    private float quantity;

    public OrderProduct (String orderId, String productName, float unitCost, float quantity) {
        this.orderId = orderId;
        this.productName = productName;
        this.unitCost = unitCost;
        this.quantity = quantity;
    }

    public int getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
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

    public float getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(float unitCost) {
        this.unitCost = unitCost;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }
}
