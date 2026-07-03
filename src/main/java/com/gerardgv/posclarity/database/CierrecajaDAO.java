package com.gerardgv.posclarity.database;

import com.gerardgv.posclarity.models.Branch;
import com.gerardgv.posclarity.models.CierreCaja;
import com.gerardgv.posclarity.utils.Session;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CierrecajaDAO {
    
    public double obtenerCajaInicial(){
        String sql = """
        SELECT caja_final
        FROM cierre_caja
        WHERE id_sucursal = ?
        ORDER BY fecha DESC, hora_cierre DESC
        LIMIT 1
    """;

    try(Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)){

        ps.setInt(1, Session.getSucursal().getId());

        ResultSet rs = ps.executeQuery();

        if(rs.next()){
            return rs.getDouble("caja_final");
        }

    }catch(Exception e){
        e.printStackTrace();
    }

    return 0;
    }
    
    public CierreCaja obtenerCierreDelDia(){
        
        CierreCaja cierre = new CierreCaja();

    int idSucursal = Session.getSucursal().getId();

    String sqlVentas = """
        SELECT
                COUNT(*) AS total_notas,
                IFNULL(SUM(total_final),0) AS venta_final
            FROM ventas
            WHERE DATE(fecha_venta) = CURDATE()
            AND estado_pago != 'CANCELADA'
            AND id_sucursal = ?
    """;

    String sqlAbonos = """
        SELECT IFNULL(SUM(p.monto),0) AS total_abonos
        FROM pagos p
        JOIN ventas v
            ON p.id_venta = v.id_ventas
        WHERE DATE(p.fecha) = CURDATE()
        AND v.id_sucursal = ?
        AND p.tipo_pago = 'ABONO'
    """;
    //se agrego folio
    String sqlDetalleNuevos = """
        SELECT
            v.id_ventas,
            v.folio,
            p.monto
        FROM pagos p
          JOIN ventas v
            ON p.id_venta = v.id_ventas
        WHERE DATE(fecha_venta) = CURDATE()
            AND v.id_sucursal = ?
            AND p.tipo_pago = 'VENTA'
        ORDER BY v.id_ventas                      
    """;
     //se agrego folio
    String sqlDetalleRecogidos = """
    SELECT
        id_ventas,
        folio,
        total_final
    FROM ventas
    WHERE DATE(fecha_entrega) = CURDATE()
        AND estado_trabajo = 'ENTREGADO'
        AND id_sucursal = ?
    ORDER BY id_ventas
""";
     //se agrego folio
    String sqlDetalleAbonos = """
    SELECT
        v.id_ventas,
        v.folio,
        p.monto
    FROM pagos p
    JOIN ventas v
        ON p.id_venta = v.id_ventas
    WHERE DATE(p.fecha) = CURDATE()
        AND v.id_sucursal = ?
        AND p.tipo_pago = 'ABONO'
    ORDER BY v.id_ventas
""";

    try(Connection conn = DBConnection.getConnection()){

        double ventaFinal = 0;
        int totalNotasNuevas = 0;
        double totalAbonos = 0;
        double totalRecogidos = 0;
        double totalAbonosDetalle = 0;
        
        StringBuilder detalleNuevos = new StringBuilder();
        StringBuilder detalleAbonos = new StringBuilder();
        StringBuilder detalleRecogidos = new StringBuilder();
        

        // Ventas del día
        try(PreparedStatement ps = conn.prepareStatement(sqlVentas)){

            ps.setInt(1, idSucursal);

            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                ventaFinal = rs.getDouble("venta_final");
                totalNotasNuevas = rs.getInt("total_notas");
            }
        }
        
        //Detalle de Notas Nuevas        
        try( PreparedStatement ps = conn.prepareStatement(sqlDetalleNuevos)){
            
            ps.setInt(1, idSucursal);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                detalleNuevos
            .append(rs.getString("folio"))
            .append(" $")
            .append(String.format("%.2f",
                    rs.getDouble("monto")))
            .append("\n");
            }
            detalleNuevos.append("\n");
            detalleNuevos.append("Total Nuevos: $")
        .append(String.format("%.2f", ventaFinal));
        }
        
        //Detalle de Notas Recogidas  
        try(PreparedStatement ps = conn.prepareStatement(sqlDetalleRecogidos)){
            ps.setInt(1, idSucursal);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                
                double monto = rs.getDouble("total_final");
                totalRecogidos += monto;
                
                detalleRecogidos
                    .append(rs.getString("folio"))
                    .append(" $")
                    .append(String.format("%.2f", monto))
                    .append("\n");
            }
        }

            detalleRecogidos.append("\n");
            detalleRecogidos.append("Total Recogidos: $")
                 .append(String.format("%.2f",totalRecogidos));
            
        // Detalle de Notas Recogidas
        try(PreparedStatement ps = conn.prepareStatement(sqlDetalleAbonos)){
            ps.setInt(1, idSucursal);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                
                double monto = rs.getDouble("monto");
                totalAbonosDetalle += monto;
                detalleAbonos
                .append(rs.getString("folio"))
                .append(" $")
                .append(String.format("%.2f", monto))
                .append("\n");
            }
        }

            detalleAbonos.append("\n");
            detalleAbonos.append("Total Abonos: $")
             .append(String.format("%.2f",totalAbonosDetalle));
        
            
        // Abonos del día
        try(PreparedStatement ps = conn.prepareStatement(sqlAbonos)){

            ps.setInt(1, idSucursal);

            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                totalAbonos = rs.getDouble("total_abonos");
            }
        }

        cierre.setSucursal(Session.getSucursal());

        cierre.setFecha(LocalDate.now());
        cierre.setHoraCierre(LocalDateTime.now());

        cierre.setCajaInicial(
                obtenerCajaInicial()
        );

        cierre.setVentaFinal(ventaFinal);
        cierre.setTotalNotasNuevas(totalNotasNuevas);
        cierre.setDetalleNuevo(detalleNuevos.toString());
        cierre.setDetalleRecogido(detalleRecogidos.toString());
        cierre.setDetalleAbonos(detalleAbonos.toString());
        cierre.setTotalAbonos(totalAbonos);
        cargarTotalesFormaPago(cierre);

        cierre.setTotalIngresos(
                cierre.getTotalEfectivo()
                        +cierre.getTotalTransferencia()
                        +cierre.getTotalTarjeta()
        );

    }catch(Exception e){
        e.printStackTrace();
    }
        cargarTotalesFormaPago(cierre);
    return cierre;
    }
    
    public boolean guardarCierre(CierreCaja cierre){
        String sql = """
        INSERT INTO cierre_caja(
            id_sucursal,
            id_usuario,
            fecha,
            hora_cierre,
            caja_inicial,
            caja_final,
            total_ventas,
            total_abonos,
            total_efectivo,
            total_transferencia,
            total_tarjeta,
            deposito,
            diferencia,
            observaciones
        )
        VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)
    """;
    
    try(Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)){
        
        ps.setInt(1, cierre.getSucursal().getId());
        
        if(cierre.getUsuario() != null){
            ps.setInt(2, cierre.getUsuario().getId_vendedor());
        }else{
            ps.setNull(2, java.sql.Types.INTEGER);
        }
        
        ps.setDate(3, Date.valueOf(cierre.getFecha()));
        ps.setTimestamp(4, Timestamp.valueOf(cierre.getHoraCierre()));
        ps.setDouble(5, cierre.getCajaInicial());
        ps.setDouble(6, cierre.getCajaFinal());
        ps.setDouble(7, cierre.getTotalVentas());
        ps.setDouble(8, cierre.getTotalAbonos());
        ps.setDouble(9, cierre.getTotalEfectivo());
        ps.setDouble(10, cierre.getTotalTransferencia());
        ps.setDouble(11, cierre.getTotalTarjeta());
        ps.setDouble(12, cierre.getDeposito());
        ps.setDouble(13, cierre.getDiferencia());
        ps.setString(14, cierre.getObservaciones());

        return ps.executeUpdate() > 0;

    }catch(Exception e){
        e.printStackTrace();
        return false;
        }
    }
    
    public boolean existeCierreHoy(int idSucursal){
         String sql = """
        SELECT COUNT(*)
        FROM cierre_caja
        WHERE fecha = CURDATE()
        AND id_sucursal = ?
    """;

    try(Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)){

        ps.setInt(1, idSucursal);

        ResultSet rs = ps.executeQuery();

        if(rs.next()){
            return rs.getInt(1) > 0;
        }

    }catch(Exception e){
        e.printStackTrace();
    }

    return false;
    }
    
    public CierreCaja obtenerUltimoCierre(){
        
         String sql = """
        SELECT *
        FROM cierre_caja
        WHERE id_sucursal = ?
        ORDER BY fecha DESC, hora_cierre DESC
        LIMIT 1
    """;

    try(Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)){

        ps.setInt(1, Session.getSucursal().getId());

        ResultSet rs = ps.executeQuery();

        if(rs.next()){

            CierreCaja c = new CierreCaja();         
            c.setFecha(rs.getDate("fecha").toLocalDate());

            if(rs.getTimestamp("hora_cierre") != null){
                c.setHoraCierre(
                    rs.getTimestamp("hora_cierre")
                    .toLocalDateTime()
                );
            }
            c.setCajaInicial(rs.getDouble("caja_inicial"));
            c.setCajaFinal(rs.getDouble("caja_final"));
            c.setTotalVentas(rs.getDouble("total_ventas"));
            c.setTotalAbonos(rs.getDouble("total_abonos"));
            c.setTotalEfectivo(rs.getDouble("total_efectivo"));
            c.setTotalTransferencia(rs.getDouble("total_transferencia"));
            c.setTotalTarjeta(rs.getDouble("total_tarjeta"));
            c.setDeposito(rs.getDouble("deposito"));
            c.setDiferencia(rs.getDouble("diferencia"));
            c.setObservaciones(rs.getString("observaciones"));
            c.setTotalIngresos(c.getTotalEfectivo()
                +c.getTotalTransferencia()
                +c.getTotalTarjeta());
            
            Branch sucursal = new Branch();
            sucursal.setId(Session.getSucursal().getId());
            sucursal.setSucursal(Session.getSucursal().getSucursal());
            c.setSucursal(sucursal);
            
            return c;
        }
    }catch(Exception e){
        e.printStackTrace();
    }
    return null;        
    }
    
    public void cargarTotalesFormaPago(CierreCaja cierre){
        
        String sql = """
        SELECT
            metodo_pago,
            SUM(monto) total
        FROM pagos p
        JOIN ventas v
            ON p.id_venta = v.id_ventas
        WHERE DATE(p.fecha) = CURDATE()
        AND v.id_sucursal = ?
        GROUP BY metodo_pago
    """;
        
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

        ps.setInt(1, Session.getSucursal().getId());

        ResultSet rs = ps.executeQuery();

        while(rs.next()){

            String metodo =
                    rs.getString("metodo_pago");

            double total =
                    rs.getDouble("total");

            switch(metodo.toUpperCase()){

                case "EFECTIVO" ->
                    cierre.setTotalEfectivo(total);

                case "TRANSFERENCIA" ->
                    cierre.setTotalTransferencia(total);

                case "TARJETA" ->
                    cierre.setTotalTarjeta(total);
            }
        }
    }catch(Exception e){
        e.printStackTrace();
    }
   }
    
    public boolean eliminarCierreHoy(int idSucursal){
        
        String sql = """
                     DELETE FROM cierre_caja
                     WHERE fecha = CURDATE()
                     AND id_sucursal = ?
                     """;
        
         try(Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)){

        ps.setInt(1, idSucursal);

        return ps.executeUpdate() > 0;

    }catch(Exception e){
        e.printStackTrace();
    }

    return false;
    }
}
