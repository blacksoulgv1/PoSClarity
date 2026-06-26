package com.gerardgv.posclarity.models;

public class GarantiaDetalle {
    
    private int idDetalleGarantia;
    private int idGarantias;
    private int idDetalle;
    private int cantidad;

    public GarantiaDetalle() {
    }

    public GarantiaDetalle(int idDetalleGarantia, int idGarantias, int idDetalle, int cantidad) {
        this.idDetalleGarantia = idDetalleGarantia;
        this.idGarantias = idGarantias;
        this.idDetalle = idDetalle;
        this.cantidad = cantidad;
    }

    public int getIdDetalleGarantia() {
        return idDetalleGarantia;
    }

    public void setIdDetalleGarantia(int idDetalleGarantia) {
        this.idDetalleGarantia = idDetalleGarantia;
    }

    public int getIdGarantias() {
        return idGarantias;
    }

    public void setIdGarantias(int idGarantias) {
        this.idGarantias = idGarantias;
    }

    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    
    
    
}
