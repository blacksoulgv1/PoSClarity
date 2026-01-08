package com.gerardgv.posclarity.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class MainController implements Initializable {

    @FXML
    private StackPane stackContent;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
         // Metodo para iniciar la ventana de ventas al abrir
        viewVista("Ventaview");
    }    

     // Metodo para el llamado de ventanas
    private void viewVista(String vista) {
        try{
            Parent root = FXMLLoader.load(
                getClass().getResource(
                        "/com/gerardgv/posclarity/views/"+ vista + ".fxml"));
            stackContent.getChildren().setAll(root);
        } catch(IOException e){
            System.out.println("Error al cargar la vista:" + vista);
            e.printStackTrace();
        }
    }
    
    // Funcionamiento de botonos para ventanas
    
    @FXML
    private void abrirNuevaVenta(){
        viewVista("Ventaview");
    }
    
    @FXML
    private void abrirPendintes(){
        viewVista("Ventas_pendientes");
    }
    
    @FXML
    private void abrirEntregados(){
        viewVista("ventas_entregadas");
    }
    
    @FXML
    private void abrirProductos(){
        viewVista("productos");
    }
    
    @FXML
    private void abrirClientes(){
        viewVista("clientes");
    }

}
