package io.github.technocrats.capstone.models;

public class OrderItem {

    private String productName;
    private int quantity;
    private float unitCost;

    public OrderItem(String productName, int quantity, float unitCost) {
        this.productName = productName;
        this.quantity = quantity;
        this.unitCost = unitCost;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(float unitCost) {
        this.unitCost = unitCost;
    }
}
