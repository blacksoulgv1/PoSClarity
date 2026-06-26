
package com.gerardgv.posclarity.models;

public class Branch {
    
    private int id;
    private String sucursal;
    private String telefono;
    private String direccion;
    private boolean activo;

    public Branch() {
    }

    public Branch(int id, String sucursal, String telefono, String direccion, boolean activo) {
        this.id = id;
        this.sucursal = sucursal;
        this.telefono = telefono;
        this.direccion = direccion;
        this.activo = activo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
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

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    public String toString(){
        return sucursal;
    }
        
}
