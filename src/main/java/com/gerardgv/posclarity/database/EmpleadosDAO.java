package com.gerardgv.posclarity.database;

import com.gerardgv.posclarity.models.Empleados;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmpleadosDAO {
    
    public boolean insert(Empleados em){
        
        String sql ="INSERT INTO vendedor (nombre,codigo,rol,estatus) VALUES (?,?,?,?)";
        
        try( Connection conn = DBConnection.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)){
            
            stm.setString(1,em.getNombre());
            stm.setInt(2, em.getCodigo());
            stm.setString(3,em.getRol());
            stm.setBoolean(4, em.isActivo());
            
            return stm.executeUpdate()>0;
            
        } catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean update(Empleados em){
        String sql ="UPDATE vendedor SET nombre=?, codigo=?, rol=?, estatus=? WHERE id_vendedor=?";
        
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)){
            
            stm.setString(1, em.getNombre());
            stm.setInt(2, em.getCodigo());
            stm.setString(3, em.getRol());
            stm.setBoolean(4, em.isActivo());
            stm.setInt(5, em.getId_vendedor());
            
            return stm.executeUpdate()>0;                       
        } catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean cambiarStatus(int id, boolean estado){
        
        String sql = "UPDATE vendedor SET estatus=? WHERE id_vendedor=?";
        
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)){
            
            stm.setBoolean(1, estado);
            stm.setInt(2, id);
            return  stm.executeUpdate()>0;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }
    
    public static List<Empleados> findAll(){
        
        List<Empleados> lista = new ArrayList<>();
        
        String sql = "SELECT * FROM vendedor";
        
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()){
            
            while(rs.next()){
                Empleados e = new Empleados();
                e.setId_vendedor(rs.getInt("id_vendedor"));
                e.setNombre(rs.getString("nombre"));
                e.setCodigo(rs.getInt("codigo"));
                e.setRol(rs.getString("rol"));
                e.setActivo(rs.getBoolean("estatus"));
                lista.add(e);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return lista;
    }
    
    public List<Empleados> buscarPorNombre(String nombre){
        
        List<Empleados> lista = new ArrayList<>();
        
        String sql = "SELECT * FROM vendedor WHERE  nombre LIKE ? AND estatus = true";
        
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)){
            
            stm.setString(1, "%" + nombre + "%");
            ResultSet rs = stm.executeQuery();
            
            while(rs.next()){
                
                Empleados e = new Empleados();
                
                e.setId_vendedor(rs.getInt("id_vendedor"));
                e.setNombre(rs.getString("nombre"));
                e.setCodigo(rs.getInt("codigo"));
                e.setRol(rs.getString("rol"));
                e.setActivo(rs.getBoolean("estatus"));
                
                lista.add(e);                
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return lista;
    }
    
    public Empleados validarAcceso(int codigo, String password){
        
        String sql ="""
                    SELECT * FROM vendedor
                    WHERE codigo = ?
                    AND pass = ?
                    AND estatus = true                    
                    """;
        
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)){
            
            stm.setInt(1, codigo);
            stm.setString(2, password);

            ResultSet rs = stm.executeQuery();
            
            if(rs.next()){
               Empleados emp = new Empleados();

            emp.setId_vendedor(rs.getInt("id_vendedor"));
            emp.setNombre(rs.getString("nombre"));
            emp.setCodigo(rs.getInt("codigo"));
            emp.setRol(rs.getString("rol"));
            emp.setActivo(rs.getBoolean("estatus"));

            return emp; 
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
}
