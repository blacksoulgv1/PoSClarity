package com.gerardgv.posclarity.utils;

import java.util.prefs.Preferences;

public class Configuracion {
    
    private static final Preferences prefs = Preferences.userRoot().node("posclarity");
    
    public static void guardarSucursal(int idBranch){
        prefs.putInt("id_sucursal", idBranch);        
    }
    
    public static int obtenerSucursal(){
        return prefs.getInt("id_sucursal", -1);
    }
    
    public static boolean existeSucursal(){
        return obtenerSucursal() != -1;
    }
    
    public static void guardarImpresora(String nombreImpresora){
        prefs.put("impresora", nombreImpresora);
    }
    
    public static String obtenerImpresora(){
        return prefs.get("impresora",null);
    }
    
}
