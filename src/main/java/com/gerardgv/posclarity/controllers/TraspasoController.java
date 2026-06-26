package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.database.*;
import com.gerardgv.posclarity.models.*;
import com.gerardgv.posclarity.utils.Configuracion;
import java.net.URL;
import java.util.*;
import javafx.collections.*;
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
        
        ObservableList<Branch> sucursales = 
                FXCollections.observableArrayList(branchDAO.getAll());
        
        cmbOrigen.setItems(sucursales);
        
        int idSucursalActual = Configuracion.obtenerSucursal();
        Branch sucursalActual = null;
        
        for(Branch b : sucursales){
            if(b.getId() ==idSucursalActual){
                sucursalActual = b;
                break;
            }
        }
        
        cmbOrigen.setValue(sucursalActual);
        
        ObservableList<Branch> destinos =
                FXCollections.observableArrayList(sucursales);
        
        destinos.remove(sucursalActual);        
        cmbDestino.setItems(destinos);
        
        cmbOrigen.setDisable(true);
        cargarInventarioOrigen();
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
        
        if(seleccionado == null){
            mostrarAlerta("Seleccionar un producto");
            return;
        }
        
        if(txtCantidad.getText().isEmpty()){
            mostrarAlerta("ingresar una Cantidad");
            txtCantidad.requestFocus();
            return;
        }
        
        int cantidad;
        
        try{
            cantidad = Integer.parseInt(txtCantidad.getText());
        }catch(NumberFormatException e){
            mostrarAlerta("La Cantidad Debe Ser Numerica");
            txtCantidad.clear();
            txtCantidad.requestFocus();
            return;
        }
        
        if(cantidad <= 0){
            mostrarAlerta("Cantidad Inválida");
            return;
        }
        
        if(cantidad > seleccionado.getStock()){
            mostrarAlerta("Stock Disponible" + seleccionado.getStock());
            return;
        }
        
        for(TraspasoDetalle d : carrito){
            
            if(d.getIdProducto() == seleccionado.getId_product()){
                
                int nuevaCantidad = d.getCantidad() + cantidad;
                
                if(nuevaCantidad > seleccionado.getStock()){
                    
                    mostrarAlerta("No Puedes Superar Stock");
                    return;
                }
                d.setCantidad(nuevaCantidad);
                tblDetalle.refresh();
                txtCantidad.clear();
                return;
            }
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
