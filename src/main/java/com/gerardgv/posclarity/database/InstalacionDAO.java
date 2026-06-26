package com.gerardgv.posclarity.database;


import java.sql.*;

public class InstalacionDAO {
    
    public static int crearSucursal(String nombre,String direccion,String telefono){
        
        String sqlSucursal = "INSERT INTO sucursal (sucursal,direccion,telefono) VALUES (?,?,?)";
        
        String sqlCliente = "INSERT INTO cliente (nombre) VALUES (?)";
        
        try(Connection cn = DBConnection.getConnection()){
            
            cn.setAutoCommit(false);
            
            //CREAR SUCURSAL            
            int idSucursal = 0;

                try(PreparedStatement ps = cn.prepareStatement(sqlSucursal,Statement.RETURN_GENERATED_KEYS)){
                    
                    ps.setString(1, nombre);
                    ps.setString(2, direccion);
                    ps.setString(3, telefono);

                    ps.executeUpdate();

                    ResultSet rs = ps.getGeneratedKeys();
                    
                    if(rs.next()){
                        idSucursal = rs.getInt(1);
                    }                    
                }
                
                if(idSucursal == 0){
                    cn.rollback();
                    return 0;
                }
                
            //CREAR SUCURSAL
            if (!clienteGeneralExiste(cn)) {

                try (PreparedStatement ps =cn.prepareStatement(sqlCliente)) {

                    ps.setString(1, "CLIENTE GENERAL");
                    ps.executeUpdate();

                }
            }
            cn.commit();
            return idSucursal;      
                        
        } catch(Exception e){
            e.printStackTrace();
        }
        return 0;
    }
    
    public static boolean clienteGeneralExiste(Connection cn) throws SQLException{
        
        String sqlCliente = "SELECT COUNT(*) FROM cliente WHERE nombre='CLIENTE GENERAL'";
        
        try(PreparedStatement ps = cn.prepareStatement(sqlCliente);
                ResultSet rs = ps.executeQuery()){
            
            if(rs.next()){
                return rs.getInt(1)>0;
            }
        }
        return false;
    }
    
}
