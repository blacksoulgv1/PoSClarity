package com.gerardgv.posclarity.models;


public class Product {
    
    private Integer id;
    private String model;
    private String brand;
    private String category;
    private double  price;
    private Boolean manageStock;
    private Boolean baseLens;
    private String productType;
    private Boolean active;
   
    private Integer stock;
    
    public Product(){
    }

    public Product(Integer id, String model, String brand, String category, double price, Boolean manageStock, Boolean baseLens, String productType, Boolean active) {
        this.id = id;
        this.model = model;
        this.brand = brand;
        this.category = category;
        this.price = price;
        this.manageStock = manageStock;
        this.baseLens = baseLens;
        this.productType = productType;
        this.active = active;
    }
   

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }    

    public Boolean getManageStock() {
        return manageStock;
    }

    public void setManageStock(Boolean manageStock) {
        this.manageStock = manageStock;
    }

    public Boolean getBaseLens() {
        return baseLens;
    }

    public void setBaseLens(Boolean baseLens) {
        this.baseLens = baseLens;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    
 }