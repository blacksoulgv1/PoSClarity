package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.api.BranchApiClient;
import com.gerardgv.posclarity.api.InventoryApiClient;
import com.gerardgv.posclarity.api.TransferApiClient;
import com.gerardgv.posclarity.models.*;
import com.gerardgv.posclarity.utils.Configuracion;
import java.net.URL;
import java.util.*;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class TransferController implements Initializable {
    
    
    @FXML private ComboBox<Branch> cmbOrigen;
    @FXML private ComboBox<Branch> cmbDestino;
    @FXML private TextField txtCantidad;
    @FXML private Button btnAgregar;
    @FXML private Button btnTraspasar;
    
    @FXML private TableView<Product> tblInventarioOrigen;
    @FXML private TableView<TransferDetail> tblDetalle;
    @FXML private TableColumn<Product, String> colModelo;
    @FXML private TableColumn<Product, String> colMarca;
    @FXML private TableColumn<Product, String> colCategoria;
    @FXML private TableColumn<Product, Integer> colStock;
    @FXML private TableColumn<TransferDetail, String> colProductoDetalle;
    @FXML private TableColumn<TransferDetail, Integer> colCantidadDetalle; 

    private ObservableList<Product> listaInventario;
    private ObservableList<TransferDetail> carrito;
        
    private final InventoryApiClient inventoryApiClient = new InventoryApiClient();
    private final TransferApiClient transferApiClient = new TransferApiClient();
    private final BranchApiClient branchApiClient = new BranchApiClient();

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
        
         try {

            ObservableList<Branch> sucursales =
                FXCollections.observableArrayList(
                        branchApiClient.getAll());

            cmbOrigen.setItems(sucursales);

            int idSucursalActual = Configuracion.obtenerSucursal();

            Branch sucursalActual = null;

            for (Branch branch : sucursales) {

                if (branch.getId() == idSucursalActual) {
                    sucursalActual = branch;
                    break;
                }
            }

            if (sucursalActual == null) {

                mostrarAlerta("No se encontró la sucursal configurada.");
                cmbOrigen.getSelectionModel().clearSelection();
                cmbDestino.getItems().clear();
                return;
            }

            cmbOrigen.setValue(sucursalActual);
            ObservableList<Branch> destinos =
                FXCollections.observableArrayList(
                        sucursales);
            destinos.removeIf(branch ->
                branch.getId() == idSucursalActual);
            cmbDestino.setItems(destinos);
            cmbOrigen.setDisable(true);
            cargarInventarioOrigen();

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
            mostrarAlerta("La consulta de sucursales fue interrumpida.");

        } catch (Exception e) {

            e.printStackTrace();
            mostrarAlerta(
                "No se pudieron cargar las sucursales.\n" + e.getMessage());
        }
    }

    private void configurarColumnas() {
        
        colModelo.setCellValueFactory(new PropertyValueFactory<>("model"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("category"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colProductoDetalle.setCellValueFactory(new PropertyValueFactory<>("model"));
        colCantidadDetalle.setCellValueFactory(new PropertyValueFactory<>("quantity"));

    }

    private void cargarInventarioOrigen() {
        
         Branch origen = cmbOrigen.getValue();

        if (origen == null) {
            return;
        }

        try {

            List<Inventory> inventory =
                inventoryApiClient.getByBranch(origen.getId());

            listaInventario = FXCollections.observableArrayList();

            for (Inventory item : inventory) {

                Product product = new Product();
                product.setId(item.getProductId());
                product.setModel(item.getProductModel());
                product.setBrand(item.getProductBrand());
                product.setStock(item.getStock());
                listaInventario.add(product);
            }
            tblInventarioOrigen.setItems(listaInventario);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            mostrarAlerta("La consulta del inventario fue interrumpida.");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(
                "No se pudo cargar el inventario.\n"
                + e.getMessage()
            );
        }
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
        
        for(TransferDetail d : carrito){
            
            if(d.getProductId() == seleccionado.getId()){
                
                int nuevaCantidad = d.getQuantity() + cantidad;
                
                if(nuevaCantidad > seleccionado.getStock()){
                    
                    mostrarAlerta("No Puedes Superar Stock");
                    return;
                }
                d.setQuantity(nuevaCantidad);
                tblDetalle.refresh();
                txtCantidad.clear();
                return;
            }
        }
        
        TransferDetail detalle = new TransferDetail();
        System.out.println("Producto seleccionado ID: " + seleccionado.getId());
        detalle.setProductId(seleccionado.getId());
        detalle.setModel(seleccionado.getModel());
        System.out.println("Producto agregado ID: " + detalle.getProductId());
        detalle.setQuantity(cantidad);
        carrito.add(detalle);
        txtCantidad.clear();        
    }

    private void realizarTraspaso() {
        
        if (cmbOrigen.getValue() == null || cmbDestino.getValue() == null) {
        mostrarAlerta("Selecciona Sucursales");
        return;
    }

    if (cmbOrigen.getValue().getId() == cmbDestino.getValue().getId()) {
        mostrarAlerta("Origen y Destino no Pueden ser Iguales");
        return;
    }

    if (carrito.isEmpty()) {
        mostrarAlerta("No Hay Productos en el Traspaso");
        return;
    }

    try {

        boolean ok = transferApiClient.realizarTransfer(
                cmbOrigen.getValue().getId(),
                cmbDestino.getValue().getId(),
                new ArrayList<>(carrito)
        );

        if (ok) {
            mostrarAlerta("Traspaso Realizado Correctamente");
            carrito.clear();
            cargarInventarioOrigen();
        } else {
            mostrarAlerta("Error al Realizar Traspaso");
        }

    } catch (InterruptedException e) {

        Thread.currentThread().interrupt();
        mostrarAlerta("La transferencia fue interrumpida.");

    } catch (Exception e) {

        e.printStackTrace();
        mostrarAlerta(
                "Error al realizar la transferencia.\n"
                + e.getMessage()
        );
    }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
