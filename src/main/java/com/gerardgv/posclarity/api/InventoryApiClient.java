package com.gerardgv.posclarity.api;

import com.gerardgv.posclarity.api.dto.inventory.StockUpdateRequest;
import com.gerardgv.posclarity.models.Inventory;
import java.io.IOException;
import java.util.List;

public class InventoryApiClient extends BaseApiClient{
    
    private static final String BASE_URL =
            ApiConfig.BASE_URL + "/inventory";

     /**
     * Obtiene todo el inventario.
     */
    public List<Inventory> getAll() throws IOException,InterruptedException{
        
        return getList(BASE_URL, Inventory.class);
    }
    
    /**
     * Obtiene un registro de inventario por ID.
     */
    public Inventory getById(Integer id)
            throws IOException, InterruptedException {

        return get(BASE_URL + "/" + id, Inventory.class);
    }

    /**
     * Obtiene el inventario de una sucursal.
     */
    public List<Inventory> getByBranch(Integer branchId)
        throws IOException,InterruptedException{
        
        return getList(BASE_URL + "/branch/" + branchId, Inventory.class);
    }
    
    /**
     * Obtiene el inventario de un producto en una sucursal.
     */
    
    public Inventory getProductStock(
            Integer branchId,
            Integer productId)
            throws IOException, InterruptedException {

        return get(
                BASE_URL +
                "/branch/" + branchId +
                "/product/" + productId,
                Inventory.class
        );
        
    }
    
    public Inventory create(Inventory inventory)
        throws IOException, InterruptedException {

        return post(
            BASE_URL,
            inventory,
            Inventory.class
        );
    }
    
    public void updateStock(Integer branchId, Integer productId,
        Integer stock) throws IOException, InterruptedException {

        StockUpdateRequest request = new StockUpdateRequest();
        request.setStock(stock);
        
        put(BASE_URL + "/branch/"+ branchId + "/product/" + productId,
                request,Void.class);        
    }
}
