package com.gerardgv.posclarity.service;

import com.gerardgv.posclarity.database.SaleDAO;
import com.gerardgv.posclarity.models.*;
import com.gerardgv.posclarity.utils.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.*;
import javafx.print.Printer;
import javafx.scene.control.Alert;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.export.*;
import net.sf.jasperreports.view.JasperViewer;

public class TicketService {
    
    private static final String Ruta_Reporte = "/com/gerardgv/posclarity/reports/ticketVenta.jrxml";
    private static final String Ruta_Orden = "/com/gerardgv/posclarity/reports/Order.jrxml";
    private static final String Ruta_Abono = "/com/gerardgv/posclarity/reports/ticketAbono.jrxml";
    private static final String Ruta_Orden_Garantia = "/com/gerardgv/posclarity/reports/OrdenGarantia.jrxml";

    
    /*
    //GENERAR & IMPRIMIR TICKET DE VENTA.
    */
    
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
            
            try{
                imprimirConImpresoraGuardada(print,2);
            } catch (Exception e){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Impresión");
                alert.setHeaderText(null);
                alert.setContentText("No se pudo imprimir el ticket. Verifica la impresora.");
                alert.showAndWait();
            }
            
            JasperViewer.viewReport(print,false);
            
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
        params.put("nota", String.valueOf(venta.getFolio()));
        //Totales & Descuentos
        double subtotal = venta.getTotalBruto();        
        double descuento = venta.getDescuentoTotal();        
        double total = venta.getTotalFinal();
        
        SaleDAO saleDAO = new SaleDAO();
        
        List<Pago> pagos = saleDAO.obtenerPagosPorVenta(venta.getId());
        double pagado = pagos.stream().mapToDouble(Pago::getMonto).sum();
        double pendiente = total - pagado;
        
        if(pendiente < 0){
            pendiente = 0;
        }

        
        params.put("subtotal", subtotal);
        params.put("descuento", descuento);
        params.put("total", total);
        params.put("pago", pagado);
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
    
    /*
    //GENERAR & IMPRIMIR ORDEN DE LABORATORIO.
    */
    
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
            
            try{
                imprimirConImpresoraGuardada(print,2);
            } catch(Exception e){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Impresión");
                alert.setHeaderText(null);
                alert.setContentText("No se pudo imprimir la orden.");
                alert.showAndWait();
            }
            JasperViewer.viewReport(print,false);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
        
    private Map<String,Object> construirParametrosOrden(Venta venta,List<SaleItem> items){
    
        Map<String,Object> params = new HashMap<>(); 
        
        params.put("sucursal", venta.getSucursal().getSucursal());
    // 🧾 Nombre + folio
        params.put("cliente", venta.getCliente().getNombre() + " #" + venta.getFolio());
    
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
    
    /*
    //GENERAR & IMPRIMIR TICKET DE ABONO.
    */
   
    public void imprimirTicketAbono(Venta venta, double montoAbono, String metodoPago){
       
       try{
           
           InputStream reportStream = getClass().getResourceAsStream(Ruta_Abono);
           
           if(reportStream == null){
               throw new RuntimeException("No Se Encontró El Reporte");
           }
           
           String jrxml = new String(reportStream.readAllBytes(), StandardCharsets.UTF_8);
           
            jrxml = jrxml.replaceAll("uuid=\"[^\"]*\"", "");
            jrxml = jrxml.replace("language=\"groovy\"","language=\"java\"");
            InputStream limpio = new ByteArrayInputStream(jrxml.getBytes(StandardCharsets.UTF_8));

            JasperReport report =JasperCompileManager.compileReport(limpio);

            Map<String, Object> params =construirParametrosAbono(
                        venta,
                        montoAbono,
                        metodoPago);
            
            JasperPrint print = JasperFillManager.fillReport(
                report,
                params,
                new JREmptyDataSource(1));
            
            try{
                imprimirConImpresoraGuardada(print, 2);
                
            } catch(Exception e){
               e.printStackTrace();

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Impresión");
                alert.setHeaderText(null);
                alert.setContentText(
                    "El abono se registró correctamente, "
                    + "pero no se pudo imprimir el comprobante.\n\n"
                    + "Verifica que la impresora esté conectada."
                );
                alert.showAndWait(); 
            }
            JasperViewer.viewReport(print,false);
       } catch (Exception e){
           e.printStackTrace();
           
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo generar el ticket de abono");
            alert.setContentText(
                e.getMessage() != null
                        ? e.getMessage()
                        : "Ocurrió un error al generar el comprobante."
            );
            alert.showAndWait();
       }       
   }
   
    private Map<String, Object> construirParametrosAbono(Venta venta, double montoAbono, String metodoPago) {
    
        Map<String, Object> params = new HashMap<>();
        
        SaleDAO saleDAO = new SaleDAO();
        
        /*
        * Se consultan nuevamente los pagos después de registrar
        * el abono para obtener el total pagado actualizado.
        */
        List<Pago> pagos =
            saleDAO.obtenerPagosPorVenta(venta.getId());

        double pagadoAcumulado = pagos.stream()
            .mapToDouble(Pago::getMonto)
            .sum();

        double saldoPendiente = Math.max(venta.getRestante(), 0);

        /*
        * Evitamos saldos negativos por centavos o redondeos.
        */
        if (saldoPendiente < 0.01) {
            saldoPendiente = 0;
        }
        
        double totalVenta = pagadoAcumulado + saldoPendiente;

        String estado = saldoPendiente == 0
            ? "VENTA LIQUIDADA"
            : "VENTA PENDIENTE";

        LocalDateTime ahora = LocalDateTime.now();

        // Datos de sucursal
        
        Branch sucursalActual = Session.getSucursal();
        
        params.put("sucursal", sucursalActual != null ?
                valorSeguro(sucursalActual.getSucursal()) : "" );

        params.put("telefono_suc", sucursalActual != null ?
                valorSeguro(sucursalActual.getTelefono()) : "" );

        params.put("direccion_suc", sucursalActual != null ?
                valorSeguro(sucursalActual.getDireccion()) : "" );

        // Datos de la venta
        params.put("folio",
            venta.getFolio() != null
                    ? String.valueOf(venta.getFolio())
                    : String.valueOf(venta.getId()));

        params.put("cliente",
            venta.getCliente() != null
                    ? valorSeguro(
                            venta.getCliente().getNombre()
                    ) : "CLIENTE GENERAL" );

        // Fecha y hora del abono
        params.put("fecha",
            ahora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        params.put("hora",
            ahora.format(DateTimeFormatter.ofPattern("HH:mm")));

        params.put("metodo",
            metodoPago != null
                    ? metodoPago.toUpperCase() : "NO ESPECIFICADO");

        // Importes
        params.put("monto_abono", montoAbono);
        params.put("total_venta", totalVenta);
        params.put("pagado_acumulado", pagadoAcumulado);
        params.put("saldo_pendiente", saldoPendiente);
        params.put("estado", estado);

        // Logo
        params.put(
            "logo",
            getClass().getResourceAsStream(
                    "/com/gerardgv/posclarity/img/logo-removebg.png"));

        return params;
    }
    
    /*
    //GENERAR & IMPRIMIR ORDEN DE GARANTIA.
    */
    
    public void imprimirOrdenGarantia(
                            Garantia garantia,
                            GarantiaGraduacion graduacionNueva,
                            Venta venta, List<SaleItem> items){
        
        try{
            InputStream reportStream = getClass().getResourceAsStream(Ruta_Orden_Garantia);

            if(reportStream == null){
                throw new RuntimeException( "No se encontró la Orden de Garantía" );
            }

            String jrxml = new String(reportStream.readAllBytes(),StandardCharsets.UTF_8);

            jrxml = jrxml.replaceAll("uuid=\"[^\"]*\"", "");
            jrxml = jrxml.replace("language=\"groovy\"","language=\"java\"");

            InputStream limpio = new ByteArrayInputStream(jrxml.getBytes(StandardCharsets.UTF_8));

            JasperReport report = JasperCompileManager.compileReport(limpio);

            Map<String,Object> params =
                construirParametrosOrdenGarantia(garantia,graduacionNueva,venta,items);

            JasperPrint print = JasperFillManager.fillReport(
                report,params,new JREmptyDataSource());

            try{
                imprimirConImpresoraGuardada(print, 2);

            } catch(Exception e){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Impresión");
                alert.setHeaderText(null);
                alert.setContentText("La garantía se registró, pero no se pudo imprimir la orden.");
                alert.showAndWait();
            }
        JasperViewer.viewReport(print, false);
        } catch(Exception e){
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Orden de garantía");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo generar la orden de garantía.");
            alert.showAndWait();
        }
    }
    
    private Map<String, Object> construirParametrosOrdenGarantia(Garantia garantia,GarantiaGraduacion graduacionNueva , Venta venta, List<SaleItem> items)  {

        Map<String,Object> params = new HashMap<>();
        
        String nombreSucursal = "";
        
        if(venta != null && venta.getSucursal() != null){
            nombreSucursal = venta.getSucursal().getSucursal();
        }

        params.put("sucursal", valorSeguro(nombreSucursal));
        params.put("cliente",valorSeguro(venta.getCliente().getNombre()));
        params.put("folio_garantia",valorSeguro(garantia.getFolio()));
        params.put("folio_venta",valorSeguro(venta.getFolio()));     
        params.put("vendedor",venta.getVendedor()!= null ? valorSeguro(venta.getVendedor().getNombre()):"");
        params.put(
            "fecha",
                garantia.getFechaSolicitud() != null
                ? garantia.getFechaSolicitud().format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                ): LocalDate.now().format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        params.put("motivo",valorSeguro(garantia.getMotivo()));
        params.put("accion",formatearAccion(garantia.getAccion()));
        params.put("tipo_lente",construirTipo(items));
        
        if(graduacionNueva != null){

            params.put("od_esf",valorGraduacion(graduacionNueva.getOdEsfera()));
            params.put("od_cil",valorGraduacion(graduacionNueva.getOdCilindro()));
            params.put("od_eje",valorGraduacion(graduacionNueva.getOdEje()));
            params.put("oi_esf",valorGraduacion(graduacionNueva.getOiEsfera()));
            params.put("oi_cil",valorGraduacion(graduacionNueva.getOiCilindro()));
            params.put("oi_eje",valorGraduacion(graduacionNueva.getOiEje()));
            params.put("add",valorGraduacion(graduacionNueva.getOdAdd()));

        }else{

            params.put("od_esf", "");
            params.put("od_cil", "");
            params.put("od_eje", "");
            params.put("oi_esf", "");
            params.put("oi_cil", "");
            params.put("oi_eje", "");
            params.put("add", "");
        }
            params.put("logo",getClass().getResourceAsStream( "/com/gerardgv/posclarity/img/logo-removebg.png"));

        return params;
    }
    
    private String formatearAccion(String accion){

        if(accion == null || accion.isBlank()){
            return "";
        }

        return switch(accion){
            case "REHACER_MICA" -> "Rehacer mica";
            case "CAMBIO_PRODUCTO" -> "Cambio de producto";
            case "REPARACION" -> "Reparación";
            case "NOTA_CREDITO" -> "Nota de crédito";
            default -> accion.replace("_", " ");
        };
    }
       
    
    private String valorSeguro(String valor){
        return valor != null ? valor : "";
    }
    
    private String valorGraduacion(String valor){
        return valor == null || valor.isBlank()
            ? ""
            : valor.trim();
    }
    
    /*
    // GUARDAR IMPRESORA.
    */
    
    private void imprimirConImpresoraGuardada(JasperPrint print, int copias) throws JRException {
       
       String nombreImpresora = Configuracion.obtenerImpresora();
       
       if(nombreImpresora == null){
            Printer printerSeleccionada = PrinterUtils.seleccionarImpresora();
            if(printerSeleccionada == null){
                return;
            }
            nombreImpresora = printerSeleccionada.getName();
       }
       
       PrintService impresora = null;
       
       for( PrintService ps : PrintServiceLookup.lookupPrintServices(null,null)){
           if(ps.getName().equals(nombreImpresora)){
               impresora = ps;
               break;
           }
       }
       
       if(impresora == null){
            Printer printerSeleccionada = PrinterUtils.seleccionarImpresora();
            if(printerSeleccionada == null){
                return;
            }
            nombreImpresora = printerSeleccionada.getName();
            
            for(PrintService ps :
            PrintServiceLookup.lookupPrintServices(null, null)){

                if(ps.getName().equals(nombreImpresora)){
                    impresora = ps;
            break;
                }
           }
            if(impresora == null){
                return;
            }
       }
       
        JRPrintServiceExporter exporter = new JRPrintServiceExporter();         
        exporter.setExporterInput( new SimpleExporterInput(print));        
        SimplePrintServiceExporterConfiguration exportConfig =
            new SimplePrintServiceExporterConfiguration();        
        exportConfig.setPrintService(impresora);
        exportConfig.setDisplayPageDialog(false);
        exportConfig.setDisplayPrintDialog(false);        
        exporter.setConfiguration(exportConfig);
        
        for(int i = 0; i < copias; i++){
            exporter.exportReport();
        }       
   }

    
        
}
