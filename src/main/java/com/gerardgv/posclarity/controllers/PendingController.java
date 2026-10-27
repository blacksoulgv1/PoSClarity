package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.Ui.*;
import com.gerardgv.posclarity.api.SaleApiClient;
import com.gerardgv.posclarity.api.dto.common.PageResponse;
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

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PendingController implements Initializable {

    // =========================================================
    // COMPONENTES FXML
    // =========================================================

    @FXML private TextField txtBuscarVenta;

    @FXML private Label lblTrabajosRealizar;
    @FXML private Label lblTrabajosEntregar;
    @FXML private Label lblSaldoRealizar;
    @FXML private Label lblSaldoEntregar;
    @FXML private Label lblTotalRegistros;

    @FXML private TableView<Sale> tblVentas;

    @FXML private TableColumn<Sale, String> colFolio;
    @FXML private TableColumn<Sale, String> colCliente;
    @FXML private TableColumn<Sale, String> colFecha;
    @FXML private TableColumn<Sale, Double> colTotal;
    @FXML private TableColumn<Sale, Double> colPagado;
    @FXML private TableColumn<Sale, Double> colRestante;
    @FXML private TableColumn<Sale, String> colEstadoPago;
    @FXML private TableColumn<Sale, String> colEstadoTrabajo;
    @FXML private TableColumn<Sale, Void> colAcciones;

    @FXML private StackPane root;

    // El fx:id del include en el FXML debe seguir siendo "pagination".
    @FXML private PaginationController paginationController;

    // =========================================================
    // DATOS Y PAGINACIÓN
    // =========================================================

    private final SaleApiClient saleApiClient = new SaleApiClient();

    private final ObservableList<Sale> listaVentas =
            FXCollections.observableArrayList();

    private final PauseTransition debounceBusqueda =
            new PauseTransition(Duration.millis(300));

    private Task<PageResponse<Sale>> tareaPaginaActual;

    private int versionSolicitud = 0;
    private long totalRegistros = 0;

    private boolean ignorarCambioPagina = false;

    public static final DateTimeFormatter FECHA_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final NumberFormat MONEDA =
            NumberFormat.getCurrencyInstance(new Locale("es", "MX"));

    // =========================================================
    // INICIALIZACIÓN
    // =========================================================

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        configurarTabla();
        configurarAcciones();
        configurarPaginacion();
        configurarBusqueda();
        suscribirEventos();

        cargarVentasPendientes();
    }

    // =========================================================
    // CONFIGURACIÓN DE TABLA
    // =========================================================

    private void configurarTabla() {

        PosTable.apply(tblVentas);

        tblVentas.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );

        configurarEstilosFilas();

        // Datos de las columnas.

        colFolio.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getFolio()
                )
        );

        colCliente.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getClient() == null
                                ? "Cliente no disponible"
                                : data.getValue().getClient().getName()
                )
        );

        colFecha.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getSaleDate() == null
                                ? "-"
                                : data.getValue()
                                        .getSaleDate()
                                        .format(FECHA_FORMATTER)
                )
        );

        colTotal.setCellValueFactory(data ->
                new SimpleDoubleProperty(
                        data.getValue().getFinalTotal()
                ).asObject()
        );

        colPagado.setCellValueFactory(data ->
                new SimpleDoubleProperty(
                        data.getValue().getPaid()
                ).asObject()
        );

        colRestante.setCellValueFactory(data ->
                new SimpleDoubleProperty(
                        data.getValue().getRemaining()
                ).asObject()
        );

        colEstadoPago.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getPaymentStatus()
                )
        );

        colEstadoTrabajo.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getWorkStatus()
                )
        );

        // Formato de columnas.

        PosTable.text(colFolio);
        PosTable.text(colCliente);
        PosTable.text(colFecha);

        PosTable.money(colTotal);
        PosTable.money(colPagado);
        PosTable.money(colRestante);

        configurarBadgesEstado();

        // Alineación.

        colFolio.setStyle("-fx-alignment: CENTER;");
        colEstadoPago.setStyle("-fx-alignment: CENTER;");
        colEstadoTrabajo.setStyle("-fx-alignment: CENTER;");
        colAcciones.setStyle("-fx-alignment: CENTER;");

        colFolio.getStyleClass().add("center-column");
        colEstadoPago.getStyleClass().add("center-column");
        colEstadoTrabajo.getStyleClass().add("center-column");
        colAcciones.getStyleClass().add("center-column");

        // Placeholder.

        PosTable.placeholder(
                tblVentas,
                "No hay ventas pendientes",
                "Las ventas activas aparecerán aquí",
                "fas-clock"
        );
    }

    // =========================================================
    // PAGINACIÓN DEL SERVIDOR
    // =========================================================

    private void configurarPaginacion() {

        tblVentas.setItems(listaVentas);

        paginationController.setItemsPerPage(6);
        paginationController.setShowItemsPerPageSelector(false);

        paginationController.setOnPageChange(pagina -> {

            if (!ignorarCambioPagina) {
                cargarPaginaPendiente(false);
            }
        });
    }

    private void configurarBusqueda() {

        debounceBusqueda.setOnFinished(event -> {

            reiniciarPagina();
            cargarPaginaPendiente(true);
        });

        txtBuscarVenta.textProperty().addListener(
                (observable, anterior, actual) -> {

                    debounceBusqueda.stop();
                    debounceBusqueda.playFromStart();
                }
        );
    }

    private void reiniciarPagina() {

        ignorarCambioPagina = true;

        try {
            paginationController.reset();
        } finally {
            ignorarCambioPagina = false;
        }
    }

    /**
     * Consulta una página de ventas pendientes.
     * La búsqueda y la paginación se ejecutan en el servidor.
     */
    private void cargarPaginaPendiente(boolean actualizarResumen) {

        if (tareaPaginaActual != null) {
            tareaPaginaActual.cancel();
        }

        final int version = ++versionSolicitud;
        final int pagina = paginationController.getCurrentPage();
        final int tamano = paginationController.getItemsPerPage();

        final String filtro = txtBuscarVenta.getText() == null
                ? ""
                : txtBuscarVenta.getText().trim();

        Task<PageResponse<Sale>> tarea = new Task<>() {

            @Override
            protected PageResponse<Sale> call() throws Exception {

                if (filtro.isBlank()) {
                    return saleApiClient.getPendingPage(
                            pagina,
                            tamano
                    );
                }

                return saleApiClient.searchPendingPage(
                        filtro,
                        pagina,
                        tamano
                );
            }
        };

        tareaPaginaActual = tarea;

        tarea.setOnSucceeded(event -> {

            // Ignora las respuestas de solicitudes anteriores.
            if (version != versionSolicitud) {
                return;
            }

            PageResponse<Sale> resultado = tarea.getValue();

            if (resultado == null) {
                mostrarError("El API no devolvió resultados.");
                return;
            }

            totalRegistros = resultado.getTotalElements();

            int totalSeguro = (int) Math.min(
                    totalRegistros,
                    Integer.MAX_VALUE
            );

            paginationController.setTotalItems(totalSeguro);

            /*
             * Si la página actual dejó de existir, vuelve a la
             * primera página. También contempla cero resultados.
             */
            if (pagina > 0 && pagina >= resultado.getTotalPages()) {

                reiniciarPagina();
                cargarPaginaPendiente(actualizarResumen);
                return;
            }

            listaVentas.setAll(
                    resultado.getContent() == null
                            ? List.of()
                            : resultado.getContent()
            );

            actualizarContador();

            if (actualizarResumen) {
                cargarResumen();
            }
        });

        tarea.setOnFailed(event -> {

            if (version != versionSolicitud) {
                return;
            }

            Throwable error = tarea.getException();

            if (error != null) {
                error.printStackTrace();
            }

            mostrarError(
                    "No se pudieron cargar las ventas pendientes."
            );
        });

        tarea.setOnCancelled(event -> {
            // No actualizar la tabla desde una tarea cancelada.
        });

        Thread hilo = new Thread(tarea, "pending-sales-page");
        hilo.setDaemon(true);
        hilo.start();
    }

    private void cargarVentasPendientes() {
        cargarPaginaPendiente(true);
    }

    private void actualizarContador() {

        lblTotalRegistros.setText(
                totalRegistros == 1
                        ? "1 Registro"
                        : totalRegistros + " registros"
        );
    }

    // =========================================================
    // ACCIONES DE LA TABLA
    // =========================================================

    private void configurarAcciones() {

        colAcciones.setCellFactory(param ->
                TableUtils.createVentaAcions(
                        this::abonarVenta,
                        this::recepcionarVenta,
                        this::entregarVenta,
                        this::cancelarVenta
                )
        );
    }

    private void entregarVenta(Sale sale) {

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

        final boolean resultado;

        try {
            resultado = saleApiClient.deliverSale(sale.getId());
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
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

    private void abonarVenta(Sale sale) {

        if (sale == null) {
            return;
        }

        // Se conserva la validación original del controlador.
        if ("COMPLETA".equalsIgnoreCase(sale.getWorkStatus())) {
            mostrarInfo("La venta ya está liquidada.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/gerardgv/posclarity/views/Abonos.fxml"
                    )
            );

            Parent vista = loader.load();

            AbonosController controller = loader.getController();
            controller.setVenta(sale);

            Stage stage = new Stage();
            stage.setScene(new Scene(vista));
            stage.setTitle("Abonos");
            stage.setResizable(false);

            stage.setOnHidden(event -> cargarVentasPendientes());

            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarError("No se pudo abrir la ventana de abonos.");
        }
    }

    private void recepcionarVenta(Sale sale) {

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

        final boolean resultado;

        try {
            resultado = saleApiClient.receiveSale(sale.getId());
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
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

    private void cancelarVenta(Sale sale) {

        if (sale == null) {
            return;
        }

        Optional<Seller> autorizado =
                AuthorizationDialog.solicitarGerente();

        if (autorizado.isEmpty()) {
            return;
        }

        Seller responsable = autorizado.get();

        try {

            boolean resultado = saleApiClient.cancelSale(sale.getId());

            if (!resultado) {
                mostrarError("No Se Puede Cancelar La Venta");
                return;
            }

            EventBus.publishVenta(sale.getId());

            mostrarExito(
                    "Venta cancelada correctamente por "
                    + responsable.getName()
                    + "."
            );

            // El EventBus actualiza la tabla; se evita una segunda consulta.

        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            e.printStackTrace();
            mostrarError("No Se Puede Cancelar Venta");
        }
    }

    // =========================================================
    // NOTIFICACIONES
    // =========================================================

    private void mostrarError(String mensaje) {
        PosNotification.error(root, "Atención", mensaje);
    }

    private void mostrarExito(String mensaje) {
        PosNotification.success(root, "Listo", mensaje);
    }

    private void mostrarInfo(String mensaje) {
        PosNotification.info(root, "Información", mensaje);
    }

    // =========================================================
    // EVENTOS
    // =========================================================

    private void suscribirEventos() {

        EventBus.subscribeVenta(idVenta ->
                Platform.runLater(this::cargarVentasPendientes)
        );
    }

    // =========================================================
    // RESUMEN GLOBAL
    // =========================================================

    private void cargarResumen() {

        try {

            PendingReport summary = saleApiClient.getPendingSummary();

            if (summary == null) {
                return;
            }

            lblTrabajosRealizar.setText(
                    String.valueOf(summary.getSalesToDo())
            );

            lblTrabajosEntregar.setText(
                    String.valueOf(summary.getSalesToDeliver())
            );

            lblSaldoRealizar.setText(
                    MONEDA.format(summary.getBalanceToDo())
            );

            lblSaldoEntregar.setText(
                    MONEDA.format(summary.getBalanceToDeliver())
            );

        } catch (IOException | InterruptedException e) {

            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }

            e.printStackTrace();

            mostrarError(
                    "No se pudo cargar el resumen de ventas pendientes."
            );
        }
    }

    // =========================================================
    // BADGES DE ESTADO
    // =========================================================

    private void configurarBadgesEstado() {

        colEstadoPago.setCellFactory(
                column -> crearCeldaBadgePago()
        );

        colEstadoTrabajo.setCellFactory(
                column -> crearCeldaBadgeTrabajo()
        );
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

                switch (estado.toUpperCase(Locale.ROOT)) {

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

                switch (estado.toUpperCase(Locale.ROOT)) {

                    case "PROCESO" ->
                            badge.getStyleClass().add(
                                    "pending-status-process"
                            );

                    case "RECIBIDO" ->
                            badge.getStyleClass().add(
                                    "pending-status-received"
                            );

                    case "LISTO" ->
                            badge.getStyleClass().add(
                                    "pending-status-ready"
                            );

                    case "ENTREGADO" ->
                            badge.getStyleClass().add(
                                    "pending-status-delivered"
                            );

                    case "CANCELADO" ->
                            badge.getStyleClass().add(
                                    "pending-status-cancelled"
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

    private String formatearEstado(String estado) {

        if (estado == null || estado.isBlank()) {
            return "";
        }

        String texto = estado
                .trim()
                .toLowerCase(Locale.ROOT)
                .replace("_", " ");

        return Character.toUpperCase(texto.charAt(0))
                + texto.substring(1);
    }

    // =========================================================
    // ESTILOS DE FILAS
    // =========================================================

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

                switch (estadoTrabajo.toUpperCase(Locale.ROOT)) {

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
