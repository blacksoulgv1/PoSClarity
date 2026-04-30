package com.gerardgv.posclarity.service;

import com.gerardgv.posclarity.models.SaleItem;
import com.gerardgv.posclarity.models.TicketItem;
import com.gerardgv.posclarity.models.Venta;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

public class TicketService {
    
    private static final String Ruta_Reporte = "/com/gerardgv/posclarity/reports/ticket_venta.jrxml";
    private static final String Ruta_Orden = "/com/gerardgv/posclarity/reports/Order.jrxml";

    
    public void imprimirTicket(Venta venta, List<SaleItem> items){
        
        try{
            InputStream reportStream = getClass().getResourceAsStream(Ruta_Reporte);
            
            if(reportStream == null){
                throw new RuntimeException("No se Encontró el reporte");
            }
            
            String jrxml = new String(reportStream.readAllBytes(),StandardCharsets.UTF_8);
            
            jrxml = jrxml.replaceAll("uuid=\"[^\"]*\"", "");            
            jrxml = jrxml.replace("language=\"groovy\"", "language=\"java\"");
                    
            InputStream limpio = new ByteArrayInputStream(jrxml.getBytes(StandardCharsets.UTF_8));

            JasperReport report = JasperCompileManager.compileReport(limpio);
            
            List<TicketItem> ticketItems = convertirItems(items);
            
            Map<String,Object> params = construirParametros(venta, items);
            
            JRBeanCollectionDataSource dataSource= new JRBeanCollectionDataSource(ticketItems);
            
            JasperPrint print = JasperFillManager.fillReport(report,params,dataSource);
            
            JasperViewer.viewReport(print,false);
            
            JasperExportManager.exportReportToPdfFile(
                print,
                "ticket_" + venta.getId() + ".pdf");
            
        } catch(Exception e){
            e.printStackTrace();
        }        
    }
    
    private Map<String,Object> construirParametros(Venta venta, List<SaleItem> items){
        
        Map<String,Object> params = new HashMap<>();
        
        //Sucursal
        params.put("sucursal", venta.getSucursal().getSucursal());
        params.put("telefono_suc", venta.getSucursal().getTelefono());
        params.put("direccion_suc", venta.getSucursal().getDireccion());
        //cliente
        params.put("cliente", venta.getCliente().getNombre());
        params.put("telefono_clien", venta.getCliente().getTelefono());
        params.put("direccion_clien", venta.getCliente().getDireccion());
        //Vendedor
        params.put("vendedor", venta.getVendedor().getNombre());
        params.put("fecha", venta.getFecha().
                format(DateTimeFormatter.ofPattern("dd/MM/yyy HH:mm")));
        params.put("nota", String.valueOf(venta.getId()));
        
        double subtotal = items.stream()
                .mapToDouble(i -> i.getPrecio()*i.getCantidad()).sum();
        
        double descuento = items.stream()
                .mapToDouble(i -> i.getDescuento()*i.getCantidad()).sum();
        
        double total = subtotal - descuento;
        
        double pago = total;
        double pendiente = 0;
        
        params.put("subtotal", subtotal);
        params.put("descuento", descuento);
        params.put("total", total);
        params.put("pago", pago);
        params.put("pendiente", pendiente);
        params.put("logo", getClass().getResourceAsStream(
                "/com/gerardgv/posclarity/img/logo-removebg.png"));
        
        return params;
        
    }

    private List<TicketItem> convertirItems(List<SaleItem> items) {
        return items.stream().map(i -> new TicketItem(
            i.getCantidad(),
            i.getProducto().getModelo(),
            i.getProducto().getPrecio(),
            i.getSubtotal(),
            i.getNombreDescuento())).toList();
    }
    
    public void imprimirOrdenLaboratorio(Venta venta,List<SaleItem> items){
        try{
            InputStream reportStream = getClass().getResourceAsStream(Ruta_Orden);
            
            if(reportStream == null){
                throw new RuntimeException("No se encontró Orden de Laboratorio");
            }
            
            String jrxml = new String(reportStream.readAllBytes(), StandardCharsets.UTF_8);
        
            jrxml = jrxml.replaceAll("uuid=\"[^\"]*\"", "");            
            jrxml = jrxml.replace("language=\"groovy\"", "language=\"java\"");
                
            InputStream limpio = new ByteArrayInputStream(jrxml.getBytes(StandardCharsets.UTF_8));

            JasperReport report = JasperCompileManager.compileReport(limpio);
        
            Map<String,Object> params = construirParametrosOrden(venta,items);
            
            JasperPrint print = JasperFillManager.fillReport(report, params, new JREmptyDataSource());
            
            JasperViewer.viewReport(print,false);
            
            JasperExportManager.exportReportToPdfFile(
                print,
                "orden_" + venta.getId() + ".pdf");            
        } catch (Exception e){
            e.printStackTrace();
        }
    }
        
    private Map<String,Object> construirParametrosOrden(Venta venta,List<SaleItem> items){
    
        Map<String,Object> params = new HashMap<>(); 
        
        params.put("sucursal", venta.getSucursal().getSucursal());
    // 🧾 Nombre + folio
        params.put("cliente", venta.getCliente().getNombre() + " #" + venta.getId());
    
    // 👓 Tipo de lente (ajústalo a tu modelo real)
        params.put("tipo_lente", construirTipo(items));
    
    // 👁️ OD
        params.put("od_esf", venta.getOdEsf() != null ? venta.getOdEsf():"");
        params.put("od_cil", venta.getOdCil() != null ? venta.getOdCil():"");
        params.put("od_eje", venta.getOdEje() != null ? venta.getOdEje():"");
    
    // 👁️ OI
        params.put("oi_esf", venta.getOiEsf() != null ? venta.getOiEsf():"");
        params.put("oi_cil", venta.getOiCil() != null ? venta.getOiCil():"");
        params.put("oi_eje", venta.getOiEje() != null ? venta.getOiEje():"");
    
    // ➕ ADD
        params.put("add", venta.getAdd() != null ? venta.getAdd():"");
        params.put("logo", getClass().getResourceAsStream(
                "/com/gerardgv/posclarity/img/logo-removebg.png"));
    
    return params;
    }
    
   private String construirTipo(List<SaleItem> items){

    String mica = "";
    Set<String> tratamientos = new LinkedHashSet<>();

    for(SaleItem item : items){

        var producto = item.getProducto();
        if(producto == null) continue;

        String categoria = producto.getCategoria();
        String modelo = producto.getModelo();

        if(categoria == null || modelo == null) continue;

        if("mica".equalsIgnoreCase(categoria)){
            mica = modelo;
        } 
        else if("tratamiento".equalsIgnoreCase(categoria)){
            tratamientos.add(modelo);
        }
    }

    StringBuilder tipo = new StringBuilder();

    if(!mica.isBlank()){
        tipo.append(mica);
    }

    if(!tratamientos.isEmpty()){
        if(tipo.length() > 0) tipo.append(" + ");
        tipo.append(String.join(" + ", tratamientos));
    }

    return tipo.length() == 0 ? "N/A" : tipo.toString();
}
    
}
