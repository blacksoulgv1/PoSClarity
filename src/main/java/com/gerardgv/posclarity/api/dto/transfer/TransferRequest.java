package com.gerardgv.posclarity.api.dto.transfer;

import java.util.List;

public class TransferRequest {
    
    private Integer originBranchId;
    private Integer destinationBranchId;
    private List<TransferDetailRequest> details;

    public TransferRequest() {
    }

    public Integer getOriginBranchId() {
        return originBranchId;
    }

    public void setOriginBranchId(Integer originBranchId) {
        this.originBranchId = originBranchId;
    }

    public Integer getDestinationBranchId() {
        return destinationBranchId;
    }

    public void setDestinationBranchId(Integer destinationBranchId) {
        this.destinationBranchId = destinationBranchId;
    }

    public List<TransferDetailRequest> getDetails() {
        return details;
    }

    public void setDetails(List<TransferDetailRequest> details) {
        this.details = details;
    }
    
}
