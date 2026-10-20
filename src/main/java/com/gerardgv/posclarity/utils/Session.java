package com.gerardgv.posclarity.utils;

import com.gerardgv.posclarity.models.*;

public class Session {

    private static int idBranch;
    private static Branch sucursal;
    private static Seller empleadoAutenticado;

    public static void setBranch(int id) {
        idBranch = id;
    }

    public static int getBranch(){
        return idBranch;
    }

    public static void setSucursal(Branch s){

        if(s == null){
            throw new IllegalArgumentException(
            "No se puede Iniciar Sesión con una Sucursal Nula");
        }

        sucursal = s;
        idBranch = s.getId();
    }

    public static Branch getSucursal(){
        return sucursal;
    }

    public static void iniciarSesion(Seller empleado){

        if (empleado == null) {
            throw new IllegalArgumentException(
                "No se puede iniciar sesión con un empleado nulo"
            );
        }

        if (!empleado.isActive()) {
            throw new IllegalArgumentException(
                "No se puede iniciar sesión con un empleado inactivo"
            );
        }
        empleadoAutenticado = empleado;
    }

    public static Seller getEmpleadoAutenticado() {
        return empleadoAutenticado;
    }

    public static boolean isAuthenticated() {
        return empleadoAutenticado != null;
    }

    public static void cerrarSesion() {
        empleadoAutenticado = null;
    }
}
