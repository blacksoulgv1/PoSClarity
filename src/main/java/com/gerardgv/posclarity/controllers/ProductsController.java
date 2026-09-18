package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.Ui.*;
import com.gerardgv.posclarity.api.*;
import com.gerardgv.posclarity.api.dto.product.ProductCreateRequest;
import com.gerardgv.posclarity.models.*;
import com.gerardgv.posclarity.utils.*;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
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
 
    private final ProductApiClient productApi = new ProductApiClient();    
    private final InventoryApiClient inventoryApi = new InventoryApiClient();
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
    @FXML private PaginationController paginationController;
    private TablePagination<Product> tablePagination;
    private boolean modoEdicion = false;
    private Product productoSeleccionado;
    
    private final ObservableList<Product> listaProductos = FXCollections.observableArrayList();
    private FilteredList<Product> productosFiltrados;


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
 
        configurarTabla();        
        configurarCombos();
        configurarEventos();
        configurarPaginacion();
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
            new SimpleIntegerProperty(data.getValue().getId()).asObject());
        colModelo.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getModel()));
        colMarca.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getBrand()));
        colCategoria.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getCategory()));
        colTipo.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getProductType()));
        colPrecio.setCellValueFactory(data ->
            new SimpleObjectProperty<>(data.getValue().getPrice()));
        colStock.setCellValueFactory(data ->
            new SimpleIntegerProperty(data.getValue().getStock()).asObject());
        colActivo.setCellValueFactory(data ->
            new SimpleBooleanProperty(data.getValue().getActive()).asObject());        
        
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
                Product::getId,
                (id, activo) -> {

                    try {

                        productApi.updateStatus(
                                id,
                                activo
                        );

                        cargarProductos();
                        
                        return true;

                    } catch(Exception e){

                        e.printStackTrace();

                        mostrarError(
                            "No se pudo actualizar el estado"
                        );
                        return false;
                    }
                }));
    
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

        PosTable.inactiveRows(tblProductos,Product::getActive);    
    }
    

    //terminado 26-02
    private void editarProducto( Product p){
        
        try {

            Product actualizado = productApi.getById(p.getId());

            if(actualizado == null){
                mostrarError( "No se pudo cargar el producto" );
                return;
            }
            
            productoSeleccionado = actualizado;
            modoEdicion = true;
            txtModelo.setText(actualizado.getModel());
            txtMarca.setText(actualizado.getBrand());
            txtPrecio.setText(String.valueOf(actualizado.getPrice()));
            cbCategoria.setValue(actualizado.getCategory());
            cbTipo.setValue(actualizado.getProductType());


            if(actualizado.getManageStock()){
                
                Inventory inventory = inventoryApi.getProductStock(
                        Session.getSucursal().getId(),
                        actualizado.getId());
                
                txtStock.setText(inventory != null
                    ? String.valueOf(inventory.getStock()) : "0");


            }else{

                txtStock.setText("0");
            }

            btnGuardar.setText("Actualizar");

            aplicarComportamientoCategoria(actualizado.getCategory());

        } catch(IOException | InterruptedException e){

            e.printStackTrace();
            mostrarError("Error conectando con la API");
        }
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
    
    p.setModel(txtModelo.getText().trim());
    p.setBrand(txtMarca.getText().trim());
    p.setPrice(precio);
    p.setCategory(categoria);
    p.setProductType(tipo);
    
    p.setManageStock(categoria.equals("producto") || categoria.equals("armazon"));
    p.setBaseLens(categoria.equals("mica"));
    p.setActive(true);
    
    if(modoEdicion){
        p.setId(productoSeleccionado.getId());
    }
    
    return p;
}
    
    private boolean guardarProducto(Product p, int stock){
        
        try {
            
            ProductCreateRequest request = new ProductCreateRequest();
            
            request.setProduct(p);
            request.setBranchId(Session.getSucursal().getId());
            request.setInitialStock(stock);

            Product creado = productApi.create(request);       
            return creado != null;

        } catch(Exception e){

            e.printStackTrace();
            return false;
        }        
    }
    
    private boolean actualizarProducto(Product p, int stock){
    
        try {
            
            Product actualizado = productApi.update(p);

            if(actualizado == null){
                return false;
            }
            
            if(p.getManageStock()){
                
                inventoryApi.updateStock(Session.getSucursal().getId(),
                        p.getId(), stock);            
            }
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
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
    
    //Funcionando con API
    private void cargarProductos() {

    try {

        List<Product> productos = productApi.getAll();

        for (Product p : productos) {

            try {

                Inventory inventory =
                        inventoryApi.getProductStock(
                                Session.getSucursal().getId(),
                                p.getId()
                        );

                if (inventory != null) {

                    Integer stock = inventory.getStock();

                    p.setStock(
                            stock != null
                                    ? stock
                                    : 0
                    );

                } else {

                    p.setStock(0);

                }

            } catch (Exception ex) {

                ex.printStackTrace();
                p.setStock(0);
            }
        }

        // --------------------------------------------------------
        // Actualizar lista principal
        // --------------------------------------------------------

        listaProductos.setAll(productos);

        // --------------------------------------------------------
        // Actualizar paginación
        // --------------------------------------------------------

        tablePagination.setItems(productos);

    } catch (Exception e) {

        e.printStackTrace();

        mostrarError(
                "Error cargando productos desde API"
        );
    }
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
        
        try{
            Integer branchId = Session.getSucursal().getId();
            
            for(Integer id : ids){
                Inventory inventory = inventoryApi.getProductStock(
                        branchId, id);
                
                for(Product p : listaProductos){
                    if(p.getId().equals(id)){
                         p.setStock(
                        inventory != null
                            ? inventory.getStock()
                            : 0 );
                        break;
                    }
                }
            }
            tblProductos.refresh();
        } catch(Exception e){
            e.printStackTrace();
        }
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
        
        productosFiltrados = SearchUtils.setupSearch(
            txtBuscar,
            listaProductos,
            Product::getModel,
            Product::getBrand
        );

        productosFiltrados.addListener(
            (javafx.collections.ListChangeListener<Product>) change -> {

                tablePagination.setFilteredItems(
                        productosFiltrados
                );
            }
        );
        
    }

    private void suscribirEventos() {
        EventBus.subscribeStock(this::refrescarInventario);
    }
    
    private void configurarPaginacion(){
        
        tablePagination = new TablePagination<>(
            tblProductos,
            paginationController
        );
        paginationController.setItemsPerPage(12);
        paginationController.setShowItemsPerPageSelector(false);
    }
}
