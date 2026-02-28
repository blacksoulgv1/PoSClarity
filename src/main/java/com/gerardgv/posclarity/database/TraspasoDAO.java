package com.gerardgv.posclarity.database;

import com.gerardgv.posclarity.models.Traspaso;
import com.gerardgv.posclarity.models.TraspasoDetalle;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TraspasoDAO {
    
    private InventarioSucursalDAO inventarioDAO = new InventarioSucursalDAO();

    public boolean realizarTraspaso(Traspaso t){

        String sqlTraspaso = """
            INSERT INTO traspaso
            (id_sucursal_origen, id_sucursal_destino, fecha, estado)
            VALUES (?,?,NOW(),'completado')
        """;

        String sqlDetalle = """
            INSERT INTO traspaso_detalle
            (id_traspaso, id_product, cantidad)
            VALUES (?,?,?)
        """;

        try(Connection conn = DBConnection.getConnection()){

            conn.setAutoCommit(false);

            // 1️⃣ Insertar traspaso
            PreparedStatement psTraspaso = conn.prepareStatement(sqlTraspaso, Statement.RETURN_GENERATED_KEYS);
            psTraspaso.setInt(1, t.getIdSucursalOrigen());
            psTraspaso.setInt(2, t.getIdSucursalDestino());
            psTraspaso.executeUpdate();

            ResultSet rs = psTraspaso.getGeneratedKeys();
            rs.next();
            int idTraspaso = rs.getInt(1);

            // 2️⃣ Insertar detalle + mover stock
            for(TraspasoDetalle d : t.getDetalles()){

                // validar stock disponible
                int stockActual = inventarioDAO.getStock(
                        t.getIdSucursalOrigen(),
                        d.getIdProducto()
                );

                if(stockActual < d.getCantidad()){
                    throw new SQLException("Stock insuficiente para el producto ID: " + d.getIdProducto());
                }

                // insertar detalle
                PreparedStatement psDetalle = conn.prepareStatement(sqlDetalle);
                psDetalle.setInt(1, idTraspaso);
                psDetalle.setInt(2, d.getIdProducto());
                psDetalle.setInt(3, d.getCantidad());
                psDetalle.executeUpdate();

                // descontar origen
                inventarioDAO.descontarStock(
                        conn,
                        t.getIdSucursalOrigen(),
                        d.getIdProducto(),
                        d.getCantidad()
                );

                // sumar destino
                inventarioDAO.sumarStock(
                        conn,
                        t.getIdSucursalDestino(),
                        d.getIdProducto(),
                        d.getCantidad()
                );
            }

            conn.commit(); // ✅ TODO OK

            return true;

        }catch(Exception e){

            e.printStackTrace();
            return false;
        }
    }
}
