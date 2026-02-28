package com.gerardgv.posclarity.models;

public class Product {
    
    private int id_product;
    private String modelo;
    private String marca;
    private String categoria;
    private double precio;
    private boolean manejaStock;
    private int stock;
    private boolean micaBase;
    private String tipo_producto; // elemento fisico o bajo_pedido
    private boolean activo;
   
    public Product(){
    }

    public Product(int id_product, String modelo, String marca, String categoria, double precio, boolean manejaStock, int stock, boolean micaBase, String tipo_producto, boolean activo) {
        this.id_product = id_product;
        this.modelo = modelo;
        this.marca = marca;
        this.categoria = categoria;
        this.precio = precio;
        this.manejaStock = manejaStock;
        this.stock = stock;
        this.micaBase = micaBase;
        this.tipo_producto = tipo_producto;
        this.activo = activo;
    }

    public int getId_product() {
        return id_product;
    }

    public void setId_product(int id_product) {
        this.id_product = id_product;
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public boolean isManejaStock() {
        return manejaStock;
    }

    public void setManejaStock(boolean manejaStock) {
        this.manejaStock = manejaStock;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public boolean isMicaBase() {
        return micaBase;
    }

    public void setMicaBase(boolean micaBase) {
        this.micaBase = micaBase;
    }

    public String getTipo_producto() {
        return tipo_producto;
    }

    public void setTipo_producto(String tipo_producto) {
        this.tipo_producto = tipo_producto;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    

}