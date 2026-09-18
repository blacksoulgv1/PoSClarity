package com.gerardgv.posclarity.api.dto.goal;

import java.math.BigDecimal;

public class GoalRequest {
    
    private Integer month;
    private Integer year;
    private BigDecimal amount;

    public GoalRequest() {
    }

    public GoalRequest(
            Integer month,
            Integer year,
            BigDecimal amount) {

        this.month = month;
        this.year = year;
        this.amount = amount;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
}
