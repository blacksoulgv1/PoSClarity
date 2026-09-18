package com.gerardgv.posclarity.service;

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
    
    public void imprimirTicket(Sale sale, List<SaleItem> items){
        
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
            
            Map<String,Object> params = construirParametros(sale, items);
            
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
    
    private Map<String,Object> construirParametros(Sale sale, List<SaleItem> items){
        
        Map<String,Object> params = new HashMap<>();
        
        System.out.println("===== DATOS CLIENTE PARA TICKET =====");
        System.out.println("cliente: " + sale.getClient().getName());
        System.out.println("telefono_clien: " + sale.getClient().getPhone());
        System.out.println("direccion_clien: " + sale.getClient().getAddress());
        System.out.println("======================================");
        //Sucursal
        params.put("sucursal", sale.getBranch().getName());
        params.put("telefono_suc", sale.getBranch().getPhone());
        params.put("direccion_suc", sale.getBranch().getAddress());
        //cliente
        params.put("cliente", sale.getClient().getName());
        params.put("telefono_clien", sale.getClient().getPhone());
        params.put("direccion_clien", sale.getClient().getAddress());
        //Vendedor
        params.put("vendedor", sale.getSeller().getName());
        params.put("fecha", sale.getSaleDate().
                format(DateTimeFormatter.ofPattern("dd/MM/yyy HH:mm")));
        params.put("nota", String.valueOf(sale.getFolio()));
        //Totales & Descuentos
        double subtotal = sale.getGrossTotal();        
        double descuento = sale.getTotalDiscount();        
        double total = sale.getFinalTotal();
        
        List<Payment> pagos = sale.getPayments();

        double pagado = pagos != null
            ? pagos.stream()
                .mapToDouble(Payment::getAmount)
                .sum(): 0;        
        
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
            i.getQuantity(),
            i.getProduct().getModel(),
            i.getProduct().getPrice(),
            i.getSubtotal(),
            i.getDiscountName())).toList();
    }
    
    /*
    //GENERAR & IMPRIMIR ORDEN DE LABORATORIO.
    */
    
    public void imprimirOrdenLaboratorio(Sale sale,List<SaleItem> items){
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
        
            Map<String,Object> params = construirParametrosOrden(sale,items);
            
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
        
    private Map<String,Object> construirParametrosOrden(Sale sale,List<SaleItem> items){
    
        Map<String,Object> params = new HashMap<>(); 
        
        params.put("sucursal", sale.getBranch().getName());
    // 🧾 Nombre + folio
        params.put("cliente", sale.getClient().getName()+ " #" + sale.getFolio());
    
    // 👓 Tipo de lente (ajústalo a tu modelo real)
        params.put("tipo_lente", construirTipo(items));
    
    // 👁️ OD
        params.put("od_esf", sale.getOdEsf() != null ? sale.getOdEsf():"");
        params.put("od_cil", sale.getOdCil() != null ? sale.getOdCil():"");
        params.put("od_eje", sale.getOdEje() != null ? sale.getOdEje():"");
    
    // 👁️ OI
        params.put("oi_esf", sale.getOiEsf() != null ? sale.getOiEsf():"");
        params.put("oi_cil", sale.getOiCil() != null ? sale.getOiCil():"");
        params.put("oi_eje", sale.getOiEje() != null ? sale.getOiEje():"");
    
    // ➕ ADD
        params.put("add", sale.getAddLens()!= null ? sale.getAddLens():"");
        params.put("logo", getClass().getResourceAsStream(
                "/com/gerardgv/posclarity/img/logo-removebg.png"));
    
    return params;
    }
    
    private String construirTipo(List<SaleItem> items){

        String mica = "";
        Set<String> tratamientos = new LinkedHashSet<>();

    for(SaleItem item : items){

        var producto = item.getProduct();
        if(producto == null) continue;

        String categoria = producto.getCategory();
        String modelo = producto.getModel();

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
   
    public void imprimirTicketAbono(Sale sale, double montoAbono, String metodoPago){
       
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
                        sale,
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
   
    private Map<String, Object> construirParametrosAbono(Sale sale, double montoAbono, String metodoPago) {
    
        Map<String, Object> params = new HashMap<>();
        
        List<Payment> pagos = sale.getPayments();
        
        if(pagos == null){
            pagos = new ArrayList<>();
        }

        double pagadoAcumulado = pagos.stream()
            .mapToDouble(Payment::getAmount)
            .sum();

        double saldoPendiente = Math.max(sale.getRemaining(), 0);

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
                valorSeguro(sucursalActual.getName()) : "" );

        params.put("telefono_suc", sucursalActual != null ?
                valorSeguro(sucursalActual.getPhone()) : "" );

        params.put("direccion_suc", sucursalActual != null ?
                valorSeguro(sucursalActual.getAddress()) : "" );

        // Datos de la venta
        params.put("folio",
            sale.getFolio() != null
                    ? String.valueOf(sale.getFolio())
                    : String.valueOf(sale.getId()));

        params.put("cliente",
            sale.getClient() != null
                    ? valorSeguro(
                            sale.getClient().getName()
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
                            Warranty garantia,
                            WarrantyGraduation graduacionNueva,
                            Sale sale, List<SaleItem> items){
        
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
                construirParametrosOrdenGarantia(garantia,graduacionNueva,sale,items);

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
    
    private Map<String, Object> construirParametrosOrdenGarantia(Warranty garantia,WarrantyGraduation graduacionNueva , Sale sale, List<SaleItem> items)  {

        Map<String,Object> params = new HashMap<>();
        
        String nombreSucursal = "";
        
        if(sale != null && sale.getBranch()!= null){
            nombreSucursal = sale.getBranch().getName();
        }

        params.put("sucursal", valorSeguro(nombreSucursal));
        params.put("cliente",valorSeguro(sale.getClient().getName()));
        params.put("folio_garantia",valorSeguro(garantia.getFolio()));
        params.put("folio_venta",valorSeguro(sale.getFolio()));     
        params.put("vendedor",sale.getSeller()!= null ? valorSeguro(sale.getSeller().getName()):"");
        params.put(
            "fecha",
                garantia.getRequestDate() != null
                ? garantia.getRequestDate().format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                ): LocalDate.now().format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        params.put("motivo",valorSeguro(garantia.getReason()));
        params.put("accion",formatearAccion(garantia.getAction()));
        params.put("tipo_lente",construirTipo(items));
        
        if(graduacionNueva != null){

            params.put("od_esf",valorGraduacion(graduacionNueva.getOdSphere()));
            params.put("od_cil",valorGraduacion(graduacionNueva.getOdCylinder()));
            params.put("od_eje",valorGraduacion(graduacionNueva.getOdAxis()));
            params.put("oi_esf",valorGraduacion(graduacionNueva.getOiSphere()));
            params.put("oi_cil",valorGraduacion(graduacionNueva.getOiCylinder()));
            params.put("oi_eje",valorGraduacion(graduacionNueva.getOiAxis()));
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
