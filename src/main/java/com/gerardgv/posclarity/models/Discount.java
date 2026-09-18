package com.gerardgv.posclarity.models;

import java.time.LocalDate;

public class Discount {
    
    private int id;
    private String name;
    private Integer  benefitProductId;
    private String benefitProductModel;
    private boolean requireFrame;
    private Double maxDiopter;
    private String applicationType;
    private String valueType;
    private Double value;
    private String couponCode;
    private String category;
    private LocalDate startDate;
    private LocalDate endDate;
    private int priority;
    private boolean active;
        
    public Discount() {
    }

    public Discount(int id, String name, Integer benefitProductId, String benefitProductModel, boolean requireFrame, Double maxDiopter, String applicationType, String valueType, Double value, String couponCode, String category, LocalDate startDate, LocalDate endDate, int priority, boolean active) {
        this.id = id;
        this.name = name;
        this.benefitProductId = benefitProductId;
        this.benefitProductModel = benefitProductModel;
        this.requireFrame = requireFrame;
        this.maxDiopter = maxDiopter;
        this.applicationType = applicationType;
        this.valueType = valueType;
        this.value = value;
        this.couponCode = couponCode;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
        this.priority = priority;
        this.active = active;
    }

    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    
    
    public String getBenefitProductModel() {
        return benefitProductModel;
    }

    public void setBenefitProductModel(String benefitProductModel) {
        this.benefitProductModel = benefitProductModel;
    }

    public boolean isRequireFrame() {
        return requireFrame;
    }

    public void setRequireFrame(boolean requireFrame) {
        this.requireFrame = requireFrame;
    }

    public Double getMaxDiopter() {
        return maxDiopter;
    }

    public void setMaxDiopter(Double maxDiopter) {
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

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    
}
