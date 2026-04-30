package com.gerardgv.posclarity.models;

import java.time.LocalDateTime;

public class ClientStats {
    
    private int totalCompras;
    private double totalGastado;
    private LocalDateTime ultimavisita;
    private LocalDateTime clienteDesde;

    public ClientStats() {
    }

    public ClientStats(int totalCompras, double totalGastado, LocalDateTime ultimavisita, LocalDateTime clienteDesde) {
        this.totalCompras = totalCompras;
        this.totalGastado = totalGastado;
        this.ultimavisita = ultimavisita;
        this.clienteDesde = clienteDesde;
    }

    public int getTotalCompras() {
        return totalCompras;
    }

    public void setTotalCompras(int totalCompras) {
        this.totalCompras = totalCompras;
    }

    public double getTotalGastado() {
        return totalGastado;
    }

    public void setTotalGastado(double totalGastado) {
        this.totalGastado = totalGastado;
    }

    public LocalDateTime getUltimavisita() {
        return ultimavisita;
    }

    public void setUltimavisita(LocalDateTime ultimavisita) {
        this.ultimavisita = ultimavisita;
    }

    public LocalDateTime getClienteDesde() {
        return clienteDesde;
    }

    public void setClienteDesde(LocalDateTime clienteDesde) {
        this.clienteDesde = clienteDesde;
    }   
}
