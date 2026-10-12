package com.gerardgv.posclarity.api;

import com.gerardgv.posclarity.models.Insurance;
import java.io.IOException;
import java.util.List;

public class InsuranceApiClient extends BaseApiClient {
    
    
    private static final String BASE_URL =
            ApiConfig.BASE_URL + "/insurances"; 
    /* 
    * Consulta todos los seguros. 
    */     
    public List<Insurance> getAll() throws IOException, InterruptedException { 
        return getList( BASE_URL, Insurance.class );
    }
    
    /* 
    * Consulta los seguros activos. 
    */ 
    public List<Insurance> getActive() throws IOException, InterruptedException { 
        return getList( 
                BASE_URL + "/active", Insurance.class ); 
    }
    
    /* 
    * Consulta un seguro por ID. */ 
    public Insurance getById(int id) throws IOException, InterruptedException {
        return get( 
                BASE_URL + "/" + id, Insurance.class ); 
    }
}
