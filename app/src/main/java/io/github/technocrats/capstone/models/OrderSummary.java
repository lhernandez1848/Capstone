package io.github.technocrats.capstone.models;

public class OrderSummary {

    private float quantity;
    private String productName;
    private String unitCost;

    public OrderSummary(float quantity, String productName, String unitCost) {
        this.productName = productName;
        this.unitCost = unitCost;
        this.quantity = quantity;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(String unitCost) {
        this.unitCost = unitCost;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
