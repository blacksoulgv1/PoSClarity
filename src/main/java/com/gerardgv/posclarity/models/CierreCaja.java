package com.gerardgv.posclarity.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CierreCaja {
    
    private int idCierre;
    private int totalNotasNuevas;
    private Branch sucursal;
    private Empleados usuario;
    private LocalDate fecha;
    private LocalDateTime horaCierre;
    private double cajaInicial;
    private double cajaFinal;
    private double totalVentas;
    private double ventaFinal;
    private double totalAbonos;
    private double totalRecogidos;
    private double totalIngresos;
    private double totalEfectivo;
    private double totalTransferencia;
    private double totalTarjeta;
    private double deposito;
    private double diferencia;
    private String observaciones;
    private String detalleNuevo;
    private String detalleRecogido;
    private String detalleAbonos;

    public CierreCaja() {
    }

    public CierreCaja(int idCierre, int totalNotasNuevas, Branch sucursal, Empleados usuario, LocalDate fecha, LocalDateTime horaCierre, double cajaInicial, double cajaFinal, double totalVentas, double ventaFinal, double totalAbonos, double totalRecogidos, double totalIngresos, double totalEfectivo, double totalTransferencia, double totalTarjeta, double deposito, double diferencia, String observaciones, String detalleNuevo, String detalleRecogido, String detalleAbonos) {
        this.idCierre = idCierre;
        this.totalNotasNuevas = totalNotasNuevas;
        this.sucursal = sucursal;
        this.usuario = usuario;
        this.fecha = fecha;
        this.horaCierre = horaCierre;
        this.cajaInicial = cajaInicial;
        this.cajaFinal = cajaFinal;
        this.totalVentas = totalVentas;
        this.ventaFinal = ventaFinal;
        this.totalAbonos = totalAbonos;
        this.totalRecogidos = totalRecogidos;
        this.totalIngresos = totalIngresos;
        this.totalEfectivo = totalEfectivo;
        this.totalTransferencia = totalTransferencia;
        this.totalTarjeta = totalTarjeta;
        this.deposito = deposito;
        this.diferencia = diferencia;
        this.observaciones = observaciones;
        this.detalleNuevo = detalleNuevo;
        this.detalleRecogido = detalleRecogido;
        this.detalleAbonos = detalleAbonos;
    }

    public int getIdCierre() {
        return idCierre;
    }

    public void setIdCierre(int idCierre) {
        this.idCierre = idCierre;
    }

    public int getTotalNotasNuevas() {
        return totalNotasNuevas;
    }

    public void setTotalNotasNuevas(int totalNotasNuevas) {
        this.totalNotasNuevas = totalNotasNuevas;
    }

    public Branch getSucursal() {
        return sucursal;
    }

    public void setSucursal(Branch sucursal) {
        this.sucursal = sucursal;
    }

    public Empleados getUsuario() {
        return usuario;
    }

    public void setUsuario(Empleados usuario) {
        this.usuario = usuario;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalDateTime getHoraCierre() {
        return horaCierre;
    }

    public void setHoraCierre(LocalDateTime horaCierre) {
        this.horaCierre = horaCierre;
    }

    public double getCajaInicial() {
        return cajaInicial;
    }

    public void setCajaInicial(double cajaInicial) {
        this.cajaInicial = cajaInicial;
    }

    public double getCajaFinal() {
        return cajaFinal;
    }

    public void setCajaFinal(double cajaFinal) {
        this.cajaFinal = cajaFinal;
    }

    public double getTotalVentas() {
        return totalVentas;
    }

    public void setTotalVentas(double totalVentas) {
        this.totalVentas = totalVentas;
    }

    public double getVentaFinal() {
        return ventaFinal;
    }

    public void setVentaFinal(double ventaFinal) {
        this.ventaFinal = ventaFinal;
    }

    public double getTotalAbonos() {
        return totalAbonos;
    }

    public void setTotalAbonos(double totalAbonos) {
        this.totalAbonos = totalAbonos;
    }

    public double getTotalRecogidos() {
        return totalRecogidos;
    }

    public void setTotalRecogidos(double totalRecogidos) {
        this.totalRecogidos = totalRecogidos;
    }

    public double getTotalIngresos() {
        return totalIngresos;
    }

    public void setTotalIngresos(double totalIngresos) {
        this.totalIngresos = totalIngresos;
    }

    public double getTotalEfectivo() {
        return totalEfectivo;
    }

    public void setTotalEfectivo(double totalEfectivo) {
        this.totalEfectivo = totalEfectivo;
    }

    public double getTotalTransferencia() {
        return totalTransferencia;
    }

    public void setTotalTransferencia(double totalTransferencia) {
        this.totalTransferencia = totalTransferencia;
    }

    public double getTotalTarjeta() {
        return totalTarjeta;
    }

    public void setTotalTarjeta(double totalTarjeta) {
        this.totalTarjeta = totalTarjeta;
    }

    public double getDeposito() {
        return deposito;
    }

    public void setDeposito(double deposito) {
        this.deposito = deposito;
    }

    public double getDiferencia() {
        return diferencia;
    }

    public void setDiferencia(double diferencia) {
        this.diferencia = diferencia;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getDetalleNuevo() {
        return detalleNuevo;
    }

    public void setDetalleNuevo(String detalleNuevo) {
        this.detalleNuevo = detalleNuevo;
    }

    public String getDetalleRecogido() {
        return detalleRecogido;
    }

    public void setDetalleRecogido(String detalleRecogido) {
        this.detalleRecogido = detalleRecogido;
    }

    public String getDetalleAbonos() {
        return detalleAbonos;
    }

    public void setDetalleAbonos(String detalleAbonos) {
        this.detalleAbonos = detalleAbonos;
    }

    
}
