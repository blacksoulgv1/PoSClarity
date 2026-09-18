package com.gerardgv.posclarity.models;

import java.time.LocalDateTime;
import java.util.List;

public class Transfer {
    
    private int id;
    private int originBranchId;
    private int destinationBranchId;
    private LocalDateTime date;
    private String status;
    private List<TransferDetail> details;

    public Transfer() {
    }

    public Transfer(
            int id,
            int originBranchId,
            int destinationBranchId,
            LocalDateTime date,
            String status,
            List<TransferDetail> details) {

        this.id = id;
        this.originBranchId = originBranchId;
        this.destinationBranchId = destinationBranchId;
        this.date = date;
        this.status = status;
        this.details = details;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOriginBranchId() {
        return originBranchId;
    }

    public void setOriginBranchId(int originBranchId) {
        this.originBranchId = originBranchId;
    }

    public int getDestinationBranchId() {
        return destinationBranchId;
    }

    public void setDestinationBranchId(int destinationBranchId) {
        this.destinationBranchId = destinationBranchId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<TransferDetail> getDetails() {
        return details;
    }

    public void setDetails(List<TransferDetail> details) {
        this.details = details;
    }
    
    
}
