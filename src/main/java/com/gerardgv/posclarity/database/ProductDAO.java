package com.gerardgv.posclarity.database;

import com.gerardgv.posclarity.models.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    
    //Insertar Producto
    public boolean insert(Product p){
        String sql = "INSERT INTO products (modelo,marca,stock,categoria,precio,tipo_producto)"
                + "VALUES (?,?,?,?,?,?)";
        
        try(Connection conn =DBConnection.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)){
            
            stm.setString(1, p.getModelo());
            stm.setString(2, p.getMarca());
            stm.setInt(3, p.getStock());
            stm.setString(4, p.getCategoria());
            stm.setDouble(5, p.getPrecio());
            stm.setString(6, p.getTipo_producto());
            
            return stm.executeUpdate() > 0;
            
        }catch(SQLException e){
            System.out.println("Errot al insertar Producto");
            e.printStackTrace();
            return false;
        }
    }
    
    //Lista de Productos    
    public List<Product> getAll(){
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products";
        
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql);
                ResultSet rs = stm.executeQuery()){
            
            while (rs.next()) {                
                Product product = new Product();
                
                product.setId_product(rs.getInt("id_product"));
                product.setModelo(rs.getString("modelo"));
                product.setMarca(rs.getString("marca"));
                product.setStock(rs.getInt("stock"));
                product.setCategoria(rs.getString("categoria"));
                product.setPrecio(rs.getDouble("precio"));              
                product.setTipo_producto(rs.getString("tipo_producto"));
                
                products.add(product);
            }
            
        }catch(SQLException e){
            System.out.println("Error al obtener lista de productos");
            e.printStackTrace();
        }
        return products;
    }
    
    //Buscar Producto por ID
    public Product getById(int id){
        
        String sql = "SELECT * FROM products WHERE id_product = ?";
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
                    product.setStock(rs.getInt("stock"));
                    product.setCategoria(rs.getString("categoria"));
                    product.setPrecio(rs.getDouble("precio"));
                    product.setTipo_producto(rs.getString("tipo_producto"));
                }
            }
        }catch(SQLException e){
            System.out.println("Error al buscar producto");
            e.printStackTrace();
        }
        return product;
    }
    
    //Actualizar Producto
    public boolean update(Product product){
        
        String sql = "UPDATE products SET modelo=?, marca=?, stock=?, categoria=?, precio=?, tipo_producto=?"
                + " WHERE id_product=?";
        
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)){
            
            stm.setString(1,product.getModelo());
            stm.setString(2,product.getMarca());
            stm.setInt(3, product.getStock());
            stm.setString(4,product.getCategoria());
            stm.setDouble(5,product.getPrecio());
            stm.setInt(7,product.getId_product());
            
            return stm.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Error al actualizar producto");
            e.printStackTrace();
            return false;
        }
    }
    
    //Eliminar producto
    public boolean delete(int id){
        String sql = "DELATE FROM products WHERE id_product = ?";
        
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)){
            
            stm.setInt(1, id);
            
            return  stm.executeUpdate() > 0;
        }catch(SQLException e){
            System.out.println("Error al eliminar producto");
            e.printStackTrace();
            return false;
        }
    }
}
