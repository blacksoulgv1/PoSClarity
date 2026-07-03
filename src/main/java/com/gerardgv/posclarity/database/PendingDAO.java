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
        v.setFecha(rs.getTimestamp("fecha_venta").toLocalDateTime());
        v.setTotal(rs.getDouble("total_final"));

        double pagado = rs.getDouble("pagado");
        v.setPagado(pagado);
        v.setRestante(v.getTotal() - pagado);

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
                c.nombre AS cliente_nombre,
                IFNULL(SUM(p.monto), 0) AS pagado
            FROM ventas v
            JOIN cliente c ON v.id_cliente = c.id_cliente
            LEFT JOIN pagos p ON v.id_ventas = p.id_venta
            WHERE v.id_sucursal = ?
              AND """ + condicion + """
            GROUP BY v.id_ventas
            ORDER BY v.fecha_venta ASC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, Session.getSucursal().getId());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapVenta(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public List<Venta> obtenerTrabajosActivos() {
        return obtenerVentasPorCondicion("""
            (
                v.estado_pago = 'PENDIENTE'
                OR v.estado_trabajo IN ('PROCESO','RECIBIDO','LISTO')
            )
            AND v.estado_trabajo NOT IN ('ENTREGADO','CANCELADO')
        """);
    }

    public List<Venta> obtenerTrabajosProceso() {
        return obtenerVentasPorCondicion("""
            v.estado_trabajo = 'PROCESO'
            AND v.estado_pago = 'PENDIENTE'
        """);
    }

    public List<Venta> obtenerTrabajosListos() {
        return obtenerVentasPorCondicion("""
            v.estado_trabajo = 'LISTO'
            AND v.estado_pago = 'PENDIENTE'
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
            AND v.estado_pago = 'PENDIENTE'
            AND v.fecha_venta < NOW() - INTERVAL 5 DAY
        """);
    }

    public List<Venta> obtenerTrabajosPorRealizar() {
        return obtenerVentasPorCondicion("""
            v.estado_trabajo = 'PROCESO'
            AND v.estado_pago = 'PENDIENTE'
        """);
    }

    public List<Venta> obtenerTrabajosPorEntregar() {
        return obtenerVentasPorCondicion("""
            v.estado_trabajo IN ('RECIBIDO','LISTO')
            AND v.estado_pago = 'PENDIENTE'
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
        ReportPendientes r = new ReportPendientes();

        String sql = """
            SELECT
                SUM(
                    CASE
                        WHEN estado_trabajo = 'PROCESO'
                             AND estado_pago = 'PENDIENTE'
                        THEN 1 ELSE 0
                    END
                ) AS trabajos_realizar,

                SUM(
                    CASE
                        WHEN estado_trabajo IN ('RECIBIDO','LISTO')
                             AND estado_pago = 'PENDIENTE'
                        THEN 1 ELSE 0
                    END
                ) AS trabajos_entregar,

                SUM(
                    CASE
                        WHEN estado_trabajo = 'PROCESO'
                             AND estado_pago = 'PENDIENTE'
                        THEN total_final - IFNULL((
                            SELECT SUM(monto)
                            FROM pagos p
                            WHERE p.id_venta = v.id_ventas
                        ),0)
                        ELSE 0
                    END
                ) AS saldo_realizar,

                SUM(
                    CASE
                        WHEN estado_trabajo IN ('RECIBIDO','LISTO')
                             AND estado_pago = 'PENDIENTE'
                        THEN total_final - IFNULL((
                            SELECT SUM(monto)
                            FROM pagos p
                            WHERE p.id_venta = v.id_ventas
                        ),0)
                        ELSE 0
                    END
                ) AS saldo_entregar

            FROM ventas v
            WHERE v.estado_trabajo NOT IN ('ENTREGADO','CANCELADO')
              AND v.id_sucursal = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, Session.getSucursal().getId());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                r.setTrabajosRealizar(rs.getInt("trabajos_realizar"));
                r.setTrabajosEntregar(rs.getInt("trabajos_entregar"));
                r.setSaldoRealizar(rs.getDouble("saldo_realizar"));
                r.setSaldoEntregar(rs.getDouble("saldo_entregar"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return r;
    }

    public boolean recepcionarVenta(int idVenta) {
        return cambiarEstadoTrabajo(idVenta, "RECIBIDO");
    }

    public boolean marcarLista(int idVenta) {
        return cambiarEstadoTrabajo(idVenta, "LISTO");
    }

    public boolean entregarVenta(int idVenta) {
        return cambiarEstadoTrabajo(idVenta, "ENTREGADO");
    }

    private boolean cambiarEstadoTrabajo(int idVenta, String estado) {
        String sql = """
            UPDATE ventas
            SET estado_trabajo = ?
            WHERE id_ventas = ?
              AND id_sucursal = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, estado);
            ps.setInt(2, idVenta);
            ps.setInt(3, Session.getSucursal().getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}