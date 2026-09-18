package com.gerardgv.posclarity.api;

import com.gerardgv.posclarity.models.Branch;
import java.io.IOException;
import java.util.List;


public class BranchApiClient extends BaseApiClient{
    
    private static final String BASE_URL =
            ApiConfig.BASE_URL + "/branches";
    
    /*
     * Consulta todas las sucursales activas.
    */    
    public List<Branch> getAll() throws IOException, InterruptedException{
        
        return getList(BASE_URL, Branch.class);
        
    }
    
    /*
     * Consulta una sucursal por ID.
     * Devuelve null cuando la API responde 404.
    */
     public Branch getById(int id)
            throws IOException, InterruptedException {

        return get(
                BASE_URL + "/" + id,
                Branch.class
        );
    }

    /*
     * Crea una sucursal.
     */
    public Branch create(Branch branch)
            throws IOException, InterruptedException {

        return post(
                BASE_URL,
                branch,
                Branch.class
        );
    }

    /*
     * Actualiza una sucursal existente.
     */
    public Branch update(Branch branch)
            throws IOException, InterruptedException {

        return put(
                BASE_URL + "/" + branch.getId(),
                branch,
                Branch.class
        );
    }

    /*
     * Activa o desactiva una sucursal.
     */
    public Branch updateStatus(int id, boolean active)
            throws IOException, InterruptedException {

        Branch requestBody = new Branch();
        requestBody.setActive(active);

        return patch(
                BASE_URL + "/" + id + "/status",
                requestBody,
                Branch.class
        );
    }
    
    public boolean existsAnyBranch()
        throws IOException, InterruptedException {

        return get(
            BASE_URL + "/exists",
            Boolean.class
        );
    }
}