package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.Ui.PosNotification;
import com.gerardgv.posclarity.api.BranchApiClient;
import com.gerardgv.posclarity.models.*;
import com.gerardgv.posclarity.utils.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

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
    
    private final BranchApiClient branchApiClient = new BranchApiClient();
        
    private ObservableList<Branch> listaSucursales = FXCollections.observableArrayList();


    private Branch sucursalSeleccionada = null;
    private boolean modoEdicion = false;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        configurarTabla();
        cargarSucursales();
        formatearCombos();
        
        SearchUtils.setupSearch(txtBuscar, tblSucursales, listaSucursales,
                s -> s.getName(),
                s -> s.getAddress());

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
    
    /*
    TABLA YA CONECTADA A "POSAPI"
    */

    private void cargarSucursales() {
        
        try{
            
            listaSucursales.setAll(branchApiClient.getAll());
            
        } catch( IOException  e){
            
            mostrarError("No Fue Posible Consultar las Sucursales Desde la API.\n"            
                    + e.getMessage());
            
        } catch(InterruptedException e){
            
            Thread.currentThread().interrupt();
            mostrarError("La consulta de sucursales fue interrumpida.");
            
        }
    }
    
    private void configurarTabla(){
        
        colId.setCellValueFactory(data ->
            new SimpleIntegerProperty(data.getValue().getId()).asObject());
        
        colSucursal.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getName()));
        
        colDireccion.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getAddress()));
        
        colTelefono.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getPhone()));
        
        colEstatus.setCellValueFactory(data ->
            new SimpleBooleanProperty(data.getValue().isActive()).asObject());
        
        tblSucursales.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    
    @FXML
    private void saveBranch(){      
                
        if(!validarCampos()){
            mostrarError("Complete los campos");
            return;
        }
        
        Branch branch = new Branch();

        branch.setName(txtSucursal.getText());
        branch.setPhone(txtTelefono.getText());
        branch.setAddress(txtDireccion.getText());

        boolean result;
        
        if(modoEdicion){
            
            branch.setId(sucursalSeleccionada.getId());
            branch.setCode(sucursalSeleccionada.getCode());
            branch.setActive(sucursalSeleccionada.isActive());
            
            try{
                
                Branch updateBranch = branchApiClient.update(branch);
                result = updateBranch != null;
                
            } catch (IOException e){
                
                mostrarError(
                "No fue posible actualizar la sucursal mediante la API.\n"
                + e.getMessage());
                return;
                
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
                mostrarError("La actualización de la sucursal fue interrumpida.");
                return;
            }
            
        } else {
            
            branch.setActive(true);

            try {
                
                Branch createdBranch = branchApiClient.create(branch);
                result = createdBranch != null;
                
            } catch (IOException e) {
                
                mostrarError("No fue posible guardar la sucursal mediante la API.\n"
                    + e.getMessage());
                return;
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
                mostrarError("El registro de la sucursal fue interrumpido.");
                return;
            }
        }
        
        if(result){
            mostrarExito(modoEdicion ? "Sucursal Actualizada Correctamente."
                    : "Sucursal Guardada Correctamente.");
            clearform();
            cargarSucursales();
        } else {
            mostrarError("No se Pudo Guardar Sucursal");
        }
    }

    @FXML
    private void clearform() {
        txtSucursal.clear();
        txtTelefono.clear();
        txtDireccion.clear();

        sucursalSeleccionada = null;
        modoEdicion = false;

        if(tblSucursales.getSelectionModel() != null){
            tblSucursales.getSelectionModel().clearSelection();
        }
                
        btnGuardar.setText("Guardar");
        txtSucursal.requestFocus();
    }
    
    private void seleccionarSucursalDesdeTabla(Branch b){
        
        sucursalSeleccionada = b;
        modoEdicion = true;
        
        txtSucursal.setText(b.getName());
        txtTelefono.setText(b.getPhone());
        txtDireccion.setText(b.getAddress()); 
        btnGuardar.setText("Actualizar");
    }
    
    private void mostrarError(String mensaje){
        PosNotification.error(root, "Atención", mensaje);
    }
    
    private void mostrarExito(String mensaje){
        PosNotification.success(root, "Listo", mensaje);
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
   }

    private void formatearCombos() {
        
        colEstatus.setCellFactory(column ->
                TableUtils.createActiveToggle(
                Branch::getId,
                (id,active) ->
                 branchApiClient.updateStatus(id, active)!= null));
        
        colAcciones.setCellFactory(param ->
            TableUtils.createEditButton(this::seleccionarSucursalDesdeTabla));
        
    }

}
