package com.gerardgv.posclarity.models;

import java.time.LocalDateTime;
import java.util.List;

public class Sale {
    
    private int id;
    private String folio;

    private Branch branch;
    private Clients client;
    private Seller seller;

    private LocalDateTime saleDate;

    private double grossTotal;
    private double totalDiscount;
    private double finalTotal;

    private double paid;
    private double remaining;

    private String paymentStatus;
    private String workStatus;

    private String odEsf;
    private String odCil;
    private String odEje;

    private String oiEsf;
    private String oiCil;
    private String oiEje;

    private String addLens;
    
    private List<SaleItem> items;
    private List<Payment> payments;

    public Sale() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public Clients getClient() {
        return client;
    }

    public void setClient(Clients client) {
        this.client = client;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public double getGrossTotal() {
        return grossTotal;
    }

    public void setGrossTotal(double grossTotal) {
        this.grossTotal = grossTotal;
    }

    public double getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(double totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public double getFinalTotal() {
        return finalTotal;
    }

    public void setFinalTotal(double finalTotal) {
        this.finalTotal = finalTotal;
    }

    public double getPaid() {
        return paid;
    }

    public void setPaid(double paid) {
        this.paid = paid;
    }

    public double getRemaining() {
        return remaining;
    }

    public void setRemaining(double remaining) {
        this.remaining = remaining;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(String workStatus) {
        this.workStatus = workStatus;
    }

    public String getOdEsf() {
        return odEsf;
    }

    public void setOdEsf(String odEsf) {
        this.odEsf = odEsf;
    }

    public String getOdCil() {
        return odCil;
    }

    public void setOdCil(String odCil) {
        this.odCil = odCil;
    }

    public String getOdEje() {
        return odEje;
    }

    public void setOdEje(String odEje) {
        this.odEje = odEje;
    }

    public String getOiEsf() {
        return oiEsf;
    }

    public void setOiEsf(String oiEsf) {
        this.oiEsf = oiEsf;
    }

    public String getOiCil() {
        return oiCil;
    }

    public void setOiCil(String oiCil) {
        this.oiCil = oiCil;
    }

    public String getOiEje() {
        return oiEje;
    }

    public void setOiEje(String oiEje) {
        this.oiEje = oiEje;
    }

    public String getAddLens() {
        return addLens;
    }

    public void setAddLens(String addLens) {
        this.addLens = addLens;
    }
    
    public List<SaleItem> getItems() {
        return items;
    }

    public void setItems(List<SaleItem> items) {
        this.items = items;
    }
    
    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }
}
