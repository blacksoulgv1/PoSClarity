package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.charts.MetaChartBuilder;
import com.gerardgv.posclarity.charts.ventasChartBuilder;
import com.gerardgv.posclarity.database.*;
import com.gerardgv.posclarity.models.*;
import com.gerardgv.posclarity.service.CierreCajaService;
import com.gerardgv.posclarity.service.SaldosService;
import com.gerardgv.posclarity.utils.Session;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.stage.Stage;


public class ReportsController implements Initializable {

    @FXML private Label lblTotalVentas;
    @FXML private Label lblTrabajosRealizar;
    @FXML private Label lblTrabajosEntregar;
    @FXML private Label lblSaldoRealizar;
    @FXML private Label lblSaldoEntregar;
    @FXML private Label lblPagado;
    @FXML private Label lblPendiente;
    @FXML private Label lblSucursalActual;
    @FXML private Label lblPeriodo;
    @FXML private LineChart<String,Number> chartVentas;
    @FXML private PieChart chartMeta;
    
    private SaleDAO saleDAO = new SaleDAO();
    private MetaDAO metaDAO = new MetaDAO();
    private PendingDAO pendingDAO = new PendingDAO();
    private CierrecajaDAO cierreDAO = new CierrecajaDAO();
    private CierreCajaService cierreService = new CierreCajaService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarDashboardActual();
    }


    @FXML
    private void openVentasMes(){
        System.out.println("Ventas Mes");
       // abrirModal("/views/reportes/VentasMes.fxml", "Ventas por Mes");
    }
    
    @FXML
    private void generarReportePendientes(){
        
        try{

        List<Venta> realizar = pendingDAO.obtenerTrabajosPorRealizar();
        List<Venta> entregar = pendingDAO.obtenerTrabajosPorEntregar();

        if(realizar.isEmpty() && entregar.isEmpty()){
            mostrarAlerta("No hay trabajos pendientes");
            return;
        }

        SaldosService.generarReportePendientes(
                realizar,
                entregar
        );

        mostrarAlerta("Reporte generado correctamente");

    }catch(Exception e){
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Error al generar reporte");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
        
    }

    @FXML
    private void openCierreDia(){
        
        if (cierreDAO.existeCierreHoy(Session.getSucursal().getId())){
            
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Cierre existente");
            alert.setHeaderText("Ya existe un cierre para hoy");
            alert.setContentText("Aceptar = Recalcular cierre\n" +
                                    "Cancelar = Ver cierre existente");
            
            Optional<ButtonType> result = alert.showAndWait();
            
            if(result.isPresent() && result.get() == ButtonType.OK){
                cierreDAO.eliminarCierreHoy(Session.getSucursal().getId());
            } else {
                CierreCaja cierre = cierreDAO.obtenerUltimoCierre();
                cierreService.generarReporte(cierre);
                return;
            }
            
        }
        
        CierreCaja cierre = cierreDAO.obtenerCierreDelDia();
        
        if(cierre == null){
            mostrarAlerta("No es Posible obtener informacion del Cierre");
            return;
        }
        
        double CAJA_OBJETIVO = 350.00;
        
        double efectivoDisponible = cierre.getCajaInicial() + cierre.getTotalEfectivo();
        double deposito = Math.floor((efectivoDisponible - CAJA_OBJETIVO) / 50.0) * 50.0;
        
        if(deposito < 0){
            deposito = 0;
        }
        
        double cajaFinal = efectivoDisponible - deposito;
        
        TextInputDialog obsDialog = new TextInputDialog();
        obsDialog.setTitle("Cierre de Caja");
        obsDialog.setHeaderText("Observaciones");
        obsDialog.setContentText("Observaciones:");

        String observaciones = obsDialog.showAndWait().orElse("");

        cierre.setDeposito(deposito);
        cierre.setCajaFinal(cajaFinal);        
        cierre.setDiferencia(0);
        cierre.setObservaciones(observaciones);

        boolean guardado = cierreDAO.guardarCierre(cierre);

        if(!guardado){
            mostrarAlerta("No fue posible guardar el cierre");
            return;
        }

        cierreService.generarReporte(cierre);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText("Cierre generado correctamente");
        alert.showAndWait();
    }

    @FXML
    private void openSaldos(){
        System.out.println("Saldos");
        //abrirModal("/views/reportes/Saldos.fxml", "Saldos");
    }

    @FXML
    private void openInventario(){
        System.out.println("Inventario");
        //abrirModal("/views/reportes/Inventario.fxml", "Inventario");
    }
    @FXML
    private void openMetas(){
    abrirModal("/com/gerardgv/posclarity/views/Metas.fxml","Metas por Sucursal");
    }

    private void abrirModal(String ruta, String titulo){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.show();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    private void mostrarAlerta(String msg){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void cargarDashboardActual() {
        
        int idSucursal = Session.getSucursal().getId();
        
        LocalDate hoy = LocalDate.now();
        
        int mes = hoy.getMonthValue();
        int anio = hoy.getYear();
        
        lblSucursalActual.setText(Session.getSucursal().getSucursal());
        lblPeriodo.setText(hoy.getMonth().name() + " " + anio);
        
        ReporteVentas r = saleDAO.obtenerReporteMensual(idSucursal, mes, anio);
        
        lblTotalVentas.setText("$" + String.format("%.2f", r.getTotal()));
        lblPagado.setText("$" + String.format("%.2f", r.getPagado()));
        lblPendiente.setText("$" + String.format("%.2f", r.getPendiente()));
        
        Map<Integer,Double> ventaspordia = saleDAO.obtenerVentasporDia(idSucursal, mes, anio);
        ventasChartBuilder.build(chartVentas, ventaspordia, hoy);        
        double meta = metaDAO.obtenerMetaMensual(idSucursal, mes, anio);        
        MetaChartBuilder.build(chartMeta, r.getTotal(), meta);
        cargarResumenPendientes();       
        
    }

    private void cargarResumenPendientes() {
        
        ReportPendientes r = pendingDAO.obtenerResumenPendientes();

        lblTrabajosRealizar.setText(r.getTrabajosRealizar() + " trabajos");
        lblTrabajosEntregar.setText(r.getTrabajosEntregar() + " trabajos");
        lblSaldoRealizar.setText("$" + String.format("%.2f", r.getSaldoRealizar()));
        lblSaldoEntregar.setText("$" + String.format("%.2f", r.getSaldoEntregar()));
    }    
}    
