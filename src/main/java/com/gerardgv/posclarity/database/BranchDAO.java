package com.gerardgv.posclarity.database;

import com.gerardgv.posclarity.models.Branch;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class BranchDAO {
    
            //INSERT
    public boolean insert(Branch b){
        
        String sql ="Insert INTO sucursal (sucursal,telefono,direccion,activo) "
                + "VALUES (?,?,?,?)";
        
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)){
            
            stm.setString(1, b.getSucursal());
            stm.setString(2, b.getTelefono());
            stm.setString(3, b.getDireccion());
            stm.setBoolean(4, b.isActivo());
            
            return stm.executeUpdate()> 0;
            
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }
    
            //UPDATE
    public boolean update(Branch b){
        
        String sql = "UPDATE sucursal SET nombre=?, telefono=?, direccion=? WHERE id_sucursal=? ";
       
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)){
            
            stm.setString(1, b.getSucursal());
            stm.setString(2, b.getTelefono());
            stm.setString(3, b.getDireccion());
            stm.setInt(4, b.getId());
            
            return stm.executeUpdate() > 0;
        } catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }
    
            //LISTA
    public List<Branch> getAll(){
        
        List<Branch> lista = new ArrayList<>();
        
        String sql = "SELECT * FROM sucursal";
        
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql);
                ResultSet rs = stm.executeQuery()){
            
            while (rs.next()){
                
                Branch b = new Branch();
            
                b.setId(rs.getInt("id_sucursal"));
                b.setSucursal(rs.getString("sucursal"));
                b.setTelefono(rs.getString("telefono"));
                b.setDireccion(rs.getString("direccion"));
                b.setActivo(rs.getBoolean("activo"));
            
                lista.add(b);
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        return lista;
    }
    
            //Activar & Desactivar
    
    public boolean updateEstado(int id, boolean estado){
        String sql ="UPDATE sucursal SET activo =? WHERE id_sucursal =?";
        
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)){
            
            stm.setBoolean(1, estado);
            stm.setInt(2, id);
            return  stm.executeUpdate()>0;
            
        } catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }
    
    public static Branch obtenerPorId(int id){
        Branch sucursal = null;
        
        String sql = "SELECT * FROM sucursal WHERE id_sucursal = ?";
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)){
            
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();
            
            if(rs.next()){
                sucursal = new Branch();
                sucursal.setId(rs.getInt("id_sucursal"));
                sucursal.setSucursal(rs.getString("sucursal"));
                sucursal.setDireccion(rs.getString("direccion"));
                sucursal.setTelefono(rs.getString("telefono"));
            }
            
        } catch(Exception e){
            e.printStackTrace();
        }
        return sucursal;
    }
    
}
