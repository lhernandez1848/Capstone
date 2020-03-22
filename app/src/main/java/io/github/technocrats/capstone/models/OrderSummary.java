package io.github.technocrats.capstone.models;

public class OrderSummary {

    private float quantity;
    private String productName;
    private String unitCost;

    public OrderSummary(String productName, float quantity, String unitCost) {
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
