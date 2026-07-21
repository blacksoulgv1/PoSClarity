package com.gerardgv.posclarity.database;

import com.gerardgv.posclarity.models.*;
import com.gerardgv.posclarity.utils.Session;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GarantiasDAO {
    
    public boolean crearGarantia(
        Garantia garantia,
        List<GarantiaDetalle> detalles,
        GarantiaGraduacion original,
        GarantiaGraduacion nueva) {

        if(garantia == null){
            return false;
        }

        if(detalles == null || detalles.isEmpty()){
            return false;
        }

        Connection conn = null;

        try{

            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String folio = generarFolio(conn);
            garantia.setFolio(folio);

        int idGarantia = insertGarantia(conn, garantia);

            if(idGarantia <= 0){
                throw new SQLException(
                    "No se pudo obtener el ID de la garantía."
            );
        }

        for(GarantiaDetalle detalle : detalles){

            if(detalle == null){
                continue;
            }

            detalle.setIdGarantias(idGarantia);
            insertDetalle(conn, detalle);
        }

        if(original != null){

            original.setIdGarantias(idGarantia);
            original.setTipo("ORIGINAL");

            insertGraduacion(conn, original);
        }

        if(nueva != null){

            nueva.setIdGarantias(idGarantia);
            nueva.setTipo("NUEVA");

            insertGraduacion(conn, nueva);
        }

        conn.commit();

        garantia.setIdgarantias(idGarantia);

        return true;

    }catch(SQLException e){

        e.printStackTrace();

        if(conn != null){

            try{
                conn.rollback();
            }catch(SQLException rollbackError){
                rollbackError.printStackTrace();
            }
        }

        return false;

    }finally{

        if(conn != null){

            try{
                conn.setAutoCommit(true);
            }catch(SQLException e){
                e.printStackTrace();
            }

            try{
                conn.close();
            }catch(SQLException e){
                e.printStackTrace();
            }
        }
    }
}
    
    private int insertGarantia(
        Connection conn,
        Garantia garantia
) throws SQLException {

    String sql = """
        INSERT INTO garantias(
            folio,
            id_ventas,
            id_cliente,
            id_sucursal,
            motivo,
            estado,
            accion,
            observaciones,
            usuario_creo
        )
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

    try(PreparedStatement stm = conn.prepareStatement(
            sql,
            java.sql.Statement.RETURN_GENERATED_KEYS
    )){

        stm.setString(1, garantia.getFolio());
        stm.setInt(2, garantia.getIdventas());
        stm.setInt(3, garantia.getIdcliente());
        stm.setInt(4, garantia.getIdsucursal());
        stm.setString(5, garantia.getMotivo());
        stm.setString(6, garantia.getEstado());
        stm.setString(7, garantia.getAccion());
        stm.setString(8, garantia.getObservaciones());
        stm.setInt(9, garantia.getUsuario());

        int filasInsertadas = stm.executeUpdate();

        if(filasInsertadas != 1){
            throw new SQLException(
                    "No se pudo insertar el encabezado de la garantía."
            );
        }

        try(ResultSet rs = stm.getGeneratedKeys()){

            if(rs.next()){
                return rs.getInt(1);
            }
        }
    }

    throw new SQLException(
            "La garantía fue insertada, pero no se obtuvo el ID generado."
    );
}
    
    private void insertDetalle(
        Connection conn,
        GarantiaDetalle detalle
) throws SQLException {

    String sql = """
        INSERT INTO garantia_detalle(
            id_garantias,
            id_detalle,
            cantidad
        )
        VALUES (?, ?, ?)
        """;

    try(PreparedStatement stm = conn.prepareStatement(sql)){

        stm.setInt(1, detalle.getIdGarantias());
        stm.setInt(2, detalle.getIdDetalle());
        stm.setInt(3, detalle.getCantidad());

        int filasInsertadas = stm.executeUpdate();

        if(filasInsertadas != 1){
            throw new SQLException(
                    "No se pudo insertar un producto en la garantía."
            );
        }
    }
}   
        
    private void insertGraduacion( Connection conn, GarantiaGraduacion graduacion ) throws SQLException {

        String sql = """
            INSERT INTO garantia_graduacion(
                id_garantias,
                tipo,
                od_esfera,
                od_cilindro,
                od_eje,
                od_add,
                oi_esfera,
                oi_cilindro,
                oi_eje,
                oi_add)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try(PreparedStatement stm = conn.prepareStatement(sql)){

            stm.setInt(1, graduacion.getIdGarantias());
            stm.setString(2, graduacion.getTipo());

            stm.setString(3, valorSeguro(graduacion.getOdEsfera()));
            stm.setString(4, valorSeguro(graduacion.getOdCilindro()));
            stm.setString(5, valorSeguro(graduacion.getOdEje()));
            stm.setString(6, valorSeguro(graduacion.getOdAdd()));

            stm.setString(7, valorSeguro(graduacion.getOiEsfera()));
            stm.setString(8, valorSeguro(graduacion.getOiCilindro()));
            stm.setString(9, valorSeguro(graduacion.getOiEje()));
            stm.setString(10, valorSeguro(graduacion.getOiAdd()));

            int filasInsertadas = stm.executeUpdate();

            if(filasInsertadas != 1){
                throw new SQLException( "No se pudo insertar la graduación de la garantía.");
        }
    }
}
    
    public String generarFolio(){
                
        String sql = "SELECT IFNULL(MAX(id_garantias),0)+1 AS siguiente FROM garantias";
        
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){
            
            if(rs.next()){                
                return String.format( "GAR-%06d", rs.getInt("siguiente"));
            }                        
        }catch(Exception e){
            e.printStackTrace();
        }
        return "GAR-000001";
    }
    
    public List<Garantia> obtenerGarantiasActivas(){
        
        List<Garantia> lista = new ArrayList<>();

    if(Session.getSucursal() == null){
        return lista;
    }

    String sql = """
        SELECT
            g.id_garantias,
            g.id_ventas,
            g.folio,
            g.motivo,
            g.estado,
            g.accion,
            g.fecha_solicitud,
            c.nombre AS cliente
        FROM garantias g
        INNER JOIN cliente c
            ON c.id_cliente = g.id_cliente
        WHERE g.estado <> 'ENTREGADO'
          AND g.id_sucursal = ?
        ORDER BY
            g.fecha_solicitud DESC,
            g.id_garantias DESC
        """;

    try(Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)){

        ps.setInt(
                1,
                Session.getSucursal().getId()
        );

        try(ResultSet rs = ps.executeQuery()){

            while(rs.next()){

                Garantia garantia = new Garantia();

                garantia.setIdgarantias(
                        rs.getInt("id_garantias")
                );

                garantia.setIdventas(
                        rs.getInt("id_ventas")
                );

                garantia.setFolio(
                        rs.getString("folio")
                );

                garantia.setMotivo(
                        rs.getString("motivo")
                );

                garantia.setEstado(
                        rs.getString("estado")
                );

                garantia.setAccion(
                        rs.getString("accion")
                );

                garantia.setCliente(
                        rs.getString("cliente")
                );

                if(rs.getTimestamp("fecha_solicitud") != null){

                    garantia.setFechaSolicitud(
                            rs.getTimestamp("fecha_solicitud")
                              .toLocalDateTime()
                    );
                }

                lista.add(garantia);
            }
        }

    }catch(SQLException e){
        e.printStackTrace();
    }

    return lista;
    }
    
    private boolean cambiarEstadoGarantia(
        int idGarantia,
        String estadoActual,
        String nuevoEstado
) {

    if(Session.getSucursal() == null){
        return false;
    }

    String sql = """
        UPDATE garantias
        SET estado = ?
        WHERE id_garantias = ?
          AND id_sucursal = ?
          AND estado = ?
        """;

    try(Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)){

        ps.setString(1, nuevoEstado);
        ps.setInt(2, idGarantia);
        ps.setInt(
                3,
                Session.getSucursal().getId()
        );
        ps.setString(4, estadoActual);

        return ps.executeUpdate() == 1;

    }catch(SQLException e){
        e.printStackTrace();
        return false;
    }
}
    
    public boolean recepcionGarantia(int idGarantia){
        return cambiarEstadoGarantia(idGarantia, "PROCESO", "RECIBIDO");
    }
    
    public boolean entregarGarantia(int idGarantia){
        return cambiarEstadoGarantia(idGarantia,"RECIBIDO","ENTREGADO");
    }
    
    private String generarFolio(Connection conn) throws SQLException{
        
        String sql = """
        SELECT IFNULL(MAX(id_garantias), 0) + 1 AS siguiente
        FROM garantias
        FOR UPDATE
        """;

        try(PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){

            if(rs.next()){
                return String.format(
                    "GAR-%06d",
                    rs.getInt("siguiente")
                );
            }
        }

        throw new SQLException(
            "No se pudo generar el folio de garantía."
        );        
    }
    
    private String valorSeguro(String valor){

    if(valor == null){
        return "";
    }

    return valor.trim();
}
    
}
