package com.gerardgv.posclarity.api.dto.sale;

import java.math.BigDecimal;


public class SaleDetailRequest {
    
    private Integer productId;
    private Integer quantity;
    private String discountName;
    private BigDecimal unitPrice;
    private BigDecimal appliedDiscount;

    public SaleDetailRequest() {
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
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
    
    
}
