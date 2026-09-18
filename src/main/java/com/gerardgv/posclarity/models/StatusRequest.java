package com.gerardgv.posclarity.models;

public class StatusRequest {
    
    private Boolean active;

    public StatusRequest() {
    }

    public StatusRequest(Boolean active) {
        this.active = active;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
