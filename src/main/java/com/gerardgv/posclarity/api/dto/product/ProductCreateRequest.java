package com.gerardgv.posclarity.api.dto.product;

import com.gerardgv.posclarity.models.Product;

public class ProductCreateRequest {
    
    private Product product;
    private Integer branchId;
    private Integer initialStock;

    public ProductCreateRequest() {
    }

    public ProductCreateRequest(Product product, Integer branchId, Integer initialStock) {
        this.product = product;
        this.branchId = branchId;
        this.initialStock = initialStock;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }

    public Integer getInitialStock() {
        return initialStock;
    }

    public void setInitialStock(Integer initialStock) {
        this.initialStock = initialStock;
    }
    
    
    
}
