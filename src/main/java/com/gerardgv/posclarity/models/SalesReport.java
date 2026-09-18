package com.gerardgv.posclarity.models;

public class SalesReport {
    
    private double total;
    private double paid;
    private double pending;

    public SalesReport() {
    }

    public SalesReport(double total, double paid, double pending) {
        this.total = total;
        this.paid = paid;
        this.pending = pending;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getPaid() {
        return paid;
    }

    public void setPaid(double paid) {
        this.paid = paid;
    }

    public double getPending() {
        return pending;
    }

    public void setPending(double pending) {
        this.pending = pending;
    }
}
