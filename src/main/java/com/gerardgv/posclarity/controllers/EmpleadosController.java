package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.Ui.*;
import com.gerardgv.posclarity.database.EmpleadosDAO;
import com.gerardgv.posclarity.models.Empleados;
import com.gerardgv.posclarity.utils.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;



public class EmpleadosController implements Initializable {
    
    @FXML private TextField txtCodigo;
    @FXML private TextField txtEmpleado;
    @FXML private TextField txtBuscar;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmarPassword;
    @FXML private ComboBox<String> cbRol;
    
    @FXML private TableView<Empleados> tblEmpleados;
    @FXML private TableColumn<Empleados, Integer> colId;
    @FXML private TableColumn<Empleados, String> colEmpleado;
    @FXML private TableColumn<Empleados, Integer> colCodigo;
    @FXML private TableColumn<Empleados, String> colRol;
    @FXML private TableColumn<Empleados, Boolean> colActivo;
    @FXML private TableColumn<Empleados, Void> colAcciones;
    @FXML private Button btnGuardar;
    @FXML private Button btnClear;
    @FXML private StackPane root;
    
    private Empleados empleadoSeleccionado =null;
    private boolean modoEdicion = false;
    
    private final EmpleadosDAO dao = new EmpleadosDAO();
    private final ObservableList<Empleados> lista = FXCollections.observableArrayList();

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
            new SimpleIntegerProperty(data.getValue().getId_vendedor()).asObject());
        colCodigo.setCellValueFactory(data ->
            new SimpleIntegerProperty(data.getValue().getCodigo()).asObject());
        colEmpleado.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getNombre()));
        colRol.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getRol()));
        colActivo.setCellValueFactory(data ->
            new SimpleBooleanProperty(data.getValue().isActivo()).asObject());
        
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
        
        colActivo.setCellFactory(colum ->
            TableUtils.createActiveToggle(
                    Empleados::getId_vendedor,
                    dao::cambiarStatus));
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

        PosTable.inactiveRows(
            tblEmpleados,
            Empleados::isActivo
        );
        
    }

    private void cargarEmpleados() {
        lista.setAll(dao.findAll());
        tblEmpleados.setItems(lista);
    }
    
    @FXML
    private void guardarEmpleado(){
        
        if (!validarFormulario()) {
            return;
        }

        Empleados empleado = construirEmpleado();
        boolean eraEdicion = modoEdicion;

        boolean resultado = eraEdicion
            ? actualizarEmpleado(empleado)
            : guardarEmpleadoNuevo(empleado);

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
        empleadoSeleccionado = null;
        modoEdicion = false;
        btnGuardar.setText("Guardar");
        tblEmpleados.getSelectionModel().clearSelection();
        txtCodigo.requestFocus();
    }
    
    private void seleccionarEmpleado(Empleados empleado){
        
        if (empleado == null) {
            return;
        }
        
        empleadoSeleccionado = empleado;
        modoEdicion = true;

        txtCodigo.setText(
            String.valueOf(empleado.getCodigo())
        );

        txtEmpleado.setText(
            empleado.getNombre()
        );

        cbRol.setValue(
            empleado.getRol()
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
        
        cbRol.setItems(FXCollections.observableArrayList(
                "vendedor",
                "gerente",
                "directivo"
        ));
        
    }

    private void configurarEventos() {
        
        btnGuardar.setOnAction(e -> guardarEmpleado());
        btnClear.setOnAction(e -> limpiarFormulario());
        
    }

    private void configurarBusqueda() {
        SearchUtils.setupSearch(txtBuscar,tblEmpleados,lista,
                e -> e.getNombre(),
                e -> String.valueOf(e.getCodigo()),
                e -> e.getRol());    
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
            modoEdicion && empleadoSeleccionado != null
                    ? empleadoSeleccionado.getId_vendedor()
                    : null;

        if (dao.existeCodigo(codigo, idExcluir)) {
            mostrarError("El código ya pertenece a otro empleado.");
            txtCodigo.requestFocus();
            return false;
        }

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

    private Empleados construirEmpleado() {
        
        Empleados empleado = new Empleados();

        empleado.setNombre(txtEmpleado.getText().trim());
        empleado.setCodigo(Integer.parseInt(txtCodigo.getText().trim()));
        empleado.setRol(cbRol.getValue());

        String password = txtPassword.getText();

        if (modoEdicion && empleadoSeleccionado != null) {

            empleado.setId_vendedor(empleadoSeleccionado.getId_vendedor());

            empleado.setActivo(empleadoSeleccionado.isActivo());

            if (password == null || password.isBlank()) {
                empleado.setPasswordHash(null);
            } else {
                empleado.setPasswordHash(PasswordUtils.hash(password));
            }
        } else {
            empleado.setActivo(true);
            empleado.setPasswordHash(PasswordUtils.hash(password));
        }
        return empleado;    
    }
    
    private boolean guardarEmpleadoNuevo(Empleados empleado) {
        return dao.insert(empleado);
    }
    
    private boolean actualizarEmpleado(Empleados empleado) {

        if (empleadoSeleccionado == null) {
            mostrarError("No hay un empleado seleccionado para actualizar.");
            return false;
        }
            return dao.update(empleado);
    }
    
}
