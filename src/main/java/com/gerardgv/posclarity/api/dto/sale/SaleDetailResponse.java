package com.gerardgv.posclarity.api.dto.sale;

import java.math.BigDecimal;


public class SaleDetailResponse {
    
    private Integer id;
    private Integer productId;
    private String productName;
    private Integer quantity;
    private String discountName;
    private BigDecimal unitPrice;
    private BigDecimal appliedDiscount;
    private BigDecimal subtotal;

    public SaleDetailResponse() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getDiscountName() {
        return discountName;
    }

    public void setDiscountName(String discountName) {
        this.discountName = discountName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getAppliedDiscount() {
        return appliedDiscount;
    }

    public void setAppliedDiscount(BigDecimal appliedDiscount) {
        this.appliedDiscount = appliedDiscount;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    
}
