package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.database.GarantiasDAO;
import com.gerardgv.posclarity.database.SaleDAO;
import com.gerardgv.posclarity.models.SaleItem;
import com.gerardgv.posclarity.models.Venta;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

public class GarantiasController implements Initializable {

    @FXML private TextField txtFolioVenta;
    @FXML private Label lblFolio;
    @FXML private Button btnBuscar;
    @FXML private Button btnGuardar;
    @FXML private Label lblCliente;
    @FXML private Label lblProducto;
    @FXML private ComboBox<String> cmbMotivo;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private TextArea txtObservaciones;
    @FXML private TableView<SaleItem> tblProductosVenta;
    @FXML private TableColumn<SaleItem,String> colProductoVenta;
    @FXML private TableColumn<SaleItem,Integer> colCantidadVenta;
    
    private ObservableList<SaleItem> productosVenta = FXCollections.observableArrayList();
    private GarantiasDAO garantiaDAO = new GarantiasDAO();
    private SaleDAO saleDAO = new SaleDAO();
    private Venta ventaSeleccionada;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        lblFolio.setText(garantiaDAO.generarFolio());
        cargarCombos();
        cargarTablaProductos();
        btnBuscar.setOnAction(e -> buscarVenta());
        btnGuardar.setOnAction(e -> guardarGarantia() );
    }
    
    private void cargarCombos(){
        
        cmbMotivo.getItems().addAll(
            "Defecto de fábrica",
            "Rayadura",
            "Mica maltratada",
            "Error de graduación",
            "Otro"
        );
         
        cmbEstado.getItems().addAll(
            "RECIBIDA",
            "EN REVISIÓN",
            "LABORATORIO",
            "ENTREGADA"
        );
        
        cmbEstado.setValue("RECIBIDA");
    }

    private void buscarVenta(){        
        String folio = txtFolioVenta.getText();


        if(folio.isEmpty()){
            alerta("Ingresa folio");
            return;
        }

        try{    
            int idVenta = Integer.parseInt(folio);
            ventaSeleccionada = saleDAO.buscarPorFolio(idVenta);

            if(ventaSeleccionada == null){
                alerta("Venta no encontrada");
                return;
            }

            lblCliente.setText(ventaSeleccionada.getCliente().getNombre());


        cargarProductosVenta();


    }catch(NumberFormatException e){

        alerta("Folio inválido");

    }
  
    }
    
    private void guardarGarantia(){
        
        if(lblCliente.getText().isEmpty()){

            alerta("Busca una venta primero");
            return;
        }


        // aquí va insertar en GarantiaDAO


        alerta("Garantía registrada");
    }
    
    private void alerta(String m){

        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText(m);
        a.showAndWait();
    }

    private void cargarTablaProductos() {
        colProductoVenta.setCellValueFactory(data ->
        new SimpleStringProperty(
            data.getValue()
            .getProducto()
            .getModelo()
        )
    );


    colCantidadVenta.setCellValueFactory(data ->
        new SimpleIntegerProperty(
            data.getValue()
            .getCantidad()
        ).asObject()
    );


    tblProductosVenta.setItems(productosVenta);
    }

    private void cargarProductosVenta() {
        productosVenta.clear();
        productosVenta.addAll(
            saleDAO.obtenerDetalleVenta(
            ventaSeleccionada.getId()));    
    }
    
    
}
