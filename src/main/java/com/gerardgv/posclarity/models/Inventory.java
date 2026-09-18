package com.gerardgv.posclarity.models;

public class Inventory {
    
    private Integer id;
    private Integer branchId;
    private String branchName;
    private Integer productId;
    private String productModel;
    private String productBrand;
    private Integer stock;
    private String updateAt;


    public Inventory() {
    }       

    public Inventory(Integer id, Integer branchId, String branchName, Integer productId, String productModel, String productBrand, Integer stock, String updateAt) {
        this.id = id;
        this.branchId = branchId;
        this.branchName = branchName;
        this.productId = productId;
        this.productModel = productModel;
        this.productBrand = productBrand;
        this.stock = stock;
        this.updateAt = updateAt;
    }    

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductModel() {
        return productModel;
    }

    public void setProductModel(String productModel) {
        this.productModel = productModel;
    }

    public String getProductBrand() {
        return productBrand;
    }

    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }
    
    
}
