package com.gerardgv.posclarity.database;

import com.gerardgv.posclarity.models.Garantia;
import com.gerardgv.posclarity.models.GarantiaDetalle;
import com.gerardgv.posclarity.models.GarantiaGraduacion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class GarantiasDAO {
    
    public boolean crearGarantia(Garantia garantia,
            List<GarantiaDetalle> detalles, GarantiaGraduacion original,
            GarantiaGraduacion nueva){
        
        Connection conn = null;
        
        try{
           conn = DBConnection.getConnection();
           
            conn.setAutoCommit(false);
            
            String folio = generarFolio();
            garantia.setFolio(folio);
            
           // 1 insertar Garantia
            int idGarantia = insertGarantia(conn, garantia);
            
            if(idGarantia == 0){
                conn.rollback();
                return false;
            }
            
        // 2 insertar detalles
        for(GarantiaDetalle detalle : detalles){
            
            detalle.setIdGarantias(idGarantia);
            insertDetalle(conn, detalle);
        }
        
        // 3 insertar graduación original
        original.setIdGarantias(idGarantia);
        insertGraduacion(conn, original);
        
        // 4 insertar graduación nueva
        nueva.setIdGarantias(idGarantia);
        insertGraduacion(conn, nueva);
            
            conn.commit();
            return true;
        } catch(SQLException e){
            
            e.printStackTrace();
            
            if(conn != null){
                try{
                    conn.rollback();
                }catch(SQLException ex){
                    ex.printStackTrace();
                }
            }        
        return false;
        } finally {
            if(conn != null){
                try{
                    conn.close();
                } catch(SQLException e){
                    e.printStackTrace();
                }
            }
        }        
    }
    
    private int insertGarantia(Connection conn, Garantia garantia) throws SQLException {
        
        String sql = """
                    INSERT INTO garantias(
                        folio,id_ventas,id,cliente,id_sucursal,motivo,estado,accion,observaciones,usuario_creo)
                    VALUES (?,?,?,?,?,?,?,?,?)
                    """;
        
        try(PreparedStatement stm = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)){
            
            stm.setString(1, garantia.getFolio());
            stm.setInt(2, garantia.getIdventas());
            stm.setInt(3, garantia.getIdcliente());
            stm.setInt(4, garantia.getIdsucursal());
            stm.setString(5, garantia.getMotivo());
            stm.setString(6, garantia.getEstado());
            stm.setString(7, garantia.getAccion());
            stm.setString(8, garantia.getObservaciones());            
            stm.setInt(9, garantia.getUsuario());
            
            stm.executeUpdate();
            
            ResultSet rs = stm.getGeneratedKeys();
            
            if(rs.next()){
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    private void insertDetalle(Connection conn, GarantiaDetalle detalle)throws SQLException {
        
        String sql = """
                    INSERT INTO garantia_detalle(id_garantias,id_detalle,cantidad)
                    VALUES (?,?,?);                     
                    """;
        
        try(PreparedStatement stm = conn.prepareStatement(sql)){
            
            stm.setInt(1, detalle.getIdDetalle());
            stm.setInt(2, detalle.getIdDetalleGarantia());
            stm.setInt(3, detalle.getCantidad());
            
            stm.executeUpdate();
            
        }
        
    }
    
    private void insertGraduacion(Connection conn, GarantiaGraduacion graduacion) throws SQLException{
        
       String sql = """
            INSERT INTO garantia_graduacion
            (id_garantias,tipo,od_esfera,od_cilindro,od_eje,od_add,
                oi_esfera,oi_cilindro,oi_eje,oi_add)
            VALUES (?,?,?,?,?,?,?,?,?,?)
            """; 
       
       try(PreparedStatement stm = conn.prepareStatement(sql)){
           
            stm.setInt(1, graduacion.getIdGarantias());
            stm.setString(2, graduacion.getTipo());
                // Ojo derecho
            stm.setString(3, graduacion.getOdEsfera());
            stm.setString(4, graduacion.getOdCilindro());
            stm.setString(5, graduacion.getOdAdd());
            stm.setString(6, graduacion.getOdAdd());
                // Ojo izquierdo
            stm.setString(7, graduacion.getOiEsfera());
            stm.setString(8, graduacion.getOiCilindro());
            stm.setString(9, graduacion.getOiAdd());
            stm.setString(10, graduacion.getOiAdd());
            
            stm.executeUpdate();           
       }        
    }
    
    public String generarFolio(){
                
        String sql = "SELECT IFNULL(MAX(id_garantias),0)+1 AS siguiente FROM garantias";
        
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){
            
            if(rs.next()){                
                return String.format( "GAR-%06d", rs.getInt("siguiente"));
            }                        
        }catch(Exception e){
            e.printStackTrace();
        }
        return "GAR-000001";
    }
    
}
