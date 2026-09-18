package com.gerardgv.posclarity.models;

public class TransferDetail {
    
    private int id;
    private int transferId;
    private int productId;
    private int quantity;
    private String model;
    private String brand;

    public TransferDetail() {
    }

    public TransferDetail(
            int id,
            int transferId,
            int productId,
            int quantity,
            String model,
            String brand) {

        this.id = id;
        this.transferId = transferId;
        this.productId = productId;
        this.quantity = quantity;
        this.model = model;
        this.brand = brand;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
   
}
