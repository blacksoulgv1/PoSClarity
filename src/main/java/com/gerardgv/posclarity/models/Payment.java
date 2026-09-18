package com.gerardgv.posclarity.models;

import java.time.LocalDateTime;

public class Payment {
    
    private int id;
    private int saleId;
    private String paymentMethod;
    private double amount;
    private String reference;
    private LocalDateTime date;
    private String paymentType;

    public Payment() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }   
       

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    // =====================================================
// COMPATIBILIDAD TEMPORAL
// =====================================================

@Deprecated
public LocalDateTime getFecha() {
    return getDate();
}

@Deprecated
public void setFecha(LocalDateTime fecha) {
    setDate(fecha);
}

@Deprecated
public String getMetodo() {
    return getPaymentMethod();
}

@Deprecated
public void setMetodo(String metodo) {
    setPaymentMethod(metodo);
}

@Deprecated
public double getMonto() {
    return getAmount();
}

@Deprecated
public void setMonto(double monto) {
    setAmount(monto);
}

@Deprecated
public String getReferencia() {
    return getReference();
}

@Deprecated
public void setReferencia(String referencia) {
    setReference(referencia);
}

@Deprecated
public String getTipoPago() {
    return getPaymentType();
}

@Deprecated
public void setTipoPago(String tipoPago) {
    setPaymentType(tipoPago);
}
    
    
}
