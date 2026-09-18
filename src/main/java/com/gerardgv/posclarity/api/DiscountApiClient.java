package com.gerardgv.posclarity.api;

import com.gerardgv.posclarity.api.dto.discount.DiscountCreateRequest;
import com.gerardgv.posclarity.models.Discount;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class DiscountApiClient extends BaseApiClient {
    
    private static final String BASE_URL =
            ApiConfig.BASE_URL + "/discounts";

    /*
     * Consulta todos los descuentos.
     */
    public List<Discount> getAll()
            throws IOException, InterruptedException {

        return getList(
                BASE_URL,
                Discount.class
        );
    }

    /*
     * Consulta descuentos activos.
     */
    public List<Discount> getActive()
            throws IOException, InterruptedException {

        return getList(
                BASE_URL + "/active",
                Discount.class
        );
    }
    
    /*
    Consultar Descuentos Por Categoria.
    */
    public List<Discount> getActiveByCategory(String category)
        throws IOException, InterruptedException {

        if (category == null || category.isBlank()) {
            return new ArrayList<>();
        }

    return getList(
            BASE_URL + "/active/category/"
                    + java.net.URLEncoder.encode(
                            category,
                            java.nio.charset.StandardCharsets.UTF_8
                    ),
            Discount.class
        );
    }
    
    /*
    Consulta descuentos generales activos.
    */
    public List<Discount> getActiveGeneral()
        throws IOException, InterruptedException {

        return getList(
            BASE_URL + "/active/general",
            Discount.class
        );
    }

    /*
     * Consulta un descuento por ID.
     */
    public Discount getById(int id)
            throws IOException, InterruptedException {

        return get(
                BASE_URL + "/" + id,
                Discount.class
        );
    }

    /*
     * Crear descuento.
     */
    public Discount create(DiscountCreateRequest request)
            throws IOException, InterruptedException {

        return post(
                BASE_URL,
                request,
                Discount.class
        );
    }

    /*
     * Activa o desactiva un descuento.
     */
    public Discount updateStatus(int id, boolean active)
            throws IOException, InterruptedException {

        return put(
                BASE_URL + "/" + id + "/status?active=" + active,
                null,
                Discount.class
        );
    }
    
    /*
     * Actualizar un descuento.
    */
    public Discount update(int id,DiscountCreateRequest request)
            throws IOException, InterruptedException {

        return put(
            BASE_URL + "/" + id,
            request,
            Discount.class);
    }
    
}
