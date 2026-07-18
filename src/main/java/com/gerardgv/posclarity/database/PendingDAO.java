package com.gerardgv.posclarity.database;

import com.gerardgv.posclarity.models.*;
import com.gerardgv.posclarity.utils.Session;
import java.sql.*;
import java.util.*;

public class PendingDAO {

    private Venta mapVenta(ResultSet rs) throws SQLException {
        
        Venta v = new Venta();
        Clients c = new Clients();

        v.setId(rs.getInt("id_ventas"));
        v.setFolio(rs.getString("folio"));
        Timestamp fecha = rs.getTimestamp("fecha_venta");
        if (fecha != null) {
            v.setFecha(fecha.toLocalDateTime());
        }
        v.setTotal(rs.getDouble("total_final"));

        double pagado = rs.getDouble("pagado");
        v.setPagado(pagado);
        v.setRestante(Math.max(0, v.getTotal() - pagado));

        v.setEstadoPago(rs.getString("estado_pago"));
        v.setEstadoTrabajo(rs.getString("estado_trabajo"));

        c.setNombre(rs.getString("cliente_nombre"));
        v.setCliente(c);

        return v;
    }

    private List<Venta> obtenerVentasPorCondicion(String condicion) {

    List<Venta> lista = new ArrayList<>();

    String sql = """
        SELECT
            v.id_ventas,
            v.folio,
            v.fecha_venta,
            v.total_final,
            v.estado_pago,
            v.estado_trabajo,
            COALESCE(c.nombre, 'Cliente no disponible') AS cliente_nombre,
            COALESCE(pg.pagado, 0) AS pagado
        FROM ventas v

        LEFT JOIN cliente c
            ON c.id_cliente = v.id_cliente

        LEFT JOIN (
            SELECT
                id_venta,
                SUM(monto) AS pagado
            FROM pagos
            GROUP BY id_venta
        ) pg
            ON pg.id_venta = v.id_ventas

        WHERE v.id_sucursal = ?
          AND (
        """ + condicion + """
          )

        ORDER BY v.fecha_venta ASC
        """;

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        int idSucursal = Session.getSucursal().getId();

        ps.setInt(1, idSucursal);

        try (ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapVenta(rs));
            }
        }

    } catch (SQLException e) {
        System.err.println(
                "Error al obtener trabajos activos: "
                + e.getMessage()
        );

        e.printStackTrace();
    }

    return lista;
}

    public List<Venta> obtenerTrabajosActivos() {
        return obtenerVentasPorCondicion("""
        v.estado_trabajo NOT IN ('ENTREGADO', 'CANCELADO')
        AND (
            v.estado_pago = 'PENDIENTE'
            OR v.estado_trabajo IN ('PROCESO', 'RECIBIDO', 'LISTO')
            )
        """);
    }

    public List<Venta> obtenerTrabajosProceso() {
        return obtenerVentasPorCondicion("""
            v.estado_trabajo = 'PROCESO'
        """);
    }

    public List<Venta> obtenerTrabajosListos() {
        return obtenerVentasPorCondicion("""
        v.estado_trabajo = 'LISTO'
        """);
    }

    public List<Venta> obtenerSaldosPendientes() {
        return obtenerVentasPorCondicion("""
            v.estado_pago = 'PENDIENTE'
            AND v.estado_trabajo NOT IN ('ENTREGADO','CANCELADO')
        """);
    }

    public List<Venta> obtenerTrabajosAtrasados() {
        return obtenerVentasPorCondicion("""
        v.estado_trabajo = 'PROCESO'
        AND v.fecha_venta < NOW() - INTERVAL 5 DAY
        """);
    }

    public List<Venta> obtenerTrabajosPorRealizar() {
        return obtenerVentasPorCondicion("""
            v.estado_trabajo = 'PROCESO'
        """);
    }

    public List<Venta> obtenerTrabajosPorEntregar() {
        return obtenerVentasPorCondicion("""
            v.estado_trabajo IN ('RECIBIDO','LISTO')
        """);
    }

    public List<Venta> buscarPendientes(String filtro) {
        List<Venta> lista = new ArrayList<>();

        String sql = """
            SELECT
                v.id_ventas,
                v.folio,
                v.fecha_venta,
                v.total_final,
                v.estado_pago,
                v.estado_trabajo,
                c.nombre AS cliente_nombre,
                IFNULL(SUM(p.monto), 0) AS pagado
            FROM ventas v
            JOIN cliente c ON v.id_cliente = c.id_cliente
            LEFT JOIN pagos p ON v.id_ventas = p.id_venta
            WHERE v.id_sucursal = ?
              AND (
                    v.estado_pago = 'PENDIENTE'
                    OR v.estado_trabajo IN ('PROCESO','RECIBIDO','LISTO')
                  )
              AND v.estado_trabajo NOT IN ('ENTREGADO','CANCELADO')
              AND (
                    v.folio LIKE ?
                    OR CAST(v.id_ventas AS CHAR) LIKE ?
                    OR c.nombre LIKE ?
                    OR c.telefono LIKE ?
                  )
            GROUP BY v.id_ventas
            ORDER BY v.fecha_venta ASC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String like = "%" + filtro + "%";

            ps.setInt(1, Session.getSucursal().getId());
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);
            ps.setString(5, like);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapVenta(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public ReportPendientes obtenerResumenPendientes() {
        ReportPendientes resumen = new ReportPendientes();

        String sql = """
            SELECT
                SUM(
                    CASE
                        WHEN v.estado_trabajo = 'PROCESO'
                        THEN 1 ELSE 0
                    END
                ) AS trabajos_realizar,

                SUM(
                    CASE
                        WHEN v.estado_trabajo IN ('RECIBIDO', 'LISTO')
                        THEN 1 ELSE 0
                    END
                ) AS trabajos_entregar,

                SUM(
                    CASE
                        WHEN v.estado_trabajo = 'PROCESO'
                        THEN GREATEST(
                            v.total_final - IFNULL(p.pagado, 0),
                            0
                        )
                        ELSE 0
                    END
                ) AS saldo_realizar,

                SUM(
                    CASE
                        WHEN v.estado_trabajo IN ('RECIBIDO', 'LISTO')
                        THEN GREATEST(
                            v.total_final - IFNULL(p.pagado, 0),
                            0
                        )
                        ELSE 0
                    END
                ) AS saldo_entregar

            FROM ventas v

            LEFT JOIN (
                SELECT
                    id_venta,
                    SUM(monto) AS pagado
                FROM pagos
                GROUP BY id_venta
            ) p ON p.id_venta = v.id_ventas

            WHERE v.id_sucursal = ?
            AND v.estado_trabajo NOT IN ('ENTREGADO', 'CANCELADO')
            """;

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, Session.getSucursal().getId());

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    resumen.setTrabajosRealizar(
                        rs.getInt("trabajos_realizar"));

                resumen.setTrabajosEntregar(
                        rs.getInt("trabajos_entregar"));

                resumen.setSaldoRealizar(
                        rs.getDouble("saldo_realizar"));

                resumen.setSaldoEntregar(
                        rs.getDouble("saldo_entregar"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resumen;
    }

    public boolean recepcionarVenta(int idVenta) {
         String sql = """
            UPDATE ventas
            SET estado_trabajo = 'RECIBIDO'
            WHERE id_ventas = ?
                AND id_sucursal = ?
                AND estado_trabajo = 'PROCESO'
            """;

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idVenta);
            ps.setInt(2, Session.getSucursal().getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean marcarLista(int idVenta) {
        String sql = """
            UPDATE ventas
            SET estado_trabajo = 'LISTO'
            WHERE id_ventas = ?
                AND id_sucursal = ?
                AND estado_trabajo = 'RECIBIDO'
            """;

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idVenta);
            ps.setInt(2, Session.getSucursal().getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean entregarVenta(int idVenta) {
        String sql = """
            UPDATE ventas
            SET estado_trabajo = 'ENTREGADO'
            WHERE id_ventas = ?
                AND id_sucursal = ?
                AND estado_pago = 'COMPLETA'
                AND estado_trabajo IN ('RECIBIDO', 'LISTO')
            """;

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idVenta);
            ps.setInt(2, Session.getSucursal().getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
}