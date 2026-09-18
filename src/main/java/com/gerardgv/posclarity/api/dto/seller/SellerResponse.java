package com.gerardgv.posclarity.api.dto.seller;

import com.gerardgv.posclarity.models.Role;
import com.gerardgv.posclarity.models.Seller;

public class SellerResponse {
    
    private Integer id;
    private Integer code;
    private String name;
    private Role role;
    private Boolean active;

    public SellerResponse() {
    }
    
    public Seller toSeller(){
        
        Seller seller = new Seller();
        
        seller.setId(id);
        seller.setCode(code);
        seller.setName(name);
        seller.setRole(role);
        seller.setActive(active);
        return seller;
        
    }

    public SellerResponse(Integer id, Integer code, String name, Role role, Boolean active) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.role = role;
        this.active = active;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
    
    
    
}
