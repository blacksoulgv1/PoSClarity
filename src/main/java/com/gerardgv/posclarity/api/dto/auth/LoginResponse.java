package com.gerardgv.posclarity.api.dto.auth;

import com.gerardgv.posclarity.api.dto.seller.SellerResponse;


public class LoginResponse {
    
    private SellerResponse seller;
    private String token;

    public LoginResponse() {
    }

    public LoginResponse(SellerResponse seller, String token) {
        this.seller = seller;
        this.token = token;
    }

    public SellerResponse getSeller() {
        return seller;
    }

    public void setSeller(SellerResponse seller) {
        this.seller = seller;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    
    

    
}
