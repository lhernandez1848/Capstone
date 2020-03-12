package io.github.technocrats.capstone.models;

public class Order {

    private String orderId;
    private int day;
    private int month;
    private int year;
    private float totalCost;
    private int statusId;
    private int storeId;

    public Order(String orderId, int day, int month, int year, float totalCost, int statusId, int storeId) {
        this.orderId = orderId;
        this.day = day;
        this.month = month;
        this.year = year;
        this.totalCost = totalCost;
        this.statusId = statusId;
        this.storeId = storeId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public float getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(float totalCost) {
        this.totalCost = totalCost;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }
}
