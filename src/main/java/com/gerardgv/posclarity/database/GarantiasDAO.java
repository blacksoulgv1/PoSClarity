package com.gerardgv.posclarity.database;

import com.gerardgv.posclarity.models.Garantia;
import com.gerardgv.posclarity.models.GarantiaDetalle;
import com.gerardgv.posclarity.models.GarantiaGraduacion;
import com.gerardgv.posclarity.utils.Session;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
                        folio,id_ventas,id_cliente,id_sucursal,motivo,estado,accion,observaciones,usuario_creo)
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
            
            stm.setInt(1, detalle.getIdGarantias());
            stm.setInt(2, detalle.getIdDetalle());
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
            stm.setString(5, graduacion.getOdEje());
            stm.setString(6, graduacion.getOdAdd());
                // Ojo izquierdo
            stm.setString(7, graduacion.getOiEsfera());
            stm.setString(8, graduacion.getOiCilindro());
            stm.setString(9, graduacion.getOiEje());
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
    
    public List<Garantia> obtenergarantiasActivas(){
        
        List<Garantia> lista = new ArrayList<>();
        
        String sqlGaratias = """
                SELECT
                    g.id_garantias,
                    g.folio,
                    g.motivo,
                    g.estado,
                    g.fecha_solicitud,
                    c.nombre AS cliente
                FROM garantias g
                INNER JOIN cliente c ON g.id_cliente = c.id_cliente
                WHERE g.estado <> 'ENTREGADO'
                ORDER BY g.fecha_solicitud DESC
                             """;
        
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sqlGaratias);
                ResultSet rs = ps.executeQuery()){
            
            while(rs.next()){
                
                Garantia g = new Garantia();
                g.setIdgarantias(rs.getInt("id_garantias"));
                g.setFolio(rs.getString("folio"));
                g.setMotivo(rs.getString("motivo"));
                g.setEstado(rs.getString("estado"));
                g.setFechaSolicitud(rs.getTimestamp("fecha_solicitud").toLocalDateTime());
                g.setCliente(rs.getString("cliente"));
                lista.add(g);
            }            
        } catch(Exception e){
            e.printStackTrace();
        }
        return lista;
    }
    
    private boolean cambiarEstadoGarantia(int idGarantia,String estado){
        
        String sql ="""
                    UPDATE garantias
                    SET estado = ?
                    WHERE id_garantias = ?
                    AND id_sucursal = ?
                    """;
        
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1,estado);
            ps.setInt(2, idGarantia);
            ps.setInt(3,Session.getSucursal().getId());
            
            return ps.executeUpdate() > 0;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean recepcionGarantia(int idGarantia){
        return cambiarEstadoGarantia(idGarantia,"RECIBIDO");
    }
    
    public boolean entregarGarantia(int idGarantia){
        return cambiarEstadoGarantia(idGarantia,"ENTREGADO");
    }
    
}
