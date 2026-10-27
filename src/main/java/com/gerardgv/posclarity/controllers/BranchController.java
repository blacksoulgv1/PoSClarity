package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.Ui.PosNotification;
import com.gerardgv.posclarity.api.BranchApiClient;
import com.gerardgv.posclarity.api.dto.common.PageResponse;
import com.gerardgv.posclarity.models.Branch;
import com.gerardgv.posclarity.utils.TableUtils;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class BranchController implements Initializable {

    @FXML private TextField txtSucursal;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtBuscar;

    @FXML private Button btnGuardar;

    @FXML private TableView<Branch> tblSucursales;
    @FXML private TableColumn<Branch, Integer> colId;
    @FXML private TableColumn<Branch, String> colSucursal;
    @FXML private TableColumn<Branch, String> colDireccion;
    @FXML private TableColumn<Branch, String> colTelefono;
    @FXML private TableColumn<Branch, Boolean> colEstatus;
    @FXML private TableColumn<Branch, Void> colAcciones;

    @FXML private StackPane root;
    @FXML private PaginationController paginationController;

    private final BranchApiClient branchApiClient = new BranchApiClient();

    private final ObservableList<Branch> listaSucursales =
            FXCollections.observableArrayList();

    private Branch sucursalSeleccionada;
    private boolean modoEdicion = false;
    private boolean actualizandoPaginacion = false;

    private Task<PageResponse<Branch>> tareaCargaSucursales;
    private int versionCargaSucursales = 0;

    private final PauseTransition debounceBusqueda =
            new PauseTransition(Duration.millis(300));

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        configurarTabla();
        configurarPaginacion();
        configurarBusqueda();
        formatearCombos();

        tblSucursales.setItems(listaSucursales);

        cargarPaginaSucursales();

        txtBuscar.sceneProperty().addListener(
                (obs, oldScene, scene) -> {
                    if (scene != null) {
                        scene.setOnKeyPressed(event -> {
                            if (event.getCode() == KeyCode.ESCAPE) {
                                clearform();
                            }
                        });
                    }
                }
        );
    }

    /*
     * CONFIGURACIÓN DE LA TABLA
     */
    private void configurarTabla() {

        colId.setCellValueFactory(data ->
                new SimpleIntegerProperty(
                        data.getValue().getId()
                ).asObject()
        );

        colSucursal.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getName()
                )
        );

        colDireccion.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getAddress()
                )
        );

        colTelefono.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getPhone()
                )
        );

        colEstatus.setCellValueFactory(data ->
                new SimpleBooleanProperty(
                        data.getValue().isActive()
                ).asObject()
        );

        tblSucursales.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        tblSucursales.setPlaceholder(
                new javafx.scene.control.Label(
                        "No hay sucursales para mostrar"
                )
        );
    }

    /*
     * CONFIGURACIÓN DE PAGINACIÓN
     */
    private void configurarPaginacion() {

        paginationController.setItemsPerPage(12);
        paginationController.setShowItemsPerPageSelector(false);

        paginationController.setOnPageChange(page -> {
            if (!actualizandoPaginacion) {
                cargarPaginaSucursales();
            }
        });
    }

    /*
     * BÚSQUEDA CON DEBOUNCE
     */
    private void configurarBusqueda() {

        debounceBusqueda.setOnFinished(event -> {

            actualizandoPaginacion = true;
            paginationController.reset();
            actualizandoPaginacion = false;

            cargarPaginaSucursales();
        });

        txtBuscar.textProperty().addListener(
                (obs, oldText, newText) ->
                        debounceBusqueda.playFromStart()
        );
    }

    /*
     * CARGA PAGINADA EN SEGUNDO PLANO
     */
    private void cargarPaginaSucursales() {

        if (tareaCargaSucursales != null
                && tareaCargaSucursales.isRunning()) {
            tareaCargaSucursales.cancel(true);
        }

        final int version = ++versionCargaSucursales;
        final int pagina = paginationController.getCurrentPage();
        final int tamano = paginationController.getItemsPerPage();
        final String busqueda = txtBuscar.getText() == null
                ? ""
                : txtBuscar.getText().trim();

        tareaCargaSucursales = new Task<>() {

            @Override
            protected PageResponse<Branch> call() throws Exception {

                if (busqueda.isBlank()) {
                    return branchApiClient.getPage(pagina, tamano);
                }

                return branchApiClient.searchPage(
                        busqueda,
                        pagina,
                        tamano
                );
            }
        };

        tareaCargaSucursales.setOnSucceeded(event -> {

            if (version != versionCargaSucursales) {
                return;
            }

            PageResponse<Branch> respuesta =
                    tareaCargaSucursales.getValue();

            if (respuesta == null) {
                listaSucursales.clear();
                tblSucursales.refresh();
                return;
            }

            int total = (int) Math.min(
                    respuesta.getTotalElements(),
                    Integer.MAX_VALUE
            );

            paginationController.setTotalItems(total);

            /*
             * Si la página cambió mientras se consultaba la API,
             * cargamos la página que corresponde.
             */
            if (paginationController.getCurrentPage() != pagina) {
                cargarPaginaSucursales();
                return;
            }

            List<Branch> sucursales = respuesta.getContent();

            listaSucursales.setAll(
                    sucursales != null ? sucursales : List.of()
            );

            tblSucursales.refresh();
        });

        tareaCargaSucursales.setOnFailed(event -> {

            if (version != versionCargaSucursales) {
                return;
            }

            Throwable error = tareaCargaSucursales.getException();

            mostrarError(
                    "No fue posible consultar las sucursales desde la API.\n"
                    + (error != null
                            ? error.getMessage()
                            : "Error desconocido.")
            );
        });

        Thread hilo = new Thread(
                tareaCargaSucursales,
                "CargaSucursales"
        );

        hilo.setDaemon(true);
        hilo.start();
    }

    /*
     * GUARDAR O ACTUALIZAR SUCURSAL
     */
    @FXML
    private void saveBranch() {

        if (!validarCampos()) {
            mostrarError("Complete los campos obligatorios.");
            return;
        }

        Branch branch = new Branch();

        branch.setName(txtSucursal.getText().trim());
        branch.setPhone(txtTelefono.getText().trim());
        branch.setAddress(txtDireccion.getText().trim());

        try {

            Branch resultado;

            if (modoEdicion) {

                branch.setId(sucursalSeleccionada.getId());
                branch.setCode(sucursalSeleccionada.getCode());
                branch.setActive(sucursalSeleccionada.isActive());

                resultado = branchApiClient.update(branch);

            } else {

                branch.setActive(true);
                resultado = branchApiClient.create(branch);
            }

            if (resultado != null) {

                mostrarExito(
                        modoEdicion
                                ? "Sucursal actualizada correctamente."
                                : "Sucursal guardada correctamente."
                );

                clearform();

                actualizandoPaginacion = true;
                paginationController.reset();
                actualizandoPaginacion = false;

                cargarPaginaSucursales();

            } else {

                mostrarError("No fue posible guardar la sucursal.");
            }

        } catch (IOException e) {

            mostrarError(
                    "No fue posible guardar la sucursal mediante la API.\n"
                    + e.getMessage()
            );

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            mostrarError(
                    "La operación de sucursal fue interrumpida."
            );
        }
    }

    /*
     * LIMPIAR FORMULARIO
     */
    @FXML
    private void clearform() {

        txtSucursal.clear();
        txtTelefono.clear();
        txtDireccion.clear();

        sucursalSeleccionada = null;
        modoEdicion = false;

        tblSucursales.getSelectionModel().clearSelection();

        btnGuardar.setText("Guardar");
        txtSucursal.requestFocus();
    }

    /*
     * SELECCIONAR SUCURSAL PARA EDICIÓN
     */
    private void seleccionarSucursalDesdeTabla(Branch branch) {

        sucursalSeleccionada = branch;
        modoEdicion = true;

        txtSucursal.setText(branch.getName());
        txtTelefono.setText(branch.getPhone());
        txtDireccion.setText(branch.getAddress());

        btnGuardar.setText("Actualizar");
    }

    /*
     * VALIDAR CAMPOS
     */
    private boolean validarCampos() {

        boolean valido = true;

        if (txtSucursal.getText().trim().isEmpty()) {
            txtSucursal.setStyle("-fx-border-color: red;");
            valido = false;
        } else {
            txtSucursal.setStyle("");
        }

        if (txtTelefono.getText().trim().isEmpty()) {
            txtTelefono.setStyle("-fx-border-color: red;");
            valido = false;
        } else {
            txtTelefono.setStyle("");
        }

        if (txtDireccion.getText().trim().isEmpty()) {
            txtDireccion.setStyle("-fx-border-color: red;");
            valido = false;
        } else {
            txtDireccion.setStyle("");
        }

        return valido;
    }

    /*
     * NUEVO REGISTRO
     */
    @FXML
    private void nuevoRegistro() {
        clearform();
    }

    /*
     * ESTADO Y ACCIONES DE LA TABLA
     */
    private void formatearCombos() {

        colEstatus.setCellFactory(column ->
                TableUtils.createActiveToggle(
                        Branch::getId,
                        (id, active) -> {
                            try {
                                Branch actualizada =
                                        branchApiClient.updateStatus(
                                                id,
                                                active
                                        );

                                return actualizada != null;

                            } catch (IOException e) {

                                mostrarError(
                                        "No fue posible cambiar el estado "
                                        + "de la sucursal.\n"
                                        + e.getMessage()
                                );

                            } catch (InterruptedException e) {

                                Thread.currentThread().interrupt();

                                mostrarError(
                                        "El cambio de estado fue interrumpido."
                                );
                            }

                            return false;
                        }
                )
        );

        colAcciones.setCellFactory(column ->
                TableUtils.createEditButton(
                        this::seleccionarSucursalDesdeTabla
                )
        );
    }

    /*
     * NOTIFICACIONES
     */
    private void mostrarError(String mensaje) {
        PosNotification.error(root, "Atención", mensaje);
    }

    private void mostrarExito(String mensaje) {
        PosNotification.success(root, "Listo", mensaje);
    }
}
