package com.gerardgv.posclarity.api.dto.discount;


import com.gerardgv.posclarity.models.Discount;
import java.math.BigDecimal;
import java.time.LocalDate;


public class DiscountCreateRequest {
    
    private String name;
    private Integer benefitProductId;
    private Boolean requireFrame;
    private BigDecimal maxDiopter;
    private String applicationType;
    private String valueType;
    private BigDecimal value;
    private String couponCode;
    private String category;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer priority;
    private Boolean active;
    
   public DiscountCreateRequest() {
    }

    public DiscountCreateRequest(Discount d) {

        this.name = d.getName();
        this.benefitProductId = d.getBenefitProductId();
        this.requireFrame = d.isRequireFrame();

        if (d.getMaxDiopter() != null) {
            this.maxDiopter = BigDecimal.valueOf(d.getMaxDiopter());
        }

        this.applicationType = d.getApplicationType();
        this.valueType = d.getValueType();

        if (d.getValue() != null) {
            this.value = BigDecimal.valueOf(d.getValue());
        }

        this.couponCode = d.getCouponCode();
        this.category = d.getCategory();
        this.startDate = d.getStartDate();
        this.endDate = d.getEndDate();
        this.priority = d.getPriority();
        this.active = d.isActive();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBenefitProductId() {
        return benefitProductId;
    }

    public void setBenefitProductId(Integer benefitProductId) {
        this.benefitProductId = benefitProductId;
    }

    public Boolean getRequireFrame() {
        return requireFrame;
    }

    public void setRequireFrame(Boolean requireFrame) {
        this.requireFrame = requireFrame;
    }

    public BigDecimal getMaxDiopter() {
        return maxDiopter;
    }

    public void setMaxDiopter(BigDecimal maxDiopter) {
        this.maxDiopter = maxDiopter;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    } 
}
