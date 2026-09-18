package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.api.InstallationApiClient;
import com.gerardgv.posclarity.app.App;
import com.gerardgv.posclarity.utils.Configuracion;
import java.math.BigDecimal;
import javafx.fxml.FXML;
import javafx.scene.control.*;


public class ConfiguracionInicialController {
    
    @FXML private TextField txtSucursal;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCajaInicial; 

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
                String cajaTexto = txtCajaInicial.getText().trim();
        
            if(nombre.isEmpty()){
                mostrarError("El Nombre de la Sucursal es Obligatorio");
                return;
            }
            
            BigDecimal cajaInicial = BigDecimal.ZERO;
             
            if (!cajaTexto.isEmpty()) {
                try {
                    cajaInicial = new BigDecimal(cajaTexto);
                } catch (NumberFormatException e) {
                    mostrarError("La Caja Inicial no es válida");
                    return;
                }
            }

            if (cajaInicial.compareTo(BigDecimal.ZERO) < 0) {
                mostrarError("La Caja Inicial no puede ser negativa");
                return;
            }
            
             InstallationApiClient apiClient =
                    new InstallationApiClient();

            int idSucursal = apiClient.createInstallation(
                    nombre,
                    direccion,
                    telefono,
                    cajaInicial
            );
        
        
        if(idSucursal <=0 ){
            mostrarError("No se pudo Crear El Sistema");
            return;
        }
        
        Configuracion.guardarSucursal(idSucursal);
        
        mostrarMensaje("PosClarity configurado correctamente");
        
        App.setRoot("main");

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("Error al configurar PosClarity");
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
