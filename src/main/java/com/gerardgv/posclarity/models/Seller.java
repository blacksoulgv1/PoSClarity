package com.gerardgv.posclarity.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Seller {
    
    @JsonAlias({"idVendedor", "id_vendedor"})
    private int id;
    @JsonAlias("codigo")
    private int code;
    @JsonAlias("nombre")
    private String name;
    @JsonAlias("rol")
    private Role role;
    private String passwordHash;
    @JsonAlias({"estatus", "activo"})
    private boolean active;

    public Seller() {
    }

    public Seller(int id, int code, String name, Role role, String passwordHash, boolean active) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.role = role;
        this.passwordHash = passwordHash;
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }


    public Boolean getActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }
   
    
    
}
