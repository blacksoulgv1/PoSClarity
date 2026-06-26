package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.app.App;
import com.gerardgv.posclarity.database.*;
import com.gerardgv.posclarity.utils.Configuracion;
import javafx.fxml.FXML;
import javafx.scene.control.*;


public class ConfiguracionInicialController {
    
    @FXML private TextField txtSucursal;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtTelefono;   

    private boolean procesando = false;

@FXML
    private void crearSistema(){
        
        if(procesando){
            return;
        }
        
        procesando = true;
    
         try{
                String nombre = txtSucursal.getText().trim();
                String direccion = txtDireccion.getText().trim();
                String telefono = txtTelefono.getText().trim();
        
            if(nombre.isEmpty()){
                mostrarError("El Nombre de la Sucursal es Obligatorio");
            return;
        }
        
        int idSucursal = InstalacionDAO.crearSucursal(nombre, direccion, telefono);
        
        if(idSucursal <=0 ){
            mostrarError("No se pudo Crear El Sistema");
            return;
        }
        
        Configuracion.guardarSucursal(idSucursal);
        
        mostrarMensaje("PosClarity configurado correctamente");
        
        App.setRoot("main");

        } catch (Exception e) {
         mostrarMensaje("Error");
        } finally{
           procesando = false;  
         }
    }
    
    private void mostrarMensaje(String msg){
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText(msg);
        a.showAndWait();
    }
    
    private void mostrarError(String mensaje){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
}
