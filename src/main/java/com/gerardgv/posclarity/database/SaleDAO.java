package com.gerardgv.posclarity.database;

import com.gerardgv.posclarity.models.*;
import com.gerardgv.posclarity.utils.Session;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class SaleDAO {
    
    public int guardarVenta(
            List<SaleItem> carrito,
            List<Pago> pagos,
            double totalBruto,
            double descuento,
            double totalFinal,
            String estadoPago,
            String estadoTrabajo,
            int idCliente,
            int idUsuario){
        
        Connection conn = null;
        
        try{
            
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            boolean esBajoPedido = contieneBajoPedido(carrito);           
            //==============
            //INSERTAR VENTA
            //==============
            String sqlVenta = "INSERT INTO ventas (id_sucursal,id_cliente,id_vendedor,nombre_promocion,total_bruto,descuento_total,total_final,estado_pago,estado_trabajo)"
                    + "VALUES(?,?,?,?,?,?,?,?,?)";
            
            PreparedStatement psVenta = conn.prepareStatement(sqlVenta, PreparedStatement.RETURN_GENERATED_KEYS);
                                    
            psVenta.setInt(1, Session.getSucursal().getId());
            psVenta.setInt(2, idCliente);
            psVenta.setInt(3, idUsuario);
            psVenta.setString(4,ObtenerNombrePromo(carrito)); //MODIFICAR
            psVenta.setDouble(5, totalBruto);
            psVenta.setDouble(6, descuento);
            psVenta.setDouble(7, totalFinal);
            psVenta.setString(8, estadoPago);
            psVenta.setString(9,estadoTrabajo);            
            psVenta.executeUpdate();
            
            ResultSet rs = psVenta.getGeneratedKeys();
                int idVenta = 0;            
            if(rs.next()){
                idVenta = rs.getInt(1);
            }
            
            //==============
            //DETALLE VENTA
            //==============            
            String sqlDetalle = "INSERT INTO detalle_venta(id_venta,id_product,cantidad,precio_unitario,descuento_aplicado,subtotal)"
                    + "VALUES (?,?,?,?,?,?)";            
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
            String sqlPago = "INSERT INTO pagos (id_venta,metodo_pago,monto,referencia)"
                    + "VALUES (?,?,?,?)";
            PreparedStatement psPago = conn.prepareStatement(sqlPago);
            
            for(Pago p : pagos){
                psPago.setInt(1, idVenta);
                psPago.setString(2, p.getMetodo());
                psPago.setDouble(3,p.getMonto());
                psPago.setString(4, p.getReferencia());
                psPago.addBatch();
            }
            psPago.executeBatch();
            conn.commit();
            return idVenta;
            
        } catch(Exception e){
            try{
                if(conn != null) conn.rollback();
            }catch (Exception ex){
                ex.printStackTrace();
            }
            e.printStackTrace();
            return -1;
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

  public List<Venta> obtenerVentasPendientes(){
      
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
        WHERE v.estado_pago = 'PENDIENTE'
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
        WHERE v.estado_pago = 'PENDIENTE'
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
              p.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
              lista.add(p);
          }
      }catch(Exception e){
          e.printStackTrace();
      }
      return lista;      
  }
  
  public boolean registrarAbono(int idVenta, String metodo, double monto){

    String sql = "INSERT INTO pagos(id_venta, metodo_pago, monto) VALUES (?,?,?)";

    try(Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)){

        ps.setInt(1, idVenta);
        ps.setString(2, metodo);
        ps.setDouble(3, monto);

        ps.executeUpdate();
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
            v.fecha_venta,
            v.total_final,
            v.estado_pago,
            v.estado_trabajo,

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
            venta.setFecha(rs.getTimestamp("fecha_venta").toLocalDateTime());
            venta.setTotal(rs.getDouble("total_final"));
            venta.setEstadoPago(rs.getString("estado_pago"));
            venta.setEstadoTrabajo(rs.getString("estado_trabajo"));

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
            CONCAT(p.marca,' ',p.modelo) AS descripcion,
            dv.precio_unitario
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
            
            p.setModelo(rs.getString("descripcion"));
            p.setPrecio(rs.getDouble("precio_unitario"));           
            item.setProducto(p);
            item.setCantidad(rs.getInt("cantidad"));

            lista.add(item);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return lista;
  }
    
}

