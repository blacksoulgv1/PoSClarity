package com.gerardgv.posclarity.api;

import com.gerardgv.posclarity.api.dto.seller.SellerRequest;
import com.gerardgv.posclarity.models.Seller;
import com.gerardgv.posclarity.models.StatusRequest;
import java.io.IOException;
import java.util.List;

public class SellerApiClient extends BaseApiClient{
    
    private static final String BASE_URL = 
            ApiConfig.BASE_URL + "/sellers";
    
    /*
     * Obtiene todos los vendedores activos.
     */
    public List<Seller> getAll()
            throws IOException, InterruptedException {

        return getList(
                BASE_URL,
                Seller.class
        );
    }

    /*
     * Busca un vendedor por su ID.
     * Devuelve null cuando la API responde 404.
     */
    public Seller getById(int id)
            throws IOException, InterruptedException {

        return get(
                BASE_URL + "/" + id,
                Seller.class
        );
    }

    /*
     * Busca un vendedor mediante su código.
     */
    public Seller getByCode(int code)
            throws IOException, InterruptedException {

        return get(
                BASE_URL + "/code/" + code,
                Seller.class
        );
    }
    
     /**
     * Registra un vendedor nuevo.
     */
    public Seller create(SellerRequest  request)
            throws IOException, InterruptedException {

        return post(
                BASE_URL,
                request,
                Seller.class
        );
    }
    
    /**
     * Actualiza un vendedor existente.
     */
    public Seller update(SellerRequest request)
            throws IOException, InterruptedException {


        return put(
                BASE_URL + "/" + request.getId(),
                request,
                Seller.class
        );
    }

    /**
     * Cambia el estado activo/inactivo.
     */
    public Seller changeStatus(int id, boolean active) 
            throws IOException, InterruptedException {
        
        StatusRequest request = new StatusRequest(active);

        return patch(
                BASE_URL + "/" + id + "/status",
                request,
                Seller.class
        );
    }
    
}
