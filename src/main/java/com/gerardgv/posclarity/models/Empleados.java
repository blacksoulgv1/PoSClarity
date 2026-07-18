package com.gerardgv.posclarity.models;

public class Empleados {
    
    private int id_vendedor;
    private int codigo;
    private String nombre;
    private String rol;
    private String passwordHash;
    private boolean activo = true;

    public Empleados() {
    }

    public Empleados(int id_vendedor, int codigo, String nombre, String rol, String passwordHash) {
        this.id_vendedor = id_vendedor;
        this.codigo = codigo;
        this.nombre = nombre;
        this.rol = rol;
        this.passwordHash = passwordHash;
    }   
    
    public int getId_vendedor() {
        return id_vendedor;
    }

    public void setId_vendedor(int id_vendedor) {
        this.id_vendedor = id_vendedor;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

}
