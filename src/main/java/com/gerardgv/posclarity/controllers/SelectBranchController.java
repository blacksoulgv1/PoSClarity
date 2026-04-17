package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.app.App;
import com.gerardgv.posclarity.database.BranchDAO;
import com.gerardgv.posclarity.models.Branch;
import com.gerardgv.posclarity.utils.Configuracion;
import com.gerardgv.posclarity.utils.Session;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.stage.Stage;

public class SelectBranchController implements Initializable {
    
    @FXML private ComboBox<Branch> cbBranch;
    private BranchDAO branchDAO = new BranchDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        cbBranch.setItems(
                FXCollections.observableArrayList(
                        branchDAO.getAll()));
        
        cbBranch.setCellFactory(lv -> new ListCell<>(){
        @Override
        protected void updateItem(Branch item, boolean empty){
            super.updateItem(item,empty);
            setText(empty || item == null ? null :item.getSucursal());
        }
    });
        cbBranch.setButtonCell(new ListCell<>(){
            @Override
            protected void updateItem(Branch item,boolean empty){
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getSucursal());
            }
        });        
    }

    @FXML
    private void handleSeleccionar(){
        
        Branch s = cbBranch.getValue();
        
        if(s == null){
            mostrarError("Selecciona una Sucursal");
            return;
        }
        
        Configuracion.guardarSucursal(s.getId());
        Session.setSucursal(s);
        
        try{
            App.setRoot("main");
        } catch(Exception e){
            e.printStackTrace();
        }               
        
        Stage stage = (Stage) cbBranch.getScene().getWindow();
        stage.close();        
    }   
    
    private void mostrarError(String mensaje){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
}
