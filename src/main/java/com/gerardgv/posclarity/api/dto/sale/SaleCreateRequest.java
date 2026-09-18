package com.gerardgv.posclarity.api.dto.sale;

import java.math.BigDecimal;
import java.util.List;


public class SaleCreateRequest {
    
    private Integer branchId;
    private Integer sellerId;
    private Integer clientId;
    private Integer discountId;

    private String folio;

    private String odEsf;
    private String odCil;
    private String odEje;

    private String oiEsf;
    private String oiCil;
    private String oiEje;

    private String addLens;

    private String promotionName;
    private String paymentMethod;
    private BigDecimal paymentAmount;
    private String paymentReference;

    private List<SaleDetailRequest> details;
    
     public SaleCreateRequest() {
    }

    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Integer getDiscountId() {
        return discountId;
    }

    public void setDiscountId(Integer discountId) {
        this.discountId = discountId;
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

    public String getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public List<SaleDetailRequest> getDetails() {
        return details;
    }

    public void setDetails(List<SaleDetailRequest> details) {
        this.details = details;
    }
}
