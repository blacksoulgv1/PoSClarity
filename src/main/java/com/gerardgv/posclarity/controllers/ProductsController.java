package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.models.Product;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;


public class ProductsController implements Initializable {
    
    // ===== TextFields =====
    @FXML private TextField txtModelo;
    @FXML private TextField txtMarca;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtStock;
    @FXML private TextField txtBuscar;
    
    // ===== Box =====
    @FXML private CheckBox chkActivo;
    @FXML private ComboBox<String> cbCategoria;
    @FXML private ComboBox<String> cbTipo;
    
    // ===== Buttons =====
    @FXML private Button btnNuevo;
    @FXML private Button btnGuardar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnBuscar;
    
    // ===== Tabla =====
    @FXML private TableView<Product> tblProductos;    
    @FXML private TableColumn<Product,String> colId;
    @FXML private TableColumn<Product,String> colModelo;
    @FXML private TableColumn<Product,String> colMarca;
    @FXML private TableColumn<Product,String> colCategoria;
    @FXML private TableColumn<Product,String> colTipo;
    @FXML private TableColumn<Product,Double> colPrecio;
    @FXML private TableColumn<Product,Integer> colStock;
    @FXML private TableColumn<Product,String> colEstatus;
    @FXML private TableColumn<Product, Void> colAcciones;
    
    @FXML private SplitPane splitPane;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        controlarStock();
        panel();
        buttonAccion();
        configurarColumnas();
    }

    private void controlarStock(){
        cbTipo.getItems().addAll("Físico","Bajo pedido","Servicio");
        
        cbTipo.valueProperty().addListener((obs,oldval,newVal) ->{
            if("Físico".equals(newVal)){
                txtStock.setDisable(false);
            } else{
                txtStock.setText("0");
                txtStock.setDisable(true);
            }
        });
       
    }
    
    private void panel(){
        splitPane.setDividerPositions(0.35);
        splitPane.getDividers().forEach(div -> div.positionProperty().addListener((
        obs,oldVal,newVal) -> {
            div.setPosition(0.35);
        }));
    }
    
    private void configurarColumnas(){
        
    colId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId_product())));
    colModelo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getModelo()));
    colMarca.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMarca()));
    colCategoria.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategoria()));
    colTipo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTipo_producto()));
    colPrecio.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPrecio()).asObject());
    colStock.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStock()).asObject());
    
    colEstatus.setCellValueFactory(data -> {
        boolean activo = data.getValue().isActivo();
        return new SimpleObjectProperty(activo ? "Activo": "Inactivo");
    });
    }
    
    private void buttonAccion(){
        colAcciones.setCellFactory(param -> new TableCell<Product, Void>(){
            
            private final Button btnEditar = new Button("✏");
            private final Button btnDesactivar = new Button("🗑");
            private final HBox contenedor = new HBox(8,btnEditar,btnDesactivar);
            
            {
                contenedor.setAlignment(Pos.CENTER)
                        ;
                btnEditar.getStyleClass().add("btn-table-edit");
                btnDesactivar.getStyleClass().add("btn-table-delete");
                
                btnEditar.setOnAction(e ->{
                    Product p = getTableView().getItems().get(getIndex());
                    editarProducto(p);
                });
                btnDesactivar.setOnAction(e -> {
                    Product p = getTableView().getItems().get(getIndex());
                    desactivarProducto(p);
                });              
            }
 
            @Override
            protected void updateItem(Void item, boolean empty){
                super.updateItem(item, empty);
                setGraphic(empty ? null: contenedor);
            }
            
        });
    }
    
    private void editarProducto( Product p){
        txtModelo.setText(p.getModelo());
        txtMarca.setText(p.getMarca());
        txtPrecio.setText(String.valueOf(p.getPrecio()));
        txtStock.setText(String.valueOf(p.getStock()));
        cbCategoria.setValue(p.getCategoria());
        cbTipo.setValue(p.getTipo_producto());
        chkActivo.setSelected(p.isActivo());
    }
    
    private void desactivarProducto(Product p){
        p.setActivo(false);
        tblProductos.refresh();
    }
    
}
