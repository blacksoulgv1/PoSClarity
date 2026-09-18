package com.gerardgv.posclarity.api.dto.cashclosing;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CashClosingResponse {
    
    private Integer id;
    private Integer branchId;
    private String branchName;

    private LocalDate date;
    private LocalDateTime closingTime;

    private BigDecimal initialCash;
    private BigDecimal finalCash;

    private BigDecimal totalSales;
    private BigDecimal totalPayments;

    private BigDecimal totalCash;
    private BigDecimal totalTransfer;
    private BigDecimal totalCard;

    private BigDecimal totalIncome;

    private BigDecimal deposit;
    private BigDecimal difference;

    private String observations;

    private int newSalesCount;

    private String newSalesDetail;
    private String deliveredSalesDetail;
    private String paymentsDetail;

    public CashClosingResponse() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
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

    public BigDecimal getInitialCash() {
        return initialCash;
    }

    public void setInitialCash(BigDecimal initialCash) {
        this.initialCash = initialCash;
    }

    public BigDecimal getFinalCash() {
        return finalCash;
    }

    public void setFinalCash(BigDecimal finalCash) {
        this.finalCash = finalCash;
    }

    public BigDecimal getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(BigDecimal totalSales) {
        this.totalSales = totalSales;
    }

    public BigDecimal getTotalPayments() {
        return totalPayments;
    }

    public void setTotalPayments(BigDecimal totalPayments) {
        this.totalPayments = totalPayments;
    }

    public BigDecimal getTotalCash() {
        return totalCash;
    }

    public void setTotalCash(BigDecimal totalCash) {
        this.totalCash = totalCash;
    }

    public BigDecimal getTotalTransfer() {
        return totalTransfer;
    }

    public void setTotalTransfer(BigDecimal totalTransfer) {
        this.totalTransfer = totalTransfer;
    }

    public BigDecimal getTotalCard() {
        return totalCard;
    }

    public void setTotalCard(BigDecimal totalCard) {
        this.totalCard = totalCard;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }

    public BigDecimal getDifference() {
        return difference;
    }

    public void setDifference(BigDecimal difference) {
        this.difference = difference;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public int getNewSalesCount() {
        return newSalesCount;
    }

    public void setNewSalesCount(int newSalesCount) {
        this.newSalesCount = newSalesCount;
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
