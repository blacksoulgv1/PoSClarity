package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.Ui.PosSearchPopup;
import com.gerardgv.posclarity.api.*;
import com.gerardgv.posclarity.api.dto.discount.DiscountCreateRequest;
import com.gerardgv.posclarity.models.*;
import com.gerardgv.posclarity.utils.*;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;



public class DiscountController implements Initializable {
    
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
    @FXML private TableColumn<Discount,Integer> colId;
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
    
    private final DiscountApiClient apiClient = new DiscountApiClient();
    private final ProductApiClient productApiClient = new ProductApiClient();
    private final PosSearchPopup<Product> popupProducts = new PosSearchPopup<>();
    
    private Product productoBeneficioSeleccionado;    
    private Discount descuentoSeleccionado;
    
    private ObservableList<Discount> listaDescuentos = FXCollections.observableArrayList();

    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        configurarColumnas();
        cargarTabla();
        cargarCombos();
        formaterCombos();
        configurarFormularioDinamico();
        configurarPopupProductoBeneficio();
        
        SearchUtils.setupSearch(txtBuscar, tabla, listaDescuentos,
                d -> d.getName(),
                d ->d.getCategory());
        
        colActivo.setCellFactory(column ->
            TableUtils.createActiveToggle(
                Discount::getId, this::actualizarActivo));
        
        colAccion.setCellFactory(param ->
            TableUtils.createEditButton(this::editarDescuento));
        
        txtBuscar.sceneProperty().addListener((obs,oldScene,scene)->{
            if(scene != null){
                scene.setOnKeyPressed(e->{
                    if(e.getCode().toString().equals("ESCAPE")){
                        limpiar();
                    }
                });
            }
        });
        tabla.setSelectionModel(null);
    }
    
    private void cargarCombos(){
        cbTipoAplicacion.setItems(FXCollections.observableArrayList(
                "GENERAL",
                "CATEGORIA",
                "CUPON"
        ));

        cbTipoValor.setItems(FXCollections.observableArrayList(
                "PORCENTAJE",
                "MONTO",
                "GRATIS"
        ));

        cbCategoria.setItems(FXCollections.observableArrayList(
                "armazon",
                "lente_contacto",
                "producto",
                "servicio",
                "mica",
                "tratamiento"
        )); 
    }
    
    private void configurarFormularioDinamico(){
        
        /*
        * Cuando cambia el tipo de aplicación
        */
        cbTipoAplicacion.valueProperty().addListener(
            (obs, oldValue, newValue) -> actualizarCampos()
        );

        /*
        * Cuando cambia el tipo de valor
        */
        cbTipoValor.valueProperty().addListener(
            (obs, oldValue, newValue) -> actualizarCampos()
        );

        /*
        * Estado inicial
        */
        actualizarCampos();
    }
    
    
    private void cargarTabla() {
        
        try {
            listaDescuentos.clear();
            listaDescuentos.addAll(apiClient.getAll());
            tabla.setItems(listaDescuentos);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            mostrarMensaje("No se pudieron cargar los descuentos");
        }
    }
    
    private void configurarColumnas(){

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

    colValor.setCellValueFactory(data -> {

        Double valor = data.getValue().getValue();

        return new SimpleObjectProperty<>(valor);
    });

    colValor.setCellFactory(column ->
        new TableCell<Discount, Double>() {

            @Override
            protected void updateItem(
                    Double valor,
                    boolean empty) {

                super.updateItem(valor, empty);

                if (empty
                        || getTableRow() == null
                        || getTableRow().getItem() == null) {

                    setText(null);
                    return;
                }

                Discount descuento =
                    getTableRow().getItem();

                if (valor == null) {

                    setText("-");

                } else if (
                        "PORCENTAJE".equals(
                            descuento.getValueType())) {

                    setText(
                        String.format(
                            "%.2f %%",
                            valor
                        )
                    );

                } else {

                    setText(
                        String.format(
                            "$%.2f",
                            valor
                        )
                    );
                }
            }
        }
    );

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
        new SimpleObjectProperty<>(
            data.getValue().getStartDate() != null
                ? data.getValue().getStartDate().toString()
                : ""
        )
    );

    colFf.setCellValueFactory(data ->
        new SimpleObjectProperty<>(
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
    
    private void editarDescuento(Discount d){
        
        descuentoSeleccionado = d;
        
        txtNombre.setText(d.getName());
        cbTipoAplicacion.setValue(d.getApplicationType());
        cbTipoValor.setValue(d.getValueType());
        
        if(d.getValue() != null){
            txtValor.setText(String.valueOf(d.getValue()));
        }else{
            txtValor.clear();
        }
        
        txtCupon.setText(d.getCouponCode());
        cbCategoria.setValue(d.getCategory());
        txtModeloProducto.setText(d.getBenefitProductModel());
        chkRequiereArmazon.setSelected(d.isRequireFrame());

        if(d.getMaxDiopter() != null){
            txtDioptriaMax.setText(String.valueOf(d.getMaxDiopter()));
        } else {
            txtDioptriaMax.clear();
        }
        
        dpInicio.setValue(d.getStartDate());
        dpFin.setValue(d.getEndDate());
        txtPrioridad.setText(String.valueOf(d.getPriority()));
        btnGuardar.setText("Actualizar");
        actualizarCampos();
    }
    
    public boolean validarDescuento(Discount d){
        
         // Nombre
        if (d.getName() == null || d.getName().isBlank()) {
            mostrarMensaje("Ingresa el nombre del descuento");
            txtNombre.requestFocus();
            return false;
        }

        // Tipo de aplicación
        if (d.getApplicationType() == null
            || d.getApplicationType().isBlank()) {
            mostrarMensaje("Selecciona el tipo de aplicación");
            cbTipoAplicacion.requestFocus();
            return false;
        }

        // Tipo de valor
        if (d.getValueType() == null
            || d.getValueType().isBlank()) {
            mostrarMensaje("Selecciona el tipo de valor");
            cbTipoValor.requestFocus();
            return false;
        }

        /*
        * PORCENTAJE
        */
        if ("PORCENTAJE".equals(d.getValueType())) {

            if (d.getValue() == null) {
                mostrarMensaje("Ingresa el porcentaje del descuento");
                txtValor.requestFocus();
                return false;
            }

            if (d.getValue() <= 0 || d.getValue() > 100) {
                mostrarMensaje(
                    "El porcentaje debe ser mayor a 0 y menor o igual a 100"
                );
                txtValor.requestFocus();
                return false;
            }
        }

        /*
        * MONTO
        */
        if ("MONTO".equals(d.getValueType())) {

            if (d.getValue() == null) {
                mostrarMensaje("Ingresa el monto del descuento");
                txtValor.requestFocus();
                return false;
            }

            if (d.getValue() <= 0) {
                mostrarMensaje(
                    "El monto del descuento debe ser mayor a 0"
                );
                txtValor.requestFocus();
                return false;
            }
        }
        
        /*
        * GRATIS
        */
        if ("GRATIS".equals(d.getValueType())) {

            if (d.getBenefitProductId() == null) {
                mostrarMensaje(
                    "Debes seleccionar el producto beneficio"
                );
                txtModeloProducto.requestFocus();
                return false;
            }
        }

        /*
        * CUPÓN
        */
        if ("CUPON".equals(d.getApplicationType())) {

            if (d.getCouponCode() == null
                || d.getCouponCode().isBlank()) {

                mostrarMensaje("Ingresa el código del cupón");
                txtCupon.requestFocus();
                return false;
            }
        }

        /*
        * CATEGORÍA
        */
        if ("CATEGORIA".equals(d.getApplicationType())) {

            if (d.getCategory() == null
                || d.getCategory().isBlank()) {

                mostrarMensaje("Selecciona una categoría");
                cbCategoria.requestFocus();
                return false;
            }
        }

        /*
        * Fechas
        */
        if (d.getStartDate() != null
            && d.getEndDate() != null
            && d.getStartDate().isAfter(d.getEndDate())) {

            mostrarMensaje("La fecha de inicio no puede ser posterior a la fecha final");
            dpInicio.requestFocus();
            return false;
        }
        
        /*
        * Dioptría máxima
        */
        if (d.getMaxDiopter() != null
            && d.getMaxDiopter() < 0) {

            mostrarMensaje("La dioptría máxima no puede ser negativa");
            txtDioptriaMax.requestFocus();
            return false;
        }

        /*
        * Prioridad
        */
        if (d.getPriority() < 0) {

            mostrarMensaje("La prioridad no puede ser negativa");
            txtPrioridad.requestFocus();
            return false;
        }
        return true;       
    }
    
    @FXML
    private void guardar(){
        
        
        try {

            Discount d = new Discount();

            d.setName(txtNombre.getText());
            d.setApplicationType(cbTipoAplicacion.getValue());
            d.setValueType(cbTipoValor.getValue());

            if (!txtValor.isDisabled() && !txtValor.getText().isBlank()) {
                d.setValue(Double.parseDouble(txtValor.getText()));
            }

                d.setCouponCode(txtCupon.getText());
                d.setCategory(cbCategoria.getValue());
                d.setRequireFrame(chkRequiereArmazon.isSelected());

            if (!txtDioptriaMax.getText().isBlank()) {
                d.setMaxDiopter(Double.parseDouble(txtDioptriaMax.getText()));
            }
                d.setStartDate(dpInicio.getValue());
                d.setEndDate(dpFin.getValue());
            if (!txtPrioridad.getText().isBlank()) {
                d.setPriority(Integer.parseInt(txtPrioridad.getText()));
            } else {
                d.setPriority(0);
            }
            
            /*
            Estado
            */
            if(descuentoSeleccionado == null){
                d.setActive(true);
            }else{
                d.setActive(descuentoSeleccionado.isActive());
            }
            
            /*
            producto Beneficio "no Tenemos"
            */
            if("GRATIS".equals(d.getValueType())){
                                
                if(productoBeneficioSeleccionado  == null){
                    mostrarMensaje("Selecciona el producto beneficio");
                    txtModeloProducto.requestFocus();
                    return;
                }
                
                d.setBenefitProductId(productoBeneficioSeleccionado.getId());
            } else {
                d.setBenefitProductId(null);
            }
            
            /*
            Validacion
            */
            if(!validarDescuento(d)){
                return;
            }

            /*
            * Crear
            */
            if (descuentoSeleccionado == null) {

                DiscountCreateRequest request = new DiscountCreateRequest(d);
                apiClient.create(request);
                mostrarMensaje("Descuento creado correctamente");

            } else {

                /*
                * Actualizar
                */
                DiscountCreateRequest request = new DiscountCreateRequest(d);
                apiClient.update(descuentoSeleccionado.getId(),request);
                mostrarMensaje("Descuento actualizado correctamente");
            }

            cargarTabla();
            limpiar();

        } catch (NumberFormatException e) {

            mostrarMensaje("Revisa los valores numéricos");

        } catch (IOException | InterruptedException e) {

            e.printStackTrace();
            mostrarMensaje("Error de comunicación con el servidor");

        } catch (Exception e) {

            e.printStackTrace();
            mostrarMensaje("Datos inválidos");
        }
    }

    @FXML
    private void limpiar(){
        txtNombre.clear();
        txtValor.clear();
        txtCupon.clear();
        txtPrioridad.clear();
        txtModeloProducto.clear();
        productoBeneficioSeleccionado = null;
        popupProducts.hide();
        txtDioptriaMax.clear();
        chkRequiereArmazon.setSelected(false);
        cbTipoAplicacion.setValue(null);
        cbTipoValor.setValue(null);
        cbCategoria.setValue(null);        
        dpInicio.setValue(null);
        dpFin.setValue(null);
        descuentoSeleccionado = null;
        btnGuardar.setText("Guardar");
        actualizarCampos();
    }
    
    private void mostrarMensaje(String mensaje){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private void formaterCombos(){
        cbCategoria.setCellFactory(lv -> new ListCell<>(){
            @Override
        protected void updateItem(String item,boolean empty){
                super.updateItem(item, empty);
                setText(empty || item == null ? null: formatearTexto(item));
            }
        });
        
        cbCategoria.setButtonCell(new ListCell<>(){
            @Override
        protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formatearTexto(item));
            }
        });
    }
    
    private String formatearTexto(String valor){
        return switch(valor){            
            case"armazon" -> "Armazón";
            case "lente_contacto" -> "Lente de contacto";
            case "producto" -> "Producto";
            case "servicio" -> "Servicio";
            case "mica" -> "Mica";
            case "tratamiento" -> "Tratamiento";
            default -> valor;                
        };
    }    
   
    private boolean actualizarActivo(int id, boolean active) {
        try {
            apiClient.updateStatus(id, active);
            return true;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            mostrarMensaje("No se pudo actualizar el descuento");
            return false;
        }
    }

    private void actualizarCampos() {
        
        String tipoAplicacion = cbTipoAplicacion.getValue();
        String tipoValor = cbTipoValor.getValue();

        /*
        * Por defecto deshabilitamos los campos que dependen de selección.
        */
        boolean esCupon = "CUPON".equals(tipoAplicacion);
        boolean esCategoria = "CATEGORIA".equals(tipoAplicacion);
        boolean esGratis = "GRATIS".equals(tipoValor);

        /*
        * PORCENTAJE y MONTO utilizan valor.
        */
        boolean usaValor = "PORCENTAJE".equals(tipoValor) || "MONTO".equals(tipoValor);
        txtValor.setDisable(!usaValor);

        /*
        * CUPÓN
        */
        txtCupon.setDisable(!esCupon);

        /*
        * CATEGORÍA
        */
        cbCategoria.setDisable(!esCategoria);

        /*
        * PRODUCTO BENEFICIO (GRATIS necesita producto).
        */
        txtModeloProducto.setDisable(!esGratis);

        /*
        * Limpiamos campos que ya no corresponden.
        */
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
        }
    }

    private void configurarPopupProductoBeneficio() {
        
        popupProducts.setTitleProvider(p ->
        p.getModel() + " - " + p.getBrand());

        popupProducts.setSubtitleProvider(p ->
            p.getCategory()
            + " | $"
            + String.format("%.2f", p.getPrice())
            + " | stock:"
            + p.getStock());

        popupProducts.setIconProvider(p -> "fas-box-open");

        popupProducts.setEmptyMessage("No se encontraron productos");

        popupProducts.setOnSelected(this::seleccionarProductoBeneficio);

        txtModeloProducto.textProperty().addListener((obs, oldText, newText) -> {

        if (!"GRATIS".equals(cbTipoValor.getValue())) {
            popupProducts.hide();
            return;
        }

        if (newText == null || newText.isBlank()) {
            productoBeneficioSeleccionado = null;
            popupProducts.hide();
            return;
        }

        try {

            List<Product> lista =
                    productApiClient.search(newText);

            popupProducts.setItems(lista);
            popupProducts.show(txtModeloProducto);

        } catch (IOException | InterruptedException e) {

            e.printStackTrace();
            popupProducts.hide();
            mostrarMensaje("Error buscando productos");
            }
        });
    }
 
    private void seleccionarProductoBeneficio(Product producto){
        
        if (producto == null) {
            productoBeneficioSeleccionado = null;
            return;
        }

        productoBeneficioSeleccionado = producto;
        txtModeloProducto.setText(producto.getModel());
        popupProducts.hide();
    }
}

