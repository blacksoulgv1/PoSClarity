package com.gerardgv.posclarity.api;

import com.gerardgv.posclarity.api.dto.product.ProductCreateRequest;
import com.gerardgv.posclarity.models.Product;
import com.gerardgv.posclarity.models.StatusRequest;
import com.gerardgv.posclarity.utils.Session;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class ProductApiClient extends BaseApiClient {
    
    private static final String BASE_URL =
            ApiConfig.BASE_URL + "/products";
    
    /*
    Consulta todos los productos.
     */
    public List<Product> getAll()
            throws IOException, InterruptedException {

        Integer branchId = Session.getSucursal().getId();
        
        System.out.println(
            BASE_URL + "?branchId=" + branchId
    );
        
        return getList(
            BASE_URL + "?branchId=" + branchId,
            Product.class
    );
    }


    /*
    Consulta un producto por ID.
     */
    public Product getById(int id)
            throws IOException, InterruptedException {

        return get(BASE_URL + "/" + id,Product.class);
    }
    
    /*
    Consulta un producto por Modelo.
    */
    public Product getByModel(String model)
            throws IOException, InterruptedException{
        
        return get(BASE_URL + "/model/" + model,Product.class);
    }
    
    /*
    Consulta un producto por Categoria.
    */
    public List<Product> getByCategory(String category)
            throws IOException, InterruptedException {
        return getList(BASE_URL + "/category/" + category, Product.class);
    }
    
    /*
    Busca productos por modelo o marca.
    */
    public List<Product> search(String text)
            throws IOException, InterruptedException {
        
        Integer branchId = Session.getSucursal().getId();
        String query = URLEncoder.encode(text, StandardCharsets.UTF_8);

        return getList(
                BASE_URL + "/search?text=" + query
                + "&branchId=" + branchId,
                Product.class
        );
    }

    /*
    Crear producto.
    */
    public Product create(ProductCreateRequest request)
            throws IOException, InterruptedException {

        return post(
                BASE_URL,
                request,
                Product.class
        );
    }


    /*
    Actualizar producto.
    */
    public Product update(Product product)
            throws IOException, InterruptedException {

        return put(
                BASE_URL + "/" + product.getId(),
                product,
                Product.class
        );
    }
    
    /*
    Activa o desactiva un producto.
    */
    public Product updateStatus(int id, boolean active)
            throws IOException, InterruptedException {
        
        StatusRequest request = new StatusRequest(active);

        return patch(
                BASE_URL + "/" + id + "/status",
                request,
                Product.class
        );
    }
}
