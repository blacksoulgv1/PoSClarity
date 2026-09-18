package com.gerardgv.posclarity.models;

public class TicketData {
    
    private String branch;
    private String branchPhone;
    private String branchAddress;
    private String client;
    private String clientPhone;
    private String clientAddress;
    private String seller;
    private String date;
    private String note;
    private double subtotal;
    private double discount;
    private double total;
    private double payment;
    private double pending;

    public TicketData() {
    }    
  
    public TicketData(
            String branch,
            String branchPhone,
            String branchAddress,
            String client,
            String clientPhone,
            String clientAddress,
            String seller,
            String date,
            String note,
            double subtotal,
            double discount,
            double total,
            double payment,
            double pending) {
        
        this.branch = branch;
        this.branchPhone = branchPhone;
        this.branchAddress = branchAddress;
        this.client = client;
        this.clientPhone = clientPhone;
        this.clientAddress = clientAddress;
        this.seller = seller;
        this.date = date;
        this.note = note;
        this.subtotal = subtotal;
        this.discount = discount;
        this.total = total;
        this.payment = payment;
        this.pending = pending;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getBranchPhone() {
        return branchPhone;
    }

    public void setBranchPhone(String branchPhone) {
        this.branchPhone = branchPhone;
    }

    public String getBranchAddress() {
        return branchAddress;
    }

    public void setBranchAddress(String branchAddress) {
        this.branchAddress = branchAddress;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getPayment() {
        return payment;
    }

    public void setPayment(double payment) {
        this.payment = payment;
    }

    public double getPending() {
        return pending;
    }

    public void setPending(double pending) {
        this.pending = pending;
    }           
}
