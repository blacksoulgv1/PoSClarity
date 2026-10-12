package com.gerardgv.posclarity.models;

import java.math.BigDecimal;


public class Insurance {
    
    private Integer id;
    private String name;
    private BigDecimal coveragePercentage;
    private Boolean active;

    public Insurance() {
    }

    public Insurance(Integer id, String name, BigDecimal coveragePercentage, Boolean active) {
        this.id = id;
        this.name = name;
        this.coveragePercentage = coveragePercentage;
        this.active = active;
    }

    

    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    

    public BigDecimal getCoveragePercentage() {
        return coveragePercentage;
    }

    public void setCoveragePercentage(BigDecimal coveragePercentage) {
        this.coveragePercentage = coveragePercentage;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
    
    
}

