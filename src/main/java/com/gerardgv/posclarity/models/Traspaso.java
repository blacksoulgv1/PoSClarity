package com.gerardgv.posclarity.models;

import java.time.LocalDateTime;
import java.util.List;

public class Traspaso {
    private int idTranspaso;
    private int idSucursalOrigen;
    private int idSucursalDestino;
    private LocalDateTime fecha;
    private String estado;
    private List<TraspasoDetalle> detalles;
    

    public Traspaso() {
    }

    public Traspaso(int idTranspaso, int idSucursalOrigen, int idSucursalDestino, LocalDateTime fecha, String estado, List<TraspasoDetalle> detalles) {
        this.idTranspaso = idTranspaso;
        this.idSucursalOrigen = idSucursalOrigen;
        this.idSucursalDestino = idSucursalDestino;
        this.fecha = fecha;
        this.estado = estado;
        this.detalles = detalles;
    }

    public int getIdTranspaso() {
        return idTranspaso;
    }

    public void setIdTranspaso(int idTranspaso) {
        this.idTranspaso = idTranspaso;
    }

    public int getIdSucursalOrigen() {
        return idSucursalOrigen;
    }

    public void setIdSucursalOrigen(int idSucursalOrigen) {
        this.idSucursalOrigen = idSucursalOrigen;
    }

    public int getIdSucursalDestino() {
        return idSucursalDestino;
    }

    public void setIdSucursalDestino(int idSucursalDestino) {
        this.idSucursalDestino = idSucursalDestino;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<TraspasoDetalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<TraspasoDetalle> detalles) {
        this.detalles = detalles;
    }
    
    
}
