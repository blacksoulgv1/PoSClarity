package com.gerardgv.posclarity.service;

import com.gerardgv.posclarity.models.SaleItem;
import com.gerardgv.posclarity.models.TicketItem;
import com.gerardgv.posclarity.models.Venta;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

public class TicketService {
    
    private static final String Ruta_Reporte = "/com/gerardgv/posclarity/reports/ticket_venta.jrxml";
    
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
            i.getSubtotal())).toList();
    }
    
}
