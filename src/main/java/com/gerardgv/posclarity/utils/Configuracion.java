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
    
}
