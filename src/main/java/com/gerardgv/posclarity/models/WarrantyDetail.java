package com.gerardgv.posclarity.models;

public class WarrantyDetail {
    
    private int id;
    private int warrantyId;
    private int detailId;
    private int quantity;
   
    public WarrantyDetail() {
    }

    public WarrantyDetail(
            int id,
            int warrantyId,
            int detailId,
            int quantity) {

        this.id = id;
        this.warrantyId = warrantyId;
        this.detailId = detailId;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWarrantyId() {
        return warrantyId;
    }

    public void setWarrantyId(int warrantyId) {
        this.warrantyId = warrantyId;
    }

    public int getDetailId() {
        return detailId;
    }

    public void setDetailId(int detailId) {
        this.detailId = detailId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
