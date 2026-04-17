package com.gerardgv.posclarity.utils;

import com.gerardgv.posclarity.models.Branch;

public class Session {
    
    private static int idBranch;
    private static Branch sucursal;

    public static void setBranch(int id) {
        idBranch = id;
    }
    
    public static int getBranch(){
        return idBranch;
    }  
    
    public static void setSucursal(Branch s){
        sucursal = s;
        idBranch = s.getId();
    }
    
    public static Branch getSucursal(){
        return sucursal;
    }
}
