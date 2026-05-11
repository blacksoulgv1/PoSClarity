package com.gerardgv.posclarity.models;

public class Meta {
    
    private int idMeta;
    private int idSucursal;
    private int mes;
    private int anio;
    private double monto;
    private String nombreSucursal;

    public Meta() {
    }

    public Meta(int idMeta, int idSucursal, int mes, int anio, double monto, String nombreSucursal) {
        this.idMeta = idMeta;
        this.idSucursal = idSucursal;
        this.mes = mes;
        this.anio = anio;
        this.monto = monto;
        this.nombreSucursal = nombreSucursal;
    }

    public int getIdMeta() {
        return idMeta;
    }

    public void setIdMeta(int idMeta) {
        this.idMeta = idMeta;
    }

    public int getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getNombreSucursal() {
        return nombreSucursal;
    }

    public void setNombreSucursal(String nombreSucursal) {
        this.nombreSucursal = nombreSucursal;
    }
    
    
    
}
