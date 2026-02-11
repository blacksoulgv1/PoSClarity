package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.models.Clients;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;


public class ClientsController implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtOdEsf;
    @FXML private TextField txtOdCil;
    @FXML private TextField txtOdEje;
    @FXML private TextField txtOiEsf;
    @FXML private TextField txtOiCil;
    @FXML private TextField txtOiEje;
    @FXML private TextField txtAdd;
    
    @FXML private Button btnNuevo;
    @FXML private Button btnGuardar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnDescativar;
    
    @FXML private TableView<Clients> tblClientes;
    @FXML private TableColumn<Clients, Integer> colId;
    @FXML private TableColumn<Clients, String> colNombre;
    @FXML private TableColumn<Clients, String> colTelefono;
    @FXML private TableColumn<Clients, String> colDireccion;
    @FXML private TableColumn<Clients, String> colGraduacion;
    @FXML private TableColumn<Clients, Void> colAcciones;
    
    private ObservableList<Clients> listaClientes = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        configurarSeleccion();
        btnNuevo.setOnAction(e -> newClient());
        btnGuardar.setOnAction(e -> saveClient());
        btnLimpiar.setOnAction(e -> clearform());
        btnDescativar.setOnAction(e -> desactivarCliente());
    }    

    private void configurarTabla() {
        
        colId.setCellValueFactory(data -> 
        new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject()
    );

        colNombre.setCellValueFactory(data ->
        new SimpleStringProperty(data.getValue().getNombre())
    );

        colTelefono.setCellValueFactory(data ->
        new SimpleStringProperty(data.getValue().getTelefono())
    );
        
        colDireccion.setCellValueFactory(data ->
        new SimpleStringProperty(data.getValue().getDireccion())
    );
        
        colGraduacion.setCellValueFactory(data -> {
        Clients c = data.getValue();

        String grad = String.format(
                "OD: %s / %s x %s\nOI: %s / %s x %s\nADD: %s",
                c.getOdEsf(), c.getOdCil(), c.getOdEje(),
                c.getOiEsf(), c.getOiCil(), c.getOiEje(),
                c.getAdd()
        );

        return new SimpleStringProperty(grad);
    });

    tblClientes.setItems(listaClientes);
        
    }

    private void newClient(){
        clearform();
        tblClientes.getSelectionModel().clearSelection();
    }
    
    private void saveClient(){
        Clients selecc = tblClientes.getSelectionModel().getSelectedItem();
        
        if(selecc == null){
            Clients nuevo = new Clients();
            cargarDatos(nuevo);
            listaClientes.add(nuevo);
        } else{
            cargarDatos(selecc);
            tblClientes.refresh();
        }
        clearform();
        tblClientes.getSelectionModel().clearSelection();
    }
    
    private void cargarDatos(Clients c) {
        c.setNombre(txtNombre.getText());
        c.setTelefono(txtTelefono.getText());
        c.setDireccion(txtDireccion.getText());

        c.setOdEsf(txtOdEsf.getText());
        c.setOdCil(txtOdCil.getText());
        c.setOdEje(txtOdEje.getText());

        c.setOiEsf(txtOiEsf.getText());
        c.setOiCil(txtOiCil.getText());
        c.setOiEje(txtOiEje.getText());

        c.setAdd(txtAdd.getText());
        
    }

    private void configurarSeleccion() {
        
         tblClientes.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, cliente) -> {
        if (cliente != null) {

            txtNombre.setText(cliente.getNombre());
            txtTelefono.setText(cliente.getTelefono());
            txtDireccion.setText(cliente.getDireccion());

            txtOdEsf.setText(cliente.getOdEsf());
            txtOdCil.setText(cliente.getOdCil());
            txtOdEje.setText(cliente.getOdEje());

            txtOiEsf.setText(cliente.getOiEsf());
            txtOiCil.setText(cliente.getOiCil());
            txtOiEje.setText(cliente.getOiEje());

            txtAdd.setText(cliente.getAdd());
        }
    });

    }

    private void clearform() {
        txtNombre.clear();
        txtTelefono.clear();
        txtDireccion.clear();
        txtOdEsf.clear();
        txtOdCil.clear();
        txtOdEje.clear();
        txtOiEsf.clear();
        txtOiCil.clear();
        txtOiEje.clear();
        txtAdd.clear();
    }
    
    private void desactivarCliente(){
        
         Clients selecc = tblClientes.getSelectionModel().getSelectedItem();
         if (selecc != null) {
            selecc.setActivo(false);
        listaClientes.remove(selecc);
            clearform();
    }
    }
        
}
