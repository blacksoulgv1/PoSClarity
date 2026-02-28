package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.database.ProductDAO;
import com.gerardgv.posclarity.models.Product;
import java.io.IOException;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class ProductsController implements Initializable {
    
    // ===== TextFields =====
    @FXML private TextField txtModelo;
    @FXML private TextField txtMarca;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtBuscar;
    @FXML private TextField txtStock;
    
    // ===== Box =====
    @FXML private CheckBox chkActivo;
    
    @FXML private ComboBox<String> cbCategoria;
    @FXML private ComboBox<String> cbTipo;
 
    private ProductDAO productDAO = new ProductDAO(); 
    
    // ===== Buttons =====
    @FXML private Button btnNuevo;
    @FXML private Button btnGuardar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnBuscar;
    @FXML private Button btnNuevaCategoria;
    
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
    
    private boolean modoEdicion = false;
    private Product productoSeleccionado;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        panel();
        buttonAccion();
        configurarColumnas();        
        cargarCombos();
        cargarProductos();
        formatearCombos();
        
        btnGuardar.setOnAction(e -> saveProduct());
        btnNuevo.setOnAction(e -> limpiarFormulario());
        
        cbCategoria.valueProperty().addListener((obs,oldVal,newVal)-> {
            if(newVal !=null){
                aplicarComportamientoCategoria(newVal);
            }
        });
        
        tblProductos.setRowFactory(tv -> new TableRow<>() {
        @Override
        protected void updateItem(Product item, boolean empty) {
            super.updateItem(item, empty);

                if (item == null || empty) {
                setStyle("");
                    } else if (!item.isActivo()) {
                    setStyle("-fx-background-color: #ffe6e6;");
                } else {
                    setStyle("");
                }
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
    
//terminado 26-02
        private void configurarColumnas(){
        
    colId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId_product())));
    colModelo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getModelo()));
    colMarca.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMarca()));
    colCategoria.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategoria()));
    colTipo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTipo_producto()));
    colPrecio.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPrecio()).asObject());
    colStock.setCellValueFactory(data ->
        new SimpleIntegerProperty(
        data.getValue().isManejaStock() ? 1 : 0
        ).asObject()
    );
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
    
    //terminado 26-02
    private void editarProducto( Product p){
        
        productoSeleccionado = p;
        modoEdicion = true;
        
        txtModelo.setText(p.getModelo());
        txtMarca.setText(p.getMarca());
        txtPrecio.setText(String.valueOf(p.getPrecio()));
        cbCategoria.setValue(p.getCategoria());
        cbTipo.setValue(p.getTipo_producto());
        chkActivo.setSelected(p.isActivo());
        btnGuardar.setText("Actualizar");
        aplicarComportamientoCategoria(p.getCategoria());
 
    }
    
    private void desactivarProducto(Product p){
        
        boolean estadoNuevo = !p.isActivo();
            if(productDAO.cambiarEstado(p.getId_product(),estadoNuevo)){
                p.setActivo(estadoNuevo);
                tblProductos.refresh();
            }
    }
    //26-02 terminado
    private void cargarCombos() {
        cbCategoria.setItems(FXCollections.observableArrayList(
            "armazon","lente_contacto","producto","mica","tratamiento"));
        cbTipo.setItems(FXCollections.observableArrayList(
            "fisico","bajo_pedido"));
    }
    
    private void saveProduct(){
        
        int stock = 0;
        double precio; 
        
        if(txtModelo.getText().isEmpty() ||
            txtMarca.getText().isEmpty() ||
            txtPrecio.getText().isEmpty() ||
            cbCategoria.getValue() == null ||
            cbTipo.getValue() == null){

            mostrarError("Completa los campos");
                return;
            }
        
        if(!txtStock.getText().isEmpty()){
            stock = Integer.parseInt(txtStock.getText());
        }               
        
        try{
             precio = Double.parseDouble(txtPrecio.getText());
            }catch(NumberFormatException e){
                mostrarError("Precio Invalido");
                return;
            }
        
        Product p = modoEdicion ? productoSeleccionado : new Product();
        
        p.setModelo(txtModelo.getText());
        p.setMarca(txtMarca.getText());
        p.setPrecio(precio);
        p.setCategoria(cbCategoria.getValue());
        p.setTipo_producto(cbTipo.getValue());

        p.setManejaStock(cbCategoria.getValue().equals("producto")||cbCategoria.getValue().equals("mica"));

        p.setMicaBase(cbCategoria.getValue().equals("mica"));
       
        p.setActivo(chkActivo.isSelected());

        boolean resultado;

            if(modoEdicion){
                p.setId_product(productoSeleccionado.getId_product());
                resultado = productDAO.update(p);
            }else{
                resultado = productDAO.insert(p);
            }

            if(resultado){
            mostrarInfo("Producto guardado");
            limpiarFormulario();
            cargarProductos();
            modoEdicion = false;
            }else{
                mostrarError("No se pudo guardar");
            }               
    }

    private void limpiarFormulario() {
        txtModelo.clear();
        txtMarca.clear();
        txtPrecio.clear();
        cbCategoria.getSelectionModel().clearSelection();
        cbTipo.getSelectionModel().clearSelection();
        chkActivo.setSelected(true);
        cbTipo.setDisable(false);
        txtStock.setDisable(false);
        txtStock.clear();
        modoEdicion = false;
        productoSeleccionado = null;    
        btnGuardar.setText("Guardar");
    }

    private void cargarProductos() {
        tblProductos.setItems(
            FXCollections.observableArrayList(
                    productDAO.getAll()));
    }
    
    //Corecto
    private void mostrarError(String mensaje){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    //Correcto
    private void mostrarInfo(String mensaje){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
//listo 26-02
    private void aplicarComportamientoCategoria(String categoria) {
        
        switch (categoria) {
            
            case "producto" -> {
                cbTipo.setValue("fisico");
                cbTipo.setDisable(true);                
                txtStock.setDisable(false);
                txtStock.clear();
            }
            case "mica" -> {
                cbTipo.setValue("fisico");
                cbTipo.setDisable(true);
                txtStock.setText("0");
                txtStock.setDisable(true);
            }
            case "tratamiento" -> {
                cbTipo.setValue("servicio");
                cbTipo.setDisable(true);
                txtStock.setText("0");
                txtStock.setDisable(true);
            }
        }
    }
    
    private void formatearCombos() {
        
        cbTipo.setCellFactory(lv -> new ListCell<>(){
            @Override
        protected void updateItem(String item,boolean empty){
                super.updateItem(item, empty);
                setText(empty || item == null ? null: formatearTexto(item));
            }
        });
        
        cbTipo.setButtonCell(new ListCell<>(){
            @Override
        protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formatearTexto(item));
            }
        });
        cbCategoria.setCellFactory(lv -> new ListCell<>() {
        @Override
        protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formatearTexto(item));
            }
        });

    cbCategoria.setButtonCell(new ListCell<>() {
        @Override
        protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formatearTexto(item));
            }
        });
    }
    
    private String formatearTexto(String valor){
         return switch (valor) {
        case "bajo_pedido" -> "Bajo pedido";
        case "lente_contacto" -> "Lente de contacto";
        case "armazon" -> "Armazón";
        case "mica" -> "Mica";
        case "producto" -> "Producto";
        case "tratamiento" -> "Tratamiento";
        default -> valor;
    };
    }
    
}
