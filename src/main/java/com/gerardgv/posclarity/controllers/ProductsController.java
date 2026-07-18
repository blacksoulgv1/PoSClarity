package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.Ui.*;
import com.gerardgv.posclarity.database.*;
import com.gerardgv.posclarity.utils.*;
import com.gerardgv.posclarity.models.Product;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;


public class ProductsController implements Initializable {
    
    // ===== TextFields =====
    @FXML private TextField txtModelo;
    @FXML private TextField txtMarca;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtBuscar;
    @FXML private TextField txtStock;
    
    // ===== Box =====  
    @FXML private ComboBox<String> cbCategoria;
    @FXML private ComboBox<String> cbTipo;
 
    private final ProductDAO productDAO = new ProductDAO(); 
    private final InventarioSucursalDAO inventarioSucursalDAO = new InventarioSucursalDAO();
    
    // ===== Buttons =====
    @FXML private Button btnGuardar;
    @FXML private Button btnClear;
    
    // ===== Tabla =====
    @FXML private TableView<Product> tblProductos;    
    @FXML private TableColumn<Product,Integer> colId;
    @FXML private TableColumn<Product,String> colModelo;
    @FXML private TableColumn<Product,String> colMarca;
    @FXML private TableColumn<Product,String> colCategoria;
    @FXML private TableColumn<Product,String> colTipo;
    @FXML private TableColumn<Product,Double> colPrecio;
    @FXML private TableColumn<Product,Integer> colStock;
    @FXML private TableColumn<Product,Boolean> colActivo;
    @FXML private TableColumn<Product, Void> colAcciones;

    @FXML private StackPane  root;
    
    private boolean modoEdicion = false;
    private Product productoSeleccionado;
    private final ObservableList<Product> listaProductos = FXCollections.observableArrayList();


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
 
        configurarTabla();        
        configurarCombos();
        configurarEventos();
        configurarBusqueda();
        suscribirEventos();
        cargarProductos();
        formatearCombos();      
                           
    }

    
//terminado 26-02
    private void configurarTabla(){
        
        PosTable.apply(tblProductos);
        
    // ==============================
    // DATOS DE LAS COLUMNAS
    // ==============================
        
        colId.setCellValueFactory(data ->
            new SimpleIntegerProperty(data.getValue().getId_product()).asObject());
        colModelo.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getModelo()));
        colMarca.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getMarca()));
        colCategoria.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getCategoria()));
        colTipo.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getTipo_producto()));
        colPrecio.setCellValueFactory(data ->
            new SimpleDoubleProperty(data.getValue().getPrecio()).asObject());
        colStock.setCellValueFactory(data ->
            new SimpleIntegerProperty(data.getValue().getStock()).asObject());
        colActivo.setCellValueFactory(data ->
            new SimpleBooleanProperty(data.getValue().isActivo()).asObject());        
        
    // ==============================
    // FORMATO DE LAS COLUMNAS
    // ==============================

        PosTable.integer(colId);
        PosTable.text(colModelo);
        PosTable.text(colMarca);
        PosTable.category(colCategoria);
        PosTable.enumColumn(colTipo);
        PosTable.money(colPrecio);
        PosTable.integer(colStock);

    // ==============================
    // ACCIONES Y ESTADO
    // ==============================

    colActivo.setCellFactory(column ->
            TableUtils.createActiveToggle(
                    Product::getId_product,
                    productDAO::cambiarEstado
            ));
    colAcciones.setCellFactory(column ->
            TableUtils.createEditButton(
                    this::editarProducto
            ));
    
    // ==============================
    // COMPORTAMIENTO GENERAL
    // ==============================
    
        PosTable.placeholder(tblProductos,
            "No hay productos registrados",
            "Agrega productos desde el formulario de la izquierda",
            "fas-box-open");

        PosTable.inactiveRows(tblProductos,Product::isActivo);    
    }
    

    //terminado 26-02
    private void editarProducto( Product p){
        
        Product actualizado = productDAO.getById(p.getId_product(),
                Session.getSucursal().getId());
        
        if(actualizado == null){
            mostrarError("No se Pudo Cargar el Producto");
            return;
        }
        
        productoSeleccionado = actualizado;
        modoEdicion = true;
        
        txtModelo.setText(actualizado.getModelo());
        txtMarca.setText(actualizado.getMarca());
        txtPrecio.setText(String.valueOf(actualizado.getPrecio()));
        cbCategoria.setValue(actualizado.getCategoria());
        cbTipo.setValue(actualizado.getTipo_producto());
        
        if(actualizado.isManejaStock()){
            txtStock.setText(String.valueOf(actualizado.getStock()));
        } else {
            txtStock.setText("0");
        }
        
        btnGuardar.setText("Actualizar");
        aplicarComportamientoCategoria(p.getCategoria());
 
    }
    
    //26-02 terminado
    private void configurarCombos() {
        cbCategoria.setItems(FXCollections.observableArrayList(
            "armazon","lente_contacto","producto","mica","tratamiento"));
        cbTipo.setItems(FXCollections.observableArrayList(
            "fisico","bajo_pedido"));
    }
    
    private void saveProduct(){
        
        if(!validarFormularioProducto()){
            return;
        }
    
        int stock = obtenerStock();        
        if(stock < 0){
            return;
        }
    
        double precio = obtenerPrecio();
        if(precio < 0){
            return;
        }
    
        Product p = construirProducto(precio);
    
        boolean resultado = modoEdicion
            ? actualizarProducto(p, stock)
            : guardarProducto(p, stock);
    
        if(resultado){
            mostrarExito(modoEdicion
                ? "Producto actualizado correctamente."
                : "Producto guardado correctamente.");
        
            limpiarFormulario();
            cargarProductos();
        }else{
            mostrarError(modoEdicion
                ? "El producto no se actualizó correctamente."
                : "El producto no se guardó correctamente.");
        }
    }
    
    private boolean validarFormularioProducto(){
    
    if(txtModelo.getText().trim().isEmpty()){
        mostrarError("El modelo es obligatorio.");
        txtModelo.requestFocus();
        return false;
    }
    
    if(txtMarca.getText().trim().isEmpty()){
        mostrarError("La marca es obligatoria.");
        txtMarca.requestFocus();
        return false;
    }
    
    if(cbCategoria.getValue() == null){
        mostrarError("Selecciona una categoría.");
        cbCategoria.requestFocus();
        return false;
    }
    
    if(cbTipo.getValue() == null){
        mostrarError("Selecciona un tipo de producto.");
        cbTipo.requestFocus();
        return false;
    }
    
    if(txtPrecio.getText().trim().isEmpty()){
        mostrarError("El precio es obligatorio.");
        txtPrecio.requestFocus();
        return false;
    }
    
    return true;
}
    
    private int obtenerStock(){
    
    if(txtStock.getText().trim().isEmpty()){
        return 0;
    }
    
    try{
        int stock = Integer.parseInt(txtStock.getText().trim());
        
        if(stock < 0){
            mostrarError("El stock no puede ser negativo.");
            txtStock.requestFocus();
            return -1;
        }
        
        return stock;
        
    }catch(NumberFormatException e){
        mostrarError("El stock debe ser un número entero.");
        txtStock.requestFocus();
        return -1;
    }
}

    private double obtenerPrecio(){
    
    try{
        double precio = Double.parseDouble(txtPrecio.getText().trim());
        
        if(precio < 0){
            mostrarError("El precio no puede ser negativo.");
            txtPrecio.requestFocus();
            return -1;
        }
        
        return precio;
        
    }catch(NumberFormatException e){
        mostrarError("El precio debe ser un número válido.");
        txtPrecio.requestFocus();
        return -1;
    }
}
    
    private Product construirProducto(double precio){
    
    Product p = modoEdicion ? productoSeleccionado : new Product();
    
    String categoria = cbCategoria.getValue();
    String tipo = cbTipo.getValue().toLowerCase().replace(" ", "_");
    
    p.setModelo(txtModelo.getText().trim());
    p.setMarca(txtMarca.getText().trim());
    p.setPrecio(precio);
    p.setCategoria(categoria);
    p.setTipo_producto(tipo);
    
    p.setManejaStock(categoria.equals("producto") || categoria.equals("armazon"));
    p.setMicaBase(categoria.equals("mica"));
    p.setActivo(true);
    
    if(modoEdicion){
        p.setId_product(productoSeleccionado.getId_product());
    }
    
    return p;
}
    
    private boolean guardarProducto(Product p, int stock){
    return productDAO.insert(p, Session.getSucursal().getId(), stock);
}
    private boolean actualizarProducto(Product p, int stock){
    
    boolean productoActualizado = productDAO.update(p);
    
    if(!productoActualizado){
        return false;
    }
    
    if(p.isManejaStock()){
        return inventarioSucursalDAO.actualizarStock(
                Session.getSucursal().getId(),
                p.getId_product(),
                stock);
    }
    
    return true;
}
    
    private void limpiarFormulario() {
        txtModelo.clear();
        txtMarca.clear();
        txtPrecio.clear();
        txtStock.clear();
        cbCategoria.getSelectionModel().clearSelection();
        cbTipo.getSelectionModel().clearSelection();
        cbTipo.setDisable(false);
        txtStock.setDisable(false);        
        modoEdicion = false;
        productoSeleccionado = null;    
        btnGuardar.setText("Guardar");
        txtModelo.requestFocus();
    }

    private void cargarProductos() {
        listaProductos.clear();
        listaProductos.addAll(productDAO.getBySucursal(Session.getSucursal().getId()));
        tblProductos.setItems(listaProductos);
    }    
    
    private void mostrarError(String msg){
        PosNotification.error(root, "Atención", msg);
    }

    private void mostrarExito(String msg){
        PosNotification.success(root, "Listo", msg);
    }   
    
//listo 26-02
    private void aplicarComportamientoCategoria(String categoria) {
        
        switch (categoria) {

        case "armazon", "producto" -> {
            cbTipo.setValue("fisico");
            cbTipo.setDisable(true);

            txtStock.setDisable(false);

            if (txtStock.getText().isBlank()) {
                txtStock.setText("0");
            }
        }

        case "mica", "tratamiento", "lente_contacto" -> {
            cbTipo.setValue("bajo_pedido");
            cbTipo.setDisable(true);

            txtStock.setText("0");
            txtStock.setDisable(true);
        }

        default -> {
            cbTipo.getSelectionModel().clearSelection();
            cbTipo.setDisable(false);

            txtStock.clear();
            txtStock.setDisable(false);
        }
    }
    }
    
    private void formatearCombos() {
        
        cbTipo.setCellFactory(listView -> crearCeldaFormateada());
        cbTipo.setButtonCell(crearCeldaFormateada());

        cbCategoria.setCellFactory(listView -> crearCeldaFormateada());
        cbCategoria.setButtonCell(crearCeldaFormateada());
        
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
    
    private ListCell<String> crearCeldaFormateada() {

    return new ListCell<>() {

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            setText(
                    empty || item == null
                            ? null
                            : formatearTexto(item)
            );
        }
    };
}

    private void refrescarInventario(List<Integer> ids) {
        for(Integer id : ids){
            
            Product actualizado = productDAO.getById(id, Session.getSucursal().getId());
            
            if(actualizado == null || !actualizado.isManejaStock()){
                continue;
            }
            
            for(Product p : listaProductos){
                if(p.getId_product() == id){
                    p.setStock(actualizado.getStock());
                    break;
                }
            }
        }
        tblProductos.refresh();
    }

    private void configurarEventos() {
        
        btnClear.setOnAction(e-> limpiarFormulario());           
        btnGuardar.setOnAction(e -> saveProduct());        
        cbCategoria.valueProperty().addListener((obs,oldVal,newVal)-> {
            if(newVal !=null){
                aplicarComportamientoCategoria(newVal);
            }
        }); 
        
    }

    private void configurarBusqueda() {
        
        SearchUtils.setupSearch(txtBuscar, tblProductos, listaProductos,
                Product::getModelo,
                Product::getMarca);
        
    }

    private void suscribirEventos() {
        EventBus.subscribeStock(this::refrescarInventario);
    }
    
}
