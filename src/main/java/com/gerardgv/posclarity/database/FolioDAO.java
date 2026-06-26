package com.gerardgv.posclarity.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FolioDAO {
    
    public String generarFolio(int idSucursal, String tipo){
        
        Connection conn = null;        
        String folio = null;
        
        String sqlFolio ="""
                        SELECT s.codigo,
                               f.consecutivo
                        FROM folios f
                        INNER JOIN sucursal s
                        ON f.id_sucursal = s.id_sucursal
                        WHERE f.id_sucursal = ?
                        AND f.tipo = ?
                        FOR UPDATE
                        """;
        
        String sqlUpdate ="""
                        UPDATE folios
                        SET consecutivo = consecutivo + 1
                        WHERE id_sucursal = ?
                        AND tipo = ?
                        """;
        
        try{
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            String codigoSucursal = "";
            int consecutivoActual = 0;
            
            PreparedStatement ps = conn.prepareStatement(sqlFolio);
            
            ps.setInt(1, idSucursal);
            ps.setString(2, tipo);
            
            ResultSet rs = ps.executeQuery();
            
            if(rs.next()){
                
                codigoSucursal = rs.getString("codigo");
                consecutivoActual = rs.getInt("consecutivo");
            }
            
            PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate);
            
            psUpdate.setInt(1, idSucursal);
            psUpdate.setString(2, tipo);
            
            psUpdate.executeUpdate();
            
            conn.commit();
            
            String letraTipo = tipo.equals("VENTA") ? "V" : "G";
            
            folio = String.format("%s-%s-%06d", codigoSucursal,letraTipo,consecutivoActual + 1);
            
        } catch (Exception e){
            
            try {
                if(conn != null){
                    conn.rollback();
                }
            } catch (Exception ex) {}
            e.printStackTrace();
        } finally{
            try{
                if(conn != null){
                   conn.setAutoCommit(true); 
                }
            } catch (Exception e){}
        }
        return folio;
    }
    
    public String ObtenerSiguienteFolio(int idSucursal, String tipo){
        
        String folio =  null;
        
        String sql ="""
                SELECT 
                    s.codigo,
                    f.consecutivo
                FROM folios f
                INNER JOIN sucursal s
                ON f.id_sucursal = s.id_sucursal
                WHERE f.id_sucursal = ?
                AND f.tipo = ?                    
                    """;
        
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)){
            
            ps.setInt(1, idSucursal);
            ps.setString(2, tipo);
            
            ResultSet rs = ps.executeQuery();
            
            if(rs.next()){
                
                String codigoSucursal = rs.getString("codigo");
                int siguiente = rs.getInt("consecutivo") + 1;
                String letraTipo = tipo.equals("VENTA") ? "V" : "G";
                folio = String.format("%s-%s-%06d", codigoSucursal,letraTipo, siguiente);
            }            
        } catch( Exception e){
            e.printStackTrace();
        }
        return folio;
    }
    
}
