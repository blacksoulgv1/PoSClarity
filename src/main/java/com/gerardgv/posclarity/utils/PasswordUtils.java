package com.gerardgv.posclarity.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {
    
    private static final int LOG_ROUNDS = 12;
    
    private PasswordUtils(){
        
    }
    
    public static String hash(String password){
        
        if(password == null || password.isBlank()){
            throw new IllegalArgumentException( "La contraseña no puede estar vacía.");
        }
        return BCrypt.hashpw(password, BCrypt.gensalt(LOG_ROUNDS));
    }
    
    public static boolean matches(
            String password,
            String passwordHash) {

        if (password == null
                || password.isBlank()
                || passwordHash == null
                || passwordHash.isBlank()) {

            return false;
        }

        try {
            return BCrypt.checkpw(password, passwordHash);

        } catch (IllegalArgumentException e) {
            return false;
        }
    } 
    
}
