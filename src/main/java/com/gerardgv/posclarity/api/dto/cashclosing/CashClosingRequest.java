package com.gerardgv.posclarity.api.dto.cashclosing;

public class CashClosingRequest {
    
    private double initialCash;
    private double finalCash;
    private double totalSales;
    private double totalPayments;
    private double totalCash;
    private double totalTransfer;
    private double totalCard;
    private double deposit;
    private double difference;
    private String observations;

    public CashClosingRequest() {
    }

    public double getInitialCash() {
        return initialCash;
    }

    public void setInitialCash(double initialCash) {
        this.initialCash = initialCash;
    }

    public double getFinalCash() {
        return finalCash;
    }

    public void setFinalCash(double finalCash) {
        this.finalCash = finalCash;
    }

    public double getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(double totalSales) {
        this.totalSales = totalSales;
    }

    public double getTotalPayments() {
        return totalPayments;
    }

    public void setTotalPayments(double totalPayments) {
        this.totalPayments = totalPayments;
    }

    public double getTotalCash() {
        return totalCash;
    }

    public void setTotalCash(double totalCash) {
        this.totalCash = totalCash;
    }

    public double getTotalTransfer() {
        return totalTransfer;
    }

    public void setTotalTransfer(double totalTransfer) {
        this.totalTransfer = totalTransfer;
    }

    public double getTotalCard() {
        return totalCard;
    }

    public void setTotalCard(double totalCard) {
        this.totalCard = totalCard;
    }

    public double getDeposit() {
        return deposit;
    }

    public void setDeposit(double deposit) {
        this.deposit = deposit;
    }

    public double getDifference() {
        return difference;
    }

    public void setDifference(double difference) {
        this.difference = difference;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }
    
}
