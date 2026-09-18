package com.gerardgv.posclarity.api.dto.inventory;

public class StockUpdateRequest {
    
    private Integer stock;

    public StockUpdateRequest() {
    }

    public StockUpdateRequest(Integer stock) {
        this.stock = stock;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
    
}
