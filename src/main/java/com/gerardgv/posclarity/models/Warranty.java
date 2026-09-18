package com.gerardgv.posclarity.models;

import java.time.LocalDateTime;

public class Warranty {
    
    private int id;
    private String folio;
    private int saleId;
    private int clientId;
    private int branchId;
    private LocalDateTime requestDate;
    private String reason;
    private String status;
    private String action;
    private String observations;
    private String clientName;
    private int userId;
    private LocalDateTime createdAt;

    public Warranty() {
    }
    
    public Warranty(
            int id,
            String folio,
            int saleId,
            int clientId,
            int branchId,
            LocalDateTime requestDate,
            String reason,
            String status,
            String action,
            String observations,
            String clientName,
            int userId,
            LocalDateTime createdAt) {

        this.id = id;
        this.folio = folio;
        this.saleId = saleId;
        this.clientId = clientId;
        this.branchId = branchId;
        this.requestDate = requestDate;
        this.reason = reason;
        this.status = status;
        this.action = action;
        this.observations = observations;
        this.clientName = clientName;
        this.userId = userId;
        this.createdAt = createdAt;
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

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }   
    
}
