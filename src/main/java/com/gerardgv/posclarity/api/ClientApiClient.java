package com.gerardgv.posclarity.api;

import com.gerardgv.posclarity.models.Clients;
import com.gerardgv.posclarity.models.StatusRequest;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import com.gerardgv.posclarity.api.dto.common.PageResponse;

public class ClientApiClient extends BaseApiClient {
    
    private static final String BASE_URL =
            ApiConfig.BASE_URL + "/clients";
    
    /*
    CONSULTAR TODOS LOS CLIENTES
    */
    public List<Clients> getAll() throws IOException, InterruptedException{
        return getList(BASE_URL, Clients.class);
    }
    
    /*
    Consultar Clientes Por ID
    */
    public  Clients getById(int id) throws IOException, InterruptedException{
        return get(BASE_URL + "/" + id, Clients.class);
    }
    
    /*
    Buscar Clientes Por Nombre
    */
    public List<Clients> search(String text) throws IOException, InterruptedException{
        
        String query = URLEncoder.encode(text, StandardCharsets.UTF_8);
        
        return getList(BASE_URL + "/search?q=" + query,Clients.class);
    }
    
    /*
    Crear Cliente
    */    
    public Clients create(Clients client) throws IOException, InterruptedException{
        return post(
                BASE_URL,
                client,
                Clients.class
        );
    }
    
    /*
    Actualizar Cliente
    */
    public Clients update(Clients client)
            throws IOException, InterruptedException {

        return put(
                BASE_URL + "/" + client.getId(),
                client,
                Clients.class
        );
    }
    
    /*
     * Activa o desactiva un cliente.
    */
    public Clients updateStatus(int id, boolean active)
            throws IOException, InterruptedException {

        StatusRequest request = new StatusRequest(active);

        return patch(
                BASE_URL + "/" + id + "/status",
                request,
                Clients.class
        );
    }
    
    /*
    * CONSULTAR CLIENTES POR PÁGINA
    */
    public PageResponse<Clients> getPage(int page, int size)
        throws IOException, InterruptedException {

        String url = BASE_URL + "/page?page=" + page + "&size=" + size;

        return getPage(url, Clients.class);
    }

    /*
    * BUSCAR CLIENTES CON PAGINACIÓN
    */
    public PageResponse<Clients> searchPage(
        String text, int page, int size)
        throws IOException, InterruptedException {

        String query = URLEncoder.encode(
            text, StandardCharsets.UTF_8);

        String url = BASE_URL + "/search/page?q=" + query
            + "&page=" + page + "&size=" + size;

        return getPage(url, Clients.class);
    }
}
