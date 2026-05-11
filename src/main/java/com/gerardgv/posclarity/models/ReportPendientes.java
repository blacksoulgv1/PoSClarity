package com.gerardgv.posclarity.models;

public class ReportPendientes {
    
    private int trabajosRealizar;
    private int trabajosEntregar;
    private Double saldoRealizar;
    private Double saldoEntregar;

    public ReportPendientes() {
    }

    public ReportPendientes(int trabajosRealizar, int trabajosEntregar, Double saldoRealizar, Double saldoEntregar) {
        this.trabajosRealizar = trabajosRealizar;
        this.trabajosEntregar = trabajosEntregar;
        this.saldoRealizar = saldoRealizar;
        this.saldoEntregar = saldoEntregar;
    }

    public int getTrabajosRealizar() {
        return trabajosRealizar;
    }

    public void setTrabajosRealizar(int trabajosRealizar) {
        this.trabajosRealizar = trabajosRealizar;
    }

    public int getTrabajosEntregar() {
        return trabajosEntregar;
    }

    public void setTrabajosEntregar(int trabajosEntregar) {
        this.trabajosEntregar = trabajosEntregar;
    }

    public Double getSaldoRealizar() {
        return saldoRealizar;
    }

    public void setSaldoRealizar(Double saldoRealizar) {
        this.saldoRealizar = saldoRealizar;
    }

    public Double getSaldoEntregar() {
        return saldoEntregar;
    }

    public void setSaldoEntregar(Double saldoEntregar) {
        this.saldoEntregar = saldoEntregar;
    }
    
    
    
}
