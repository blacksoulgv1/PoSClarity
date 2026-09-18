package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.Ui.PosNotification;
import com.gerardgv.posclarity.Ui.PosTable;
import com.gerardgv.posclarity.api.PaymentApiClient;
import com.gerardgv.posclarity.api.SaleApiClient;
import com.gerardgv.posclarity.models.*;
import com.gerardgv.posclarity.utils.EventBus;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;


public class AbonosController implements Initializable {
    
    @FXML private StackPane root;

    @FXML private Label lblFolio;
    @FXML private Label lblCliente;    
    @FXML private Label lblTotal;
    @FXML private Label lblPagado;
    @FXML private Label lblRestante;
    @FXML private Label lblEstado;
    @FXML private Label lblTotalPagos;
    
    @FXML private TextField txtMonto;
    @FXML private ComboBox<String> cbMetodo;
    @FXML private Button btnAbonar;
    

    @FXML private TableView<Payment> tblPagos;
    @FXML private TableColumn<Payment,String> colFecha;
    @FXML private TableColumn<Payment,String> colMetodo;
    @FXML private TableColumn<Payment,Double> colMonto;

    private Sale sale;
    private final PaymentApiClient paymentApiClient = new PaymentApiClient();
    private final SaleApiClient saleApiClient = new SaleApiClient();
    
    private final ObservableList<Payment> listaPagos = FXCollections.observableArrayList();
    
    private static final DateTimeFormatter FECHA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final NumberFormat MONEDA = NumberFormat.getCurrencyInstance(new Locale("es","MX"));
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        configuraMetodosPago();
        configurarTabla();
        configurarCampoMonto();       
                
    }
    
    public void setVenta(Sale sale){
        this.sale = sale;
        cargarDatos();
    }

    private void configurarTabla() {
        
        PosTable.apply(tblPagos);
        
        colFecha.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getDate()== null ? "-" : data.getValue().getFecha().format(FECHA_FORMATTER)));
        colMetodo.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getMetodo() == null ? "-": data.getValue().getMetodo()));
        colMonto.setCellValueFactory(data ->
            new SimpleDoubleProperty(data.getValue().getMonto()).asObject());
        
        PosTable.text(colFecha);
        PosTable.text(colMetodo);
        PosTable.money(colMonto);
        
        tblPagos.setItems(listaPagos);
        
        PosTable.placeholder(tblPagos, 
                "Sin Pagos Registrados", "Los Abonos Aparecerán en el Historial", "fas-receipt");
        
    }

    private void cargarDatos() {
        
        if(sale == null){
            return;
        }
        lblFolio.setText(textoSeguro(sale.getFolio(),"-"));
        
        lblCliente.setText(
            sale.getClient() == null ? "Cliente no disponible"
            : textoSeguro(sale.getClient().getName(), "Cliente no Disponible"));
        lblTotal.setText(MONEDA.format(sale.getFinalTotal()));
        lblPagado.setText(MONEDA.format(sale.getPaid()));
        lblRestante.setText(MONEDA.format(Math.max(sale.getRemaining(), 0)));

        cargarHistorial();
        actualizarEstadoVisual();
        actualizarDisponibilidadAbono();
    }
    
    @FXML
    private void handleAbonar(){

        if(sale  == null){
            mostrarError("No Se Encontró la Venta Seleccionada.");
            return;
        }
        
        String textoMonto = txtMonto.getText().trim();
        
        if(textoMonto.isEmpty()){
            mostrarAdvertencia("Ingresa el Monto del Abono.");
            txtMonto.requestFocus();
            return;
        }
        
        double monto;
        
        try{
            monto = Double.parseDouble(textoMonto);
        }catch(NumberFormatException ex){
            mostrarAdvertencia("El Monto Ingresado No Es Válido.");
            txtMonto.requestFocus();
            return;
        }
        
        if(monto <= 0){
            mostrarAdvertencia("El Abono Debe Ser Mayor a Cero.");
            txtMonto.requestFocus();
            return;
        }
        
        double saldoActual = Math.max(sale.getRemaining(), 0);
        
        if(saldoActual <= 0.009){
            mostrarInfo("La Venta Ya Está Liquidada.");
            actualizarDisponibilidadAbono();
            return;            
        }
        
        if(monto > saldoActual + 0.009){
            mostrarAdvertencia("El Abono No Puede Superar El Saldo De" + MONEDA.format(saldoActual)+".");
            txtMonto.requestFocus();
            return;
        }
        
        String metodo = cbMetodo.getValue();
        
        if(metodo == null || metodo.isBlank()){
            mostrarAdvertencia("Selecciona Un Método de Pago.");
            return;
        }
        
        try {
            
            paymentApiClient.create(
                sale.getId(),
                metodo,
                monto,
                null
            );
        } catch (IOException | InterruptedException ex) {
            mostrarError("No Se Pudo Registrar El Abono.");
            return;
        }
        
        try{
            sale = saleApiClient.getById(sale.getId());
        } catch(IOException | InterruptedException ex){
            mostrarError("El Abono Se Registró, Pero No Se Pudo Actualizar La Venta.");
            return;
        }
        /*
        TicketService ticketService = new TicketService();
        
        ticketService.imprimirTicketAbono(sale, monto, metodo);
        */
        EventBus.publishVenta(sale.getId());
        
        boolean liquidada = sale.getRemaining() <= 0.009;
        
        mostrarExito(liquidada ? " La Venta Quedó Completamente Liquidada." : "Abono Registrado Correctamente.");
        
        txtMonto.clear();
        txtMonto.requestFocus();
        cargarDatos();
    }

    private void mostrarExito(String mensaje){
        PosNotification.success(root, "Listo", mensaje);
    }
    
    private void mostrarError(String mensaje){
        PosNotification.error(root, "Atención", mensaje);
    }
    
    private void mostrarAdvertencia(String mensaje){
        PosNotification.warning(root, "Atención", mensaje);
    }
    
    private void mostrarInfo(String mensaje){
        PosNotification.info(root, "Atención", mensaje);
    }

    private void configuraMetodosPago() {
        
        cbMetodo.getItems().addAll("EFECTIVO","TARJETA","TRANSFERENCIA");
        cbMetodo.setValue("EFECTIVO");
        
    }

    private void configurarCampoMonto() {
        
        txtMonto.setTextFormatter(new TextFormatter<>(change -> {
            String nuevoTexto = change.getControlNewText();
            
            if(nuevoTexto.matches("\\d{0,8}([.]\\d{0,2})?")){
                return change;
            }
            return null;
        }));
        
    }
    
    private String textoSeguro(String valor, String valorPredeterminado){
        return valor == null || valor.isBlank() ? valorPredeterminado : valor;
    }

    private void cargarHistorial() {
        
        try {

            listaPagos.setAll(paymentApiClient.getBySale(sale.getId()));
            int totalPagos = listaPagos.size();
            lblTotalPagos.setText(
                totalPagos == 1
                        ? "1 pago"
                        : totalPagos + " pagos"
            );
        } catch (IOException | InterruptedException ex) {
            listaPagos.clear();
            lblTotalPagos.setText("0 pagos");
            mostrarError("No Se Pudo Cargar El Historial De Pagos.");
        }        
    }

    private void actualizarEstadoVisual() {
        
        lblEstado.getStyleClass().removeAll("abonos-status-pending","abonos-status-complete");
        
        boolean liquidada = sale.getRemaining() <= 0.009 ||
                "completa".equalsIgnoreCase(sale.getPaymentStatus());
        
        if(liquidada){
            lblEstado.setText("Liquidada");
            lblEstado.getStyleClass().add("abonos-status-complete");
        } else {
            lblEstado.setText("Pendiente");
            lblEstado.getStyleClass().add("abonos-status-pending");
        }
        
    }

    private void actualizarDisponibilidadAbono() {
        
        boolean liquidada = sale.getRemaining() <= 0.009 || 
                "completa".equalsIgnoreCase(sale.getPaymentStatus());
        
        txtMonto.setDisable(liquidada);
        cbMetodo.setDisable(liquidada);
        btnAbonar.setDisable(liquidada);
        
        if(liquidada){
            btnAbonar.setText("Venta Liquidada");
        } else {
            btnAbonar.setText("Registrar Abono");
        }        
    }

}