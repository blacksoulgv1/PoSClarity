package com.gerardgv.posclarity.models;

import java.time.LocalDate;

public class Descuento {
    
    private int id;
    private String nombre;
    private String tipoAplicacion;
    private String tipoValor;
    private double valor;
    private String codigoCupon;
    private String categoria;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private int prioridad;
    private boolean activo = true;
        
    public Descuento() {
    }

    public Descuento(int id, String nombre, String tipoAplicacion, String tipoValor, double valor, String codigoCupon, String categoria, LocalDate fechaInicio, LocalDate fechaFin, int prioridad) {
        this.id = id;
        this.nombre = nombre;
        this.tipoAplicacion = tipoAplicacion;
        this.tipoValor = tipoValor;
        this.valor = valor;
        this.codigoCupon = codigoCupon;
        this.categoria = categoria;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.prioridad = prioridad;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoAplicacion() {
        return tipoAplicacion;
    }

    public void setTipoAplicacion(String tipoAplicacion) {
        this.tipoAplicacion = tipoAplicacion;
    }

    public String getTipoValor() {
        return tipoValor;
    }

    public void setTipoValor(String tipoValor) {
        this.tipoValor = tipoValor;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getCodigoCupon() {
        return codigoCupon;
    }

    public void setCodigoCupon(String codigoCupon) {
        this.codigoCupon = codigoCupon;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }    
   
}
