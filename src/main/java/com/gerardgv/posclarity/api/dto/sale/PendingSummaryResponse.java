package com.gerardgv.posclarity.api.dto.sale;

import java.math.BigDecimal;


public class PendingSummaryResponse {
    
    private int salesToDo;
    private int salesToDeliver;
    private BigDecimal balanceToDo;
    private BigDecimal balanceToDeliver;

    public PendingSummaryResponse() {
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

    public BigDecimal getBalanceToDo() {
        return balanceToDo;
    }

    public void setBalanceToDo(BigDecimal balanceToDo) {
        this.balanceToDo = balanceToDo;
    }

    public BigDecimal getBalanceToDeliver() {
        return balanceToDeliver;
    }

    public void setBalanceToDeliver(BigDecimal balanceToDeliver) {
        this.balanceToDeliver = balanceToDeliver;
    }
    
}
