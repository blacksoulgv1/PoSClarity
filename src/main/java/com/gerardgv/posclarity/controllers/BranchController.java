package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.database.BranchDAO;
import com.gerardgv.posclarity.models.Branch;
import com.gerardgv.posclarity.models.Product;
import com.gerardgv.posclarity.utils.SearchUtils;
import com.gerardgv.posclarity.utils.TableUtils;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

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

    private final BranchDAO branchDAO = new BranchDAO();
    private ObservableList<Branch> listaSucursales = FXCollections.observableArrayList();
    private FilteredList<Branch> filteredData;

    private Branch sucursalSeleccionada = null;
    private boolean modoEdicion = false;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        configurarTabla();
        cargarSucursales();
        
        SearchUtils.setupSearch(txtBuscar, tblSucursales, listaSucursales,
                s -> s.getSucursal(),
                s -> s.getDireccion());
        
        tblSucursales.setSelectionModel(null);
        
        colEstatus.setCellFactory(column ->
                TableUtils.createActiveToggle(
                Branch::getId,
                branchDAO::updateEstado));
        
        colAcciones.setCellFactory(param ->
            TableUtils.createEditButton(this::seleccionarSucursalDesdeTabla));
        
        txtBuscar.sceneProperty().addListener((obs,oldScene,scene)->{
            if(scene != null){
                scene.setOnKeyPressed(e->{
                    if(e.getCode().toString().equals("ESCAPE")){
                        clearform();
                    }
                });
            }
        });
    }

    private void cargarSucursales() {
        listaSucursales.clear();
        listaSucursales.addAll(branchDAO.getAll());
    }
    
    private void configurarTabla(){
        
        colId.setCellValueFactory(data ->
            new SimpleIntegerProperty(data.getValue().getId()).asObject());
        
        colSucursal.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getSucursal()));
        
        colDireccion.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getDireccion()));
        
        colTelefono.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getTelefono()));
        
        colEstatus.setCellValueFactory(data ->
            new SimpleBooleanProperty(data.getValue().isActivo()).asObject());
        
        tblSucursales.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    
    @FXML
    private void saveBranch(){      
                
        if(!validarCampos()){
            mostrarError("Complete los campos");
            return;
        }
        
        Branch b = new Branch();

        b.setSucursal(txtSucursal.getText());
        b.setTelefono(txtTelefono.getText());
        b.setDireccion(txtDireccion.getText());

        boolean resultado;
        
        if(modoEdicion){
            b.setId(sucursalSeleccionada.getId());
            b.setActivo(sucursalSeleccionada.isActivo());
            resultado = branchDAO.update(b);
        } else {
            b.setActivo(true);
            resultado = branchDAO.insert(b);
        }
        
        if(resultado){
            mostrarInfo("Sucursal Guardada");
            clearform();
            cargarSucursales();
        } else {
            mostrarError("No se Pudo Guardar");
        }
    }

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
    
    private void seleccionarSucursalDesdeTabla(Branch b){
        
        sucursalSeleccionada = b;
        modoEdicion = true;
        
        txtSucursal.setText(b.getSucursal());
        txtTelefono.setText(b.getTelefono());
        txtDireccion.setText(b.getDireccion()); 
        btnGuardar.setText("Actualizar");
    }
    
    private void mostrarError(String mensaje){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private void mostrarInfo(String mensaje){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private boolean validarCampos(){
        
        boolean valido = true;
        
        if (txtSucursal.getText().isEmpty()){
            txtSucursal.setStyle("-fx-border-color:red;");
            valido = false;
        }else{
            txtSucursal.setStyle("");
        }
        
        if(txtTelefono.getText().isEmpty()){
            txtTelefono.setStyle("-fx-border-color:red;");
            valido = false;
        } else {
            txtTelefono.setStyle("");
        }

        if(txtDireccion.getText().isEmpty()){
            txtDireccion.setStyle("-fx-border-color:red;");
            valido = false;
        } else {
            txtDireccion.setStyle("");
        }
    return valido;
    }
    
    @FXML
    private void nuevoRegistro(){
        clearform();
        modoEdicion = false;
        sucursalSeleccionada = null;
        
        tblSucursales.getSelectionModel().clearSelection();
        btnGuardar.setText("Guardar");
        txtSucursal.requestFocus();
   }

}
