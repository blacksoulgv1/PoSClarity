package com.gerardgv.posclarity.utils;

import com.gerardgv.posclarity.models.Seller;

public final class AuthSession {

    private static Seller empleado;
    private static String token;

    private AuthSession() {
    }

    public static void iniciar(Seller seller, String authToken) {
        if (seller == null) {
            throw new IllegalArgumentException(
                    "No se puede iniciar una sesión sin empleado."
            );
        }

        if (authToken == null || authToken.isBlank()) {
            throw new IllegalArgumentException(
                    "No se puede iniciar una sesión sin token."
            );
        }

        empleado = seller;
        token = authToken;
    }

    public static Seller getEmpleado() {
        return empleado;
    }

    public static String getToken() {
        return token;
    }

    public static boolean haySesion() {
        return empleado != null
                && token != null
                && !token.isBlank();
    }

    public static void cerrarSesion() {
        empleado = null;
        token = null;
    }
}
