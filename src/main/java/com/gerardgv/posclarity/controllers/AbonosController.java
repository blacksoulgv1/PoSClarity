package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.Ui.PosNotification;
import com.gerardgv.posclarity.Ui.PosTable;
import com.gerardgv.posclarity.database.SaleDAO;
import com.gerardgv.posclarity.models.*;
import com.gerardgv.posclarity.service.TicketService;
import com.gerardgv.posclarity.utils.EventBus;
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
    

    @FXML private TableView<Pago> tblPagos;
    @FXML private TableColumn<Pago,String> colFecha;
    @FXML private TableColumn<Pago,String> colMetodo;
    @FXML private TableColumn<Pago,Double> colMonto;

    private Venta venta;
    private final SaleDAO saleDAO = new SaleDAO();
    private final ObservableList<Pago> listaPagos = FXCollections.observableArrayList();
    
    private static final DateTimeFormatter FECHA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final NumberFormat MONEDA = NumberFormat.getCurrencyInstance(new Locale("es","MX"));
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        configuraMetodosPago();
        configurarTabla();
        configurarCampoMonto();       
                
    }
    
    public void setVenta(Venta v){
        this.venta = v;
        cargarDatos();
    }

    private void configurarTabla() {
        
        PosTable.apply(tblPagos);
        
        colFecha.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getFecha() == null ? "-" : data.getValue().getFecha().format(FECHA_FORMATTER)));
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
        
        if(venta == null){
            return;
        }
        lblFolio.setText(textoSeguro(venta.getFolio(),"-"));
        
        lblCliente.setText(
            venta.getCliente() == null ? "Cliente no disponible"
            : textoSeguro(venta.getCliente().getNombre(), "Cliente no Disponible"));
        lblTotal.setText(MONEDA.format(venta.getTotal()));
        lblPagado.setText(MONEDA.format(venta.getPagado()));
        lblRestante.setText(MONEDA.format(Math.max(venta.getRestante(), 0)));

        cargarHistorial();
        actualizarEstadoVisual();
        actualizarDisponibilidadAbono();
    }
    
    @FXML
    private void handleAbonar(){

        if(venta == null){
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
        
        double saldoActual = Math.max(venta.getRestante(), 0);
        
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
        
        boolean registrado = saleDAO.registrarAbono(venta.getId(), metodo, monto);
        
        if(!registrado){
            mostrarError("No Se Pudo Registrar El Abono");
            return;
        }
        
        actualizarVentaDespuesDelAbono(monto);
        
        TicketService ticketService = new TicketService();
        
        ticketService.imprimirTicketAbono(venta, monto, metodo);
        
        EventBus.publishVenta(venta.getId());
        
        boolean liquidada = venta.getRestante() <= 0.009;
        
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
        
        listaPagos.setAll(saleDAO.obtenerPagosPorVenta(venta.getId()));
        
        int totalPagos = listaPagos.size();
        
        lblTotalPagos.setText(totalPagos == 1 ? "1 pago" : totalPagos + "pagos" );
        
    }

    private void actualizarEstadoVisual() {
        
        lblEstado.getStyleClass().removeAll("abonos-status-pending","abonos-status-complete");
        
        boolean liquidada = venta.getRestante() <= 0.009 || "completa".equalsIgnoreCase(venta.getEstadoPago());
        
        if(liquidada){
            lblEstado.setText("Liquidada");
            lblEstado.getStyleClass().add("abonos-status-complete");
        } else {
            lblEstado.setText("Pendiente");
            lblEstado.getStyleClass().add("abonos-status-pending");
        }
        
    }

    private void actualizarDisponibilidadAbono() {
        
        boolean liquidada = venta.getRestante() <= 0.009 || "completa".equalsIgnoreCase(venta.getEstadoPago());
        
        txtMonto.setDisable(liquidada);
        cbMetodo.setDisable(liquidada);
        btnAbonar.setDisable(liquidada);
        
        if(liquidada){
            btnAbonar.setText("Venta Liquidada");
        } else {
            btnAbonar.setText("Registrar Abono");
        }        
    }

    private void actualizarVentaDespuesDelAbono(double monto) {
        
        double nuevoPagado = venta.getPagado() + monto;        
        double nuevoRestante = Math.max(venta.getTotal() - nuevoPagado, 0);
        
        venta.setPagado(nuevoPagado);
        venta.setRestante(nuevoRestante);
        
        if(nuevoRestante <= 0.009){
            venta.setEstadoPago("COMPLETA");
        }
        
    }
    
    
}