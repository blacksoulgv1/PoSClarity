package com.gerardgv.posclarity.api.dto.sale;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class SaleResponse {
    
    private Integer id;

    private Integer branchId;
    private String branchName;

    private Integer discountId;
    private String promotionName;

    private Integer sellerId;
    private String sellerName;

    private Integer clientId;
    private String clientName;

    private String folio;

    private String odEsf;
    private String odCil;
    private String odEje;

    private String oiEsf;
    private String oiCil;
    private String oiEje;

    private String addLens;

    private LocalDateTime saleDate;
    private LocalDateTime deliveryDate;
    private LocalDateTime receivedDate;

    private BigDecimal grossTotal;
    private BigDecimal totalDiscount;
    private BigDecimal finalTotal;
    private BigDecimal paid;
    private BigDecimal remaining;

    private String paymentStatus;
    private String workStatus;

    private LocalDateTime createdAt;

    private List<SaleDetailResponse> details;
    private List<PaymentResponse> payments;
    
    public SaleResponse() {
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

    public Integer getDiscountId() {
        return discountId;
    }

    public void setDiscountId(Integer discountId) {
        this.discountId = discountId;
    }

    public String getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getOdEsf() {
        return odEsf;
    }

    public void setOdEsf(String odEsf) {
        this.odEsf = odEsf;
    }

    public String getOdCil() {
        return odCil;
    }

    public void setOdCil(String odCil) {
        this.odCil = odCil;
    }

    public String getOdEje() {
        return odEje;
    }

    public void setOdEje(String odEje) {
        this.odEje = odEje;
    }

    public String getOiEsf() {
        return oiEsf;
    }

    public void setOiEsf(String oiEsf) {
        this.oiEsf = oiEsf;
    }

    public String getOiCil() {
        return oiCil;
    }

    public void setOiCil(String oiCil) {
        this.oiCil = oiCil;
    }

    public String getOiEje() {
        return oiEje;
    }

    public void setOiEje(String oiEje) {
        this.oiEje = oiEje;
    }

    public String getAddLens() {
        return addLens;
    }

    public void setAddLens(String addLens) {
        this.addLens = addLens;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public LocalDateTime getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(LocalDateTime receivedDate) {
        this.receivedDate = receivedDate;
    }

    public BigDecimal getGrossTotal() {
        return grossTotal;
    }

    public void setGrossTotal(BigDecimal grossTotal) {
        this.grossTotal = grossTotal;
    }

    public BigDecimal getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(BigDecimal totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public BigDecimal getFinalTotal() {
        return finalTotal;
    }

    public void setFinalTotal(BigDecimal finalTotal) {
        this.finalTotal = finalTotal;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(String workStatus) {
        this.workStatus = workStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<SaleDetailResponse> getDetails() {
        return details;
    }

    public void setDetails(List<SaleDetailResponse> details) {
        this.details = details;
    }
    
    public BigDecimal getPaid() {
        return paid;
    }

    public void setPaid(BigDecimal paid) {
        this.paid = paid;
    }

    public BigDecimal getRemaining() {
        return remaining;
    }

    public void setRemaining(BigDecimal remaining) {
        this.remaining = remaining;
    }

    public List<PaymentResponse> getPayments() {
        return payments;
    }

    public void setPayments(List<PaymentResponse> payments) {
        this.payments = payments;
    }
    
    
}
