package com.gerardgv.posclarity.models;

public class Clients {
    
    private int id;
    private String name;
    private String phone;
    private String address;
    private String odEsf, odCil, odEje;
    private String oiEsf, oiCil, oiEje;
    private String add;
    private boolean active;

    public Clients() {
    }
    
    public String getGraduacionCompleta(){
        return "OD: " + odEsf + " / " + odCil + " x " + odEje +
              "\nOI: " + oiEsf + " / " + oiCil + " x " + oiEje +
                "\nADD: " + add;
    }

    public Clients(int id, String name, String phone, String address, String odEsf, String odCil, String odEje, String oiEsf, String oiCil, String oiEje, String add, boolean active) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.odEsf = odEsf;
        this.odCil = odCil;
        this.odEje = odEje;
        this.oiEsf = oiEsf;
        this.oiCil = oiCil;
        this.oiEje = oiEje;
        this.add = add;
        this.active = active;
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

    public String getOdEsf() {
        return odEsf;
    }

    public void setOdEsf(String odEsf) {
        this.odEsf = odEsf;
    }

    public String getOdCil() {
        return odCil;
    }

    public void setOdCil(String odCil) {
        this.odCil = odCil;
    }

    public String getOdEje() {
        return odEje;
    }

    public void setOdEje(String odEje) {
        this.odEje = odEje;
    }

    public String getOiEsf() {
        return oiEsf;
    }

    public void setOiEsf(String oiEsf) {
        this.oiEsf = oiEsf;
    }

    public String getOiCil() {
        return oiCil;
    }

    public void setOiCil(String oiCil) {
        this.oiCil = oiCil;
    }

    public String getOiEje() {
        return oiEje;
    }

    public void setOiEje(String oiEje) {
        this.oiEje = oiEje;
    }

    public String getAdd() {
        return add;
    }

    public void setAdd(String add) {
        this.add = add;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
    
}