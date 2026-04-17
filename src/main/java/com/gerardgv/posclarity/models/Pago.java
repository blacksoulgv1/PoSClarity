package com.gerardgv.posclarity.models;

import java.time.LocalDateTime;

public class Pago {
    
    private String metodo;
    private double monto;
    private String referencia;
    private LocalDateTime fecha;

    public Pago() {
    }

    public Pago(String metodo, double monto, String referencia, LocalDateTime fecha) {
        this.metodo = metodo;
        this.monto = monto;
        this.referencia = referencia;
        this.fecha = fecha;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }   

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
    
    
    
}
