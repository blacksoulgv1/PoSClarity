package com.gerardgv.posclarity.service;

import com.gerardgv.posclarity.models.*;
import java.io.InputStream;
import java.util.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import net.sf.jasperreports.engine.JREmptyDataSource;


public class SaldosService {

    public static void generarReportePendientes(
            List<Venta> realizar, List<Venta> entregar) {
        
        try{
            
            StringBuilder entregarTexto = new StringBuilder();
            StringBuilder realizarTexto = new StringBuilder();

            for(Venta v : entregar){
                entregarTexto.append("Folio:")
                .append(v.getFolio())
                .append(" - $")
                .append(String.format("%.2f", v.getRestante()))
                .append("\n");
            }

            for(Venta v : realizar){
                realizarTexto.append("Folio:")
                .append(v.getFolio())
                .append(" - $")
                .append(String.format("%.2f", v.getRestante()))
                .append("\n");
                }
                        
            double saldoEntregar = entregar.stream().mapToDouble(Venta::getRestante).sum();
            double saldoRealizar = realizar.stream().mapToDouble(Venta::getRestante).sum();
            
            Map<String,Object> params = new HashMap<>();
            
            params.put("TOTAL_ENTREGAR", entregar.size());
            params.put("TOTAL_REALIZAR", realizar.size());
            params.put("SALDO_ENTREGAR", saldoEntregar);
            params.put("SALDO_REALIZAR", saldoRealizar);
            params.put("ENTREGAR_TEXTO", entregarTexto.toString());
            params.put("REALIZAR_TEXTO", realizarTexto.toString());
            
            JREmptyDataSource ds = new JREmptyDataSource(1);
            
            InputStream reportStream = SaldosService.class.getResourceAsStream("/com/gerardgv/posclarity/reports/ReportePendientes.jrxml");
            
            JasperReport report = JasperCompileManager.compileReport(reportStream);
            
            JasperPrint print = JasperFillManager.fillReport(report, params,ds);
            
            JasperViewer.viewReport(print,false);    
            
        } catch(Exception e){
            e.printStackTrace();
        }
    }   
    
}
