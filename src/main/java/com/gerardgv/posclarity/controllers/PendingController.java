package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.Ui.*;
import com.gerardgv.posclarity.api.SaleApiClient;
import com.gerardgv.posclarity.models.*;
import com.gerardgv.posclarity.utils.*;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
    
    @FXML private TableView<Sale> tblVentas;
    @FXML private TableColumn<Sale,String> colFolio;
    @FXML private TableColumn<Sale,String> colCliente;
    @FXML private TableColumn<Sale,String> colFecha;
    @FXML private TableColumn<Sale,Double> colTotal;
    @FXML private TableColumn<Sale,Double> colPagado;
    @FXML private TableColumn<Sale,Double> colRestante;
    @FXML private TableColumn<Sale,String> colEstadoPago;
    @FXML private TableColumn<Sale,String> colEstadoTrabajo;
    @FXML private TableColumn<Sale,Void> colAcciones;
    @FXML private StackPane root;
    
    @FXML private PaginationController paginationController;
    private TablePagination<Sale> tablePagination;
    
    private final ObservableList<Sale> listaVentas =
        FXCollections.observableArrayList();

    private FilteredList<Sale> listaFiltrada;
    

    private final SaleApiClient saleApiClient = new SaleApiClient();
    
    public static final DateTimeFormatter FECHA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        configurarTabla();
        configurarAcciones();
        configurarPaginacion();
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
                    data.getValue().getClient()== null
                            ? "Cliente no disponible"
                            : data.getValue().getClient().getName()));
        colFecha.setCellValueFactory(data ->
            new SimpleStringProperty(
                    data.getValue().getSaleDate() == null
                            ? "-"
                            : data.getValue()
                                    .getSaleDate()
                                    .format(FECHA_FORMATTER)));
        colTotal.setCellValueFactory(data ->
            new SimpleDoubleProperty(
                    data.getValue().getFinalTotal()).asObject());
        colPagado.setCellValueFactory(data ->
            new SimpleDoubleProperty(
                    data.getValue().getPaid()).asObject());
        colRestante.setCellValueFactory(data ->
            new SimpleDoubleProperty(
                    data.getValue().getRemaining()).asObject());
        colEstadoPago.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getPaymentStatus()));
        colEstadoTrabajo.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getWorkStatus()));

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
                
        try {

            List<Sale> trabajosPorRealizar =
                saleApiClient.getSalesToDo();

            List<Sale> trabajosPorEntregar =
                saleApiClient.getSalesToDeliver();

            listaVentas.clear();
            listaVentas.addAll(trabajosPorRealizar);
            listaVentas.addAll(trabajosPorEntregar);
            actualizarContador();
            cargarResumen();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            mostrarError("No se pudieron cargar las ventas desde el API.");
        }
    }

    private void configurarBusqueda() {
        
        listaFiltrada = SearchUtils.setupSearch(
            txtBuscarVenta,
            listaVentas,
            Sale::getFolio,
            sale -> sale.getClient() != null
                    ? sale.getClient().getName()
                    : null
        );

        listaFiltrada.addListener(
            (javafx.collections.ListChangeListener<Sale>) change -> {

                tablePagination.setFilteredItems(
                        listaFiltrada
                );

                actualizarContador();
            }
        );                
    }
        
    private void entregarVenta(Sale sale){
        
        if (sale == null) {
            return;
        }

        if (!"COMPLETA".equalsIgnoreCase(sale.getPaymentStatus())) {
            mostrarError(
                "No puedes entregar una venta que todavía tiene saldo pendiente."
        );
            return;
        }

        if ("ENTREGADO".equalsIgnoreCase(sale.getWorkStatus())) {
            mostrarInfo("La venta ya fue entregada.");
            return;
        }

        boolean resultado;
        
        try{
            resultado = saleApiClient.deliverSale(sale.getId());
        }catch(IOException | InterruptedException e){
            e.printStackTrace();
            mostrarError("No se pudo entregar la venta.");
            return;
        }

        if (!resultado) {
            mostrarError("No se pudo entregar la venta.");
            return;
        }

        EventBus.publishVenta(sale.getId());
        mostrarExito("Venta entregada correctamente.");
    }
    
    private void abonarVenta(Sale sale){
        
        if (sale == null) {
            return;
        }

        if ("COMPLETA".equalsIgnoreCase(sale.getWorkStatus())) {
            mostrarInfo("La venta ya está liquidada.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/com/gerardgv/posclarity/views/Abonos.fxml"));

            Parent vista = loader.load();

            AbonosController controller = loader.getController();
            controller.setVenta(sale);

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

    private void recepcionarVenta(Sale sale){
        
        if (sale == null) {
            return;
        }

        if ("RECIBIDO".equalsIgnoreCase(sale.getWorkStatus())) {
            mostrarInfo("La venta ya fue recibida en óptica.");
            return;
        }

        if ("ENTREGADO".equalsIgnoreCase(sale.getWorkStatus())) {
            mostrarError(
                "No puedes recepcionar una venta que ya fue entregada."
            );
            return;
        }

        boolean resultado;
        
        try{
            resultado = saleApiClient.receiveSale(sale.getId());
        } catch(IOException | InterruptedException e){
            e.printStackTrace();
            mostrarError("No se pudo actualizar la recepción.");
            return;
        }

        if (!resultado) {
            mostrarError("No se pudo actualizar la recepción.");
            return;
        }

        EventBus.publishVenta(sale.getId());
        mostrarExito("Producto recibido en óptica.");        
    }
    
    private void cancelarVenta(Sale sale){
        
        if(sale == null){
            return;
        }
        
        Optional<Seller> autorizado = AuthorizationDialog.solicitarGerente();
        
        if(autorizado.isEmpty()){
            return;
        }
        
        Seller responsable = autorizado.get();
                        
        try{
            
           boolean resultado = saleApiClient.cancelSale(sale.getId());
           
           if(!resultado){
                mostrarError("No Se Puede Cancelar La Venta");
                return;
            }
           
            EventBus.publishVenta(sale.getId());
            mostrarExito(
                "Venta cancelada correctamente por "
                + responsable.getName()
                + "."
            );
            cargarVentasPendientes();
        }catch(IOException | InterruptedException e)  {
            e.printStackTrace();
            mostrarError("No Se Puede Cancelar Venta");
        }
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

        try{
            PendingReport summary =saleApiClient.getPendingSummary();
            
            lblTrabajosRealizar.setText(String.valueOf(summary.getSalesToDo()));
            lblTrabajosEntregar.setText(String.valueOf(summary.getSalesToDeliver()));
            lblSaldoRealizar.setText(MONEDA.format(summary.getBalanceToDo()));
            lblSaldoEntregar.setText(MONEDA.format(summary.getBalanceToDeliver()));
            
        } catch(IOException | InterruptedException e){
            e.printStackTrace();
            mostrarError(
                "No se pudo cargar el resumen de ventas pendientes."
            );
        }        

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
    
    private TableCell<Sale, String> crearCeldaBadgePago() {

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
    
    private TableCell<Sale, String> crearCeldaBadgeTrabajo() {

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
        protected void updateItem(Sale sale, boolean empty) {
            super.updateItem(sale, empty);

            getStyleClass().removeAll(
                    "pending-row-process",
                    "pending-row-received",
                    "pending-row-ready",
                    "pending-row-unpaid",
                    "pending-row-default"
            );

            if (empty || sale == null) {
                return;
            }

            String estadoTrabajo = sale.getWorkStatus();
            String estadoPago = sale.getPaymentStatus();

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
    
    private void configurarPaginacion() {

        tablePagination = new TablePagination<>(
        tblVentas,
        paginationController
        );
        paginationController.setItemsPerPage(6);
        paginationController.setShowItemsPerPageSelector(false); 
    }
    
}
