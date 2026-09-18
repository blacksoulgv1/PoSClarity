package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.Ui.*;
import com.gerardgv.posclarity.api.SellerApiClient;
import com.gerardgv.posclarity.api.dto.seller.SellerRequest;
import com.gerardgv.posclarity.models.Role;
import com.gerardgv.posclarity.models.Seller;
import com.gerardgv.posclarity.utils.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;



public class SellersController implements Initializable {
    
    @FXML private TextField txtCodigo;
    @FXML private TextField txtEmpleado;
    @FXML private TextField txtBuscar;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmarPassword;
    @FXML private ComboBox<Role> cbRol;
    
    @FXML private TableView<Seller> tblEmpleados;
    @FXML private TableColumn<Seller, Integer> colId;
    @FXML private TableColumn<Seller, String> colEmpleado;
    @FXML private TableColumn<Seller, Integer> colCodigo;
    @FXML private TableColumn<Seller, String> colRol;
    @FXML private TableColumn<Seller, Boolean> colActivo;
    @FXML private TableColumn<Seller, Void> colAcciones;
    @FXML private Button btnGuardar;
    @FXML private Button btnClear;
    @FXML private StackPane root;
    
    private Seller selectedSeller  =null;
    private boolean modoEdicion = false;
    
    private final SellerApiClient sellerApi =
        new SellerApiClient();
    
    private final ObservableList<Seller> sellerList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        configurarTabla();
        cargarEmpleados();
        configurarEventos();
        configurarBusqueda();
        configurarCombos();
        limpiarFormulario();
        
    }

    public void configurarTabla(){
        
        PosTable.apply(tblEmpleados);
        
        // ==============================
        // DATOS DE LAS COLUMNAS
        // ==============================
        
        colId.setCellValueFactory(data ->
            new SimpleIntegerProperty(data.getValue().getId()).asObject());
        colCodigo.setCellValueFactory(data ->
            new SimpleIntegerProperty(data.getValue().getCode()).asObject());
        colEmpleado.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getName()));
        colRol.setCellValueFactory(data -> {
            Role role = data.getValue().getRole();
                return new SimpleStringProperty(
                    role != null
                    ? role.getDisplayName()
                    : ""); });
        colActivo.setCellValueFactory(data ->
            new SimpleBooleanProperty(data.getValue().getActive()).asObject());
        
        // ==============================
        // FORMATO DE LAS COLUMNAS
        // ==============================

        PosTable.integer(colId);
        PosTable.integer(colCodigo);
        PosTable.text(colEmpleado);
        PosTable.enumColumn(colRol);
        
        // ==============================
        // ACCIONES Y ESTADO
        // ==============================
        
        colActivo.setCellFactory(column ->
            TableUtils.createActiveToggle(
                Seller::getId,(id, activo) -> {

            try {

                sellerApi.changeStatus(
                    id,
                    activo);

                cargarEmpleados();

                return true;

            } catch(Exception e){

                e.printStackTrace();

                mostrarError(
                    "No se pudo actualizar el estado"
                );

                return false;
            }}));
        
        colAcciones.setCellFactory(param ->
            TableUtils.createEditButton(this::seleccionarEmpleado));
        
        colActivo.setStyle("-fx-alignment: CENTER;");
        colAcciones.setStyle("-fx-alignment: CENTER;");
        
        // ==============================
        // COMPORTAMIENTO GENERAL
        // ==============================
        
        PosTable.placeholder(
            tblEmpleados,
            "No hay empleados registrados",
            "Agrega empleados desde el formulario de la izquierda",
            "fas-user-tie"
        );

        PosTable.inactiveRows(tblEmpleados,
            Seller::isActive
        );
        
    }

    private void cargarEmpleados() {

        try {

            sellerList.setAll(sellerApi.getAll());
            tblEmpleados.setItems(sellerList);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarError(
                "No se pudieron cargar los empleados desde el servidor."
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            mostrarError(
                "La consulta de empleados fue interrumpida."
            );
        }
    }
    
    @FXML
    private void guardarEmpleado(){
        
        if (!validarFormulario()) {
            return;
        }

        SellerRequest request = buildSellerRequest();
        
        boolean eraEdicion = modoEdicion;

        boolean resultado = eraEdicion
            ? actualizarEmpleado(request)
            : guardarEmpleadoNuevo(request);

        if (resultado) {
            cargarEmpleados();
            limpiarFormulario();
            
            mostrarExito(
                eraEdicion
                        ? "Empleado actualizado correctamente."
                        : "Empleado guardado correctamente."
            );
        } else{
            mostrarError(
            eraEdicion
                    ? "No se pudo actualizar el empleado."
                    : "No se pudo guardar el empleado."
            );
        }
    }
    
    private void mostrarError(String mensaje){
        PosNotification.error(root, "Atención", mensaje);
    }
    
    private void mostrarExito(String mensaje) {
        PosNotification.success(root, "Listo", mensaje);
    }
    
    private void limpiarFormulario(){
        
        txtCodigo.clear();
        txtEmpleado.clear();
        cbRol.getSelectionModel().clearSelection();
        txtPassword.clear();
        txtConfirmarPassword.clear();
        txtPassword.setPromptText("Contraseña");
        txtConfirmarPassword.setPromptText("Confirmar contraseña");
        selectedSeller = null;
        modoEdicion = false;
        btnGuardar.setText("Guardar");
        tblEmpleados.getSelectionModel().clearSelection();
        txtCodigo.requestFocus();
    }
    
    private void seleccionarEmpleado(Seller empleado){
        
        if (empleado == null) {
            return;
        }
        
        selectedSeller = empleado;
        modoEdicion = true;

        txtCodigo.setText(
            String.valueOf(empleado.getCode())
        );

        txtEmpleado.setText(
            empleado.getName()
        );

        cbRol.setValue(
            empleado.getRole()
        );
        
        txtPassword.setPromptText(
        "Dejar vacío para conservar la contraseña");

        txtConfirmarPassword.setPromptText(
        "Confirmar nueva contraseña");

        txtPassword.clear();
        txtConfirmarPassword.clear();
        btnGuardar.setText("Actualizar");
        txtEmpleado.requestFocus();
    }

    private void configurarCombos() {
        
        cbRol.setItems(
        FXCollections.observableArrayList(Role.values()));        
    }

    private void configurarEventos() {
        
        btnGuardar.setOnAction(e -> guardarEmpleado());
        btnClear.setOnAction(e -> limpiarFormulario());
        
    }

    private void configurarBusqueda() {
        SearchUtils.setupSearch(
                txtBuscar,
                tblEmpleados,
                sellerList,
                e -> e.getName(),
                e -> String.valueOf(e.getCode()),
                e -> e.getRole() != null
                ? e.getRole().getDisplayName()
                : "");   
    }

    private boolean validarFormulario() {        
        
        String nombre = txtEmpleado.getText().trim();
        String codigoTexto = txtCodigo.getText().trim();
        String password = txtPassword.getText();
        String confirmacion = txtConfirmarPassword.getText();

        if (nombre.isEmpty()) {
            mostrarError("El nombre del empleado es obligatorio.");
            txtEmpleado.requestFocus();
            return false;
        }

        if (codigoTexto.isEmpty()) {
            mostrarError("El código del empleado es obligatorio.");
            txtCodigo.requestFocus();
            return false;
        }

        if (cbRol.getValue() == null) {
            mostrarError("Selecciona un rol.");
            cbRol.requestFocus();
            return false;
        }

        int codigo;

        try {
            codigo = Integer.parseInt(codigoTexto);

            if (codigo <= 0) {
                mostrarError("El código debe ser mayor que cero.");
                txtCodigo.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarError("El código debe contener solo números.");
            txtCodigo.requestFocus();
            return false;
        }

        Integer idExcluir =
            modoEdicion && selectedSeller != null
                    ? selectedSeller.getId()
                    : null;

        
        boolean passwordVacio =
            password == null || password.isBlank();

        boolean confirmacionVacia =
            confirmacion == null || confirmacion.isBlank();

        if (!modoEdicion && passwordVacio) {
            mostrarError("La contraseña es obligatoria para empleados nuevos.");
            txtPassword.requestFocus();
            return false;
        }

        if (passwordVacio != confirmacionVacia) {
            mostrarError("Debes completar ambos campos de contraseña.");
            txtPassword.requestFocus();
            return false;
        }

        if (!passwordVacio) {

            if (password.length() < 6) {
                mostrarError("La contraseña debe tener al menos 6 caracteres.");
                txtPassword.requestFocus();
                return false;
            }

            if (!password.equals(confirmacion)) {
                mostrarError("Las contraseñas no coinciden.");
                txtConfirmarPassword.requestFocus();
                return false;
            }
        }
        return true;
    }

    private SellerRequest  buildSellerRequest() {
        
        SellerRequest request = new SellerRequest ();

        request.setName(txtEmpleado.getText().trim());
        request.setCode(Integer.parseInt(txtCodigo.getText().trim()));
        request.setRole(cbRol.getValue());

        String password = txtPassword.getText();

        if (modoEdicion && selectedSeller != null) {

            request.setId(selectedSeller.getId());

            request.setActive(selectedSeller.getActive());

            if (password == null && password.isBlank()) {
                request.setPassword(null);
            }
            } else {
            request.setActive(true);
            request.setPassword(password);
        }
        return request;    
    }
    
    private boolean guardarEmpleadoNuevo(SellerRequest request) {

        try {

        Seller saved = sellerApi.create(request);

        return saved != null && saved.getId() > 0;

        } catch (IOException e) {

            e.printStackTrace();
            return false;

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    private boolean actualizarEmpleado(SellerRequest request) {

        if (selectedSeller == null) {

            mostrarError("No hay un empleado seleccionado para actualizar.");
            return false;
        }

        try {

            Seller updated = sellerApi.update(request);

            return updated != null && updated.getId() > 0;

        } catch (IOException e) {

            e.printStackTrace();
            return false;

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    private boolean cambiarEstado(
        int id,
        boolean active
) {

    try {

        Seller updated =
                sellerApi.changeStatus(
                        id,
                        active
                );

        return updated != null;

    } catch (IOException e) {

        e.printStackTrace();

        mostrarError(
                "No se pudo cambiar el estado del empleado."
        );

        return false;

    } catch (InterruptedException e) {

        Thread.currentThread().interrupt();

        mostrarError(
                "La operación fue interrumpida."
        );

        return false;
    }
}
    
       
}
