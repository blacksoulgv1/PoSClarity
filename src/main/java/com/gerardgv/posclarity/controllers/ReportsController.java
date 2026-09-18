package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.api.*;
import com.gerardgv.posclarity.api.dto.report.ReportSalesSummaryResponse;
import com.gerardgv.posclarity.charts.*;
import com.gerardgv.posclarity.models.*;
import com.gerardgv.posclarity.service.*;
import com.gerardgv.posclarity.utils.Session;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import javafx.fxml.*;
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
    
    private ReportApiClient reportApiClient = new ReportApiClient();
    private SaleApiClient saleApiClient = new SaleApiClient();
    private CashClosingApiClient  cashClosingApiClient = new CashClosingApiClient();
    private GoalApiClient goalApiClient = new GoalApiClient();
    
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

            List<Sale> salesToDo = saleApiClient.getSalesToDo();

            List<Sale> salesToDeliver = saleApiClient.getSalesToDeliver();

            if(salesToDo.isEmpty() && salesToDeliver.isEmpty()){
                mostrarAlerta("No hay trabajos pendientes");
                return;
            }

            SaldosService.generatePendingReport(
                salesToDo,
                salesToDeliver
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
    private void openCierreDia() {

        boolean existsToday;

        try {
            existsToday = cashClosingApiClient.existsToday();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            mostrarAlerta(
                "No fue posible verificar el cierre del día"
            );
            return;
        }

        if (existsToday) {

            Alert alert =
                new Alert(Alert.AlertType.CONFIRMATION);

            alert.setTitle("Cierre existente");
            alert.setHeaderText("Ya existe un cierre para hoy");
            alert.setContentText(
                "Aceptar = Recalcular cierre\n"
                + "Cancelar = Ver cierre existente"
            );

            Optional<ButtonType> result =
                alert.showAndWait();

        if (result.isPresent()
                && result.get() == ButtonType.OK) {

            // No eliminamos el cierre.
            // saveClosing() actualizará el cierre existente.

        } else {

            CashClosing closing;

            try {
                closing =
                        cashClosingApiClient.getLatestClosing();

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                mostrarAlerta(
                        "No fue posible obtener el cierre existente"
                );
                return;
            }

                if (closing == null) {
                    mostrarAlerta(
                        "No fue posible obtener el cierre existente"
                    );
                    return;
                }

                cierreService.generarReporte(closing);
                return;
            }
        }

        CashClosing closing;

        try {
            closing =
                cashClosingApiClient.getTodayClosing();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            mostrarAlerta(
                "No fue posible obtener la información del cierre"
            );
            return;
        }

        if (closing == null) {
            mostrarAlerta(
                "No es Posible obtener informacion del Cierre"
            );
            return;
        }

        double CAJA_OBJETIVO = 350.00;

        double efectivoDisponible =
            closing.getInitialCash()
            + closing.getTotalCash();

        double deposito =
            Math.floor(
                    (efectivoDisponible - CAJA_OBJETIVO) / 50.0
            ) * 50.0;

        if (deposito < 0) {
            deposito = 0;
        }

        double cajaFinal =
            efectivoDisponible - deposito;

        TextInputDialog obsDialog =
            new TextInputDialog();

        obsDialog.setTitle("Cierre de Caja");
        obsDialog.setHeaderText("Observaciones");
        obsDialog.setContentText("Observaciones:");

        String observaciones =
            obsDialog.showAndWait().orElse("");

        closing.setDeposit(deposito);
        closing.setFinalCash(cajaFinal);
        closing.setDifference(0);
        closing.setObservations(observaciones);

        try {

            CashClosing savedClosing =
                cashClosingApiClient.saveClosing(closing);

            if (savedClosing == null) {
                mostrarAlerta(
                    "No fue posible guardar el cierre"
                );
                return;
            }
            
            cierreService.generarReporte(savedClosing);

        } catch (IOException | InterruptedException e) {

            e.printStackTrace();

            mostrarAlerta(
                "No fue posible guardar el cierre"
            );

            return;
        }

        

        Alert alert =
            new Alert(Alert.AlertType.INFORMATION);

        alert.setHeaderText(null);
        alert.setContentText(
            "Cierre generado correctamente"
        );

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
        
        lblSucursalActual.setText(Session.getSucursal().getName());
        lblPeriodo.setText(hoy.getMonth().name() + " " + anio);
        
        try{
            
            ReportSalesSummaryResponse response =
                reportApiClient.getMonthlySalesSummary(
                        mes,
                        anio
                );
            
            SalesReport r = new SalesReport(
                response.getTotal() != null
                        ? response.getTotal().doubleValue()
                        : 0.0,
                response.getPaid() != null
                        ? response.getPaid().doubleValue()
                        : 0.0,
                response.getPending() != null
                        ? response.getPending().doubleValue()
                        : 0.0
            );
            
            lblTotalVentas.setText("$" + String.format("%.2f", r.getTotal()));
            lblPagado.setText("$" + String.format("%.2f", r.getPaid()));
            lblPendiente.setText("$" + String.format("%.2f", r.getPending()));
            
            Map<Integer,Double> ventaspordia = 
                    reportApiClient.getDailySales(mes,anio);
            
            ventasChartBuilder.build(chartVentas, ventaspordia, hoy);
            
            Goal goal = goalApiClient.getMonthly(
                    idSucursal, mes, anio);
            
            double meta = goal != null ? goal.getAmount() : 0.0;            
            MetaChartBuilder.build(chartMeta, r.getTotal(), meta);
            cargarResumenPendientes();
            
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void cargarResumenPendientes() {
        
        try{
            PendingReport report = saleApiClient.getPendingSummary();
            lblTrabajosRealizar.setText(report.getSalesToDo()+ " trabajos");
            lblTrabajosEntregar.setText(report.getSalesToDeliver()+ " trabajos");
            lblSaldoRealizar.setText("$" + String.format("%.2f", report.getBalanceToDo()));
            lblSaldoEntregar.setText("$" + String.format("%.2f", report.getBalanceToDeliver()));
        }catch(Exception e){
            
           e.printStackTrace();
            lblTrabajosRealizar.setText("0 trabajos");
            lblTrabajosEntregar.setText("0 trabajos");
            lblSaldoRealizar.setText("$0.00");
            lblSaldoEntregar.setText("$0.00"); 
        }

    } 
    
}    

