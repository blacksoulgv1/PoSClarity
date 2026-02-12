package com.gerardgv.posclarity.models;

public class Clients {
    
    private int id;
    private String nombre;
    private String telefono;
    private String direccion;
    private String odEsf, odCil, odEje;
    private String oiEsf, oiCil, oiEje;
    private String add;
    private boolean activo = true;

    public Clients() {
    }
    
    public String getGraduacionCompleta(){
        return "OD: " + odEsf + " / " + odCil + " x " + odEje +
              "\nOI: " + oiEsf + " / " + oiCil + " x " + oiEje +
                "\nADD: " + add;
    }
    
    public Clients(int id, String nombre, String telefono, String direccion, String odEsf, String odCil, String odEje, String oiEsf, String oiCil, String oiEje, String add) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
        this.odEsf = odEsf;
        this.odCil = odCil;
        this.odEje = odEje;
        this.oiEsf = oiEsf;
        this.oiCil = oiCil;
        this.oiEje = oiEje;
        this.add = add;
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getOdEsf() {
        return odEsf;
    }

    public void setOdEsf(String odEsf) {
        this.odEsf = odEsf;
    }

    public String getOdCil() {
        return odCil;
    }

    public void setOdCil(String odCil) {
        this.odCil = odCil;
    }

    public String getOdEje() {
        return odEje;
    }

    public void setOdEje(String odEje) {
        this.odEje = odEje;
    }

    public String getOiEsf() {
        return oiEsf;
    }

    public void setOiEsf(String oiEsf) {
        this.oiEsf = oiEsf;
    }

    public String getOiCil() {
        return oiCil;
    }

    public void setOiCil(String oiCil) {
        this.oiCil = oiCil;
    }

    public String getOiEje() {
        return oiEje;
    }

    public void setOiEje(String oiEje) {
        this.oiEje = oiEje;
    }

    public String getAdd() {
        return add;
    }

    public void setAdd(String add) {
        this.add = add;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    
    
}