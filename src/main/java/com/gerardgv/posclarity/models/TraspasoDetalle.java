package com.gerardgv.posclarity.models;

public class TraspasoDetalle {
    
    private int idDetalle;
    private int idTranspaso;
    private int idProducto;
    private int cantidad;
    private String modelo;
    private String marca;

    public TraspasoDetalle() {
    }

    public TraspasoDetalle(int idDetalle, int idTranspaso, int idProducto, int cantidad, String modelo, String marca) {
        this.idDetalle = idDetalle;
        this.idTranspaso = idTranspaso;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.modelo = modelo;
        this.marca = marca;
    }

    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public int getIdTranspaso() {
        return idTranspaso;
    }

    public void setIdTranspaso(int idTranspaso) {
        this.idTranspaso = idTranspaso;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    
    
    
    
}
