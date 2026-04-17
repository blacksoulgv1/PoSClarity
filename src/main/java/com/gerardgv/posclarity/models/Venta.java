package com.gerardgv.posclarity.models;

import java.time.LocalDateTime;

public class Venta {
    
    private int id;
    private Branch sucursal;
    private Clients cliente;
    private Empleados vendedor;
    private LocalDateTime fecha;
    private double total;
    private double pagado;
    private double restante;
    private String estadoPago;
    private String estadoTrabajo;

    public Venta() {
    }

    public Venta(int id, Branch sucursal, Clients cliente, Empleados vendedor, LocalDateTime fecha, double total, double pagado, double restante, String estadoPago, String estadoTrabajo) {
        this.id = id;
        this.sucursal = sucursal;
        this.cliente = cliente;
        this.vendedor = vendedor;
        this.fecha = fecha;
        this.total = total;
        this.pagado = pagado;
        this.restante = restante;
        this.estadoPago = estadoPago;
        this.estadoTrabajo = estadoTrabajo;
    }

    public Branch getSucursal() {
        return sucursal;
    }

    public void setSucursal(Branch sucursal) {
        this.sucursal = sucursal;
    }

    public Clients getCliente() {
        return cliente;
    }

    public void setCliente(Clients cliente) {
        this.cliente = cliente;
    }

    public Empleados getVendedor() {
        return vendedor;
    }

    public void setVendedor(Empleados vendedor) {
        this.vendedor = vendedor;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getPagado() {
        return pagado;
    }

    public void setPagado(double pagado) {
        this.pagado = pagado;
    }

    public double getRestante() {
        return restante;
    }

    public void setRestante(double restante) {
        this.restante = restante;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public String getEstadoTrabajo() {
        return estadoTrabajo;
    }

    public void setEstadoTrabajo(String estadoTrabajo) {
        this.estadoTrabajo = estadoTrabajo;
    }
      
}
