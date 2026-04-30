package com.gerardgv.posclarity.models;

public class TicketItem {
    
    private int cantidad;
    private String descripcion;
    private double precio;
    private double monto;
    private String promo;

    public TicketItem() {
    }

    public TicketItem(int cantidad, String descripcion, double precio, double monto, String promo) {
        this.cantidad = cantidad;
        this.descripcion = descripcion;
        this.precio = precio;
        this.monto = monto;
        this.promo = promo;
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

    public String getPromo() {
        return promo;
    }

    public void setPromo(String promo) {
        this.promo = promo;
    }        
    
}
