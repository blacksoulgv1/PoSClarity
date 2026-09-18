package com.gerardgv.posclarity.models;

public class Goal {
    
    private int id;
    private int branchId;
    private int month;
    private int year;
    private double amount;
    private String branchName;

    public Goal() {
    }
    
    public Goal(
            int id,
            int branchId,
            int month,
            int year,
            double amount,
            String branchName) {

        this.id = id;
        this.branchId = branchId;
        this.month = month;
        this.year = year;
        this.amount = amount;
        this.branchName = branchName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }
}
