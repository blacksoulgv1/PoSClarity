package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.database.BranchDAO;
import com.gerardgv.posclarity.models.Branch;
import java.net.URL;
import java.util.ResourceBundle;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

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
    @FXML private TableColumn<Branch, String> colEstatus;
    @FXML private TableColumn<Branch, Void> colAcciones;

    private final BranchDAO branchDAO = new BranchDAO();
    private ObservableList<Branch> listaSucursales = FXCollections.observableArrayList();
    private FilteredList<Branch> filteredData;

    private Branch sucursalSeleccionada = null;
    private boolean modoEdicion = false;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        configurarSeleccion();
        cargarSucursales();
        configurarBuscador();
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
            new SimpleStringProperty(data.getValue().isActivo()? "Activo": "Inactivo"));        
        
        colAcciones.setCellFactory(col -> new TableCell<Branch, Void>(){
            private final Button btnEditar = new Button("Editar");
            private final Button btnToggle =  new Button();
            {
                btnEditar.setOnAction(e -> {
                    Branch b = getTableView().getItems().get(getIndex());
                    seleccionarSucursalDesdeTabla(b);
                });
                
                btnToggle.setOnAction(e -> {
                    Branch b = getTableView().getItems().get(getIndex());
                    
                    boolean nuevoEstado = !b.isActivo();
                    
                    if(branchDAO.updateEstado(b.getId(), nuevoEstado)){
                        b.setActivo(nuevoEstado);
                        tblSucursales.refresh();
                    }
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty){
                super.updateItem(item, empty);
                
                if(empty){
                    setGraphic(null);
                } else {
                    Branch b = getTableView().getItems().get(getIndex());
                    btnToggle.setText(b.isActivo() ? "Desactivar" : "Activar");
                    
                    HBox box = new HBox(5,btnEditar, btnToggle);
                    setGraphic(box);
                }
            }
        });
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
    }

    private void configurarSeleccion() {
        tblSucursales.getSelectionModel().selectedItemProperty().addListener(
            (obs,oldSel,sucursal) -> {
                if(sucursal != null){
                    sucursalSeleccionada = sucursal;
                    modoEdicion = true;
                    
                    txtSucursal.setText(sucursal.getSucursal());
                    txtTelefono.setText(sucursal.getTelefono());
                    txtDireccion.setText(sucursal.getDireccion());
                    
                    btnGuardar.setText("Actualizar");
                }
            });
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
    private void seleccionarSucursal(){
        sucursalSeleccionada = tblSucursales.getSelectionModel().getSelectedItem();
        
        if (sucursalSeleccionada != null) {

        txtSucursal.setText(sucursalSeleccionada.getSucursal());
        txtTelefono.setText(sucursalSeleccionada.getTelefono());
        txtDireccion.setText(sucursalSeleccionada.getDireccion());

        btnGuardar.setText("Actualizar");
        }        
    }

    private void configurarBuscador() {
        filteredData = new FilteredList<>(listaSucursales, b-> true);
        
        txtBuscar.textProperty().addListener((observable, oldValue, newValue)-> {
            filteredData.setPredicate(branch ->{
                
                if(newValue == null || newValue.isEmpty()){
                    return true;
                }
                
                String filtro = newValue.toLowerCase();
                
            if (String.valueOf(branch.getId()).contains(filtro)) {
                return true;
            }

            if (branch.getSucursal().toLowerCase().contains(filtro)) {
                return true;
            }
            if (branch.getDireccion().toLowerCase().contains(filtro)) {
                return true;
            }
            if (branch.getTelefono().toLowerCase().contains(filtro)) {
                return true;
            }

            return false;                
            });
        });
        
        SortedList<Branch> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tblSucursales.comparatorProperty());
        tblSucursales.setItems(sortedData);
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
