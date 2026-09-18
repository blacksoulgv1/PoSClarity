package com.gerardgv.posclarity.api.dto.seller;

import com.gerardgv.posclarity.models.Role;

public class SellerRequest {
    
    private Integer id;
    private String name;
    private Integer code;
    private String password;
    private Role role;
    private Boolean active;

    public SellerRequest() {
    }

    public SellerRequest(Integer id, String name, Integer code, String password, Role role, Boolean active) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.password = password;
        this.role = role;
        this.active = active;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
