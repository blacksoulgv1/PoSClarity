package com.gerardgv.posclarity.api;

public class ApiConfig {
    
    private static final String HOST =
            "http://192.168.1.50:8080";

    public static final String BASE_URL =
            HOST + "/api";

    private ApiConfig() {
        // Evita que la clase pueda instanciarse.
    }
    
}
