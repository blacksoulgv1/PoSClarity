
package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.database.EmpleadosDAO;
import com.gerardgv.posclarity.models.Empleados;
import com.gerardgv.posclarity.utils.SearchUtils;
import com.gerardgv.posclarity.utils.TableUtils;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;



public class EmpleadosController implements Initializable {
    
    @FXML private TextField txtCodigo;
    @FXML private TextField txtEmpleado;
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbRol;
    
    @FXML private TableView<Empleados> tblEmpleados;
    @FXML private TableColumn<Empleados, Integer> colId;
    @FXML private TableColumn<Empleados, String> colEmpleado;
    @FXML private TableColumn<Empleados, Integer> colCodigo;
    @FXML private TableColumn<Empleados, String> colRol;
    @FXML private TableColumn<Empleados, Boolean> colActivo;
    @FXML private TableColumn<Empleados, Void> colAcciones;
    
    private Empleados empleadoSeleccionado =null;
    private boolean modoEdicion = false;
    
    private EmpleadosDAO dao = new EmpleadosDAO();
    private ObservableList<Empleados> lista = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        configurarTabla();
        cargarEmpleados();
        
        cbRol.setItems(FXCollections.observableArrayList(
                "vendedor",
                "gerente",
                "directivo"
        ));
        
        SearchUtils.setupSearch(txtBuscar,tblEmpleados,lista,
                e -> e.getNombre(),
                e -> String.valueOf(e.getCodigo()),
                e -> e.getRol());
        colActivo.setCellFactory(colum ->
            TableUtils.createActiveToggle(
                    Empleados::getId_vendedor,
                    dao::cambiarStatus));
        colAcciones.setCellFactory(param ->
            TableUtils.createEditButton(this::seleccionar));
        
    }

    public void configurarTabla(){
        colId.setCellValueFactory(data ->
            new SimpleIntegerProperty(data.getValue().getId_vendedor()).asObject());
        colEmpleado.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getNombre()));
        colCodigo.setCellValueFactory(data ->
            new SimpleIntegerProperty(data.getValue().getCodigo()).asObject());
        colRol.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getRol()));
        colActivo.setCellValueFactory(data ->
            new SimpleBooleanProperty(data.getValue().isActivo()).asObject());
    tblEmpleados.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void cargarEmpleados() {
        lista.clear();
        lista.addAll(EmpleadosDAO.findAll());
        tblEmpleados.setItems(lista);
    }
    
    @FXML
    private void guardar(){
        if(txtEmpleado.getText().isEmpty() || txtCodigo.getText().isEmpty()){
            mostrarMensaje("Completa los campos");
            return;
        }
        
        Empleados e = new Empleados();
        e.setNombre(txtEmpleado.getText());
        e.setCodigo(Integer.parseInt(txtCodigo.getText()));
        e.setRol(cbRol.getValue());
        e.setActivo(true);
        
        boolean ok;
        
        if(modoEdicion && empleadoSeleccionado != null){
            e.setId_vendedor(empleadoSeleccionado.getId_vendedor());
            ok = dao.update(e);
        } else {
            ok = dao.insert(e);
        }
        if(ok){
            mostrarMensaje("Guardado Correctamente");
            cargarEmpleados();
            limpiar();
        } else{
            mostrarMensaje("Error al Guardar");
        }
    }
    
    private void mostrarMensaje(String mensaje){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private void limpiar(){
        txtEmpleado.clear();
        txtCodigo.clear();
        cbRol.setValue(null);
        //btnGuardar.setText("Guardar");
    }
    
    private void seleccionar(Empleados e){
        empleadoSeleccionado = e;
        modoEdicion = true;
        
        txtEmpleado.setText(e.getNombre());
        txtCodigo.setText(String.valueOf(e.getCodigo()));
        cbRol.setValue(e.getRol());
    }
}
