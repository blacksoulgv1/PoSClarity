package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.database.BranchDAO;
import com.gerardgv.posclarity.database.InventarioSucursalDAO;
import com.gerardgv.posclarity.database.TraspasoDAO;
import com.gerardgv.posclarity.models.Product;
import com.gerardgv.posclarity.models.TraspasoDetalle;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

public class TraspasoController implements Initializable {
    
    @FXML private ComboBox<String> cmbOrigen;
    @FXML private ComboBox<String> cmbDestino;
    private ObservableList<Product> productosOrigen;
    private ObservableList<TraspasoDetalle> carritoTraspaso;
    private TraspasoDAO traspasoDAO = new TraspasoDAO();
    private InventarioSucursalDAO inventarioDAO = new InventarioSucursalDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
       // cargarSucursales();
    }   
}
