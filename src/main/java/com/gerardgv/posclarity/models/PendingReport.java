package com.gerardgv.posclarity.models;

public class PendingReport {
    
    private int salesToDo;
    private int salesToDeliver;
    private Double balanceToDo;
    private Double balanceToDeliver;

    public PendingReport() {
    }

    public PendingReport(int salesToDo, int salesToDeliver, Double balanceToDo, Double balanceToDeliver) {
        this.salesToDo = salesToDo;
        this.salesToDeliver = salesToDeliver;
        this.balanceToDo = balanceToDo;
        this.balanceToDeliver = balanceToDeliver;
    }

    public int getSalesToDo() {
        return salesToDo;
    }

    public void setSalesToDo(int salesToDo) {
        this.salesToDo = salesToDo;
    }

    public int getSalesToDeliver() {
        return salesToDeliver;
    }

    public void setSalesToDeliver(int salesToDeliver) {
        this.salesToDeliver = salesToDeliver;
    }

    public Double getBalanceToDo() {
        return balanceToDo;
    }

    public void setBalanceToDo(Double balanceToDo) {
        this.balanceToDo = balanceToDo;
    }

    public Double getBalanceToDeliver() {
        return balanceToDeliver;
    }

    public void setBalanceToDeliver(Double balanceToDeliver) {
        this.balanceToDeliver = balanceToDeliver;
    }

    
}
