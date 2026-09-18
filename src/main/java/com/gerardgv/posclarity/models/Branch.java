package com.gerardgv.posclarity.models;


public class Branch {
    
    private int id;
    private String name;
    private String phone;
    private String address;
    private boolean active;
    private String code;

    public Branch() {
    }

    public Branch(
            int id,
            String name,
            String phone,
            String address,
            boolean active,
            String code
    ) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.active = active;
        this.code = code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return name;
    } 
}
