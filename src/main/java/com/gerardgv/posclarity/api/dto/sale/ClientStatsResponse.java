package com.gerardgv.posclarity.api.dto.sale;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ClientStatsResponse {
    
    private int totalPurchases;
    private BigDecimal totalSpent;
    private LocalDateTime lastVisit;
    private LocalDateTime clientSince;
    
    public ClientStatsResponse() {
    }

    public int getTotalPurchases() {
        return totalPurchases;
    }

    public void setTotalPurchases(int totalPurchases) {
        this.totalPurchases = totalPurchases;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }

    public LocalDateTime getLastVisit() {
        return lastVisit;
    }

    public void setLastVisit(LocalDateTime lastVisit) {
        this.lastVisit = lastVisit;
    }

    public LocalDateTime getClientSince() {
        return clientSince;
    }

    public void setClientSince(LocalDateTime clientSince) {
        this.clientSince = clientSince;
    }
    
}
