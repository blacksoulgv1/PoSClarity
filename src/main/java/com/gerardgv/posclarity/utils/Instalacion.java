package com.gerardgv.posclarity.utils;

import com.gerardgv.posclarity.api.BranchApiClient;

public class Instalacion {
    
    public static boolean existeSucursal(){
        
        try{
            
            BranchApiClient apiClient = new BranchApiClient();        
            return apiClient.existsAnyBranch();
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
    
}
