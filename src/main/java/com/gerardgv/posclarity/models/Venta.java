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
    private String odEsf, odCil, odEje;
    private String oiEsf, oiCil, oiEje;
    private String add;

    public Venta() {
    }


    public Venta(int id, Branch sucursal, Clients cliente, Empleados vendedor, LocalDateTime fecha, double total, double pagado, double restante, String estadoPago, String estadoTrabajo, String odEsf, String odCil, String odEje, String oiEsf, String oiCil, String oiEje, String add) {
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
        this.odEsf = odEsf;
        this.odCil = odCil;
        this.odEje = odEje;
        this.oiEsf = oiEsf;
        this.oiCil = oiCil;
        this.oiEje = oiEje;
        this.add = add;
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
    
      
}
