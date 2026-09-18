package com.gerardgv.posclarity.models;

public class TicketItem {
    
    private int quantity;
    private String description;
    private double price;
    private double amount;
    private String promotion;

    public TicketItem() {
    }

    public TicketItem(
            int quantity,
            String description,
            double price,
            double amount,
            String promotion) {
        this.quantity = quantity;
        this.description = description;
        this.price = price;
        this.amount = amount;
        this.promotion = promotion;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPromotion() {
        return promotion;
    }

    public void setPromotion(String promotion) {
        this.promotion = promotion;
    }
}
