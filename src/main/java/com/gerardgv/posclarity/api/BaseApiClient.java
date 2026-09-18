package com.gerardgv.posclarity.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.util.List;

public abstract class BaseApiClient {

    protected final HttpClient httpClient;
    protected final ObjectMapper objectMapper;

    protected BaseApiClient() {

        this.httpClient = HttpClient.newHttpClient();

        this.objectMapper = new ObjectMapper()
                .findAndRegisterModules();

        this.objectMapper.setSerializationInclusion(
                JsonInclude.Include.NON_NULL
        );
    }

    /*
     * GET de un solo objeto.
     * Si el servidor responde 404, devuelve null.
     */
    protected <T> T get(
            String url,
            Class<T> responseType
    ) throws IOException, InterruptedException {

        HttpRequest request = baseRequest(url)
                .GET()
                .build();

        HttpResponse<String> response = send(request);

        if (response.statusCode() == 404) {
            return null;
        }

        validateResponse(response, 200);
        System.out.println(response.body());

        return readObject(response.body(), responseType);
    }

    /*
     * GET de una lista de objetos.
     */
    protected <T> List<T> getList(
            String url,
            Class<T> elementType
    ) throws IOException, InterruptedException {

        HttpRequest request = baseRequest(url)
                .GET()
                .build();

        HttpResponse<String> response = send(request);

        validateResponse(response, 200);

        JavaType listType = objectMapper
                .getTypeFactory()
                .constructCollectionType(List.class, elementType);

        return objectMapper.readValue(
                response.body(),
                listType
        );
    }

    /*
     * POST
     */
    protected <B, T> T post(
            String url,
            B body,
            Class<T> responseType
    ) throws IOException, InterruptedException {

        String json = objectMapper.writeValueAsString(body);

        HttpRequest request = baseRequest(url)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = send(request);

        validateResponse(response, 200, 201);

        return readObject(response.body(), responseType);
    }

    /*
     * PUT
     */
    protected <B, T> T put(
            String url,
            B body,
            Class<T> responseType
    ) throws IOException, InterruptedException {

        String json = objectMapper.writeValueAsString(body);

        HttpRequest request = baseRequest(url)
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = send(request);

        validateResponse(response, 200);

        return readObject(response.body(), responseType);
    }

    /*
     * PATCH
     */
    protected <B, T> T patch(
        String url,
        B body,
        Class<T> responseType
        ) throws IOException, InterruptedException {

        HttpRequest.Builder builder = baseRequest(url);

        if (body == null) {

            builder.method(
                "PATCH",
                HttpRequest.BodyPublishers.noBody()
            );

        } else {

            String json = objectMapper.writeValueAsString(body);
            builder.method(
                "PATCH",
                HttpRequest.BodyPublishers.ofString(json)
            );
        }

        HttpResponse<String> response = send(builder.build());
        validateResponse(response, 200, 204);
        return readObject(response.body(), responseType);
    }

    /*
     * DELETE
     */
    protected void delete(
            String url
    ) throws IOException, InterruptedException {

        HttpRequest request = baseRequest(url)
                .DELETE()
                .build();

        HttpResponse<String> response = send(request);

        validateResponse(response, 200, 204);
    }

    /*
     * Configuración común de cada petición.
     */
    private HttpRequest.Builder baseRequest(String url) {

        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");
    }

    /*
     * Envía la petición HTTP.
     */
    private HttpResponse<String> send(
            HttpRequest request
    ) throws IOException, InterruptedException {

        return httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
    }

    /*
     * Convierte una respuesta JSON en un objeto.
     */
    private <T> T readObject(
            String body,
            Class<T> responseType
    ) throws IOException {

        if (body == null || body.isBlank()) {
            return null;
        }

        return objectMapper.readValue(
                body,
                responseType
        );
    }

    /*
     * Comprueba que el servidor haya regresado
     * uno de los códigos HTTP esperados.
     */
    private void validateResponse(
            HttpResponse<String> response,
            int... expectedStatusCodes
    ) throws IOException {

        int actualStatus = response.statusCode();

        for (int expectedStatus : expectedStatusCodes) {

            if (actualStatus == expectedStatus) {
                return;
            }
        }

        throw new IOException(
                buildErrorMessage(response)
        );
    }

    /*
     * Construye un mensaje útil para depuración.
     */
    private String buildErrorMessage(
        HttpResponse<String> response
) {

    try {

        if(response.body() != null 
                && !response.body().isBlank()){

            var json = objectMapper.readTree(
                    response.body()
            );

            if(json.has("message")){
                return json.get("message").asText();
            }
        }

    } catch(Exception e){

        // Si no es JSON continúa abajo
    }


    return "Error de API HTTP "
            + response.statusCode();
}
}