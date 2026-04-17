package com.gerardgv.posclarity.models;

public class TicketItem {
    
    private int cantidad;
    private String descripcion;
    private double precio;
    private double monto;

    public TicketItem() {
    }

    public TicketItem(int cantidad, String descripcion, double precio, double monto) {
        this.cantidad = cantidad;
        this.descripcion = descripcion;
        this.precio = precio;
        this.monto = monto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }
    
    
    
}
