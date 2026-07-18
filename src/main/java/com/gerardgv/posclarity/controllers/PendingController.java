package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.Ui.*;
import com.gerardgv.posclarity.database.*;
import com.gerardgv.posclarity.models.*;
import com.gerardgv.posclarity.utils.*;
import java.net.URL;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class PendingController implements Initializable {
    
    @FXML private TextField txtBuscarVenta;
    @FXML private Label lblTrabajosRealizar;
    @FXML private Label lblTrabajosEntregar;
    @FXML private Label lblSaldoRealizar;
    @FXML private Label lblSaldoEntregar;
    @FXML private Label lblTotalRegistros;
    
    @FXML private TableView<Venta> tblVentas;
    @FXML private TableColumn<Venta,String> colFolio;
    @FXML private TableColumn<Venta,String> colCliente;
    @FXML private TableColumn<Venta,String> colFecha;
    @FXML private TableColumn<Venta,Double> colTotal;
    @FXML private TableColumn<Venta,Double> colPagado;
    @FXML private TableColumn<Venta,Double> colRestante;
    @FXML private TableColumn<Venta,String> colEstadoPago;
    @FXML private TableColumn<Venta,String> colEstadoTrabajo;
    @FXML private TableColumn<Venta,Void> colAcciones;
    @FXML private StackPane root;
    
    private final ObservableList<Venta> listaVentas =
        FXCollections.observableArrayList();

    private final FilteredList<Venta> listaFiltrada =
        new FilteredList<>(listaVentas, venta -> true);
    
    
    private final PendingDAO pendingDAO = new PendingDAO();
    private final SaleDAO saleDAO = new SaleDAO();
    
    public static final DateTimeFormatter FECHA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        configurarTabla();
        configurarAcciones();
        configurarBusqueda();
        suscribirEventos();
        cargarVentasPendientes();    
    }    

    private void configurarTabla(){

        PosTable.apply(tblVentas);
        tblVentas.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );
        configurarEstilosFilas();

    // ==============================
    // DATOS DE LAS COLUMNAS
    // ==============================

        colFolio.setCellValueFactory(data -> new SimpleStringProperty(
                    data.getValue().getFolio()));
        colCliente.setCellValueFactory(data ->
            new SimpleStringProperty(
                    data.getValue().getCliente() == null
                            ? "Cliente no disponible"
                            : data.getValue().getCliente().getNombre()));
        colFecha.setCellValueFactory(data ->
            new SimpleStringProperty(
                    data.getValue().getFecha() == null
                            ? "-"
                            : data.getValue()
                                    .getFecha()
                                    .format(FECHA_FORMATTER)));
        colTotal.setCellValueFactory(data ->
            new SimpleDoubleProperty(
                    data.getValue().getTotal()).asObject());
        colPagado.setCellValueFactory(data ->
            new SimpleDoubleProperty(
                    data.getValue().getPagado()).asObject());
        colRestante.setCellValueFactory(data ->
            new SimpleDoubleProperty(
                    data.getValue().getRestante()).asObject());
        colEstadoPago.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getEstadoPago()));
        colEstadoTrabajo.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getEstadoTrabajo()));

    // ==============================
    // FORMATO DE LAS COLUMNAS
    // ==============================

        PosTable.text(colFolio);
        PosTable.text(colCliente);
        PosTable.text(colFecha);
        PosTable.money(colTotal);
        PosTable.money(colPagado);
        PosTable.money(colRestante);
        configurarBadgesEstado();
        
        // ==============================
        // ALINEACIÓN
        // ==============================

        colFolio.setStyle("-fx-alignment: CENTER;");
        colEstadoPago.setStyle("-fx-alignment: CENTER;");
        colEstadoTrabajo.setStyle("-fx-alignment: CENTER;");
        colAcciones.setStyle("-fx-alignment: CENTER;");

        colFolio.getStyleClass().add("center-column");
        colEstadoPago.getStyleClass().add("center-column");
        colEstadoTrabajo.getStyleClass().add("center-column");
        colAcciones.getStyleClass().add("center-column");

    // ==============================
    // COMPORTAMIENTO GENERAL
    // ==============================

        PosTable.placeholder(
            tblVentas,
            "No hay ventas pendientes",
            "Las ventas activas aparecerán aquí",
            "fas-clock"
        );
    }

    private void cargarVentasPendientes() {
                
        listaVentas.setAll(
            pendingDAO.obtenerTrabajosActivos()
        );
        actualizarContador();
        cargarResumen();
    }

    private void configurarBusqueda() {
        
        txtBuscarVenta.textProperty().addListener((obs, oldValue, newValue) -> {

        String filtro = newValue == null
                ? ""
                : newValue.trim().toLowerCase();

        listaFiltrada.setPredicate(venta -> {

            if (filtro.isBlank()) {
                return true;
            }

            return venta.getFolio().toLowerCase().contains(filtro)
                    || venta.getCliente().getNombre().toLowerCase().contains(filtro);
        });

        actualizarContador();
    });

    tblVentas.setItems(listaFiltrada);
    }
        
    private void entregarVenta(Venta venta){
        
        if (venta == null) {
            return;
        }

        if (!"COMPLETA".equalsIgnoreCase(venta.getEstadoPago())) {
            mostrarError(
                "No puedes entregar una venta que todavía tiene saldo pendiente."
        );
            return;
        }

        if ("ENTREGADO".equalsIgnoreCase(venta.getEstadoTrabajo())) {
            mostrarInfo("La venta ya fue entregada.");
            return;
        }

        boolean resultado = pendingDAO.entregarVenta(venta.getId());

        if (!resultado) {
            mostrarError("No se pudo entregar la venta.");
            return;
        }

        EventBus.publishVenta(venta.getId());
        mostrarExito("Venta entregada correctamente.");
    }
    
    private void abonarVenta(Venta venta){
        
        if (venta == null) {
            return;
        }

        if ("COMPLETA".equalsIgnoreCase(venta.getEstadoPago())) {
            mostrarInfo("La venta ya está liquidada.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/com/gerardgv/posclarity/views/Abonos.fxml"));

            Parent vista = loader.load();

            AbonosController controller = loader.getController();
            controller.setVenta(venta);

            Stage stage = new Stage();
            stage.setScene(new Scene(vista));
            stage.setTitle("Abonos");
            stage.setResizable(false);

            stage.setOnHidden(event ->cargarVentasPendientes());

            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarError("No se pudo abrir la ventana de abonos.");
        }
    }

    private void recepcionarVenta(Venta venta){
        
        if (venta == null) {
            return;
        }

        if ("RECIBIDO".equalsIgnoreCase(venta.getEstadoTrabajo())) {
            mostrarInfo("La venta ya fue recibida en óptica.");
            return;
        }

        if ("ENTREGADO".equalsIgnoreCase(venta.getEstadoTrabajo())) {
            mostrarError(
                "No puedes recepcionar una venta que ya fue entregada."
            );
            return;
        }

        boolean resultado = pendingDAO.recepcionarVenta(venta.getId());

        if (!resultado) {
            mostrarError("No se pudo actualizar la recepción.");
            return;
        }

        EventBus.publishVenta(venta.getId());
        mostrarExito("Producto recibido en óptica.");        
    }
    
    private void cancelarVenta(Venta venta){
        
        if(venta == null){
            return;
        }
        
        Optional<Empleados> autorizado = AuthorizationDialog.solicitarGerente();
        
        if(autorizado.isEmpty()){
            return;
        }
        
        Empleados responsable = autorizado.get();
        
        boolean resultado = saleDAO.cancelarVenta(venta.getId());
        
        if(!resultado){
            mostrarError("No se Puede Cancelar Venta");
            return;
        }
        
        EventBus.publishVenta(venta.getId());
        
        mostrarExito("Venta cancelada correctamente por "
            + responsable.getNombre()
            + ".");       
    }
    
    private void mostrarError(String mensaje) {
        PosNotification.error(root, "Atención", mensaje);
    }

    private void mostrarExito(String mensaje) {
        PosNotification.success(root, "Listo", mensaje);
    }

    private void mostrarInfo(String mensaje) {
        PosNotification.info(root, "Información", mensaje);
    }   

    private void configurarAcciones() {
        
        colAcciones.setCellFactory(param -> TableUtils.createVentaAcions(
                this::abonarVenta,
                this::recepcionarVenta,
                this::entregarVenta,
                this::cancelarVenta)
        );
        
    }

    private void suscribirEventos() {

        EventBus.subscribeVenta(idVenta ->
            javafx.application.Platform.runLater(
                    this::cargarVentasPendientes
            )
    );
        
    }
    
    private static final NumberFormat MONEDA =
        NumberFormat.getCurrencyInstance(
                new Locale("es", "MX")
        );
    
    private void cargarResumen() {

        ReportPendientes resumen = pendingDAO.obtenerResumenPendientes();

        lblTrabajosRealizar.setText(String.valueOf(resumen.getTrabajosRealizar()));
        lblTrabajosEntregar.setText(String.valueOf(resumen.getTrabajosEntregar()));
        lblSaldoRealizar.setText(MONEDA.format(resumen.getSaldoRealizar()));
        lblSaldoEntregar.setText(MONEDA.format(resumen.getSaldoEntregar()));

    }

    private void actualizarContador() {
        
        int total = listaFiltrada.size();
        
        lblTotalRegistros.setText(total == 1 
                ? "1 Registro" : total + "registros");
        
    }
    
    private void configurarBadgesEstado() {

        colEstadoPago.setCellFactory(column -> crearCeldaBadgePago());

        colEstadoTrabajo.setCellFactory(column -> crearCeldaBadgeTrabajo());
    }
    
    private TableCell<Venta, String> crearCeldaBadgePago() {

    return new TableCell<>() {

        private final Label badge = new Label();

        {
            badge.getStyleClass().add("pending-status-badge");
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            setAlignment(javafx.geometry.Pos.CENTER);
        }

        @Override
        protected void updateItem(String estado, boolean empty) {
            super.updateItem(estado, empty);

            if (empty || estado == null || estado.isBlank()) {
                setGraphic(null);
                return;
            }

            badge.setText(formatearEstado(estado));

            badge.getStyleClass().removeAll(
                    "pending-status-pending",
                    "pending-status-complete",
                    "pending-status-process",
                    "pending-status-received",
                    "pending-status-ready",
                    "pending-status-delivered",
                    "pending-status-cancelled",
                    "pending-status-default"
            );

            switch (estado.toUpperCase()) {

                case "PENDIENTE" ->
                    badge.getStyleClass().add(
                            "pending-status-pending"
                    );

                case "COMPLETA" ->
                    badge.getStyleClass().add(
                            "pending-status-complete"
                    );

                default ->
                    badge.getStyleClass().add(
                            "pending-status-default"
                    );
            }
            setGraphic(badge);
            }
        };
    }
    
    private TableCell<Venta, String> crearCeldaBadgeTrabajo() {

        return new TableCell<>() {

            private final Label badge = new Label();
            {
                badge.getStyleClass().add("pending-status-badge");
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setAlignment(javafx.geometry.Pos.CENTER);
            }

        @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);

                if (empty || estado == null || estado.isBlank()) {
                    setGraphic(null);
                    return;
                }

                badge.setText(formatearEstado(estado));

                badge.getStyleClass().removeAll(
                    "pending-status-pending",
                    "pending-status-complete",
                    "pending-status-process",
                    "pending-status-received",
                    "pending-status-ready",
                    "pending-status-delivered",
                    "pending-status-cancelled",
                    "pending-status-default"
                );

            switch (estado.toUpperCase()) {

                case "PROCESO" ->
                    badge.getStyleClass().add(
                            "pending-status-process");

                case "RECIBIDO" ->
                    badge.getStyleClass().add(
                            "pending-status-received");

                case "LISTO" ->
                    badge.getStyleClass().add(
                            "pending-status-ready");

                case "ENTREGADO" ->
                    badge.getStyleClass().add(
                            "pending-status-delivered");

                case "CANCELADO" ->
                    badge.getStyleClass().add(
                            "pending-status-cancelled");

                default ->
                    badge.getStyleClass().add(
                            "pending-status-default");
            }
            setGraphic(badge);
            }
        };
    }
    
    private String formatearEstado(String estado) {

        if (estado == null || estado.isBlank()) {
            return "";
        }

        String texto = estado
            .trim()
            .toLowerCase()
            .replace("_", " ");

        return Character.toUpperCase(texto.charAt(0))
            + texto.substring(1);
    }

    private void configurarEstilosFilas() {
        
        tblVentas.setRowFactory(table -> new TableRow<>() {

        @Override
        protected void updateItem(Venta venta, boolean empty) {
            super.updateItem(venta, empty);

            getStyleClass().removeAll(
                    "pending-row-process",
                    "pending-row-received",
                    "pending-row-ready",
                    "pending-row-unpaid",
                    "pending-row-default"
            );

            if (empty || venta == null) {
                return;
            }

            String estadoTrabajo = venta.getEstadoTrabajo();
            String estadoPago = venta.getEstadoPago();

            if ("PENDIENTE".equalsIgnoreCase(estadoPago)
                    && ("RECIBIDO".equalsIgnoreCase(estadoTrabajo)
                    || "LISTO".equalsIgnoreCase(estadoTrabajo))) {

                getStyleClass().add("pending-row-unpaid");
                return;
            }

            if (estadoTrabajo == null) {
                getStyleClass().add("pending-row-default");
                return;
            }

            switch (estadoTrabajo.toUpperCase()) {

                case "PROCESO" ->
                    getStyleClass().add("pending-row-process");

                case "RECIBIDO" ->
                    getStyleClass().add("pending-row-received");

                case "LISTO" ->
                    getStyleClass().add("pending-row-ready");

                default ->
                    getStyleClass().add("pending-row-default");
            }
        }
    });
    }
    
}
