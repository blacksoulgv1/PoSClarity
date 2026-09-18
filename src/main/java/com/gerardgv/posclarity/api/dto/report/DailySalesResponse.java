package com.gerardgv.posclarity.api.dto.report;

import java.math.BigDecimal;


public class DailySalesResponse {
    
    private Integer day;
    private BigDecimal total;

    public DailySalesResponse() {
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    
}
