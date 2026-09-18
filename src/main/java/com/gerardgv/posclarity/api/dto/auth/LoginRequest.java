package com.gerardgv.posclarity.api.dto.auth;


public class LoginRequest {
    
    private Integer code;
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(Integer code, String password) {
        this.code = code;
        this.password = password;
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

    
    
}
