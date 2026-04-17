package com.gerardgv.posclarity.models;

public class SaleItem {
    
    private Product producto;
    private String nombreDescuento;
    private int cantidad;
    private double descuento;
    private String descripcion;
    private double precio;

    public SaleItem() {
    }

    public SaleItem(Product producto, String nombreDescuento, double descuento) {
        this.producto = producto;
        this.nombreDescuento = nombreDescuento;
        this.cantidad = 1;
        this.descuento = descuento;
    }   

    public String getNombreDescuento() {
        return nombreDescuento;
    }

    public void setNombreDescuento(String nombreDescuento) {
        this.nombreDescuento = nombreDescuento;
    }
    
    public Product getProducto() {
        return producto;
    }

    public void setProducto(Product producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }
    
    public double getSubtotal(){
        double total = producto.getPrecio() * cantidad;
        return total - (descuento * cantidad);
    }
    
    public String getDescripcion(){
        return descripcion != null ? descripcion : producto.getModelo();
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public double getPrecio(){
        return precio != 0 ? precio : producto.getPrecio();
    }
    
    public void setPrecio(double precio) {
        this.precio = precio;
}
}
