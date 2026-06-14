package com.gerardgv.posclarity.database;

import com.gerardgv.posclarity.models.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InventarioSucursalDAO {
    
    public List<Product> getProductosBySucursal(int idSucursal){
     List<Product> lista = new ArrayList<>();

        String sql = """
            SELECT p.*, i.stock
            FROM inventario_sucursal i
            JOIN products p ON p.id_product = i.id_product
            WHERE i.id_sucursal = ? AND p.activo = true
        """;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setInt(1, idSucursal);

            try(ResultSet rs = ps.executeQuery()){

                while(rs.next()){
                    Product p = new Product();

                    p.setId_product(rs.getInt("id_product"));
                    p.setModelo(rs.getString("modelo"));
                    p.setMarca(rs.getString("marca"));
                    p.setCategoria(rs.getString("categoria"));
                    p.setPrecio(rs.getDouble("precio"));
                    p.setStock(rs.getInt("stock")); // 👈 importante

                    lista.add(p);
                }
            }

        }catch(SQLException e){
            e.printStackTrace();
        }

        return lista;   
    }
    
     public int getStock(int idSucursal, int idProducto){

        String sql = """
            SELECT stock FROM inventario_sucursal
            WHERE id_sucursal = ? AND id_product = ?
        """;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setInt(1, idSucursal);
            ps.setInt(2, idProducto);

            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                return rs.getInt("stock");
            }

        }catch(SQLException e){
            e.printStackTrace();
        }

        return 0;
    }
     
     public boolean descontarStock(int idSucursal, int idProducto, int cantidad){

        String sql = """
            UPDATE inventario_sucursal
            SET stock = stock - ?
            WHERE id_sucursal = ? AND id_product = ?
        """;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setInt(1, cantidad);
            ps.setInt(2, idSucursal);
            ps.setInt(3, idProducto);

            return ps.executeUpdate() > 0;

        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }
     
     public boolean sumarStock(int idSucursal, int idProducto, int cantidad){

        String verificar = """
            SELECT id_inventario FROM inventario_sucursal
            WHERE id_sucursal = ? AND id_product = ?
        """;

        String insertar = """
            INSERT INTO inventario_sucursal (id_sucursal, id_product, stock)
            VALUES (?, ?, ?)
        """;

        String actualizar = """
            UPDATE inventario_sucursal
            SET stock = stock + ?
            WHERE id_sucursal = ? AND id_product = ?
        """;

        try(Connection conn = DBConnection.getConnection()){

            // 🔍 verificar si existe
            PreparedStatement psVerificar = conn.prepareStatement(verificar);
            psVerificar.setInt(1, idSucursal);
            psVerificar.setInt(2, idProducto);

            ResultSet rs = psVerificar.executeQuery();

            if(rs.next()){
                PreparedStatement psUpdate = conn.prepareStatement(actualizar);
                psUpdate.setInt(1, cantidad);
                psUpdate.setInt(2, idSucursal);
                psUpdate.setInt(3, idProducto);
                

                return psUpdate.executeUpdate() > 0;

            }else{
                PreparedStatement psInsert = conn.prepareStatement(insertar);
                psInsert.setInt(1, idSucursal);
                psInsert.setInt(2, idProducto);
                psInsert.setInt(3, cantidad);

                return psInsert.executeUpdate() > 0;
            }

        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean descontarStock(Connection conn, int idSucursal, int idProducto, int cantidad) throws SQLException {
        String sql = """
        UPDATE inventario_sucursal
        SET stock = stock - ?
        WHERE id_sucursal = ? AND id_product = ?
    """;

    PreparedStatement ps = conn.prepareStatement(sql);
    ps.setInt(1, cantidad);
    ps.setInt(2, idSucursal);
    ps.setInt(3, idProducto);

    return ps.executeUpdate() > 0;
    }
     
    public boolean sumarStock(Connection conn, int idSucursal, int idProducto, int cantidad) throws SQLException {

    String verificar = """
        SELECT id_inventario FROM inventario_sucursal
        WHERE id_sucursal = ? AND id_product = ?
    """;

    PreparedStatement psVerificar = conn.prepareStatement(verificar);
    psVerificar.setInt(1, idSucursal);
    psVerificar.setInt(2, idProducto);

    ResultSet rs = psVerificar.executeQuery();

    if(rs.next()){

        String actualizar = """
            UPDATE inventario_sucursal
            SET stock = stock + ?
            WHERE id_sucursal = ? AND id_product = ?
        """;

        PreparedStatement psUpdate = conn.prepareStatement(actualizar);
        psUpdate.setInt(1, cantidad);
        psUpdate.setInt(2, idSucursal);
        psUpdate.setInt(3, idProducto);

        return psUpdate.executeUpdate() > 0;

    }else{

        String insertar = """
            INSERT INTO inventario_sucursal (id_sucursal, id_product, stock)
            VALUES (?, ?, ?)
        """;

        PreparedStatement psInsert = conn.prepareStatement(insertar);
        psInsert.setInt(1, idSucursal);
        psInsert.setInt(2, idProducto);
        psInsert.setInt(3, cantidad);

        return psInsert.executeUpdate() > 0;
    }
}
    
    public boolean actualizarStock(int idSucursal, int idProducto, int stockNuevo){
        
        String sqlStock = """
                          UPDATE inventario_sucursal
                          SET stock = ?
                          WHERE id_sucursal = ?
                          AND id_product = ?
                          """;
        
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sqlStock)){
            
            ps.setInt(1, stockNuevo);
            ps.setInt(2, idSucursal);
            ps.setInt(3, idProducto);
            
            return ps.executeUpdate()> 0;
            
        } catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }
    
}
