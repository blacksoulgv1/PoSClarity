package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.Ui.*;
import com.gerardgv.posclarity.api.*;
import com.gerardgv.posclarity.models.*;
import com.gerardgv.posclarity.service.TicketService;
import com.gerardgv.posclarity.utils.*;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

public class SaleController implements Initializable {
    
    // ====== DATOS GENERALES ======
    @FXML private TextField txtColaborador;
    @FXML private TextField txtSucursal;
    @FXML private TextField txtDireccionSuc;
    @FXML private TextField txtTelefonoSuc;
    @FXML private TextField txtDate;
    @FXML private TextField txtFolio;
    @FXML private TextField txtCliente;
    @FXML private TextField txtDireccionClient;
    @FXML private TextField txtTelefonoClient;
    
    // ====== GRADUACIÓN ======
    @FXML private TextField txtEsfOD;
    @FXML private TextField txtEsfOI;
    @FXML private TextField txtCylOD;
    @FXML private TextField txtCylOI;
    @FXML private TextField txtEjeOD;
    @FXML private TextField txtEjeOI;
    @FXML private TextField txtAdd;
    @FXML private TextField txtMonto;
    
    // ====== PRODUCTO ======    
    @FXML private TextField txtBuscarProductos;  
    @FXML private Label lblTotal;
    @FXML private Label lblTotalBruto;
    @FXML private Label lblDescuento;
    @FXML private Label lblRestante;
    @FXML private Label lblCambio;
    @FXML private ComboBox<String> cbMetodoPago;
    
    @FXML private TableView<SaleItem> tableProduct;
    @FXML private TableColumn<SaleItem, Integer> colCantidad;
    @FXML private TableColumn<SaleItem,String> colModelo;
    @FXML private TableColumn<SaleItem,String> colMarca;
    @FXML private TableColumn<SaleItem,String> colCategoria;
    @FXML private TableColumn<SaleItem,Double> colDescuento;
    @FXML private TableColumn<SaleItem,Double> colPrecio;
    @FXML private TableColumn<SaleItem,Double> colSubtotal;
    @FXML private TableColumn<SaleItem,Void> colAcciones;
    @FXML private StackPane rootSale;
    /*
    API
    */
    private final ProductApiClient productApiClient = new ProductApiClient();
    private final InventoryApiClient inventoryApiClient = new InventoryApiClient();
    private final SaleApiClient saleApiClient = new SaleApiClient();
    private final ClientApiClient clientApiClient = new ClientApiClient();
    private final SellerApiClient sellerApiClient = new SellerApiClient();
    private final DiscountApiClient discountApiClient = new DiscountApiClient();
    
    private ObservableList<SaleItem> carrito = FXCollections.observableArrayList(); 
    
    
    private Clients clienteSeleccionado = null;     
    private Seller vendedorSeleccionado = null; 
    
    
    private PosSearchPopup<Product> popupProductos = new PosSearchPopup<>();
    private PosSearchPopup<Clients> popupClientes  = new PosSearchPopup<>();
    private PosSearchPopup<Seller> popupVendedores  = new PosSearchPopup<>();
    
   private static class ResultadoPromocion {
       private final Discount promocion;
        private final double descuento;

        public ResultadoPromocion(
            Discount promocion,
            double descuento) {

            this.promocion = promocion;
            this.descuento = descuento;
        }

        public Discount getPromocion() {
            return promocion;
        }

        public double getDescuento() {
            return descuento;
        }
   }
               
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        aplicarComportamientos();
        cargarSucursal();
        cargarFecha();
        configurarTabla();
        configurarPopupProductos();
        configurarPopupClientes();
        configurarPopupVendedores();
        
        cbMetodoPago.getItems().addAll("EFECTIVO","TARJETA","TRANSFERENCIA");
        cbMetodoPago.setValue("EFECTIVO");
        
        
        txtMonto.textProperty().addListener((obs, oldVal, newVal) -> {
            actualizarPago();
        });
        
        cbMetodoPago.setOnAction(e ->{
            if(cbMetodoPago.getValue().equals("EFECTIVO")){
                txtMonto.setText(String.valueOf(calcularTotalVenta()));
            }
        });
        
        colAcciones.setCellFactory(param ->
            PosActions.delete(item ->{
                carrito.remove(item);
                recalcularPromociones();
                actualizarTotal();
                actualizarPago();
            })
        ); 
        
        PosActions.delete(item ->{
            carrito.remove(item);
            recalcularPromociones();
            actualizarTotal();
            actualizarPago();
            mostrarExito("Producto Eliminado Del Carrito");
        });
        
    }
    
    
    private void actualizarPago(){
        double total = calcularTotalVenta();
        double ingresado = 0;
        
        try{
            if(!txtMonto.getText().isEmpty()){
                ingresado = Double.parseDouble(txtMonto.getText());
            }
        } catch(Exception e){
            return;
        }
        double restante = total - ingresado;
        double cambio = ingresado - total;
        
        if(restante > 0){
            lblRestante.setText("$" + String.format("%.2f",restante));
            lblRestante.setStyle("-fx-text-fill:red;");
        } else {
            lblRestante.setText("$0.00");
            lblRestante.setStyle("-fx-text-fill:green;");
        }
        
        if(cambio > 0){
            lblCambio.setText("$" + String.format("%.2f",cambio));
            lblCambio.setStyle("-fx-text-fill:green;");
        } else {
            lblCambio.setText("$0.00");
            lblCambio.setStyle("");
        }
        
    }
    
    private void configurarTabla(){
        
        PosTable.apply(tableProduct);
        
        colCantidad.setCellValueFactory(data ->
            new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());
        
        colCantidad.setCellFactory(
                javafx.scene.control.cell.TextFieldTableCell.forTableColumn(
                new javafx.util.converter.IntegerStringConverter()));
        
        colCantidad.setOnEditCommit(e ->{
            SaleItem item = e.getRowValue();
            item.setQuantity(e.getNewValue());
            tableProduct.refresh();
            actualizarTotal();
        });
        
        colModelo.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getProduct().getModel()));
        
        colMarca.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getProduct().getBrand()));
        
        colCategoria.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getProduct().getCategory()));
        
        colDescuento.setCellValueFactory(data -> 
            new SimpleDoubleProperty(data.getValue().getDiscount()).asObject());
        
        colPrecio.setCellValueFactory(data ->
            new SimpleDoubleProperty(data.getValue().getProduct().getPrice()).asObject());
        
        colSubtotal.setCellValueFactory(data ->
            new SimpleDoubleProperty(data.getValue().getSubtotal()).asObject());
        
        colCategoria.setCellFactory(col -> PosTable.categoryBadgeCell());
        colPrecio.setCellFactory(col -> PosTable.moneyCell(false));
        colDescuento.setCellFactory(col -> PosTable.discountCell());
        colSubtotal.setCellFactory(col -> PosTable.moneyCell(true));
        
        PosTable.placeholder(tableProduct,
                "No Hay Productos en el Carrito",
                "Buscar Productos y Agregalos al Carrito",
                "fas-shopping-cart");
        
        tableProduct.setEditable(true);
        PosTable.animationRows(tableProduct);
    }
    
    private void cargarFecha(){
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        txtDate.setText(LocalDate.now().format(formato));
    }

    private void aplicarComportamientos() {
        
        /*Limpiar Campos de Texto*/
        configurarAutoLimpieza(txtColaborador);
        configurarAutoLimpieza(txtCliente);
        configurarAutoLimpieza(txtDireccionClient);
        configurarAutoLimpieza(txtTelefonoClient);
        
        /*Valida que solo acepte numeracion Enteros y Decimales*/
        ValidacionSphCyl(txtEsfOD);
        ValidacionSphCyl(txtCylOD);
        ValidacionSphCyl(txtEsfOI);
        ValidacionSphCyl(txtCylOI);
        
        /*Validacion Solo Positivos de Add*/
        ValidationAdd(txtAdd);
        
        
    }
    
    /*Metodo para Limpiar Campos de Texto*/    
    private void configurarAutoLimpieza(TextField txt){
        
        txt.focusedProperty().addListener((obs,oldVal,newVal) -> {
            if(newVal){ // Cuando entre al Texto.
                txt.setStyle("-fx-background-color:white;");
                
                //Si solo tiene espacios limpia el texto.
                if(txt.getText().trim().isEmpty()){
                    txt.clear();
                }
            }else{
                txt.setStyle("");
            }
        });
    }
  
    /*Metodo para Decimales y Numeros*/    
    private void ValidacionSphCyl(TextField txt){
        txt.setTextFormatter(new TextFormatter<>(c -> {
            if(c.getControlNewText().matches("[+-]?\\d*(\\.\\d*)?")){
                return c;
            }
            return null;
        }));
    }
    
    private void ValidationAdd(TextField txt){
        txt.setTextFormatter(new TextFormatter<>(c ->{
            if(c.getControlNewText().matches("\\d*(\\.\\d*)?")){
                return c;
            }
            return null;
        }));
    }

    private void cargarSucursal() {
        Branch suc = Session.getSucursal();
        
        if(suc != null){
            txtSucursal.setText(suc.getName());
            txtDireccionSuc.setText(suc.getAddress());
            txtTelefonoSuc.setText(suc.getPhone());            
        }
    }
           
    private void configurarPopupProductos(){
        
        popupProductos.setTitleProvider(p ->
            p.getModel()+ " - " + p.getBrand());
        
        popupProductos.setSubtitleProvider(p ->
            p.getCategory()+ " | $" + String.format("%.2f", p.getPrice()) + " | stock:" + p.getStock());
        
        popupProductos.setIconProvider(p->"fas-box-open");
        popupProductos.setEmptyMessage("No Se Encontro Productos");
        
        popupProductos.setOnSelected(this::agregarProducto);
        
        txtBuscarProductos.textProperty().addListener((obs, oldText, newText) -> {
            if(newText == null || newText.isBlank()){
                popupProductos.hide();
                return;
            }

        try{
            List<Product> lista =
                    productApiClient.search(newText);

            popupProductos.setItems(lista);
            popupProductos.show(txtBuscarProductos);
        }catch(IOException | InterruptedException e){

            e.printStackTrace();
            mostrarError("Error buscando productos");
        }
        });

        txtBuscarProductos.setOnAction(e -> {
            Product p = popupProductos.getFirst();

            if(p != null){
                agregarProducto(p);
            }
        });        
    }
    
    @FXML
    private void agregarProducto(){
        
        String texto = txtBuscarProductos.getText();
        if(texto.isEmpty()) return;
        
        try{
            
            Product p = productApiClient.getByModel(texto);

            if (p != null) {
                agregarProducto(p);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            mostrarError("Error al buscar producto");
        }
    }
    
    private void agregarProducto(Product p){
        
        Product productDB;
        
        try{
            
            productDB = productApiClient.getById(p.getId());
            
            if (productDB == null) {
                mostrarError("Error al obtener producto");
                return;
            }

            Inventory inventory = inventoryApiClient.getProductStock(
                Session.getSucursal().getId(),
                productDB.getId());

            if (inventory != null) {
                productDB.setStock(inventory.getStock());
            } else {
                productDB.setStock(0);
            }           
        } catch(IOException | InterruptedException e) {
            e.printStackTrace();
            mostrarError("Error al obtener producto");
        return;
        }
        
        if(productDB.getManageStock()&& productDB.getStock()<=0){
            mostrarError("Sin Stock");
            return;
        }
        
        for(SaleItem item : carrito){
            
            if(item.getProduct().getId() == productDB.getId()){
                
                if(productDB.getManageStock() && item.getQuantity() 
                        >= productDB.getStock()){
                  mostrarError("Stock Máximo");
                    return;  
                }
                item.setQuantity(item.getQuantity()+1);
                recalcularPromociones();
                return;
            }            
        }       
        
        carrito.add(new SaleItem(productDB,"Sin Descuento",0));
        tableProduct.setItems(carrito);
        recalcularPromociones();
        mostrarExito("Producto Agregado Al Carrito");       
        txtBuscarProductos.clear();
        popupProductos.hide();
    }
    
    @FXML
    private void handleAgregarManual(){
        String texto = txtBuscarProductos.getText();
        
        if(texto.isEmpty()) return;
        
        try {

            Product p = productApiClient.getByModel(texto);

        if (p != null) {
            agregarProducto(p);
        }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            mostrarError("Error al buscar producto");
        }
    }
    
    private void configurarPopupClientes(){
        
        popupClientes.setTitleProvider(c -> c.getName());
        popupClientes.setSubtitleProvider(c -> "Tel: " + c.getPhone());
        popupClientes.setIconProvider(c -> "fas-user");
        popupClientes.setEmptyMessage("No se encontraron clientes");
    
        popupClientes.setOnSelected(this::llenarDatosCliente);

        txtCliente.textProperty().addListener((obs, oldText, newText) -> {

            if(newText == null || newText.isBlank()){
                popupClientes.hide();
                return;
            }

            try{
                List<Clients> lista = clientApiClient.search(newText);
                
                popupClientes.setItems(lista);
                popupClientes.show(txtCliente);
            } catch (IOException | InterruptedException e){
                e.printStackTrace();
                popupClientes.hide();
                
                if(e instanceof InterruptedException){
                    Thread.currentThread().interrupt();
                }
            }
        });

        txtCliente.setOnAction(e -> {
            Clients c = popupClientes.getFirst();

            if(c != null){
                llenarDatosCliente(c);
            }
        });
    }  

    private void llenarDatosCliente(Clients c){
        clienteSeleccionado = c;
        txtCliente.setText(c.getName());
        txtTelefonoClient.setText(c.getPhone());
        txtDireccionClient.setText(c.getAddress());
        txtEsfOD.setText(c.getOdEsf());
        txtCylOD.setText(c.getOdCil());
        txtEjeOD.setText(c.getOdEje());
        txtEsfOI.setText(c.getOiEsf());
        txtCylOI.setText(c.getOiCil());
        txtEjeOI.setText(c.getOiEje());
        txtAdd.setText(c.getAdd());        
       
        popupClientes.hide();
    }

    private ResultadoPromocion evaluarPromocion(Product p) {

    try {

        List<Discount> promociones =
                discountApiClient.getActiveByCategory(
                        p.getCategory()
                );
System.out.println(
        "\n===== PROMOCIONES PARA: "
        + p.getModel()
        + " | CATEGORIA: "
        + p.getCategory()
        + " ====="
);

if (promociones != null) {

    System.out.println(
            "Cantidad: " + promociones.size()
    );

    for (Discount promo : promociones) {

        System.out.println(
                "ID: " + promo.getId()
                + " | Nombre: " + promo.getName()
                + " | Tipo: " + promo.getValueType()
                + " | Valor: " + promo.getValue()
                + " | ProductoID: "
                + promo.getBenefitProductId()
                + " | Modelo: "
                + promo.getBenefitProductModel()
                + " | RequiereArmazon: "
                + promo.isRequireFrame()
                + " | MaxDioptria: "
                + promo.getMaxDiopter()
                + " | Prioridad: "
                + promo.getPriority()
                + " | Activo: "
                + promo.isActive()
        );
    }
}

        if (promociones == null || promociones.isEmpty()) {
            return new ResultadoPromocion(null, 0);
        }

        /*
         * ==========================================
         * ORDENAR POR PRIORIDAD
         * ==========================================
         *
         * La prioridad más alta se evalúa primero.
         */
        promociones.sort(
                Comparator.comparingInt(
                        Discount::getPriority
                ).reversed()
        );

        for (Discount promo : promociones) {

            /*
             * ==========================================
             * PRODUCTO BENEFICIADO
             * ==========================================
             *
             * Si la promoción tiene un producto
             * específico, debe coincidir con el producto
             * que estamos evaluando.
             */
            if (promo.getBenefitProductId() != null) {

                if (p.getId() != promo.getBenefitProductId()) {
                    continue;
                }
            }

            /*
             * ==========================================
             * MODELO BENEFICIADO
             * ==========================================
             *
             * Compatibilidad con promociones que
             * utilizan el modelo en lugar del ID.
             */
            if (promo.getBenefitProductModel() != null
                    && !promo.getBenefitProductModel().isBlank()) {

                if (!p.getModel().equalsIgnoreCase(
                        promo.getBenefitProductModel())) {

                    continue;
                }
            }

            /*
             * ==========================================
             * REQUIERE ARMAZÓN
             * ==========================================
             */
            if (promo.isRequireFrame()
                    && !ventaTieneArmazon()) {

                continue;
            }

            /*
             * ==========================================
             * DIÓPTRIA MÁXIMA
             * ==========================================
             */
            if (promo.getMaxDiopter() != null) {

                double dioptriaMayor =
                        obtenerDioptriaMayor();

                if (dioptriaMayor >
                        promo.getMaxDiopter()) {

                    continue;
                }
            }

            /*
             * ==========================================
             * TIPO DE PROMOCIÓN
             * ==========================================
             */
            String tipo = promo.getValueType();

            if (tipo == null || tipo.isBlank()) {
                continue;
            }

            switch (tipo.toUpperCase()) {

                /*
                 * --------------------------------------
                 * PORCENTAJE
                 * --------------------------------------
                 */
                case "PORCENTAJE":

                    if (promo.getValue() == null) {
                        continue;
                    }

                    double descuentoPorcentaje =
                            p.getPrice()
                            * (promo.getValue() / 100);

                    return new ResultadoPromocion(
                            promo,
                            descuentoPorcentaje
                    );

                /*
                 * --------------------------------------
                 * MONTO
                 * --------------------------------------
                 */
                case "MONTO":

                    if (promo.getValue() == null) {
                        continue;
                    }

                    /*
                     * Nunca permitir que el descuento
                     * sea mayor al precio del producto.
                     */
                    double descuentoMonto =
                            Math.min(
                                    promo.getValue(),
                                    p.getPrice()
                            );

                    return new ResultadoPromocion(
                            promo,
                            descuentoMonto
                    );

                /*
                 * --------------------------------------
                 * GRATIS
                 * --------------------------------------
                 */
                case "GRATIS":

                    /*
                     * En GRATIS el valor puede ser NULL.
                     *
                     * El descuento es el precio completo
                     * del producto.
                     */
                    return new ResultadoPromocion(
                            promo,
                            p.getPrice()
                    );

                /*
                 * --------------------------------------
                 * TIPO DESCONOCIDO
                 * --------------------------------------
                 */
                default:

                    continue;
            }
        }

    } catch (IOException | InterruptedException e) {

        e.printStackTrace();

        mostrarError(
                "No se pudieron consultar las promociones."
        );
    }

    return new ResultadoPromocion(null, 0);
}
    
    private double calcularTotalVenta() {

    double total = 0;

    // ==============================
    // 1. TOTAL DE PRODUCTOS
    // ==============================

    for (SaleItem item : carrito) {

        if (item.getProduct() == null) {
            continue;
        }

        total += item.getSubtotal();
    }

    // ==============================
    // 2. DESCUENTO GENERAL
    // ==============================

    try {

        List<Discount> descuentosGenerales =
                discountApiClient.getActiveGeneral();

        if (descuentosGenerales != null
                && !descuentosGenerales.isEmpty()) {

            Discount descuentoGeneral =
                    descuentosGenerales.stream()

                            // IMPORTANTE:
                            // Un GRATIS no es descuento general
                            .filter(d ->
                                    "PORCENTAJE".equalsIgnoreCase(
                                            d.getValueType())
                                    ||
                                    "MONTO".equalsIgnoreCase(
                                            d.getValueType())
                            )

                            .filter(d ->
                                    d.getValue() != null
                            )

                            .max(
                                    Comparator.comparingInt(
                                            Discount::getPriority
                                    )
                            )

                            .orElse(null);

            if (descuentoGeneral != null) {

                if ("PORCENTAJE".equalsIgnoreCase(
                        descuentoGeneral.getValueType())) {

                    total -= total *
                            (descuentoGeneral.getValue() / 100);

                } else if ("MONTO".equalsIgnoreCase(
                        descuentoGeneral.getValueType())) {

                    total -= Math.min(
                            descuentoGeneral.getValue(),
                            total
                    );
                }
            }
        }

    } catch (IOException | InterruptedException e) {

        e.printStackTrace();

        mostrarError(
                "No se pudieron consultar los descuentos generales."
        );
    }

    // Nunca permitir total negativo
    return Math.max(total, 0);
}
    
    private void actualizarTotal() {
        
    double totalBruto = 0;
    double descuentoProductos = 0;

    /*
     * ==============================
     * CALCULAR PRODUCTOS
     * ==============================
     */
    for (SaleItem item : carrito) {
        

        if (item.getProduct() == null) {
            continue;
        }

        double precio = item.getProduct().getPrice();
        int cantidad = item.getQuantity();
        double descuento = item.getDiscount();

        totalBruto += precio * cantidad;
        descuentoProductos += descuento * cantidad;
    }

    /*
     * Total después de descuentos
     * por producto
     */
    double totalConDescuento =
            totalBruto - descuentoProductos;

    double descuentoGeneral = 0;

    /*
     * ==============================
     * DESCUENTO GENERAL
     * ==============================
     */
    try {

        List<Discount> descuentosGenerales =
                discountApiClient.getActiveGeneral();

        if (descuentosGenerales != null
                && !descuentosGenerales.isEmpty()) {

            Discount dGeneral =
                    descuentosGenerales.stream()
                            .max((d1, d2) ->
                                    Integer.compare(
                                            d1.getPriority(),
                                            d2.getPriority()
                                    ))
                            .orElse(null);

            if (dGeneral != null) {

                if ("PORCENTAJE".equalsIgnoreCase(
                        dGeneral.getValueType())) {

                    descuentoGeneral =
                            totalConDescuento
                            * (dGeneral.getValue() / 100);

                } else if ("MONTO".equalsIgnoreCase(
                        dGeneral.getValueType())) {

                    descuentoGeneral =
                            dGeneral.getValue();
                }
            }
        }

    } catch (IOException | InterruptedException e) {

        e.printStackTrace();

        mostrarError(
                "No se pudieron consultar los descuentos generales."
        );

    } catch (Exception e) {

        /*
         * Evitamos que un problema con promociones
         * impida mostrar los totales de la venta.
         */
        e.printStackTrace();

        mostrarError(
                "Ocurrió un error al consultar las promociones."
        );
    }

    /*
     * ==============================
     * TOTAL FINAL
     * ==============================
     */
    double totalFinal =
            totalConDescuento - descuentoGeneral;

    /*
     * Nunca permitir negativos
     */
    if (totalFinal < 0) {
        totalFinal = 0;
    }

    /*
     * ==============================
     * ACTUALIZAR INTERFAZ
     * ==============================
     */
    lblTotalBruto.setText(
            "$" + String.format("%.2f", totalBruto)
    );

    lblDescuento.setText(
            "$" + String.format(
                    "%.2f",
                    descuentoProductos + descuentoGeneral
            )
    );

    lblTotal.setText(
            "$" + String.format("%.2f", totalFinal)
    );

    /*
     * Actualizar restante/cambio
     */
    //actualizarPago();
}
          
    @FXML
    private void guardarVenta(){
        
        if(clienteSeleccionado == null){
            mostrarError("Selecciona un Cliente");
            return;
        }
        
        if(vendedorSeleccionado == null){
            mostrarError("Selecciona un Vendedor");
            return;
        }
        
        if(carrito.isEmpty()){
            mostrarError("No Hay Productos en la Venta");
            return;
        }
        
        double total = calcularTotalVenta();
        double monto = 0;
        boolean esBajoPedido = contieneBajoPedido(carrito);
   
        try{
            monto = txtMonto.getText().isEmpty() ? 0 : Double.parseDouble(txtMonto.getText());
        } catch(Exception e){
            mostrarError("Monto Inválido");
            return;
        }
        
        if(esBajoPedido){
            double anticipoMinimo = total * 0.30;
            if(monto < anticipoMinimo){
                mostrarError("Se Requiere Mínimo el 30% de Anticipo: $"
                        + String.format("%.2f",anticipoMinimo));
                return;
            }
        } else{
            if(monto<=0){
                mostrarError("Debe ingresar un monto");
                return;
            }
        }       
                
        Payment payment = null;
        
        if(monto > 0){
            
            payment = new Payment();
            
            payment.setPaymentMethod(cbMetodoPago.getValue());
            payment.setAmount(monto);
            payment.setReference(null);
            payment.setPaymentType("VENTA");
        }
        
        Sale sale;
        
        try{
            
            sale = saleApiClient.create(
                    new ArrayList<>(carrito),
                    payment,
                    clienteSeleccionado.getId(),
                    vendedorSeleccionado.getId(),
                    txtEsfOD.getText(),
                    txtCylOD.getText(),
                    txtEjeOD.getText(),
                    txtEsfOI.getText(),
                    txtCylOI.getText(),
                    txtEjeOI.getText(),
                    txtAdd.getText(),
                    null );
            
            if(clienteSeleccionado != null){
                sale.setClient(clienteSeleccionado);
            }
            
        } catch (IOException | InterruptedException e){
            e.printStackTrace();
            mostrarError("No se Puede Guardar Venta Con El Servidor");
            return;
        }
        
        if(sale == null){
            mostrarError("Error al Guardar Venta");
            return;
        }
        
        txtFolio.setText(sale.getFolio());
        
        List<SaleItem> items = sale.getItems();
        
        TicketService ticketService = new TicketService();

        ticketService.imprimirTicket(
            sale,
            items
        );

        ticketService.imprimirOrdenLaboratorio(
            sale,
            items
        );
        
        EventBus.publishVenta(
            clienteSeleccionado.getId()
        );

        List<Integer> productosIds = carrito.stream()
            .map(item -> item.getProduct().getId())
            .toList();

        EventBus.publishStock(
            productosIds
        );
        
        mostrarExito("Venta Realizada Correctamente");

        limpiarVenta();
    }
    
    private void mostrarError(String msg){
        PosNotification.error(rootSale, "Atención", msg);
    }

    private void mostrarExito(String msg){
        PosNotification.success(rootSale, "Listo", msg);
    }
    
    
    private boolean contieneBajoPedido(List<SaleItem> carrito){
        
        return carrito.stream().anyMatch(item -> 
                !item.getProduct().getManageStock());
    }
    
    private void limpiarVenta(){
        
        //==== CLIENTE ====
        clienteSeleccionado = null;
        txtCliente.clear();
        txtTelefonoClient.clear();
        txtDireccionClient.clear();
        
        //==== GRADUACIÓN ====
        txtEsfOD.clear();
        txtCylOD.clear();
        txtEjeOD.clear();
        txtEsfOI.clear();
        txtCylOI.clear();
        txtEjeOI.clear();
        txtAdd.clear();
        
        //==== PRODUCTOS ====
        carrito.clear();
        tableProduct.refresh();
        tableProduct.refresh();
        txtBuscarProductos.clear();
        
        //==== PAGO ====
        txtMonto.clear();
        cbMetodoPago.setValue("EFECTIVO");
        
        //==== PAGO ====
        lblTotalBruto.setText("$0.00");
        lblDescuento.setText("$0.00");
        lblTotal.setText("$0.00");
        lblRestante.setText("$0.00");
        lblCambio.setText("$0.00");
        actualizarTotal();
    }
    
    private double calcularTotalBruto(){
        
        return carrito.stream()
            .mapToDouble(item  -> 
                    item.getProduct().getPrice()
                    * item .getQuantity())
            .sum();
    }

    private double calcularDescuento(){
        
        return carrito.stream()
            .mapToDouble(item ->
                    item.getDiscount()
                    * item.getQuantity())
            .sum();
    }

    private double calcularTotalFinal(){
        return calcularTotalBruto() - calcularDescuento();
    }
    
    private void configurarPopupVendedores(){
        
        popupVendedores.setTitleProvider(emp -> emp.getName());
        popupVendedores.setSubtitleProvider(emp -> "Código: " + emp.getCode());
        popupVendedores.setIconProvider(emp -> "fas-user-tie");
        popupVendedores.setEmptyMessage("No se encontraron colaboradores");

        popupVendedores.setOnSelected(emp -> {
            vendedorSeleccionado = emp;
            txtColaborador.setText(emp.getName());
        });

        txtColaborador.textProperty().addListener((obs, oldText, newText) -> {

            if(newText == null || newText.isBlank()){
                popupVendedores.hide();
                return;
            }

            try {

                List<Seller> lista = sellerApiClient.getAll()
                    .stream() .filter(emp ->
                            emp.getName()
                            .toLowerCase()
                            .contains(newText.toLowerCase())
                    ) .toList();
                
            popupVendedores.setItems(lista);
            popupVendedores.show(txtColaborador);

        } catch(IOException | InterruptedException e){

            e.printStackTrace();
            mostrarError("Error cargando vendedores");
        } });

        txtColaborador.setOnAction(e -> {
            Seller emp = popupVendedores.getFirst();

            if(emp != null){
                vendedorSeleccionado = emp;
                txtColaborador.setText(emp.getName());
                popupVendedores.hide();
            }
        });
    }
    
    private boolean ventaTieneArmazon(){

    return carrito.stream()
            .anyMatch(item ->
                    item.getProduct()
                            .getCategory()
                            .equalsIgnoreCase("armazon"));
    }
    
    private double obtenerDioptriaMayor(){

        try{
            double od = txtEsfOD.getText().isBlank()
                ? 0
                : Math.abs(Double.parseDouble(txtEsfOD.getText()));
            double oi = txtEsfOI.getText().isBlank()
                ? 0
                : Math.abs(Double.parseDouble(txtEsfOI.getText()));

            return Math.max(od, oi);
        }catch(Exception e){
            return 0;
        }
    }
    
    private void recalcularPromociones(){
        
        for (SaleItem item : carrito) {
            
            Product product = item.getProduct();
            ResultadoPromocion resultado = evaluarPromocion(product);
            item.setDiscount(resultado.getDescuento());
            
            if(resultado.getPromocion() != null){
                item.setDiscountName(
                    resultado.getPromocion().getName()
            );
            }else{
                item.setDiscountName(
                    "Sin Descuento"
            );
            }
        }
        
        tableProduct.refresh();
        actualizarTotal();
    }
      
}
