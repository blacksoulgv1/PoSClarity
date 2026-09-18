package com.gerardgv.posclarity.models;

public class ReportePendienteRow {
    
    private String entregarTexto;
    private String realizarTexto;

    public ReportePendienteRow() {
    }

    public ReportePendienteRow(String entregarTexto, String realizarTexto) {
        this.entregarTexto = entregarTexto;
        this.realizarTexto = realizarTexto;
    }

    public String getEntregarTexto() {
        return entregarTexto;
    }

    public void setEntregarTexto(String entregarTexto) {
        this.entregarTexto = entregarTexto;
    }

    public String getRealizarTexto() {
        return realizarTexto;
    }

    public void setRealizarTexto(String realizarTexto) {
        this.realizarTexto = realizarTexto;
    }
}
