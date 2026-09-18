package com.gerardgv.posclarity.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CashClosing {
    
    private int id; 
    private int newSalesCount; 
    private Branch branch; 
    private Seller user; 
    private LocalDate date; 
    private LocalDateTime closingTime; 
    private double initialCash; 
    private double finalCash; 
    private double totalSales; 
    private double finalSale; 
    private double totalPayments; 
    private double totalDelivered; 
    private double totalIncome; 
    private double totalCash; 
    private double totalTransfer; 
    private double totalCard; 
    private double deposit; 
    private double difference; 
    private String observations; 
    private String newSalesDetail; 
    private String deliveredSalesDetail; 
    private String paymentsDetail;

    public CashClosing() {
    }

    public CashClosing(int id, int newSalesCount, Branch branch, Seller user, LocalDate date, LocalDateTime closingTime, double initialCash, double finalCash, double totalSales, double finalSale, double totalPayments, double totalDelivered, double totalIncome, double totalCash, double totalTransfer, double totalCard, double deposit, double difference, String observations, String newSalesDetail, String deliveredSalesDetail, String paymentsDetail) {
        this.id = id;
        this.newSalesCount = newSalesCount;
        this.branch = branch;
        this.user = user;
        this.date = date;
        this.closingTime = closingTime;
        this.initialCash = initialCash;
        this.finalCash = finalCash;
        this.totalSales = totalSales;
        this.finalSale = finalSale;
        this.totalPayments = totalPayments;
        this.totalDelivered = totalDelivered;
        this.totalIncome = totalIncome;
        this.totalCash = totalCash;
        this.totalTransfer = totalTransfer;
        this.totalCard = totalCard;
        this.deposit = deposit;
        this.difference = difference;
        this.observations = observations;
        this.newSalesDetail = newSalesDetail;
        this.deliveredSalesDetail = deliveredSalesDetail;
        this.paymentsDetail = paymentsDetail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNewSalesCount() {
        return newSalesCount;
    }

    public void setNewSalesCount(int newSalesCount) {
        this.newSalesCount = newSalesCount;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public Seller getUser() {
        return user;
    }

    public void setUser(Seller user) {
        this.user = user;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDateTime getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(LocalDateTime closingTime) {
        this.closingTime = closingTime;
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

    public double getFinalSale() {
        return finalSale;
    }

    public void setFinalSale(double finalSale) {
        this.finalSale = finalSale;
    }

    public double getTotalPayments() {
        return totalPayments;
    }

    public void setTotalPayments(double totalPayments) {
        this.totalPayments = totalPayments;
    }

    public double getTotalDelivered() {
        return totalDelivered;
    }

    public void setTotalDelivered(double totalDelivered) {
        this.totalDelivered = totalDelivered;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(double totalIncome) {
        this.totalIncome = totalIncome;
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

    public String getNewSalesDetail() {
        return newSalesDetail;
    }

    public void setNewSalesDetail(String newSalesDetail) {
        this.newSalesDetail = newSalesDetail;
    }

    public String getDeliveredSalesDetail() {
        return deliveredSalesDetail;
    }

    public void setDeliveredSalesDetail(String deliveredSalesDetail) {
        this.deliveredSalesDetail = deliveredSalesDetail;
    }

    public String getPaymentsDetail() {
        return paymentsDetail;
    }

    public void setPaymentsDetail(String paymentsDetail) {
        this.paymentsDetail = paymentsDetail;
    }    
}
