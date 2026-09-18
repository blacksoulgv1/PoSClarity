package com.gerardgv.posclarity.service;

import com.gerardgv.posclarity.models.CashClosing;
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
    
    public void generarReporte(CashClosing closing) { 
        
        Map<String, Object> params = new HashMap<>(); 
        
        params.put("sucursal", closing.getBranch().getName()); 
        params.put("fecha", closing.getDate().toString()); 
        params.put("hora", closing.getClosingTime().toLocalTime().toString()); 
        params.put("cajaInicial", closing.getInitialCash()); 
        params.put("ventaFinal", closing.getFinalSale()); 
        params.put("totalNotasNuevas", closing.getNewSalesCount()); 
        params.put("totalVentas", closing.getTotalSales()); 
        params.put("totalAbonos", closing.getTotalPayments()); 
        params.put("detalleNuevos", closing.getNewSalesDetail()); 
        params.put("detalleRecogidos", closing.getDeliveredSalesDetail()); 
        params.put("detalleAbonos", closing.getPaymentsDetail());
        
        System.out.println("=== CIERRE CAJA ===");
System.out.println("NewSalesDetail: " + closing.getNewSalesDetail());
System.out.println("DeliveredSalesDetail: " + closing.getDeliveredSalesDetail());
System.out.println("PaymentsDetail: " + closing.getPaymentsDetail());

        params.put("totalIngresos", closing.getTotalIncome()); 
        params.put("totalEfectivo", closing.getTotalCash()); 
        params.put("totalTransferencia", closing.getTotalTransfer()); 
        params.put("totalTarjeta", closing.getTotalCard()); 
        params.put("deposito", closing.getDeposit()); 
        params.put("diferencia", closing.getDifference()); 
        params.put("cajaFinal", closing.getFinalCash()); 
        params.put("observaciones", closing.getObservations()); 
        
        try { 
            InputStream reporte = getClass().getResourceAsStream( "/com/gerardgv/posclarity/reports/cierre_caja.jrxml"); 
            JasperReport jasperReport = JasperCompileManager.compileReport(reporte); 
            JasperPrint jasperPrint = JasperFillManager.fillReport( jasperReport, params, new JREmptyDataSource()); 
            JasperViewer.viewReport(jasperPrint, false); 
        } catch (Exception e) { 
            e.printStackTrace(); 
            Throwable t = e; while (t != null) 
        { 
            System.out.println("Error Real:" + t.getMessage()); t = t.getCause(); 
        } 
        } 
    }
}
