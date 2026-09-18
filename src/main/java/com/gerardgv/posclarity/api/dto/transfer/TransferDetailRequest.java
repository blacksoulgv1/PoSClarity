package com.gerardgv.posclarity.api.dto.transfer;

public class TransferDetailRequest {
    
    private Integer productId;
    private Integer quantity;

    public TransferDetailRequest() {
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
}
