package com.gerardgv.posclarity.models;

public class WarrantyGraduation {
    
    private int id;
    private int warrantyId;
    private String type;

    private String odSphere;
    private String odCylinder;
    private String odAxis;
    private String odAdd;

    private String oiSphere;
    private String oiCylinder;
    private String oiAxis;
    private String oiAdd;

    public WarrantyGraduation() {
    }
    
    public WarrantyGraduation(
            int id,
            int warrantyId,
            String type,
            String odSphere,
            String odCylinder,
            String odAxis,
            String odAdd,
            String oiSphere,
            String oiCylinder,
            String oiAxis,
            String oiAdd) {

        this.id = id;
        this.warrantyId = warrantyId;
        this.type = type;
        this.odSphere = odSphere;
        this.odCylinder = odCylinder;
        this.odAxis = odAxis;
        this.odAdd = odAdd;
        this.oiSphere = oiSphere;
        this.oiCylinder = oiCylinder;
        this.oiAxis = oiAxis;
        this.oiAdd = oiAdd;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWarrantyId() {
        return warrantyId;
    }

    public void setWarrantyId(int warrantyId) {
        this.warrantyId = warrantyId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOdSphere() {
        return odSphere;
    }

    public void setOdSphere(String odSphere) {
        this.odSphere = odSphere;
    }

    public String getOdCylinder() {
        return odCylinder;
    }

    public void setOdCylinder(String odCylinder) {
        this.odCylinder = odCylinder;
    }

    public String getOdAxis() {
        return odAxis;
    }

    public void setOdAxis(String odAxis) {
        this.odAxis = odAxis;
    }

    public String getOdAdd() {
        return odAdd;
    }

    public void setOdAdd(String odAdd) {
        this.odAdd = odAdd;
    }

    public String getOiSphere() {
        return oiSphere;
    }

    public void setOiSphere(String oiSphere) {
        this.oiSphere = oiSphere;
    }

    public String getOiCylinder() {
        return oiCylinder;
    }

    public void setOiCylinder(String oiCylinder) {
        this.oiCylinder = oiCylinder;
    }

    public String getOiAxis() {
        return oiAxis;
    }

    public void setOiAxis(String oiAxis) {
        this.oiAxis = oiAxis;
    }

    public String getOiAdd() {
        return oiAdd;
    }

    public void setOiAdd(String oiAdd) {
        this.oiAdd = oiAdd;
    }
}
