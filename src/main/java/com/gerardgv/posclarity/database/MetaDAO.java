package com.gerardgv.posclarity.database;

import com.gerardgv.posclarity.models.Meta;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class MetaDAO {
    
    public boolean saveMeta(int idSucursal, int mes, int anio, double monto){
        
        String sql ="""
            INSERT INTO metas(id_sucursal,mes,anio,monto)
                VALUES (?,?,?,?)
                ON DUPLICATE KEY UPDATE monto = VALUES (monto)
                    """;
        
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, idSucursal);
            ps.setInt(2, mes);
            ps.setInt(3, anio);
            ps.setDouble(4, monto);
            
            return ps.executeUpdate() > 0;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Meta> obtenerMetas(){
        
        List<Meta> lista = new ArrayList<>();
        
        String sql = """
            SELECT m.*,s.sucursal
                FROM metas m
                JOIN sucursal s ON m.id_sucursal = s.id_sucursal
                ORDER BY m.anio DESC, m.mes DESC
                     """;
        
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()){
            
            while(rs.next()){
                
                Meta m = new Meta();
                m.setIdMeta(rs.getInt("id_meta"));
                m.setIdSucursal(rs.getInt("id_sucursal"));
                m.setMes(rs.getInt("mes"));
                m.setAnio(rs.getInt("anio"));
                m.setMonto(rs.getDouble("monto"));
                m.setNombreSucursal(rs.getString("sucursal"));
                
                lista.add(m);                
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return lista;
    }
    
    public double obtenerMetaMensual(int idSucursal, int mes, int anio){
        
        String sql = " SELECT monto FROM metas WHERE id_sucursal = ? AND mes =? AND anio = ?";
        
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)){
            
            ps.setInt(1, idSucursal);
            ps.setInt(2, mes);
            ps.setInt(3, anio);
            
            ResultSet rs = ps.executeQuery();
            
            if(rs.next()){
                return rs.getDouble("monto");
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return 0;
    }
    
}
