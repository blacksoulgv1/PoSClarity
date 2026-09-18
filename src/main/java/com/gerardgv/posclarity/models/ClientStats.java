package com.gerardgv.posclarity.models;

import java.time.LocalDateTime;

public class ClientStats {

    private int totalPurchases;
    private double totalSpent;
    private LocalDateTime lastVisit;
    private LocalDateTime clientSince;

    public ClientStats() {
    }

    public ClientStats(
            int totalPurchases,
            double totalSpent,
            LocalDateTime lastVisit,
            LocalDateTime clientSince) {

        this.totalPurchases = totalPurchases;
        this.totalSpent = totalSpent;
        this.lastVisit = lastVisit;
        this.clientSince = clientSince;
    }

    public int getTotalPurchases() {
        return totalPurchases;
    }

    public void setTotalPurchases(int totalPurchases) {
        this.totalPurchases = totalPurchases;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(double totalSpent) {
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

    // =====================================================
    // COMPATIBILIDAD TEMPORAL
    // =====================================================

    @Deprecated
    public int getTotalCompras() {
        return getTotalPurchases();
    }

    @Deprecated
    public void setTotalCompras(int totalCompras) {
        setTotalPurchases(totalCompras);
    }

    @Deprecated
    public double getTotalGastado() {
        return getTotalSpent();
    }

    @Deprecated
    public void setTotalGastado(double totalGastado) {
        setTotalSpent(totalGastado);
    }

    @Deprecated
    public LocalDateTime getUltimavisita() {
        return getLastVisit();
    }

    @Deprecated
    public void setUltimavisita(LocalDateTime ultimavisita) {
        setLastVisit(ultimavisita);
    }

    @Deprecated
    public LocalDateTime getClienteDesde() {
        return getClientSince();
    }

    @Deprecated
    public void setClienteDesde(LocalDateTime clienteDesde) {
        setClientSince(clienteDesde);
    }
}