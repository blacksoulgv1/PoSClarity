package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.Ui.PosSearchPopup;
import com.gerardgv.posclarity.api.DiscountApiClient;
import com.gerardgv.posclarity.api.ProductApiClient;
import com.gerardgv.posclarity.api.dto.common.PageResponse;
import com.gerardgv.posclarity.api.dto.discount.DiscountCreateRequest;
import com.gerardgv.posclarity.models.Discount;
import com.gerardgv.posclarity.models.Product;
import com.gerardgv.posclarity.utils.TableUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class DiscountController implements Initializable {

    // =========================================================
    // 1. CONTROLES FXML
    // =========================================================

    @FXML private TextField txtNombre;
    @FXML private TextField txtDescuento;
    @FXML private TextField txtValor;
    @FXML private TextField txtCupon;
    @FXML private TextField txtPrioridad;
    @FXML private TextField txtBuscar;
    @FXML private TextField txtModeloProducto;
    @FXML private TextField txtDioptriaMax;

    @FXML private CheckBox chkRequiereArmazon;

    @FXML private ComboBox<String> cbCategoria;
    @FXML private ComboBox<String> cbTipoAplicacion;
    @FXML private ComboBox<String> cbTipoValor;

    @FXML private DatePicker dpInicio;
    @FXML private DatePicker dpFin;

    @FXML private Button btnGuardar;

    @FXML private TableView<Discount> tabla;

    @FXML private TableColumn<Discount, Integer> colId;
    @FXML private TableColumn<Discount, String> colNombre;
    @FXML private TableColumn<Discount, String> colTAplicacion;
    @FXML private TableColumn<Discount, String> colTValor;
    @FXML private TableColumn<Discount, Double> colValor;
    @FXML private TableColumn<Discount, String> colcupon;
    @FXML private TableColumn<Discount, String> colCategoria;
    @FXML private TableColumn<Discount, String> colFi;
    @FXML private TableColumn<Discount, String> colFf;
    @FXML private TableColumn<Discount, Integer> colPrioridad;
    @FXML private TableColumn<Discount, Boolean> colActivo;
    @FXML private TableColumn<Discount, Void> colAccion;

    @FXML private PaginationController paginationController;

    // =========================================================
    // 2. SERVICIOS Y ESTADO
    // =========================================================

    private final DiscountApiClient apiClient =
            new DiscountApiClient();

    private final ProductApiClient productApiClient =
            new ProductApiClient();

    private final PosSearchPopup<Product> popupProducts =
            new PosSearchPopup<>();

    private final ObservableList<Discount> listaDescuentos =
            FXCollections.observableArrayList();

    private Discount descuentoSeleccionado;
    private Product productoBeneficioSeleccionado;

    private Task<PageResponse<Discount>> tareaCargaDescuentos;

    private boolean actualizandoPaginacion = false;
    private int versionCargaDescuentos = 0;

    private final PauseTransition debounceBusqueda =
            new PauseTransition(Duration.millis(300));

    // =========================================================
    // 3. INICIALIZACIÓN
    // =========================================================

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        configurarPaginacion();
        configurarBusqueda();
        configurarCombos();
        configurarFormularioDinamico();
        configurarPopupProductoBeneficio();
        configurarAtajosTeclado();

        cargarTabla();
    }

    // =========================================================
    // 4. CONFIGURACIÓN DE TABLA
    // =========================================================

    private void configurarTabla() {
        configurarColumnas();

        tabla.setItems(listaDescuentos);
        tabla.setSelectionModel(null);

        colActivo.setCellFactory(column ->
                TableUtils.createActiveToggle(
                        Discount::getId,
                        this::actualizarActivo
                )
        );

        colAccion.setCellFactory(column ->
                TableUtils.createEditButton(this::editarDescuento)
        );
    }

    private void configurarColumnas() {

        colId.setCellValueFactory(data ->
                new SimpleIntegerProperty(
                        data.getValue().getId()
                ).asObject()
        );

        colNombre.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getName()
                )
        );

        colTAplicacion.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getApplicationType()
                )
        );

        colTValor.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getValueType()
                )
        );

        colValor.setCellValueFactory(data ->
                new SimpleObjectProperty<>(
                        data.getValue().getValue()
                )
        );

        configurarFormatoValor();

        colcupon.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getCouponCode()
                )
        );

        colCategoria.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getCategory()
                )
        );

        colFi.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getStartDate() != null
                                ? data.getValue().getStartDate().toString()
                                : ""
                )
        );

        colFf.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getEndDate() != null
                                ? data.getValue().getEndDate().toString()
                                : ""
                )
        );

        colPrioridad.setCellValueFactory(data ->
                new SimpleIntegerProperty(
                        data.getValue().getPriority()
                ).asObject()
        );

        colActivo.setCellValueFactory(data ->
                new SimpleBooleanProperty(
                        data.getValue().isActive()
                ).asObject()
        );
    }

    private void configurarFormatoValor() {

        colValor.setCellFactory(column ->
                new TableCell<Discount, Double>() {

            @Override
            protected void updateItem(Double valor, boolean empty) {
                super.updateItem(valor, empty);

                if (empty || getTableRow() == null
                        || getTableRow().getItem() == null) {
                    setText(null);
                    return;
                }

                Discount descuento = getTableRow().getItem();

                if (valor == null) {
                    setText("-");
                } else if ("PORCENTAJE".equals(
                        descuento.getValueType())) {
                    setText(String.format("%.2f %%", valor));
                } else {
                    setText(String.format("$%.2f", valor));
                }
            }
        });
    }

    // =========================================================
    // 5. PAGINACIÓN Y BÚSQUEDA
    // =========================================================

    private void configurarPaginacion() {

        paginationController.setItemsPerPage(12);
        paginationController.setShowItemsPerPageSelector(false);

        paginationController.setOnPageChange(page -> {
        if (!actualizandoPaginacion) {
            cargarTabla();
        }
        });
        tabla.setItems(listaDescuentos);
    }

    private void configurarBusqueda() {

        debounceBusqueda.setOnFinished(event -> cargarTabla());

        txtBuscar.textProperty().addListener(
                (obs, oldText, newText) -> {

            debounceBusqueda.stop();

            actualizandoPaginacion = true;
            paginationController.reset();
            actualizandoPaginacion = false;

            debounceBusqueda.playFromStart();
        });
    }

    private void cargarTabla() {

        int pagina = paginationController.getCurrentPage();
        int tamano = paginationController.getItemsPerPage();

        String busqueda = txtBuscar.getText() == null
                ? ""
                : txtBuscar.getText().trim();

        int version = ++versionCargaDescuentos;

        cancelarCargaAnterior();

        Task<PageResponse<Discount>> tarea = new Task<>() {

            @Override
            protected PageResponse<Discount> call() throws Exception {

                if (busqueda.isBlank()) {
                    return apiClient.getPage(pagina, tamano);
                }

                return apiClient.searchPage(
                        busqueda,
                        pagina,
                        tamano
                );
            }
        };

        tareaCargaDescuentos = tarea;

        tarea.setOnSucceeded(event -> {

            if (version != versionCargaDescuentos) {
                return;
            }

            PageResponse<Discount> respuesta = tarea.getValue();

            if (respuesta == null) {
                return;
            }

            paginationController.setTotalItems(
                    Math.toIntExact(respuesta.getTotalElements())
            );

            if (paginationController.getCurrentPage() != pagina) {
                cargarTabla();
                return;
            }

            listaDescuentos.setAll(respuesta.getContent());
            tabla.refresh();
        });

        tarea.setOnFailed(event -> {

            if (version != versionCargaDescuentos) {
                return;
            }

            Throwable error = tarea.getException();

            if (error != null) {
                error.printStackTrace();
            }

            mostrarMensaje("No se pudieron cargar los descuentos");
        });

        Thread hilo = new Thread(tarea, "carga-descuentos");
        hilo.setDaemon(true);
        hilo.start();
    }

    private void cancelarCargaAnterior() {

        if (tareaCargaDescuentos != null
                && tareaCargaDescuentos.isRunning()) {
            tareaCargaDescuentos.cancel();
        }
    }

    // =========================================================
    // 6. COMBOS Y FORMULARIO DINÁMICO
    // =========================================================

    private void configurarCombos() {
        cargarCombos();
        formatearCombos();
    }

    private void cargarCombos() {

        cbTipoAplicacion.setItems(
                FXCollections.observableArrayList(
                        "GENERAL",
                        "CATEGORIA",
                        "CUPON"
                )
        );

        cbTipoValor.setItems(
                FXCollections.observableArrayList(
                        "PORCENTAJE",
                        "MONTO",
                        "GRATIS"
                )
        );

        cbCategoria.setItems(
                FXCollections.observableArrayList(
                        "armazon",
                        "lente_contacto",
                        "producto",
                        "servicio",
                        "mica",
                        "tratamiento"
                )
        );
    }

    private void formatearCombos() {

        cbCategoria.setCellFactory(listView ->
                new ListCell<>() {

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null
                        ? null
                        : formatearTexto(item));
            }
        });

        cbCategoria.setButtonCell(new ListCell<>() {

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null
                        ? null
                        : formatearTexto(item));
            }
        });
    }

    private String formatearTexto(String valor) {

        return switch (valor) {
            case "armazon" -> "Armazón";
            case "lente_contacto" -> "Lente de contacto";
            case "producto" -> "Producto";
            case "servicio" -> "Servicio";
            case "mica" -> "Mica";
            case "tratamiento" -> "Tratamiento";
            default -> valor;
        };
    }

    private void configurarFormularioDinamico() {

        cbTipoAplicacion.valueProperty().addListener(
                (obs, oldValue, newValue) -> actualizarCampos()
        );

        cbTipoValor.valueProperty().addListener(
                (obs, oldValue, newValue) -> actualizarCampos()
        );

        actualizarCampos();
    }

    private void actualizarCampos() {

        String tipoAplicacion = cbTipoAplicacion.getValue();
        String tipoValor = cbTipoValor.getValue();

        boolean esCupon = "CUPON".equals(tipoAplicacion);
        boolean esCategoria = "CATEGORIA".equals(tipoAplicacion);
        boolean esGratis = "GRATIS".equals(tipoValor);

        boolean usaValor = "PORCENTAJE".equals(tipoValor)
                || "MONTO".equals(tipoValor);

        txtValor.setDisable(!usaValor);
        txtCupon.setDisable(!esCupon);
        cbCategoria.setDisable(!esCategoria);
        txtModeloProducto.setDisable(!esGratis);

        if (!usaValor) {
            txtValor.clear();
        }

        if (!esCupon) {
            txtCupon.clear();
        }

        if (!esCategoria) {
            cbCategoria.setValue(null);
        }

        if (!esGratis) {
            txtModeloProducto.clear();
            productoBeneficioSeleccionado = null;
            popupProducts.hide();
        }
    }

    // =========================================================
    // 7. CREACIÓN Y EDICIÓN
    // =========================================================

    @FXML
    private void guardar() {

        try {
            Discount descuento = construirDescuento();

            if (!validarDescuento(descuento)) {
                return;
            }

            DiscountCreateRequest request =
                    new DiscountCreateRequest(descuento);

            if (descuentoSeleccionado == null) {
                apiClient.create(request);
                mostrarMensaje("Descuento creado correctamente");
            } else {
                apiClient.update(
                        descuentoSeleccionado.getId(),
                        request
                );

                mostrarMensaje("Descuento actualizado correctamente");
            }

            limpiarFormulario();
            recargarDesdePrimeraPagina();

        } catch (NumberFormatException e) {
            mostrarMensaje("Revisa los valores numéricos");

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error de comunicación con el servidor");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            mostrarMensaje("La operación fue interrumpida");

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("Datos inválidos");
        }
    }

    private Discount construirDescuento() {

        Discount descuento = new Discount();

        descuento.setName(txtNombre.getText());
        descuento.setApplicationType(cbTipoAplicacion.getValue());
        descuento.setValueType(cbTipoValor.getValue());

        if (!txtValor.isDisabled()
                && !txtValor.getText().isBlank()) {
            descuento.setValue(
                    Double.parseDouble(txtValor.getText())
            );
        }

        descuento.setCouponCode(txtCupon.getText());
        descuento.setCategory(cbCategoria.getValue());
        descuento.setRequireFrame(
                chkRequiereArmazon.isSelected()
        );

        if (!txtDioptriaMax.getText().isBlank()) {
            descuento.setMaxDiopter(
                    Double.parseDouble(txtDioptriaMax.getText())
            );
        }

        descuento.setStartDate(dpInicio.getValue());
        descuento.setEndDate(dpFin.getValue());

        descuento.setPriority(
                txtPrioridad.getText().isBlank()
                        ? 0
                        : Integer.parseInt(txtPrioridad.getText())
        );

        descuento.setActive(
                descuentoSeleccionado == null
                        || descuentoSeleccionado.isActive()
        );

        if ("GRATIS".equals(descuento.getValueType())) {

            if (productoBeneficioSeleccionado == null) {
                throw new IllegalArgumentException(
                        "Selecciona el producto beneficio"
                );
            }

            descuento.setBenefitProductId(
                    productoBeneficioSeleccionado.getId()
            );

        } else {
            descuento.setBenefitProductId(null);
        }

        return descuento;
    }

    private void editarDescuento(Discount descuento) {

        descuentoSeleccionado = descuento;
        productoBeneficioSeleccionado = null;

        txtNombre.setText(descuento.getName());
        cbTipoAplicacion.setValue(descuento.getApplicationType());
        cbTipoValor.setValue(descuento.getValueType());

        txtValor.setText(descuento.getValue() == null
                ? ""
                : String.valueOf(descuento.getValue()));

        txtCupon.setText(descuento.getCouponCode());
        cbCategoria.setValue(descuento.getCategory());
        txtModeloProducto.setText(descuento.getBenefitProductModel());

        chkRequiereArmazon.setSelected(descuento.isRequireFrame());

        txtDioptriaMax.setText(descuento.getMaxDiopter() == null
                ? ""
                : String.valueOf(descuento.getMaxDiopter()));

        dpInicio.setValue(descuento.getStartDate());
        dpFin.setValue(descuento.getEndDate());

        txtPrioridad.setText(
                String.valueOf(descuento.getPriority())
        );

        btnGuardar.setText("Actualizar");

        actualizarCampos();
    }

    // =========================================================
    // 8. VALIDACIONES
    // =========================================================

    public boolean validarDescuento(Discount descuento) {

        if (descuento.getName() == null
                || descuento.getName().isBlank()) {
            return errorValidacion(
                    "Ingresa el nombre del descuento",
                    txtNombre
            );
        }

        if (descuento.getApplicationType() == null
                || descuento.getApplicationType().isBlank()) {
            return errorValidacion(
                    "Selecciona el tipo de aplicación",
                    cbTipoAplicacion
            );
        }

        if (descuento.getValueType() == null
                || descuento.getValueType().isBlank()) {
            return errorValidacion(
                    "Selecciona el tipo de valor",
                    cbTipoValor
            );
        }

        if ("PORCENTAJE".equals(descuento.getValueType())) {

            if (descuento.getValue() == null
                    || descuento.getValue() <= 0
                    || descuento.getValue() > 100) {
                return errorValidacion(
                        "El porcentaje debe ser mayor a 0 "
                                + "y menor o igual a 100",
                        txtValor
                );
            }
        }

        if ("MONTO".equals(descuento.getValueType())) {

            if (descuento.getValue() == null
                    || descuento.getValue() <= 0) {
                return errorValidacion(
                        "El monto del descuento debe ser mayor a 0",
                        txtValor
                );
            }
        }

        if ("GRATIS".equals(descuento.getValueType())) {

            if (descuento.getBenefitProductId() == null) {
                return errorValidacion(
                        "Debes seleccionar el producto beneficio",
                        txtModeloProducto
                );
            }
        }

        if ("CUPON".equals(descuento.getApplicationType())) {

            if (descuento.getCouponCode() == null
                    || descuento.getCouponCode().isBlank()) {
                return errorValidacion(
                        "Ingresa el código del cupón",
                        txtCupon
                );
            }
        }

        if ("CATEGORIA".equals(descuento.getApplicationType())) {

            if (descuento.getCategory() == null
                    || descuento.getCategory().isBlank()) {
                return errorValidacion(
                        "Selecciona una categoría",
                        cbCategoria
                );
            }
        }

        if (descuento.getStartDate() != null
                && descuento.getEndDate() != null
                && descuento.getStartDate()
                        .isAfter(descuento.getEndDate())) {
            return errorValidacion(
                    "La fecha de inicio no puede ser posterior "
                            + "a la fecha final",
                    dpInicio
            );
        }

        if (descuento.getMaxDiopter() != null
                && descuento.getMaxDiopter() < 0) {
            return errorValidacion(
                    "La dioptría máxima no puede ser negativa",
                    txtDioptriaMax
            );
        }

        if (descuento.getPriority() < 0) {
            return errorValidacion(
                    "La prioridad no puede ser negativa",
                    txtPrioridad
            );
        }

        return true;
    }

    private boolean errorValidacion(
            String mensaje,
            javafx.scene.Node control) {

        mostrarMensaje(mensaje);
        control.requestFocus();
        return false;
    }

    // =========================================================
    // 9. ACTIVACIÓN Y RECARGA
    // =========================================================

    private boolean actualizarActivo(int id, boolean active) {

        try {
            apiClient.updateStatus(id, active);
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("No se pudo actualizar el descuento");
            return false;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            mostrarMensaje("La operación fue interrumpida");
            return false;
        }
    }

    private void recargarDesdePrimeraPagina() {

        actualizandoPaginacion = true;
        paginationController.reset();
        actualizandoPaginacion = false;

        cargarTabla();
    }

    // =========================================================
    // 10. PRODUCTO BENEFICIO
    // =========================================================

    private void configurarPopupProductoBeneficio() {

        popupProducts.setTitleProvider(producto ->
                producto.getModel() + " - " + producto.getBrand()
        );

        popupProducts.setSubtitleProvider(producto ->
                producto.getCategory()
                        + " | $"
                        + String.format("%.2f", producto.getPrice())
                        + " | stock:"
                        + producto.getStock()
        );

        popupProducts.setIconProvider(producto -> "fas-box-open");

        popupProducts.setEmptyMessage(
                "No se encontraron productos"
        );

        popupProducts.setOnSelected(
                this::seleccionarProductoBeneficio
        );

        txtModeloProducto.textProperty().addListener(
                (obs, oldText, newText) -> buscarProductoBeneficio(newText)
        );
    }

    private void buscarProductoBeneficio(String texto) {

        if (!"GRATIS".equals(cbTipoValor.getValue())) {
            popupProducts.hide();
            return;
        }

        if (texto == null || texto.isBlank()) {
            productoBeneficioSeleccionado = null;
            popupProducts.hide();
            return;
        }

        try {
            List<Product> productos =
                    productApiClient.search(texto);

            popupProducts.setItems(productos);
            popupProducts.show(txtModeloProducto);

        } catch (IOException e) {
            e.printStackTrace();
            popupProducts.hide();
            mostrarMensaje("Error buscando productos");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            popupProducts.hide();
            mostrarMensaje("La búsqueda de productos fue interrumpida");
        }
    }

    private void seleccionarProductoBeneficio(Product producto) {

        if (producto == null) {
            productoBeneficioSeleccionado = null;
            return;
        }

        productoBeneficioSeleccionado = producto;
        txtModeloProducto.setText(producto.getModel());
        popupProducts.hide();
    }

    // =========================================================
    // 11. LIMPIEZA Y ATAJOS
    // =========================================================

    @FXML
    private void limpiar() {
        limpiarFormulario();
    }

    private void limpiarFormulario() {

        txtNombre.clear();
        txtValor.clear();
        txtCupon.clear();
        txtPrioridad.clear();
        txtModeloProducto.clear();
        txtDioptriaMax.clear();

        productoBeneficioSeleccionado = null;
        descuentoSeleccionado = null;

        popupProducts.hide();

        chkRequiereArmazon.setSelected(false);

        cbTipoAplicacion.setValue(null);
        cbTipoValor.setValue(null);
        cbCategoria.setValue(null);

        dpInicio.setValue(null);
        dpFin.setValue(null);

        btnGuardar.setText("Guardar");

        actualizarCampos();
    }

    private void configurarAtajosTeclado() {

        txtBuscar.sceneProperty().addListener(
                (obs, oldScene, scene) -> {

            if (scene != null) {
                scene.setOnKeyPressed(event -> {

                    if (event.getCode()
                            == javafx.scene.input.KeyCode.ESCAPE) {
                        limpiarFormulario();
                    }
                });
            }
        });
    }

    // =========================================================
    // 12. MENSAJES
    // =========================================================

    private void mostrarMensaje(String mensaje) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
