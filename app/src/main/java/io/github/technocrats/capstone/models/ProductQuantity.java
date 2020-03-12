package io.github.technocrats.capstone.models;

public class ProductQuantity
{
    private String product;
    private String quantity;

    public ProductQuantity(String product, String quantity)
    {
        this.product = product;
        this.quantity = quantity;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}