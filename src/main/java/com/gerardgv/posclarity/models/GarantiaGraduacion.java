package com.gerardgv.posclarity.models;


public class GarantiaGraduacion {
    
    private int idGraduacion;
    private int idGarantias;
    private String tipo;
    private String odEsfera;
    private String odCilindro;
    private String odEje;
    private String odAdd;
    private String oiEsfera;
    private String oiCilindro;
    private String oiEje;
    private String oiAdd;

    public GarantiaGraduacion() {
    }

    public GarantiaGraduacion(int idGraduacion, int idGarantias, String tipo, String odEsfera, String odCilindro, String odEje, String odAdd, String oiEsfera, String oiCilindro, String oiEje, String oiAdd) {
        this.idGraduacion = idGraduacion;
        this.idGarantias = idGarantias;
        this.tipo = tipo;
        this.odEsfera = odEsfera;
        this.odCilindro = odCilindro;
        this.odEje = odEje;
        this.odAdd = odAdd;
        this.oiEsfera = oiEsfera;
        this.oiCilindro = oiCilindro;
        this.oiEje = oiEje;
        this.oiAdd = oiAdd;
    }

    public int getIdGraduacion() {
        return idGraduacion;
    }

    public void setIdGraduacion(int idGraduacion) {
        this.idGraduacion = idGraduacion;
    }

    public int getIdGarantias() {
        return idGarantias;
    }

    public void setIdGarantias(int idGarantias) {
        this.idGarantias = idGarantias;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getOdEsfera() {
        return odEsfera;
    }

    public void setOdEsfera(String odEsfera) {
        this.odEsfera = odEsfera;
    }

    public String getOdCilindro() {
        return odCilindro;
    }

    public void setOdCilindro(String odCilindro) {
        this.odCilindro = odCilindro;
    }

    public String getOdEje() {
        return odEje;
    }

    public void setOdEje(String odEje) {
        this.odEje = odEje;
    }

    public String getOdAdd() {
        return odAdd;
    }

    public void setOdAdd(String odAdd) {
        this.odAdd = odAdd;
    }

    public String getOiEsfera() {
        return oiEsfera;
    }

    public void setOiEsfera(String oiEsfera) {
        this.oiEsfera = oiEsfera;
    }

    public String getOiCilindro() {
        return oiCilindro;
    }

    public void setOiCilindro(String oiCilindro) {
        this.oiCilindro = oiCilindro;
    }

    public String getOiEje() {
        return oiEje;
    }

    public void setOiEje(String oiEje) {
        this.oiEje = oiEje;
    }

    public String getOiAdd() {
        return oiAdd;
    }

    public void setOiAdd(String oiAdd) {
        this.oiAdd = oiAdd;
    }
    
    
    
}
