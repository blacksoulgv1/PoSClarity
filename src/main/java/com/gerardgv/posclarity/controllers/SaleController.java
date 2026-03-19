package com.gerardgv.posclarity.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;


public class SaleController implements Initializable {
    
    // ====== DATOS GENERALES ======
    @FXML private TextField txtColaborador;
    @FXML private TextField txtSucursal;
    @FXML private TextField txtDireccionSuc;
    @FXML private TextField txtTelefonoSuc;
    @FXML private TextField txtDate;
    @FXML private TextField txtNote;
    @FXML private TextField txtCliente;
    @FXML private TextField txtDireccionClient;
    @FXML private TextField txtTelefonoClient;
    
    // ====== GRADUACIÓN ======
    @FXML private TextField txtEsfOD;
    @FXML private TextField txtEsfOI;
    @FXML private TextField txtCylOD;
    @FXML private TextField txtCylOI;
    @FXML private TextField txtEjeOD;
    @FXML private TextField txtEjeOI;
    @FXML private TextField txtAdd;
    
    // ====== PRODUCTO ======
    //@FXML private TextField txtPrecio;
    //@FXML private TextField txtStock;
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        aplicarComportamientos();
    }    

    private void aplicarComportamientos() {
        
        /*Limpiar Campos de Texto*/
        configurarAutoLimpieza(txtColaborador);
        //configurarAutoLimpieza(txtNombreVendedor);
        //configurarAutoLimpieza(txtTienda);
        configurarAutoLimpieza(txtCliente);
        //configurarAutoLimpieza(txtDireccion);
        //configurarAutoLimpieza(txtTelefono);
        
        /*Valida que solo acepte numeracion Enteros y Decimales*/
        NumericalValidation(txtEsfOD);
        NumericalValidation(txtCylOD);
        NumericalValidation(txtEsfOI);
        NumericalValidation(txtCylOI);        
        //NumericalValidation(txtPrecio);
        
        /*Validacion Solo Positivos de Add*/
        ValidationAdd(txtAdd);
        
        /*Valida que solo acepte numeracion Enteros*/
        NumericalValidation2(txtEjeOD);
        NumericalValidation2(txtEjeOI);
        //NumericalValidation2(txtStock);
        
    }
    
    /*Metodo para Limpiar Campos de Texto*/    
    private void configurarAutoLimpieza(TextField txt){
        
        txt.focusedProperty().addListener((obs,oldVal,newVal) -> {
            if(newVal){ // Cuando entre al Texto.
                txt.setStyle("-fx-background-color:white;");
                
                //Si solo tiene espacios limpia el texto.
                if(txt.getText().trim().isEmpty()){
                    txt.clear();
                }
            }else{
                txt.setStyle("");
            }
        });
        
    }
    
    /*Metodo para Decimales y Numeros*/    
    private void NumericalValidation(TextField txt){
        txt.setTextFormatter(new TextFormatter<>(c -> {
            if(c.getControlNewText().matches("-?\\d*(\\.\\d*)?")){
                return c;
            }
            return null;
        }));
    }
    
    private void NumericalValidation2(TextField txt){
        txt.setTextFormatter(new TextFormatter<>(c -> {
            if(c.getControlNewText().matches("\\d*")){
                return c;
            }
            return null;
        }));
    }
    
    private void ValidationAdd(TextField txt){
        txt.setTextFormatter(new TextFormatter<>(c ->{
            if(c.getControlNewText().matches("\\d*(\\.\\d*)?")){
                return c;
            }
            return null;
        }));
    }
    
}
