package com.gerardgv.posclarity.api.dto.sale;

import java.math.BigDecimal;


public class PaymentCreateRequest {
    
    private Integer saleId;
    private String paymentMethod;
    private BigDecimal amount;
    private String reference;

    public PaymentCreateRequest() {
    }

    public Integer getSaleId() {
        return saleId;
    }

    public void setSaleId(Integer saleId) {
        this.saleId = saleId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
    
    
}
