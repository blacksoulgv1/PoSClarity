package com.gerardgv.posclarity.database;

import com.gerardgv.posclarity.models.*;
import com.gerardgv.posclarity.utils.Session;
import java.sql.*;
import java.util.*;


public class SaleDAO {
    
            //==============
            //REALIZAR VENTA
            //==============
    public VentaResultado guardarVenta(
            List<SaleItem> carrito,
            List<Pago> pagos,
            double totalBruto,
            double descuento,
            double totalFinal,
            String estadoPago,
            int idCliente,
            int idUsuario,
            String odEsf, String odCil, String odEje,
            String oiEsf, String oiCil, String oiEje,
            String add){
        
        Connection conn = null;
        
        try{
            
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            boolean esBajoPedido = contieneBajoPedido(carrito); 
            String estadoTrabajo = determinarEstadoTrabajo(carrito);
            
            FolioDAO folioDAO = new FolioDAO();
            String folioVenta = folioDAO.generarFolio(Session.getSucursal().getId(), "VENTA");
            
            //==============
            //INSERTAR VENTA
            //==============
            String sqlVenta = """
                            INSERT INTO VENTAS (
                                folio,id_sucursal,id_cliente,id_vendedor,nombre_promocion,total_bruto,
                                descuento_total,total_final,estado_pago,estado_trabajo,
                                od_esf,od_cil,od_eje, oi_esf,oi_cil,oi_eje,add_lente)
                            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
                              """;
            
            PreparedStatement psVenta = conn.prepareStatement(sqlVenta, PreparedStatement.RETURN_GENERATED_KEYS);
                                    
            psVenta.setString(1, folioVenta);
            psVenta.setInt(2, Session.getSucursal().getId());
            psVenta.setInt(3, idCliente);
            psVenta.setInt(4, idUsuario);
            psVenta.setString(5,ObtenerNombrePromo(carrito)); //MODIFICAR
            psVenta.setDouble(6, totalBruto);
            psVenta.setDouble(7, descuento);
            psVenta.setDouble(8, totalFinal);
            psVenta.setString(9, estadoPago);
            psVenta.setString(10,estadoTrabajo);
            psVenta.setString(11, odEsf);
            psVenta.setString(12, odCil);
            psVenta.setString(13, odEje);
            psVenta.setString(14, oiEsf);
            psVenta.setString(15, oiCil);
            psVenta.setString(16, oiEje);
            psVenta.setString(17, add);
            psVenta.executeUpdate();
            
            ResultSet rs = psVenta.getGeneratedKeys();
                int idVenta = 0;            
            if(rs.next()){
                idVenta = rs.getInt(1);
            }
            
            //==============
            //DETALLE VENTA
            //==============            
            String sqlDetalle = "INSERT INTO detalle_venta(id_venta,id_product,cantidad,precio_unitario,descuento_aplicado,subtotal,nombre_descuento)"
                    + "VALUES (?,?,?,?,?,?,?)";            
            PreparedStatement psDetalle = conn.prepareStatement(sqlDetalle);
            
            String sqlStock = "UPDATE inventario_sucursal SET stock = stock -? WHERE id_sucursal=? AND id_product =?";
            PreparedStatement psStock = conn.prepareStatement(sqlStock);
            
            for(SaleItem item : carrito){
                psDetalle.setInt(1, idVenta);
                psDetalle.setInt(2, item.getProducto().getId_product());
                psDetalle.setInt(3, item.getCantidad());
                psDetalle.setDouble(4, item.getProducto().getPrecio());
                psDetalle.setDouble(5, item.getDescuento());
                psDetalle.setDouble(6, item.getSubtotal());
                psDetalle.setString(7, item.getNombreDescuento());
                psDetalle.addBatch();               
                
                //==============
                //Actualizar Inventario
                //=============              
                if(item.getProducto().isManejaStock()){
                    
                    psStock.setInt(1, item.getCantidad());
                    psStock.setInt(2, Session.getSucursal().getId());
                    psStock.setInt(3, item.getProducto().getId_product());
                    psStock.addBatch();
                }
            }            
            psDetalle.executeBatch();
            psStock.executeBatch();
            
            //==============
            //Forma de Pago
            //=============
            String sqlPago = """
                    INSERT INTO pagos 
                        (id_venta,metodo_pago,monto,referencia,tipo_pago)
                    VALUES (?,?,?,?,?)
                    """;
            PreparedStatement psPago = conn.prepareStatement(sqlPago);
            
            for(Pago p : pagos){
                psPago.setInt(1, idVenta);
                psPago.setString(2, p.getMetodo());
                psPago.setDouble(3,p.getMonto());
                psPago.setString(4, p.getReferencia());
                psPago.setString(5, p.getTipoPago());
                psPago.addBatch();
            }
            psPago.executeBatch();
            conn.commit();
            return new VentaResultado(idVenta,folioVenta);
            
        } catch(Exception e){
            try{
                if(conn != null) conn.rollback();
            }catch (Exception ex){
                ex.printStackTrace();
            }
            e.printStackTrace();
            return null;
        } finally{
            try{
                if(conn !=null){
                    conn.setAutoCommit(true);
                    conn.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
       
            
    private double calcularSubTotal(List<SaleItem> carrito){
        return carrito.stream().mapToDouble(i -> i.getProducto().getPrecio() * i.getCantidad()).sum();
    }
    
    private double calcularDescuentoTotal(List<SaleItem> carrito){
        return carrito.stream().mapToDouble(i -> i.getDescuento()*i.getCantidad()).sum();
    }
    
    private double totalPagado(List<Pago> pagos){
        return pagos.stream().mapToDouble(Pago::getMonto).sum();
    }
    
    private String ObtenerNombrePromo(List<SaleItem> carrito){
        for(SaleItem item : carrito){
            if(item.getDescuento()>0){
                return item.getNombreDescuento();
            }
        }
        return "Sin Promocion";
    }
    
    private boolean contieneBajoPedido (List<SaleItem> carrito){
        return carrito.stream()
                .anyMatch(i-> "bajo_pedido".equals(i.getProducto().getTipo_producto()));
    }
    
    private List<Venta> obtenerVentasPorCondicion(String condicion){
    
    List<Venta> lista = new ArrayList<>();

    String sql = """
        SELECT
            v.id_ventas,
            v.fecha_venta,
            v.total_final,
            v.estado_pago,
            v.estado_trabajo,
            c.nombre AS cliente_nombre,
            IFNULL(SUM(p.monto), 0) AS pagado
        FROM ventas v
        JOIN cliente c ON v.id_cliente = c.id_cliente
        LEFT JOIN pagos p ON v.id_ventas = p.id_venta
        WHERE """ + condicion + """
        GROUP BY v.id_ventas
    """;

    try(Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()){

        while(rs.next()){

            Venta v = new Venta();
            Clients c = new Clients();

            v.setId(rs.getInt("id_ventas"));
            v.setFecha(rs.getTimestamp("fecha_venta").toLocalDateTime());
            v.setTotal(rs.getDouble("total_final"));

            double pagado = rs.getDouble("pagado");

            v.setPagado(pagado);
            v.setRestante(v.getTotal() - pagado);

            v.setEstadoPago(rs.getString("estado_pago"));
            v.setEstadoTrabajo(rs.getString("estado_trabajo"));

            c.setNombre(rs.getString("cliente_nombre"));

            v.setCliente(c);

            lista.add(v);
        }

    }catch(Exception e){
        e.printStackTrace();
    }

    return lista;
}

  public List<Venta> obtenerTrabajosActivos(){
      
      return obtenerVentasPorCondicion("""
        v.estado_pago = 'PENDIENTE'
        OR v.estado_trabajo IN ('PROCESO','RECIBIDO')
    """);
  }
  
  public List<Venta> obtenerTrabajosProceso(){

    return obtenerVentasPorCondicion("""
        v.estado_trabajo = 'PROCESO'
    """);
}
  
  public List<Venta> obtenerTrabajosListos(){

    return obtenerVentasPorCondicion("""
        v.estado_trabajo = 'LISTO'
    """);
}
  
  public List<Venta> obtenerSaldosPendientes(){

    return obtenerVentasPorCondicion("""
        v.estado_pago = 'PENDIENTE'
    """);
}
  
  public List<Venta> obtenerTrabajosAtrasados(){

    return obtenerVentasPorCondicion("""
        v.estado_trabajo = 'PROCESO'
        AND v.fecha_venta < NOW() - INTERVAL 5 DAY
    """);
}
  
  private String determinarEstadoTrabajo(List<SaleItem> carrito){

    boolean requiereProceso = carrito.stream().anyMatch(item -> {

        Product p = item.getProducto();

        String categoria = p.getCategoria() != null
                ? p.getCategoria().toLowerCase()
                : "";

        String tipo = p.getTipo_producto() != null
                ? p.getTipo_producto().toLowerCase()
                : "";

        return categoria.contains("lente")
                || categoria.contains("mica")
                || categoria.contains("tratamiento")
                || tipo.equals("bajo_pedido");
    });

    return requiereProceso
            ? "PROCESO"
            : "ENTREGADO";
}
  
  public List<Venta> buscarVentasPendientesPorCliente(String filtro){
      
      List<Venta> lista = new ArrayList<>();
      
      String sql = """
        SELECT 
            v.id_ventas,
            v.fecha_venta,
            v.total_final,
            v.estado_pago,
            v.estado_trabajo,
            c.nombre AS cliente_nombre,
            IFNULL(SUM(p.monto), 0) AS pagado
        FROM ventas v
        JOIN cliente c ON v.id_cliente = c.id_cliente
        LEFT JOIN pagos p ON v.id_ventas = p.id_venta
        WHERE (v.estado_pago = 'PENDIENTE'
                   OR v.estado_trabajo = 'PROCESO')
        AND (c.nombre LIKE ? OR c.telefono LIKE ?)
        GROUP BY v.id_ventas
    """;
      
      try(Connection conn = DBConnection.getConnection();
              PreparedStatement ps = conn.prepareStatement(sql)){
          ps.setString(1, "%" + filtro + "%");
          ps.setString(2, "%" + filtro + "%");
          
          ResultSet rs = ps.executeQuery();
          
          while (rs.next()) {
              
              Venta v = new Venta();
              Clients c = new Clients();
              
            v.setId(rs.getInt("id_ventas"));
            v.setFecha(rs.getTimestamp("fecha_venta").toLocalDateTime());
            v.setTotal(rs.getDouble("total_final"));

            double pagado = rs.getDouble("pagado");
            v.setPagado(pagado);
            v.setRestante(v.getTotal() - pagado);

            v.setEstadoPago(rs.getString("estado_pago"));
            v.setEstadoTrabajo(rs.getString("estado_trabajo"));
            c.setNombre(rs.getString("cliente_nombre"));
            v.setCliente(c);
            //v.setTelefono(rs.getString("telefono"));

            lista.add(v);              
          }
      } catch(Exception e){
          e.printStackTrace();
      }
      return lista;
  }
    
  public List<Pago> obtenerPagosPorVenta(int idVenta){
      
      List<Pago> lista = new ArrayList<>();
      
      String sql = "SELECT * FROM pagos WHERE id_venta = ?";
      
      try(Connection conn = DBConnection.getConnection();
              PreparedStatement ps = conn.prepareStatement(sql)){
          ps.setInt(1, idVenta);
          ResultSet rs = ps.executeQuery();
          
          while(rs.next()){
              Pago p = new Pago();
            p.setMetodo(rs.getString("metodo_pago"));
            p.setMonto(rs.getDouble("monto"));
            p.setTipoPago(rs.getString("tipo_pago"));
            p.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
              lista.add(p);
          }
      }catch(Exception e){
          e.printStackTrace();
      }
      return lista;      
  }
  
            //==============
            //REALIZAR ABONO
            //==============  
  public boolean registrarAbono(int idVenta, String metodo, double monto){

    String sql = """
                INSERT INTO pagos
                    (id_venta, metodo_pago, monto,tipo_pago)
                VALUES (?,?,?,?)
                 """;

    try(Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)){

        ps.setInt(1, idVenta);
        ps.setString(2, metodo);
        ps.setDouble(3, monto);
        ps.setString(4, "ABONO");
        
        ps.executeUpdate();
        
        actualizarEstadoPago(idVenta,conn);
        return true;

    }catch(Exception e){
        e.printStackTrace();
        return false;
    }
}
  
    public Venta obtenerVentaCompleta(int idVenta){
      
      Venta venta = null;
      
      String sql = """
        SELECT 
            v.id_ventas,
            v.folio,
            v.fecha_venta,
            v.total_bruto,
            v.descuento_total,
            v.total_final,
            v.estado_pago,
            v.estado_trabajo,
            v.od_esf,
            v.od_cil,
            v.od_eje,
            v.oi_esf,
            v.oi_cil,
            v.oi_eje,
            v.add_lente,

            c.id_cliente,
            c.nombre AS cliente_nombre,
            c.telefono AS cliente_tel,
            c.direccion AS cliente_dir,


            e.id_vendedor,
            e.nombre AS vendedor_nombre,

            s.id_sucursal,
            s.sucursal  AS sucursal_nombre,
            s.telefono AS sucursal_tel,
            s.direccion AS sucursal_dir

        FROM ventas v
        JOIN cliente c ON v.id_cliente = c.id_cliente
        JOIN vendedor e ON v.id_vendedor = e.id_vendedor
        JOIN sucursal s ON v.id_sucursal = s.id_sucursal
        WHERE v.id_ventas = ?
    """;
      
      try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, idVenta);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {

            // ======================
            // Crear objetos
            // ======================

            Branch sucursal = new Branch();
            sucursal.setId(rs.getInt("id_sucursal"));
            sucursal.setSucursal(rs.getString("sucursal_nombre"));
            sucursal.setTelefono(rs.getString("sucursal_tel"));
            sucursal.setDireccion(rs.getString("sucursal_dir"));

            Clients cliente = new Clients();
            cliente.setId(rs.getInt("id_cliente"));
            cliente.setNombre(rs.getString("cliente_nombre"));
            cliente.setTelefono(rs.getString("cliente_tel"));
            cliente.setDireccion(rs.getString("cliente_dir"));

            Empleados vendedor = new Empleados();
            vendedor.setId_vendedor(rs.getInt("id_vendedor"));
            vendedor.setNombre(rs.getString("vendedor_nombre"));

            // ======================
            // Crear venta
            // ======================

            venta = new Venta();
            venta.setId(rs.getInt("id_ventas"));
            venta.setFolio(rs.getString("folio"));
            venta.setFecha(rs.getTimestamp("fecha_venta").toLocalDateTime());
            venta.setTotal(rs.getDouble("total_final"));
            venta.setTotalBruto(rs.getDouble("total_bruto"));
            venta.setDescuentoTotal(rs.getDouble("descuento_total"));
            venta.setTotalFinal(rs.getDouble("total_final"));
            venta.setEstadoPago(rs.getString("estado_pago"));
            venta.setEstadoTrabajo(rs.getString("estado_trabajo"));
            venta.setOdEsf(rs.getString("od_esf"));
            venta.setOdCil(rs.getString("od_cil"));
            venta.setOdEje(rs.getString("od_eje"));
            venta.setOiEsf(rs.getString("oi_esf"));
            venta.setOiCil(rs.getString("oi_cil"));
            venta.setOiEje(rs.getString("oi_eje"));
            venta.setAdd(rs.getString("add_lente"));            

            venta.setSucursal(sucursal);
            venta.setCliente(cliente);
            venta.setVendedor(vendedor);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return venta;
  }
  
    public List<SaleItem> obtenerDetalleVenta(int idVenta){
      List<SaleItem> lista = new ArrayList<>();

    String sql = """
        SELECT 
            dv.cantidad,
            p.modelo,
            p.categoria,
            dv.precio_unitario,
            dv.descuento_aplicado,
            dv.nombre_descuento
        FROM detalle_venta dv
        JOIN products p ON dv.id_product = p.id_product
        WHERE dv.id_venta = ?
    """;

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, idVenta);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            
            Product p = new Product();
            SaleItem item = new SaleItem();
            
            p.setModelo(rs.getString("modelo"));
            p.setCategoria(rs.getString("categoria"));
            p.setPrecio(rs.getDouble("precio_unitario"));
            item.setProducto(p);
            item.setCantidad(rs.getInt("cantidad"));
            item.setDescuento(rs.getDouble("descuento_aplicado"));
            item.setNombreDescuento(rs.getString("nombre_descuento"));

            lista.add(item);
            }
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    return lista;
  }
    
    public ClientStats obtenerEstadisticasCliente(int idClient){
      
      ClientStats stats = new ClientStats();
      
      String sql ="""
        SELECT 
            COUNT(v.id_ventas) AS total_compras,
            IFNULL(SUM(v.total_final), 0) AS total_gastado,
            MAX(v.fecha_venta) AS ultima_visita,
            MIN(v.fecha_venta) AS cliente_desde
        FROM ventas v
        WHERE v.id_cliente = ?
    """;
      
      try(Connection conn = DBConnection.getConnection();
              PreparedStatement ps = conn.prepareStatement(sql)){
          
          ps.setInt(1, idClient);
          ResultSet rs = ps.executeQuery();
          
          if(rs.next()){
              stats.setTotalCompras(rs.getInt("total_compras"));
              stats.setTotalGastado(rs.getDouble("total_gastado"));
              
              if(rs.getTimestamp("ultima_visita") != null){
                  stats.setUltimavisita(
                          rs.getTimestamp("ultima_visita").toLocalDateTime());
              }
              if(rs.getTimestamp("cliente_desde") != null){
                  stats.setUltimavisita(
                          rs.getTimestamp("cliente_desde").toLocalDateTime());
              }              
          }
      } catch(Exception e){
          e.printStackTrace();
      }
      return stats;
  }

    private void actualizarEstadoPago(int idVenta, Connection conn) {
        
        String sql = """
        UPDATE ventas v
        SET estado_pago = (
            CASE 
                WHEN (
                    SELECT IFNULL(SUM(monto),0)
                    FROM pagos
                    WHERE id_venta = v.id_ventas
                ) >= v.total_final
                THEN 'COMPLETA'
                ELSE 'PENDIENTE'
            END
        )
        WHERE v.id_ventas = ?
    """;
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, idVenta);
            ps.executeUpdate();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public boolean entregarVenta(int idVenta){
        String sql = "UPDATE ventas SET estado_trabajo ='ENTREGADO' WHERE id_ventas =?";
        
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, idVenta);
            return ps.executeUpdate()>0;
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
    
    public ReporteVentas obtenerReporteMensual(int idSucursal, int mes, int anio){
        String sql = """
            SELECT
                IFNULL (SUM(v.total_final),0) AS total,
                     
                IFNULL ((
                    SELECT SUM(p.monto)
                    FROM pagos p
                    WHERE p.id_venta IN (
                        SELECT id_ventas
                        FROM ventas
                        WHERE id_sucursal = ?
                        AND MONTH (fecha_venta) = ?
                        AND YEAR (fecha_venta) = ?
                    )),0) AS pagado,
                IFNULL((
                    SELECT SUM(v2.total_final - IFNULL((
                        SELECT SUM(p2.monto)
                        FROM pagos p2
                        WHERE p2.id_venta = v2.id_ventas),0))
                        FROM ventas v2
                        WHERE v2.id_sucursal = ?
                        AND MONTH(v2.fecha_venta) = ?
                        AND YEAR(v2.fecha_venta) = ?
                        AND v2.estado_pago = 'PENDIENTE'
                        ), 0) AS pendiente
                FROM ventas v
                    WHERE v.id_sucursal = ?
                    AND MONTH (v.fecha_venta) = ?
                    AND YEAR (v.fecha_venta) = ?
            """;
        
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)){
            
            ps.setInt(1, idSucursal);
            ps.setInt(2, mes);
            ps.setInt(3, anio);
            
            ps.setInt(4, idSucursal);
            ps.setInt(5, mes);
            ps.setInt(6, anio);
            
            ps.setInt(7, idSucursal);
            ps.setInt(8, mes);
            ps.setInt(9, anio);
            
            ResultSet rs = ps.executeQuery();
            
            if(rs.next()){
                 double total = rs.getDouble("total");
                 double pagado = rs.getDouble("pagado");
                 double pendiente = rs.getDouble("pendiente");
                 
                 return new ReporteVentas(total,pagado,pendiente);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return new ReporteVentas(0,0,0);
    }

    public Map<Integer,Double>obtenerVentasporDia(int idSucursal, int mes, int anio){
        
        Map<Integer,Double> datos = new HashMap<>();
        
        String sql = """
                SELECT 
                    DAY(fecha_venta) AS dia,
                    SUM(total_final) AS total
                FROM ventas
                WHERE id_sucursal = ?
                AND MONTH (fecha_venta) = ?
                AND YEAR (fecha_venta) = ?
                GROUP BY dia
                ORDER BY dia
                     """;
        
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, idSucursal);
            ps.setInt(2, mes);
            ps.setInt(3, anio);
            
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                datos.put(
                    rs.getInt("dia"),
                    rs.getDouble("total"));
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return datos;
    }
    
    public boolean recepcionarVenta(int idVenta){
        
        String sql = """
        UPDATE ventas
        SET estado_trabajo = 'RECIBIDO'
        WHERE id_ventas = ?
    """;

    try(Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)){

        ps.setInt(1, idVenta);

        return ps.executeUpdate() > 0;

    }catch(Exception e){
        e.printStackTrace();
        return false;
    }
    }
    
    public boolean cancelarVenta(int idVenta){
        
        String sql = """
        UPDATE ventas
        SET estado_pago = 'CANCELADA',
            estado_trabajo = 'CANCELADO'
        WHERE id_ventas = ?
    """;

    try(Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)){

        ps.setInt(1, idVenta);

        return ps.executeUpdate() > 0;
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
    
    public ReportPendientes obtenerResumenPendientes(int idSucursal){
        
        ReportPendientes r = new ReportPendientes();
        
        String sql = """
        SELECT

        SUM(
            CASE
                WHEN estado_trabajo = 'PROCESO'
                     AND estado_pago = 'PENDIENTE'
                THEN 1
                ELSE 0
            END
        ) AS trabajos_realizar,

        SUM(
            CASE
                WHEN estado_trabajo = 'RECIBIDO'
                     AND estado_pago = 'PENDIENTE'
                THEN 1
                ELSE 0
            END
        ) AS trabajos_entregar,

        SUM(
            CASE
                WHEN estado_trabajo = 'PROCESO'
                     AND estado_pago = 'PENDIENTE'
                THEN (
                    total_final -
                    IFNULL((
                        SELECT SUM(monto)
                        FROM pagos p
                        WHERE p.id_venta = v.id_ventas
                    ),0)
                )
                ELSE 0
            END
        ) AS saldo_realizar,

        SUM(
            CASE
                WHEN estado_trabajo = 'RECIBIDO'
                     AND estado_pago = 'PENDIENTE'
                THEN (
                    total_final -
                    IFNULL((
                        SELECT SUM(monto)
                        FROM pagos p
                        WHERE p.id_venta = v.id_ventas
                    ),0)
                )
                ELSE 0
            END
        ) AS saldo_entregar

        FROM ventas v
        WHERE estado_trabajo != 'ENTREGADO'
        AND estado_trabajo != 'CANCELADO'
        AND v.id_sucursal = ?
    """;
        
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)){
            
            ps.setInt(1, Session.getSucursal().getId());
            
            ResultSet rs = ps.executeQuery();
            
            if(rs.next()){
                r.setTrabajosRealizar(rs.getInt("trabajos_realizar"));
                r.setTrabajosEntregar(rs.getInt("trabajos_entregar"));
                r.setSaldoRealizar(rs.getDouble("saldo_realizar"));
                r.setSaldoEntregar(rs.getDouble("saldo_entregar"));
            }           
        } catch (Exception e){
            e.printStackTrace();
        }
        return r;
    }
    
    public List<Venta> obtenerTrabajosPorRealizar(){
        
         List<Venta> lista = new ArrayList<>();

    String sql = """
        SELECT
            v.id_ventas,
            v.fecha_venta,
            v.total_final,
            v.estado_pago,
            v.estado_trabajo,

            c.nombre AS cliente_nombre,

            IFNULL(SUM(p.monto),0) AS pagado

        FROM ventas v

        JOIN cliente c
            ON v.id_cliente = c.id_cliente

        LEFT JOIN pagos p
            ON v.id_ventas = p.id_venta

        WHERE v.estado_trabajo = 'PROCESO'
        AND V.estado_pago = 'PENDIENTE'
        AND v.id_sucursal = ?

        GROUP BY v.id_ventas

        ORDER BY v.fecha_venta ASC
    """;

    try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
        
            ps.setInt(1, Session.getSucursal().getId());
            
            ResultSet rs = ps.executeQuery();
        
        while(rs.next()){
            
            Venta v = new Venta();
            Clients c = new Clients();
            
            v.setId(rs.getInt("id_ventas"));
            v.setFecha(rs.getTimestamp("fecha_venta").toLocalDateTime());
            v.setTotal(rs.getDouble("total_final"));            
            double pagado = rs.getDouble("pagado");
            v.setPagado(pagado);
            v.setRestante(v.getTotal()- pagado);
            v.setEstadoPago(rs.getString("estado_pago"));
            v.setEstadoTrabajo(rs.getString("estado_trabajo"));
            c.setNombre(rs.getString("cliente_nombre"));
            v.setCliente(c);
            
            lista.add(v);            
        }
    } catch(Exception e){
        e.printStackTrace();
    }
    return lista;
    }
    
    public List<Venta> obtenerTrabajosPorEntregar(){
        
        List<Venta> lista = new ArrayList<>();
        
        String sql = """
                     SELECT
                        v.id_ventas,
                        v.fecha_venta,
                        v.total_final,
                        v.estado_pago,
                        v.estado_trabajo,
                        c.nombre AS cliente_nombre,
                    IFNULL(SUM(p.monto),0) AS pagado
                    FROM ventas v
                    JOIN cliente c
                    ON v.id_cliente = c.id_cliente
                    LEFT JOIN pagos p
                    ON v.id_ventas = p.id_venta
                    WHERE v.estado_trabajo = 'RECIBIDO'
                    AND v.estado_pago = 'PENDIENTE'
                    AND v.id_sucursal = ?
                    GROUP BY v.id_ventas
                    ORDER BY v.fecha_venta ASC
                    """;
        
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)){
            
            ps.setInt(1, Session.getSucursal().getId());
            
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                
                Venta v = new Venta();
                Clients c = new Clients();
                
                v.setId(rs.getInt("id_ventas"));
                v.setFecha(rs.getTimestamp("fecha_venta").toLocalDateTime());
                v.setTotal(rs.getDouble("total_final"));
                double pagado = rs.getDouble("pagado");
                v.setPagado(pagado);
                v.setRestante(v.getTotal() - pagado);
                v.setEstadoPago(rs.getString("estado_pago"));
                v.setEstadoTrabajo(rs.getString("estado_trabajo"));
                c.setNombre(rs.getString("cliente_nombre"));
                v.setCliente(c);
                
                lista.add(v);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return lista;
    }
    
    public int obtenerSiguienteiDVenta(){
        
        String sql = "SELECT IFNULL(MAX(id_ventas),0) + 1 AS siguiente FROM ventas";

    try(Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()){

        if(rs.next()){
            return rs.getInt("siguiente");
        }

    }catch(Exception e){
        e.printStackTrace();
    }

    return 1;
    }
    
    public Venta buscarPorFolio(int folio){
         
        Venta venta = null;

        String sql = """
                SELECT
                    v.id_ventas,
                    v.fecha_venta,
                    v.total_final,
                    c.id_cliente,
                    c.nombre AS cliente_nombre,
                    c.telefono
                FROM ventas v
                JOIN cliente c
                ON v.id_cliente = c.id_cliente
                WHERE v.id_ventas = ?
                """;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, folio);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                venta = new Venta();
                Clients cliente = new Clients();
                venta.setId(rs.getInt("id_ventas"));
                venta.setFecha(rs.getTimestamp("fecha_venta").toLocalDateTime());
                venta.setTotal(rs.getDouble("total_final"));
                cliente.setId(rs.getInt("id_cliente"));
                cliente.setNombre(rs.getString("cliente_nombre"));
                cliente.setTelefono(rs.getString("telefono"));
                venta.setCliente(cliente);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    return venta;
    }
       
}
