package com.gerardgv.posclarity.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBConnection {
    
    public static final String HOST = "localhost"; //Direccion de servidor.
    public static final String PORT = "3306"; //puertoMySQL
    public static final String DATABASE = "posclarity"; //Nombre de Base de Datos.
    public static final String USER = "root"; //Usuario.
    public static final String PASSWORD = ""; //Contraseña.
    
    public static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE 
                                     + "?useSSL=false&serverTimezone=UTC";
    
    private static Connection connection;
    
    public static Connection getConnection(){
        if(connection == null){
            try{
                connection = DriverManager.getConnection(URL,USER,PASSWORD);
                System.out.println("Conexión Correcta" + URL);
            } catch(SQLException e){
                System.out.println("Error con la Conexión" + e.getMessage());
                e.printStackTrace();
            }
        }
        return connection;
    }
}
