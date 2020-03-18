package io.github.technocrats.capstone.models;

public class Product {

    private String productId;
    private String productName;
    private float unitCost;

    public Product(String productId, String productName, float unitCost) {
        this.productId = productId;
        this.productName = productName;
        this.unitCost = unitCost;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
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

}
