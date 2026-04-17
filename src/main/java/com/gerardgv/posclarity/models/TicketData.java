package com.gerardgv.posclarity.models;

public class TicketData {
    
    private String sucursal;
    private String telefono_suc;
    private String direccion_suc;
    private String cliente;
    private String telefono_clien;
    private String direccion_clien;
    private String vendedor;
    private String fecha;
    private String nota;
    private double subtotal;
    private double descuento;
    private double total;
    private double pago;
    private double pendiente;

    public TicketData() {
    }    
  
    public TicketData(String sucursal, String telefono_suc, String direccion_suc, String cliente, String telefono_clien, String direccion_clien, String vendedor, String fecha, String nota, double subtotal, double descuento, double total, double pago, double pendiente) {
        this.sucursal = sucursal;
        this.telefono_suc = telefono_suc;
        this.direccion_suc = direccion_suc;
        this.cliente = cliente;
        this.telefono_clien = telefono_clien;
        this.direccion_clien = direccion_clien;
        this.vendedor = vendedor;
        this.fecha = fecha;
        this.nota = nota;
        this.subtotal = subtotal;
        this.descuento = descuento;
        this.total = total;
        this.pago = pago;
        this.pendiente = pendiente;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public String getTelefono_suc() {
        return telefono_suc;
    }

    public void setTelefono_suc(String telefono_suc) {
        this.telefono_suc = telefono_suc;
    }

    public String getDireccion_suc() {
        return direccion_suc;
    }

    public void setDireccion_suc(String direccion_suc) {
        this.direccion_suc = direccion_suc;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getTelefono_clien() {
        return telefono_clien;
    }

    public void setTelefono_clien(String telefono_clien) {
        this.telefono_clien = telefono_clien;
    }

    public String getDireccion_clien() {
        return direccion_clien;
    }

    public void setDireccion_clien(String direccion_clien) {
        this.direccion_clien = direccion_clien;
    }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getPago() {
        return pago;
    }

    public void setPago(double pago) {
        this.pago = pago;
    }

    public double getPendiente() {
        return pendiente;
    }

    public void setPendiente(double pendiente) {
        this.pendiente = pendiente;
    }
    
    
    
}
