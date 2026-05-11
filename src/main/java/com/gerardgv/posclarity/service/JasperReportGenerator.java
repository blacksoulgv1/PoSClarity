package com.gerardgv.posclarity.service;

import com.gerardgv.posclarity.models.*;
import java.io.InputStream;
import java.util.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;


public class JasperReportGenerator {

    public static void generarReportePendientes(
            List<Venta> realizar, List<Venta> entregar) {
        
        try{
            
            List<ReportePendienteRow> rows = new ArrayList<>();
            
            int max = Math.max(realizar.size(),entregar.size());
            
            for(int i = 0; i < max; i++){
                
                String textoEntregar = "";
                String textoRealizar = "";
                
                if(i < entregar.size()){
                    
                    Venta v = entregar.get(i);                    
                    textoEntregar = "Nota #" + v.getId() + " - $" + String.format("%.2f", v.getRestante());
                }
                
                if( i < realizar.size()){
                    
                    Venta v = realizar.get(i);                    
                    textoRealizar = "Nota #" + v.getId() + " - $" + String.format("%.2f", v.getRestante());
                }
                rows.add(new ReportePendienteRow(textoEntregar,textoRealizar));
            }
            
            double saldoEntregar = entregar.stream().mapToDouble(Venta::getRestante).sum();
            double saldoRealizar = realizar.stream().mapToDouble(Venta::getRestante).sum();
            
            Map<String,Object> params = new HashMap<>();
            
            params.put("TOTAL_ENTREGAR", entregar.size());
            params.put("TOTAL_REALIZAR", realizar.size());
            params.put("SALDO_ENTREGAR", saldoEntregar);
            params.put("SALDO_REALIZAR", saldoRealizar);
            
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(rows);
            
            InputStream reportStream = JasperReportGenerator.class.getResourceAsStream("/com/gerardgv/posclarity/reports/ReportePendientes.jrxml");
            
            JasperReport report = JasperCompileManager.compileReport(reportStream);
            
            JasperPrint print = JasperFillManager.fillReport(report, params,ds);
            
            JasperViewer.viewReport(print,false);    
            
        } catch(Exception e){
            e.printStackTrace();
        }
    }   
    
}
