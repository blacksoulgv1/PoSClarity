package com.gerardgv.posclarity.models;

public class ReporteVentas {
    
    private double total;
    private double pagado;
    private double pendiente;

    public ReporteVentas() {
    }

    public ReporteVentas(double total, double pagado, double pendiente) {
        this.total = total;
        this.pagado = pagado;
        this.pendiente = pendiente;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getPagado() {
        return pagado;
    }

    public void setPagado(double pagado) {
        this.pagado = pagado;
    }

    public double getPendiente() {
        return pendiente;
    }

    public void setPendiente(double pendiente) {
        this.pendiente = pendiente;
    }
    
    
    
}
