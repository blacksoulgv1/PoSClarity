package com.gerardgv.posclarity.service;

import com.gerardgv.posclarity.models.CierreCaja;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

public class CierreCajaService {
    
    public void generarReporte(CierreCaja cierre){
        
        Map<String, Object> params = new HashMap<>();
        
        params.put("sucursal",cierre.getSucursal().getSucursal());
        params.put("fecha",cierre.getFecha().toString());
        params.put("hora",cierre.getHoraCierre().toLocalTime().toString());
        params.put("cajaInicial",cierre.getCajaInicial());
        params.put("ventaFinal",cierre.getVentaFinal());
        params.put("totalNotasNuevas",cierre.getTotalNotasNuevas());
        params.put("totalVentas",cierre.getTotalVentas());
        params.put("totalAbonos",cierre.getTotalAbonos());
        params.put("detalleNuevos",cierre.getDetalleNuevo());
        params.put("detalleRecogidos",cierre.getDetalleRecogido());
        params.put("detalleAbonos",cierre.getDetalleAbonos());
        params.put("ventaFinal",cierre.getVentaFinal());
        params.put("totalNotasNuevas",cierre.getTotalNotasNuevas());        
        params.put("totalIngresos",cierre.getTotalIngresos());
        params.put("totalEfectivo",cierre.getTotalEfectivo());
        params.put("totalTransferencia",cierre.getTotalTransferencia());
        params.put("totalTarjeta",cierre.getTotalTarjeta());
        params.put("deposito",cierre.getDeposito());
        params.put("diferencia",cierre.getDiferencia());
        params.put("cajaFinal",cierre.getCajaFinal());
        params.put("observaciones",cierre.getObservaciones());
        
        try {

            InputStream reporte =
                getClass().getResourceAsStream(
                    "/com/gerardgv/posclarity/reports/cierre_caja.jrxml");
            
            JasperReport jasperReport =
                JasperCompileManager.compileReport(reporte);
            
            JasperPrint jasperPrint =
                JasperFillManager.fillReport(
                    jasperReport,
                    params,
                    new JREmptyDataSource());
            
            JasperViewer.viewReport(jasperPrint,false);
            
        } catch (Exception e) {
            e.printStackTrace();
            
            Throwable t = e;
            
            while(t != null){
                System.out.println("Error Real:" + t.getMessage());
                t=t.getCause();
            }
        }        
    }
    
}
