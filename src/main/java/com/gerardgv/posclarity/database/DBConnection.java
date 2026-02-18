package com.gerardgv.posclarity.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBConnection {
    
    public static final String HOST = "localhost"; //Direccion de servidor.
    public static final String PORT = "3306"; //puertoMySQL
    public static final String DATABASE = "clarity"; //Nombre de Base de Datos.
    public static final String USER = "root"; //Usuario.
    public static final String PASSWORD = "clarity2524"; //Contraseña.
    
    public static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
        + "?useSSL=false"
        + "&allowPublicKeyRetrieval=true"
        + "&serverTimezone=UTC";
    
        
    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(URL,USER,PASSWORD);
        
    }
}
