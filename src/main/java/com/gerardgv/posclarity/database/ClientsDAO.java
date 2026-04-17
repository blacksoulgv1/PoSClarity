package com.gerardgv.posclarity.database;

import com.gerardgv.posclarity.models.Clients;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientsDAO {
    
    //Insertar Producto
    public boolean insert(Clients c){
        
        String sql = "INSERT INTO cliente ("
                + "nombre,telefono,direccion,"
                + "od_esf, od_cil, od_eje,"
                + "oi_esf, oi_cil, oi_eje,"
                + "add_lente,activo)"
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        
        try(Connection conn =DBConnection.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)){
            
            stm.setString(1, c.getNombre());
            stm.setString(2, c.getTelefono());
            stm.setString(3, c.getDireccion());
            stm.setString(4, c.getOdEsf());
            stm.setString(5, c.getOdCil());
            stm.setString(6, c.getOdEje());
            stm.setString(7, c.getOiEsf());
            stm.setString(8, c.getOiCil());
            stm.setString(9, c.getOiEje());
            stm.setString(10, c.getAdd());
            stm.setBoolean(11, c.isActivo());

            return stm.executeUpdate() > 0;
            
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }
    
    //Actualizar Producto    
    public boolean update(Clients c){
        String sql = "UPDATE cliente SET "
            + "nombre=?, telefono=?, direccion=?, "
            + "od_esf=?, od_cil=?, od_eje=?, "
            + "oi_esf=?, oi_cil=?, oi_eje=?, "
            + "add_lente=? "
            + "WHERE id_cliente=?";

    try(Connection conn = DBConnection.getConnection();
        PreparedStatement stm = conn.prepareStatement(sql)){

        stm.setString(1, c.getNombre());
        stm.setString(2, c.getTelefono());
        stm.setString(3, c.getDireccion());
        stm.setString(4, c.getOdEsf());
        stm.setString(5, c.getOdCil());
        stm.setString(6, c.getOdEje());
        stm.setString(7, c.getOiEsf());
        stm.setString(8, c.getOiCil());
        stm.setString(9, c.getOiEje());
        stm.setString(10, c.getAdd());
        stm.setInt(11, c.getId());

        return stm.executeUpdate() > 0;

    }catch(SQLException e){
        e.printStackTrace();
        return false;
    }
    }
    
    //Actualizar Estado Producto    
    public boolean updateEstado(int id, boolean estado){
        String sql = "UPDATE cliente SET activo=? WHERE id_cliente=?";
    try(Connection conn = DBConnection.getConnection();
        PreparedStatement stm = conn.prepareStatement(sql)){

        stm.setBoolean(1, estado);
        stm.setInt(2, id);

        return stm.executeUpdate() > 0;

    } catch(SQLException e){
        e.printStackTrace();
        return false;
    }
    }
    
    //Lista Producto
    public List<Clients> findAll(){

    List<Clients> lista = new ArrayList<>();

    String sql = "SELECT * FROM cliente";

    try(Connection conn = DBConnection.getConnection();
        PreparedStatement stm = conn.prepareStatement(sql);
        ResultSet rs = stm.executeQuery()){

        while(rs.next()){

            Clients c = new Clients();

            c.setId(rs.getInt("id_cliente"));
            c.setNombre(rs.getString("nombre"));
            c.setTelefono(rs.getString("telefono"));
            c.setDireccion(rs.getString("direccion"));

            c.setOdEsf(rs.getString("od_esf"));
            c.setOdCil(rs.getString("od_cil"));
            c.setOdEje(rs.getString("od_eje"));

            c.setOiEsf(rs.getString("oi_esf"));
            c.setOiCil(rs.getString("oi_cil"));
            c.setOiEje(rs.getString("oi_eje"));

            c.setAdd(rs.getString("add_lente"));
            c.setActivo(rs.getBoolean("activo"));

            lista.add(c);
        }

    }catch(SQLException e){
        e.printStackTrace();
    }

    return lista;
}    
    
    //Busqueda Por Nombre
    public List<Clients> buscarPorNombre(String nombre){
        
        List<Clients> lista = new ArrayList<>();
        
        String sql = "SELECT * FROM cliente WHERE  nombre LIKE ? AND activo = true";
        
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)){
            
            stm.setString(1, "%" + nombre + "%");
            ResultSet rs = stm.executeQuery();
            
            while(rs.next()){
                
                Clients c = new Clients();
                
                c.setId(rs.getInt("id_cliente"));
                c.setNombre(rs.getString("nombre"));
                c.setTelefono(rs.getString("telefono"));
                c.setDireccion(rs.getString("direccion"));

                c.setOdEsf(rs.getString("od_esf"));
                c.setOdCil(rs.getString("od_cil"));
                c.setOdEje(rs.getString("od_eje"));
                
                c.setOiEsf(rs.getString("oi_esf"));
                c.setOiCil(rs.getString("oi_cil"));
                c.setOiEje(rs.getString("oi_eje"));
                
                c.setAdd(rs.getString("add_lente"));
                
                lista.add(c);                
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return lista;
    }
    
    
}
