package com.gerardgv.posclarity.database;

import com.gerardgv.posclarity.models.Descuento;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DescuentoDAO {
    
    public boolean insert(Descuento d){
        
        String sql = "INSERT INTO descuento (nombre,tipo_aplicacion,tipo_valor,valor,codigo_cupon,categoria,fecha_inicio,fecha_fin,prioridad,activo)"
                + "VALUES (?,?,?,?,?,?,?,?,?,?)";
        
        try(Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)){
            
            ps.setString(1, d.getNombre());
            ps.setString(2, d.getTipoAplicacion());
            ps.setString(3, d.getTipoValor());
            ps.setDouble(4, d.getValor());
            ps.setString(5, d.getCodigoCupon());
            ps.setString(6, d.getCategoria());
            ps.setDate(7, Date.valueOf(d.getFechaInicio()));
            ps.setDate(8, Date.valueOf(d.getFechaFin()));
            ps.setInt(9, d.getPrioridad());
            ps.setBoolean(10, d.isActivo());
            
            ps.executeUpdate();
            return true;
        } catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean update(Descuento d){
        
        String sql = "UPDATE descuento SET nombre=?, tipo_aplicacion=?, tipo_valor=?, " +
                "valor=?, codigo_cupon=?, categoria=?, fecha_inicio=?, fecha_fin=?, prioridad=?, activo=? " +
                "WHERE id_descuento=?";
        
        try(Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)){
            
            ps.setString(1, d.getNombre());
            ps.setString(2, d.getTipoAplicacion());
            ps.setString(3, d.getTipoValor());
            ps.setDouble(4, d.getValor());
            ps.setString(5, d.getCodigoCupon());
            ps.setString(6, d.getCategoria());
            ps.setDate(7, d.getFechaInicio() != null ? Date.valueOf(d.getFechaInicio()) : null);
            ps.setDate(8, d.getFechaFin()!= null ? Date.valueOf(d.getFechaFin()) : null);
            ps.setInt(9, d.getPrioridad());
            ps.setBoolean(10, d.isActivo());
            ps.setInt(11, d.getId());
            
            ps.executeUpdate();
            return true;
        } catch( SQLException e){
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean eliminar(int id){
        
        String sql = "DELETE FROM descuento WHERE id_descuento=?";
        
        try(Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)){
            
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Descuento> getAll(){
        
        List<Descuento> lista = new ArrayList<>();
        String sql = "SELECT * FROM descuento ORDER BY prioridad ASC";
        
        try(Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()){
            
            while(rs.next()){
                
                Descuento d = new Descuento();
                
                d.setId(rs.getInt("id_descuento"));
                d.setNombre(rs.getString("nombre"));
                d.setTipoAplicacion(rs.getString("tipo_aplicacion"));
                d.setTipoValor(rs.getString("tipo_valor"));
                d.setValor(rs.getDouble("valor"));
                d.setCodigoCupon(rs.getString("codigo_cupon"));
                d.setCategoria(rs.getString("categoria"));
                
                Date fi = rs.getDate("fecha_inicio");
                Date ff = rs.getDate("fecha_fin");
                
                if(fi != null) d.setFechaInicio(fi.toLocalDate());
                if(ff != null) d.setFechaFin(ff.toLocalDate());
                
                d.setPrioridad(rs.getInt("prioridad"));
                d.setActivo(rs.getBoolean("activo"));
                
                lista.add(d);
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        return lista;
    }
    
    public Descuento obtenerGeneralActivo(){
        
        String sql = "SELECT * FROM descuento " +
                "WHERE tipo_aplicacion='venta_total' " +
                "AND activo=true " +
                "AND CURDATE() BETWEEN fecha_inicio AND fecha_fin " +
                "ORDER BY prioridad ASC LIMIT 1";

        return ejecutarConsultaUnica(sql, null);
    }
    
    public Descuento obtenerPorCategoria(String categoria){
        
        String sql = "SELECT * FROM descuento " +
                "WHERE tipo_aplicacion='categoria' " +
                "AND categoria=? " +
                "AND activo=true " +
                "AND CURDATE() BETWEEN fecha_inicio AND fecha_fin " +
                "ORDER BY prioridad ASC LIMIT 1";

        return ejecutarConsultaUnica(sql, categoria);        
    }
    
    public Descuento obtenerCuponValido(String codigo){
        
        String sql = "SELECT * FROM descuento " +
                "WHERE tipo_aplicacion='cupon' " +
                "AND codigo_cupon=? " +
                "AND activo=true " +
                "AND CURDATE() BETWEEN fecha_inicio AND fecha_fin " +
                "ORDER BY prioridad ASC LIMIT 1";

        return ejecutarConsultaUnica(sql, codigo);
    }
    
    private Descuento ejecutarConsultaUnica(String sql, String parametro){
        
        try(Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)){
            
            if(parametro != null)
                ps.setString(1, parametro);
            
            ResultSet rs = ps.executeQuery();
            
            if(rs.next()){
                
                Descuento d = new Descuento();
                
                d.setId(rs.getInt("id_descuento"));
                d.setNombre(rs.getString("nombre"));
                d.setTipoAplicacion(rs.getString("tipo_aplicacion"));
                d.setTipoValor(rs.getString("tipo_valor"));
                d.setValor(rs.getDouble("valor"));
                d.setCodigoCupon(rs.getString("codigo_cupon"));
                d.setCategoria(rs.getString("categoria"));
                
                Date fi = rs.getDate("fecha_inicio");
                Date ff = rs.getDate("fecha_fin");
                
                if(fi != null) d.setFechaInicio(fi.toLocalDate());
                if(ff != null) d.setFechaFin(ff.toLocalDate());
                d.setPrioridad(rs.getInt("prioridad"));
                d.setActivo(rs.getBoolean("activo"));
                
                return d;
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean actualizarActivo(int id, boolean activo){
        String sql = "UPDATE descuento SET activo =? WHERE id_descuento=?";
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setBoolean(1, activo);
            ps.setInt(2, id);
            
            return  ps.executeUpdate() >0;
        } catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
