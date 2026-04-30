package com.gerardgv.posclarity.database;

import com.gerardgv.posclarity.models.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    
    //Insertar Producto TERMINADO
    public boolean insert(Product p, int idSucursal, int stockInicial){
                
        String sqlProducto = "INSERT INTO products "
            + "(modelo,marca,categoria,precio,maneja_stock,mica_base,tipo_producto,activo)"
            + "VALUES (?,?,?,?,?,?,?,?)";
        
        String sqlInventario = "INSERT INTO inventario_sucursal (id_sucursal,id_product,stock)"
                + "VALUES (?,?,?)";
        
        try(Connection conn =DBConnection.getConnection()){
            
            conn.setAutoCommit(false);
            
            //Insert Producto
            
            PreparedStatement stmProd = conn.prepareStatement(
                    sqlProducto, Statement.RETURN_GENERATED_KEYS);
            
            stmProd.setString(1, p.getModelo());
            stmProd.setString(2, p.getMarca());
            stmProd.setString(3, p.getCategoria());
            stmProd.setDouble(4, p.getPrecio());
            stmProd.setBoolean(5, p.isManejaStock());
            stmProd.setBoolean(6, p.isMicaBase());
            stmProd.setString(7, p.getTipo_producto());
            stmProd.setBoolean(8, p.isActivo());
            
            stmProd.executeUpdate();
            
            ResultSet rs = stmProd.getGeneratedKeys();
            
            if(rs.next()){
                
                int idProducto= rs.getInt(1);
                
                //Insert Inventario
                PreparedStatement stmInv = conn.prepareStatement(sqlInventario);
                stmInv.setInt(1, idSucursal);
                stmInv.setInt(2, idProducto);
                stmInv.setInt(3, stockInicial);
                
                stmInv.executeUpdate();                
            }
            
            conn.commit();
            return true;
            
        }catch(SQLException e){
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
    public Product getById(int idProducto, int idSucursal){
        
        String sql = """
        SELECT p.*, 
               IFNULL(i.stock, 0) AS stock
        FROM products p
        LEFT JOIN inventario_sucursal i 
            ON p.id_product = i.id_product 
            AND i.id_sucursal = ?
        WHERE p.id_product = ?
    """;
        
        Product product = null;
        
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)){
            
            stm.setInt(1, idSucursal);
            stm.setInt(2, idProducto);
            
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
                    product.setStock(rs.getInt("stock"));
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
    
    public List<Product> buscarPorNombre(String texto, int idSucursal){
        
        List<Product> lista = new ArrayList<>();
        String sql = """
            SELECT p.*, i.stock
            FROM products p
            JOIN inventario_sucursal i 
                ON p.id_product = i.id_product
            WHERE i.id_sucursal = ?
            AND (p.modelo LIKE ? OR p.marca LIKE ?)
            AND p.activo = true
            """;
        
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)){
            
            stm.setInt(1, idSucursal);
            stm.setString(2, "%" + texto + "%");
            stm.setString(3, "%" + texto + "%");
            
            ResultSet rs = stm.executeQuery();
            
            while(rs.next()){
                Product p = new Product();
                p.setId_product(rs.getInt("id_product"));
                p.setModelo(rs.getString("modelo"));
                p.setMarca(rs.getString("marca"));
                p.setCategoria(rs.getString("categoria"));
                p.setPrecio(rs.getDouble("precio"));
                p.setStock(rs.getInt("stock"));
                lista.add(p);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return lista;
    }
    
    public boolean actulizarStock(int idProducto, int idSucursal, int cantidad){
        
        String sql = "UPDATE inventario_sucursal SET stock = stock -? WHERE id_product = ? AND id_sucursal = ?";
        
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setInt(1, cantidad);
            stm.setInt(2, idProducto);
            stm.setInt(3, idSucursal);
            
            return stm.executeUpdate()>0;
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Product> getBySucursal(int idSucursal){
        
        List<Product> lista = new ArrayList<>();
        
        String sql = " SELECT p.*, i.stock FROM products p "
                + "JOIN inventario_sucursal i ON p.id_product = i.id_product "
                + "WHERE i.id_sucursal =?";
        
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)){
            
            stm.setInt(1, idSucursal);
            
            ResultSet rs = stm.executeQuery();
            
            while (rs.next()){
                Product p = new Product();
                
                p.setId_product(rs.getInt("id_product"));
                p.setModelo(rs.getString("modelo"));
                p.setMarca(rs.getString("marca"));
                p.setCategoria(rs.getString("categoria"));
                p.setPrecio(rs.getDouble("precio"));
                p.setStock(rs.getInt("stock"));
                p.setActivo(rs.getBoolean("activo"));
                lista.add(p);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return lista;
    }
}
