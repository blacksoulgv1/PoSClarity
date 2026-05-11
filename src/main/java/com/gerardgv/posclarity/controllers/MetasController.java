package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.database.*;
import com.gerardgv.posclarity.models.*;
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
    @FXML private TableView<Meta> tblMetas;
    @FXML private TableColumn<Meta,String> colSucursal;
    @FXML private TableColumn<Meta,String> colMes;
    @FXML private TableColumn<Meta,Integer> colAnio;
    @FXML private TableColumn<Meta,Double> colMeta;
    
    private BranchDAO branchDAO = new BranchDAO();
    private MetaDAO metaDAO = new MetaDAO();

    
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
        
        cbSucursal.getItems().addAll(branchDAO.getAll());
        
        cbSucursal.setCellFactory(lv -> new ListCell<>(){
            @Override
            protected void updateItem(Branch b, boolean empty){
                super.updateItem(b, empty);
                setText(empty || b == null ? null : b.getSucursal());
            }
        });
        
        cbSucursal.setButtonCell(new ListCell<>(){
            @Override
            protected void updateItem(Branch b, boolean empty){
                super.updateItem(b, empty);
                setText(empty || b == null ? null : b.getSucursal());
            }
        });
        
    }

    private void configurarTabla() {
        
        colSucursal.setCellValueFactory(d ->
        new SimpleStringProperty(d.getValue().getNombreSucursal()));
        colMes.setCellValueFactory(d ->{
            
            String[] meses = {
                "Enero","Febrero","Marzo","Abril",
                "Mayo","Junio","Julio","Agosto",
                "Septiembre","Octubre","Noviembre","Diciembre"
            };
            
            int index = d.getValue().getMes() -1;
            return new SimpleStringProperty(meses[index]);
        });
        colAnio.setCellValueFactory(d ->
        new SimpleIntegerProperty(d.getValue().getAnio()).asObject());
        colMeta.setCellValueFactory(d ->
        new SimpleDoubleProperty(d.getValue().getMonto()).asObject());
    }

    private void cargarMetas() {        
        tblMetas.getItems().setAll(metaDAO.obtenerMetas());        
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
        int mes = cbMes.getSelectionModel().getSelectedIndex();
        int anio = spAnio.getValue();
        
        boolean ok = metaDAO.saveMeta(idSucursal, mes, anio, monto);
        
        if(ok){
            alerta("Meta Guardada");
            txtMeta.clear();
            cargarMetas();
        } else {
            alerta("Error al Guardar");
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
