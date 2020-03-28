package io.github.technocrats.capstone.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {

    private String productId;
    private String productName;
    private float unitCost;
    private float quantity;
    private int subcategory;
    private int category;

    public Product(String productId, String productName, float unitCost, float quantity, int subcategory, int category) {
        this.productId = productId;
        this.productName = productName;
        this.unitCost = unitCost;
        this.quantity = quantity;
        this.subcategory = subcategory;
        this.category = category;
    }

    public Product (Parcel parcel) {
        productId = parcel.readString();
        productName = parcel.readString();
        unitCost = parcel.readFloat();
        quantity = parcel.readFloat();
        subcategory = parcel.readInt();
        category = parcel.readInt();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    //
    // getters and setters
    //
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

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public int getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(int subcategory) {
        this.subcategory = subcategory;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        // write object values to parcel for storage - follow order of constructor
        dest.writeString(productId);
        dest.writeString(productName);
        dest.writeFloat(unitCost);
        dest.writeFloat(quantity);
        dest.writeInt(subcategory);
        dest.writeInt(category);
    }
}
