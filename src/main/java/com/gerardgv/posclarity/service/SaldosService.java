package com.gerardgv.posclarity.service;

import com.gerardgv.posclarity.models.*;
import java.io.InputStream;
import java.util.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import net.sf.jasperreports.engine.JREmptyDataSource;


public class SaldosService {

    public static void generatePendingReport(
            List<Sale> salesToDo, List<Sale> salesToDeliver) {
        
        try{
            
            StringBuilder deliverText  = new StringBuilder();
            StringBuilder toDoText = new StringBuilder();

            for(Sale sale : salesToDeliver){
                deliverText.append("Folio:")
                .append(sale.getFolio())
                .append(" - $")
                .append(String.format("%.2f", sale.getRemaining()))
                .append("\n");
            }

            for(Sale sale : salesToDo){
                toDoText.append("Folio:")
                .append(sale.getFolio())
                .append(" - $")
                .append(String.format("%.2f", sale.getRemaining()))
                .append("\n");
                }
                        
            double balanceToDeliver =
                salesToDeliver.stream()
                        .mapToDouble(Sale::getRemaining)
                        .sum();

            double balanceToDo =
                salesToDo.stream()
                        .mapToDouble(Sale::getRemaining)
                        .sum();
            
            Map<String,Object> params = new HashMap<>();
            
            params.put("TOTAL_ENTREGAR", salesToDeliver.size());
            params.put("TOTAL_REALIZAR", salesToDo.size());
            params.put("SALDO_ENTREGAR", balanceToDeliver);
            params.put("SALDO_REALIZAR", balanceToDo);
            params.put("ENTREGAR_TEXTO", deliverText.toString());
            params.put("REALIZAR_TEXTO", toDoText.toString());
            
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
