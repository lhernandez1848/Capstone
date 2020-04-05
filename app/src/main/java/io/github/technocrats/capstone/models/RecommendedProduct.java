package io.github.technocrats.capstone.models;

public class RecommendedProduct {
    private String product;
    private double quantity;
    private double par;

    public RecommendedProduct(String product, double quantity, double par) {
        this.product = product;
        this.quantity = quantity;
        this.par = par;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getPar() {
        return par;
    }

    public void setPar(double par) {
        this.par = par;
    }
}
