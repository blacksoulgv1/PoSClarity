package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.api.*;
import com.gerardgv.posclarity.models.*;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

public class MetasController implements Initializable {
    
    @FXML private ComboBox<Branch> cbSucursal;
    @FXML private ComboBox<String> cbMes;
    @FXML private Spinner<Integer> spAnio;
    @FXML private TextField txtMeta;
    @FXML private TableView<Goal> tblMetas;
    @FXML private TableColumn<Goal,String> colSucursal;
    @FXML private TableColumn<Goal,String> colMes;
    @FXML private TableColumn<Goal,Integer> colAnio;
    @FXML private TableColumn<Goal,Double> colMeta;
    
    private final GoalApiClient goalApiClient = new GoalApiClient();
    private final BranchApiClient branchApiClient = new BranchApiClient();

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarMes();
        cargarSucursales();
        configurarTabla();
        cargarMetas();
        spAnio.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
        2024,
        2035,
        LocalDate.now().getYear()
    )
);
    }

    private void cargarSucursales() {
        
        try{
            
            cbSucursal.getItems().setAll(branchApiClient.getAll());
            
        } catch(InterruptedException e){
            
            Thread.currentThread().interrupt();
            alerta("La consulta de Sucursales Fue Interrumpida.");
            
        } catch (Exception e){
            
            e.printStackTrace();
            alerta("No Se Pudieron Cargar Las Sucursales.\n"
                + e.getMessage());            
        }
        
        cbSucursal.setCellFactory(lv -> new ListCell<>(){
            @Override
            protected void updateItem(Branch branch, boolean empty){
                super.updateItem(branch, empty);
                setText(empty || branch == null ? null : branch.getName());
            }
        });
        
        cbSucursal.setButtonCell(new ListCell<>(){
            @Override
            protected void updateItem(Branch branch, boolean empty){
                super.updateItem(branch, empty);
                setText(empty || branch == null ? null : branch.getName());
            }
        });
        
    }

    private void configurarTabla() {
        
        colSucursal.setCellValueFactory(d ->
        new SimpleStringProperty(d.getValue().getBranchName()));
        colMes.setCellValueFactory(d ->{
            
            String[] meses = {
                "Enero","Febrero","Marzo","Abril",
                "Mayo","Junio","Julio","Agosto",
                "Septiembre","Octubre","Noviembre","Diciembre"
            };
            
            int index = d.getValue().getMonth()-1;
            return new SimpleStringProperty(meses[index]);
        });
        colAnio.setCellValueFactory(d ->
        new SimpleIntegerProperty(d.getValue().getYear()).asObject());
        colMeta.setCellValueFactory(d ->
        new SimpleDoubleProperty(d.getValue().getAmount()).asObject());
    }

    private void cargarMetas() {
        
        try{
            
            tblMetas.getItems().setAll(goalApiClient.getAll());
            
        } catch(IOException | InterruptedException e){
            e.printStackTrace();
            alerta(
                "No fue posible cargar las metas"
            );
        }
    }
    
    @FXML
    private void handleGuardar(){
        
        if(cbSucursal.getValue() == null || cbMes.getValue() == null){
            alerta("Selecciona Sucursal & Mes");
            return;
        }
        
        double monto;
        
        try{
            monto = Double.parseDouble(txtMeta.getText());
        } catch (Exception e){
            alerta("Monto Inválido");
            return;
        }
        
        int idSucursal = cbSucursal.getValue().getId();
        int mes = cbMes.getSelectionModel().getSelectedIndex()+1;
        int anio = spAnio.getValue();
        
        try{
            Goal savedGoal = goalApiClient.save(
                    idSucursal, mes, anio, monto);
            
            if(savedGoal ==null){
                alerta("No fue posible guardar la meta");
                return;
            }
            cargarMetas();
        } catch(IOException | InterruptedException e){
            
            e.printStackTrace();
            alerta(
            "No fue posible guardar la meta"
            );
        }
  
    }
    
    private void alerta(String msg){
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setHeaderText(null);
    alert.setContentText(msg);
    alert.showAndWait();
    }

    private void cargarMes() {
        cbMes.setItems(FXCollections.observableArrayList(
        "Enero",
        "Febrero",
        "Marzo",
        "Abril",
        "Mayo",
        "Junio",
        "Julio",
        "Agosto",
        "Septiembre",
        "Octubre",
        "Noviembre",
        "Diciembre"
        ));
        cbMes.getSelectionModel().select(LocalDate.now().getMonthValue()-1);
    }
    
}
