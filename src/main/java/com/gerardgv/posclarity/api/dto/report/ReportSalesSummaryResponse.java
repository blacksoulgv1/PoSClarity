package com.gerardgv.posclarity.api.dto.report;

import java.math.BigDecimal;

public class ReportSalesSummaryResponse {
    
    private BigDecimal total;
    private BigDecimal paid;
    private BigDecimal pending;

    public ReportSalesSummaryResponse() {
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getPaid() {
        return paid;
    }

    public void setPaid(BigDecimal paid) {
        this.paid = paid;
    }

    public BigDecimal getPending() {
        return pending;
    }

    public void setPending(BigDecimal pending) {
        this.pending = pending;
    }
    
}
