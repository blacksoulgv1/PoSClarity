package com.gerardgv.posclarity.models;

import java.time.LocalDateTime;

public class Garantia {
    
    private int idgarantias;
    private String folio;
    private int idventas;
    private int idcliente;
    private int idsucursal;
    private LocalDateTime fechaSolicitud;
    private String motivo;
    private String estado;
    private String accion;
    private String observaciones;
    private int usuario;
    private LocalDateTime fechaCreacion;

    public Garantia() {
    }

    public Garantia(int idgarantias, String folio, int idventas, int idcliente, int idsucursal, LocalDateTime fechaSolicitud, String motivo, String estado, String accion, String observaciones, int usuario, LocalDateTime fechaCreacion) {
        this.idgarantias = idgarantias;
        this.folio = folio;
        this.idventas = idventas;
        this.idcliente = idcliente;
        this.idsucursal = idsucursal;
        this.fechaSolicitud = fechaSolicitud;
        this.motivo = motivo;
        this.estado = estado;
        this.accion = accion;
        this.observaciones = observaciones;
        this.usuario = usuario;
        this.fechaCreacion = fechaCreacion;
    }

    public int getIdgarantias() {
        return idgarantias;
    }

    public void setIdgarantias(int idgarantias) {
        this.idgarantias = idgarantias;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public int getIdventas() {
        return idventas;
    }

    public void setIdventas(int idventas) {
        this.idventas = idventas;
    }

    public int getIdcliente() {
        return idcliente;
    }

    public void setIdcliente(int idcliente) {
        this.idcliente = idcliente;
    }

    public int getIdsucursal() {
        return idsucursal;
    }

    public void setIdsucursal(int idsucursal) {
        this.idsucursal = idsucursal;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public int getUsuario() {
        return usuario;
    }

    public void setUsuario(int usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
