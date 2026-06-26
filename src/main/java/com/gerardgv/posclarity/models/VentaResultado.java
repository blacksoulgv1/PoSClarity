package com.gerardgv.posclarity.models;

public class VentaResultado {
    
    private int idVenta;
    private String folio;

    public VentaResultado() {
    }

    public VentaResultado(int idVenta, String folio) {
        this.idVenta = idVenta;
        this.folio = folio;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }   
       
}
