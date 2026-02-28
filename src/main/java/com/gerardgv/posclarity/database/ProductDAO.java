package com.gerardgv.posclarity.database;

import com.gerardgv.posclarity.models.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    
    //Insertar Producto TERMINADO
    public boolean insert(Product p){
        String sql = "INSERT INTO products "
            + "(modelo,marca,categoria,precio,maneja_stock,mica_base,tipo_producto,activo)"
            + "VALUES (?,?,?,?,?,?,?,?)";
        
        try(Connection conn =DBConnection.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)){
            
            stm.setString(1, p.getModelo());
            stm.setString(2, p.getMarca());
            stm.setString(3, p.getCategoria());
            stm.setDouble(4, p.getPrecio());
            stm.setBoolean(5, p.isManejaStock());
            stm.setBoolean(6, p.isMicaBase());
            stm.setString(7, p.getTipo_producto());
            stm.setBoolean(8, p.isActivo());
            
            return stm.executeUpdate() > 0;
            
        }catch(SQLException e){
            System.out.println();
            e.printStackTrace();
            return false;
        }
    }
    
    //Lista de Productos TERMINADO
    public List<Product> getAll(){
        
        List<Product> lista = new ArrayList<>();
        
        String sql ="SELECT * FROM products";        
        
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql);
                ResultSet rs = stm.executeQuery()){
            
            while (rs.next()) {                
                Product p = new Product();
                
                p.setId_product(rs.getInt("id_product"));
                p.setModelo(rs.getString("modelo"));
                p.setMarca(rs.getString("marca"));
                p.setCategoria(rs.getString("categoria"));
                p.setPrecio(rs.getDouble("precio"));
                p.setManejaStock(rs.getBoolean("maneja_stock"));
                p.setMicaBase(rs.getBoolean("mica_base"));
                p.setTipo_producto(rs.getString("tipo_producto"));
                p.setActivo(rs.getBoolean("activo"));

                
                lista.add(p);
            }
            
        }catch(SQLException e){
            e.printStackTrace();
        }
        return lista;
    }
    
    //Buscar Producto por Modelo TERMINADO
    public Product getByModel(String model){
        
        String sql =" SELECT * FROM products WHERE modelo=?";
        Product p = null;
        
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)){
            
            stm.setString(1, model);
            
            try(ResultSet rs = stm.executeQuery()){
                
                if(rs.next()){
                    
                    p = new Product();
                    
                p.setId_product(rs.getInt("id_product"));
                p.setModelo(rs.getString("modelo"));
                p.setMarca(rs.getString("marca"));
                p.setCategoria(rs.getString("categoria"));
                p.setPrecio(rs.getDouble("precio"));
                p.setManejaStock(rs.getBoolean("maneja_stock"));
                p.setMicaBase(rs.getBoolean("mica_base"));
                p.setTipo_producto(rs.getString("tipo_producto"));
                p.setActivo(rs.getBoolean("activo"));
                    
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p;
    }
    
    //Buscar Producto por ID TERMINADO
    public Product getById(int id){
        
        String sql ="SELECT * FRON products WHERE id_product=?";
        
        Product product = null;
        
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)){
            
            stm.setInt(1, id);
            
            try(ResultSet rs = stm.executeQuery()){
                
                if(rs.next()){
                    
                    product = new Product();
                    product.setId_product(rs.getInt("id_product"));
                    product.setModelo(rs.getString("modelo"));
                    product.setMarca(rs.getString("marca"));
                    product.setCategoria(rs.getString("categoria"));
                    product.setPrecio(rs.getDouble("precio"));
                    product.setManejaStock(rs.getBoolean("maneja_stock"));
                    product.setMicaBase(rs.getBoolean("mica_base"));
                    product.setTipo_producto(rs.getString("tipo_producto"));
                    product.setActivo(rs.getBoolean("activo"));
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return product;
    }
    
    //Actualizar Producto TERMINADO
    public boolean update(Product p){
        
        String sql ="""
        UPDATE products SET
        modelo=?,
        marca=?,
        categoria=?,
        precio=?,
        maneja_stock=?,
        mica_base=?,
        tipo_producto=?,
        activo=?
        WHERE id_product=?
        """;
        
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)){
            
            stm.setString(1, p.getModelo());
            stm.setString(2, p.getMarca());
            stm.setString(3, p.getCategoria());
            stm.setDouble(4, p.getPrecio());
            stm.setBoolean(5, p.isManejaStock());
            stm.setBoolean(6, p.isMicaBase());
            stm.setString(7, p.getTipo_producto());
            stm.setBoolean(8, p.isActivo());
            stm.setInt(9, p.getId_product());
            
            return stm.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    //Eliminar producto TERMINADO
    public boolean cambiarEstado(int id, boolean estado){
        
        String sql = "UPDATE products SET activo=? WHERE id_product=?";
        
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)){
            
            stm.setBoolean(1, estado);
            stm.setInt(2, id);
                        
            return  stm.executeUpdate() > 0;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }
}
