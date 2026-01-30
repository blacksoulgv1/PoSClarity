package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.models.Product;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;


public class ProductsController implements Initializable {
    
    // ===== TextFields =====
    @FXML private TextField txtModelo;
    @FXML private TextField txtMarca;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtStock;
    @FXML private TextField txtBuscar;
    
    // ===== Box =====
    @FXML private CheckBox chkActivo;
    @FXML private ComboBox<String> cbCategoria;
    @FXML private ComboBox<String> cbTipo;
    
    // ===== Buttons =====
    @FXML private Button btnNuevo;
    @FXML private Button btnGuardar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnBuscar;
    
    // ===== Tabla =====
    @FXML private TableView<Product> tblProductos;
    
    @FXML private TableColumn<Product,String> colId;
    @FXML private TableColumn<Product,String> colModelo;
    @FXML private TableColumn<Product,String> colMarca;
    @FXML private TableColumn<Product,String> colCategoria;
    @FXML private TableColumn<Product,String> colTipo;
    @FXML private TableColumn<Product,Double> colPrecio;
    @FXML private TableColumn<Product,Integer> colStock;
    @FXML private TableColumn<Product,String> colEstatus;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        controlarStock();
    }    
    
    private void controlarStock(){
        cbTipo.getItems().addAll("Físico","Bajo pedido","Servicio");
        
        cbTipo.valueProperty().addListener((obs,oldval,newVal) ->{
            if(newVal.equals("Físico")){
                txtStock.setDisable(false);
            } else{
                txtStock.setText("0");
                txtStock.setDisable(true);
            }
        });
       
    }
    
}
