/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.database.SaleDAO;
import com.gerardgv.posclarity.models.*;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;


public class AbonosController implements Initializable {

    @FXML private Label lblCliente, lblTotal, lblPagado, lblRestante;
    @FXML private TextField txtMonto;
    @FXML private ComboBox<String> cbMetodo;

    @FXML private TableView<Pago> tblPagos;
    @FXML private TableColumn<Pago,String> colFecha;
    @FXML private TableColumn<Pago,String> colMetodo;
    @FXML private TableColumn<Pago,Double> colMonto;

    private Venta venta;
    private SaleDAO saleDAO = new SaleDAO();
    private ObservableList<Pago> listaPagos = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbMetodo.getItems().addAll("EFECTIVO","TARJETA","TRANSFERENCIA");
        cbMetodo.setValue("EFECTIVO");
        configurarTabla();
    }
    
    public void setVenta(Venta v){
        this.venta = v;
        cargarDatos();
    }

    private void configurarTabla() {
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyy HH:mm");
        colFecha.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getFecha().format(formatter)));
        colMetodo.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getMetodo()));
        colMonto.setCellValueFactory(data ->
            new SimpleDoubleProperty(data.getValue().getMonto()).asObject());
    }

    private void cargarDatos() {
        lblCliente.setText("Cliente: " + venta.getCliente());
        lblTotal.setText("Total: $" + venta.getTotal());
        lblPagado.setText("Pagado: $" + venta.getPagado());
        lblRestante.setText("Restante: $" + venta.getRestante());

        listaPagos.clear();
        listaPagos.addAll(saleDAO.obtenerPagosPorVenta(venta.getId()));
        tblPagos.setItems(listaPagos); 
    }
@FXML
    private void handleAbonar(){

        double monto;

        try{
            monto = Double.parseDouble(txtMonto.getText());
        }catch(Exception e){
            mostrarAlerta("Monto inválido");
            return;
        }

        boolean ok = saleDAO.registrarAbono(
                venta.getId(),
                cbMetodo.getValue(),
                monto
        );

        if(ok){
            mostrarAlerta("Abono realizado");
            txtMonto.clear();
            cargarDatos(); // refresca todo
        }else{
            mostrarAlerta("Error al abonar");
        }
    }

    private void mostrarAlerta(String msg){
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText(msg);
        a.showAndWait();
    }
}