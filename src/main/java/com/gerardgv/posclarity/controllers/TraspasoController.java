package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.database.BranchDAO;
import com.gerardgv.posclarity.database.InventarioSucursalDAO;
import com.gerardgv.posclarity.database.TraspasoDAO;
import com.gerardgv.posclarity.models.Branch;
import com.gerardgv.posclarity.models.Product;
import com.gerardgv.posclarity.models.Traspaso;
import com.gerardgv.posclarity.models.TraspasoDetalle;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class TraspasoController implements Initializable {
    
    
    @FXML private ComboBox<Branch> cmbOrigen;
    @FXML private ComboBox<Branch> cmbDestino;
    @FXML private TextField txtCantidad;
    @FXML private Button btnAgregar;
    @FXML private Button btnTraspasar;
    
    @FXML private TableView<Product> tblInventarioOrigen;
    @FXML private TableView<TraspasoDetalle> tblDetalle;
    @FXML private TableColumn<Product, String> colModelo;
    @FXML private TableColumn<Product, String> colMarca;
    @FXML private TableColumn<Product, String> colCategoria;
    @FXML private TableColumn<Product, Integer> colStock;
    @FXML private TableColumn<TraspasoDetalle, String> colProductoDetalle;
    @FXML private TableColumn<TraspasoDetalle, Integer> colCantidadDetalle; 

    private ObservableList<Product> listaInventario;
    private ObservableList<TraspasoDetalle> carrito;
    
    private TraspasoDAO traspasoDAO = new TraspasoDAO();
    private BranchDAO branchDAO = new BranchDAO();
    private InventarioSucursalDAO inventarioDAO = new InventarioSucursalDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        configurarColumnas();
        cargarSucursales();
        
        carrito = FXCollections.observableArrayList();
        tblDetalle.setItems(carrito);
        
        cmbOrigen.setOnAction(e -> cargarInventarioOrigen());
        btnAgregar.setOnAction(e -> agregarProduto());
        btnTraspasar.setOnAction(e -> realizarTraspaso());
        
    }   

    private void cargarSucursales() {
        
        cmbOrigen.setItems(FXCollections.observableArrayList(branchDAO.getAll()));
        cmbDestino.setItems(FXCollections.observableArrayList(branchDAO.getAll()));

    }

    private void configurarColumnas() {
        
        colModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colProductoDetalle.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colCantidadDetalle.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

    }

    private void cargarInventarioOrigen() {
        
        Branch origen = cmbOrigen.getValue();
        
        if(origen == null) return;
        
        listaInventario = FXCollections.observableArrayList(inventarioDAO.getProductosBySucursal(origen.getId()));
        tblInventarioOrigen.setItems(listaInventario);
        
    }

    private void agregarProduto() {
        
        Product seleccionado = tblInventarioOrigen.getSelectionModel().getSelectedItem();
        
        if (seleccionado == null) return;
        
        int cantidad = Integer.parseInt(txtCantidad.getText());
        
        if(cantidad <=0 || cantidad > seleccionado.getStock()){
            mostrarAlerta("Cantidad Inválida");
            return;
        }
        
        TraspasoDetalle detalle = new TraspasoDetalle();
        detalle.setIdProducto(seleccionado.getId_product());
        detalle.setModelo(seleccionado.getModelo());
        detalle.setCantidad(cantidad);
        
        carrito.add(detalle);
        txtCantidad.clear();
    }

    private void realizarTraspaso() {
        
        if(cmbOrigen.getValue() == null || cmbDestino.getValue() == null){
            mostrarAlerta("Selecciona Sucursales");
            return;
        }
        if(cmbOrigen.getValue().getId() == cmbDestino.getValue().getId()){
            mostrarAlerta("Origen y Destino no Pueden ser Iguales");
            return;
        }
        if(carrito.isEmpty()){
            mostrarAlerta("No Hay Productos en el Traspaso");
            return;
        }
        
        Traspaso t = new Traspaso();
        t.setIdSucursalOrigen(cmbOrigen.getValue().getId());
        t.setIdSucursalDestino(cmbDestino.getValue().getId());
        t.setDetalles(new ArrayList<>(carrito));
        
        boolean ok = traspasoDAO.realizarTraspaso(t);
        
        if(ok){
            mostrarAlerta("Traspaso Realizado Correctamente");
            carrito.clear();
            cargarInventarioOrigen();
        } else {
            mostrarAlerta("Error al Realizar Traspaso");
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
