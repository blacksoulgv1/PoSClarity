package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.database.*;
import com.gerardgv.posclarity.models.*;
import com.gerardgv.posclarity.service.TicketService;
import com.gerardgv.posclarity.utils.EventBus;
import com.gerardgv.posclarity.utils.Session;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.stage.Popup;


public class SaleController implements Initializable {
    
    // ====== DATOS GENERALES ======
    @FXML private TextField txtColaborador;
    @FXML private TextField txtSucursal;
    @FXML private TextField txtDireccionSuc;
    @FXML private TextField txtTelefonoSuc;
    @FXML private TextField txtDate;
    @FXML private TextField txtNote;
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
    
    private ProductDAO productDAO = new ProductDAO();
    private Popup popupProductos = new Popup();
    private ListView<Product> listViewProduct = new ListView<>();    
    private ObservableList<Product> resultados = FXCollections.observableArrayList();    
    private ObservableList<SaleItem> carrito = FXCollections.observableArrayList(); 
    private ClientsDAO clientsDAO = new ClientsDAO();
    private Popup popupClientes = new Popup();
    private ListView<Clients> listViewClients = new ListView<>();
    private ObservableList<Clients> resultadosClients = FXCollections.observableArrayList();
    private DescuentoDAO descuentoDAO = new DescuentoDAO();
    private SaleDAO saleDAO = new SaleDAO(); 
    private Clients clienteSeleccionado = null;
    
    private Empleados vendedorSeleccionado = null;
    private EmpleadosDAO empleadosDAO = new EmpleadosDAO();
    private Popup popupVendedores = new Popup();
    private ListView<Empleados> listViewVendedores = new ListView<>();
    private ObservableList<Empleados> resultadosVendedores = FXCollections.observableArrayList();
           
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
        
        
        colAcciones.setCellFactory(param -> new TableCell<>(){
            private final Button btnEliminar = new Button("X");
            {
                btnEliminar.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                btnEliminar.setOnAction(e -> {
                    SaleItem item = getTableView().getItems().get(getIndex());
                    carrito.remove(item);
                    actualizarTotal();
                    actualizarPago();
                });
            }
            @Override
            protected void updateItem(Void item,boolean empty){
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnEliminar);
            }
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
        
        colCantidad.setCellValueFactory(data ->
            new SimpleIntegerProperty(data.getValue().getCantidad()).asObject());
        colCantidad.setCellFactory(
                javafx.scene.control.cell.TextFieldTableCell.forTableColumn(
                new javafx.util.converter.IntegerStringConverter()));
        colCantidad.setOnEditCommit(e ->{
            SaleItem item = e.getRowValue();
            item.setCantidad(e.getNewValue());
            tableProduct.refresh();
            actualizarTotal();
        });
        
        colModelo.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getProducto().getModelo()));
        colMarca.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getProducto().getMarca()));
        colCategoria.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getProducto().getCategoria()));
        colDescuento.setCellValueFactory(data -> 
            new SimpleDoubleProperty(data.getValue().getDescuento()).asObject());
        colPrecio.setCellValueFactory(data ->
            new SimpleDoubleProperty(data.getValue().getProducto().getPrecio()).asObject());
        tableProduct.setEditable(true);
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
        NumericalValidation(txtEsfOD);
        NumericalValidation(txtCylOD);
        NumericalValidation(txtEsfOI);
        NumericalValidation(txtCylOI);        
        //NumericalValidation(txtPrecio);
        
        /*Validacion Solo Positivos de Add*/
        ValidationAdd(txtAdd);
        
        /*Valida que solo acepte numeracion Enteros*/
        //NumericalValidation2(txtEjeOD);
        //NumericalValidation2(txtEjeOI);
        //NumericalValidation2(txtStock);
        
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
    private void NumericalValidation(TextField txt){
        txt.setTextFormatter(new TextFormatter<>(c -> {
            if(c.getControlNewText().matches("-?\\d*(\\.\\d*)?")){
                return c;
            }
            return null;
        }));
    }
    
    private void NumericalValidation2(TextField txt){
        txt.setTextFormatter(new TextFormatter<>(c -> {
            if(c.getControlNewText().matches("\\d*")){
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
            txtSucursal.setText(suc.getSucursal());
            txtDireccionSuc.setText(suc.getDireccion());
            txtTelefonoSuc.setText(suc.getTelefono());            
        }
    }
        
    @FXML
    private void SeleccionarProducto(){
        
        Product p = listViewProduct.getSelectionModel().getSelectedItem();
        
        if(p != null){
            agregarProducto(p);
        }
    }
    
    private void configurarPopupProductos(){
        
        listViewProduct.setItems(resultados);
        listViewProduct.setPrefHeight(150);
        
        popupProductos.getContent().add(listViewProduct);
        popupProductos.setAutoHide(true);
        
        txtBuscarProductos.textProperty().addListener((obs,oldText,newText) -> {
            if(newText.isEmpty()){
                popupProductos.hide();
                return;
            }
            
            List<Product> lista = productDAO.buscarPorNombre(
                    newText,
                    Session.getSucursal().getId());
            if(lista.isEmpty()){
                popupProductos.hide();
                return;
            }
            resultados.setAll(lista);
            mostrarPopupProductos();
        });
        //clic en item
        listViewProduct.getSelectionModel().selectedItemProperty().addListener(
            (obs,oldVal,newVal) -> {
                if(newVal !=null){
                    agregarProducto(newVal);
                }
            });
        
        //Funcion de seleccion con enter
        txtBuscarProductos.setOnAction(e ->{
            
            if(!resultados.isEmpty()){
                agregarProducto(resultados.get(0));
            }
        });
        
        listViewProduct.setCellFactory(lv -> new ListCell<>(){
            @Override
            protected void updateItem(Product p, boolean empty){
                super.updateItem(p, empty);
                if(empty || p == null){
                    setText(null);
                } else{
                    setText(p.getModelo() + " - $" + p.getPrecio() + " | Stock:"+ p.getStock());
                }
            }
        });
    }
    
    private void mostrarPopupProductos(){
        if(!popupProductos.isShowing()){
            Bounds boundsp = txtBuscarProductos.localToScreen(
            txtBuscarProductos.getBoundsInLocal());
            
            popupProductos.show(
                    txtBuscarProductos,
                    boundsp.getMinX(),
                    boundsp.getMaxY());
        }    }
    
    private void mostrarPopupClientes(){
        
        if(!popupClientes.isShowing()){
            Bounds boundsc = txtCliente.localToScreen(
            txtCliente.getBoundsInLocal());
            
            popupClientes.show(
                    txtCliente,
                    boundsc.getMinX(),
                    boundsc.getMaxY());
        }
    }
    
    @FXML
    private void agregarProducto(){
        
        String texto = txtBuscarProductos.getText();
        if(texto.isEmpty()) return;
        
        Product p = productDAO.getByModel(texto);
        
        if(p != null){
            agregarProducto(p);
        }
    }
    
    private void agregarProducto(Product p){
        
        Product productDB = productDAO.getById(p.getId_product(),
                Session.getSucursal().getId());
        
        if(productDB == null){
            mostrarAlerta("Error al obtener producto");
            return;
        }
        
        productDB.setStock(p.getStock());
        
        if(productDB.isManejaStock() && productDB.getStock()<=0){
            mostrarAlerta("Sin Stock");
            return;
        }

        
        for(SaleItem item : carrito){
            if(item.getProducto().getId_product() == productDB.getId_product()){
                if(productDB.isManejaStock() && item.getCantidad() >= productDB.getStock()){
                  mostrarAlerta("Stock Máximo");
                    return;  
                }
                item.setCantidad(item.getCantidad()+1);
                tableProduct.refresh();
                actualizarTotal();
                return;
            }            
        }       
        
        double descuento = calcularDescuento(productDB);        
        Descuento dCategoria = descuentoDAO.obtenerPorCategoria(productDB.getCategoria());
        String nombreDescuento = (dCategoria != null) ? dCategoria.getNombre() : "Sin Descuento";
        
        carrito.add(new SaleItem(productDB,nombreDescuento,descuento));
        tableProduct.setItems(carrito);
        actualizarTotal();
        txtBuscarProductos.clear();
        popupProductos.hide();
    }
    
    @FXML
    private void handleAgregarManual(){
        String texto = txtBuscarProductos.getText();
        
        if(texto.isEmpty()) return;
        
        Product p = productDAO.getByModel(texto);
        
        if(p != null){
            agregarProducto(p);
        }
    }
    
    private void configurarPopupClientes(){
        
        listViewClients.setItems(resultadosClients);
        listViewClients.setPrefHeight(150);
        
        popupClientes.getContent().add(listViewClients);
        popupClientes.setAutoHide(true);
        
        txtCliente.textProperty().addListener((obs, oldText,newText) -> {
            
            if(newText.isEmpty()){
                popupClientes.hide();
                return;
            }
            
            List<Clients> lista = clientsDAO.buscarPorNombre(newText);
            
            if(lista.isEmpty()){
                popupClientes.hide();
                return;
            }
            
            resultadosClients.setAll(lista);
            mostrarPopupClientes();
        });
        
        listViewClients.setOnMouseClicked(e -> seleccionarCliente());
        
        txtCliente.setOnAction(e ->{
            if(!resultadosClients.isEmpty()){
                seleccionarClienteDirecto(resultadosClients.get(0));
            }
        });
        
        listViewClients.setCellFactory(param -> new ListCell<>(){
            @Override
            protected void updateItem(Clients item,boolean empty){
                super.updateItem(item, empty);
                
                if(empty || item == null){
                    setText(null);
                } else {
                    setText(item.getNombre() + " - " + item.getTelefono());
                }
            }
        });
    }
    
    private void seleccionarCliente(){
        
        Clients c = listViewClients.getSelectionModel().getSelectedItem();
        
        if(c != null){
            llenarDatosCliente(c);
        }
    }
    
    private void seleccionarClienteDirecto(Clients c){
        llenarDatosCliente(c);
    }
    
    private void llenarDatosCliente(Clients c){
        clienteSeleccionado = c;
        txtCliente.setText(c.getNombre());
        txtTelefonoClient.setText(c.getTelefono());
        txtDireccionClient.setText(c.getDireccion());
        txtEsfOD.setText(c.getOdEsf());
        txtCylOD.setText(c.getOdCil());
        txtEjeOD.setText(c.getOdEje());
        txtEsfOI.setText(c.getOiEsf());
        txtCylOI.setText(c.getOiCil());
        txtEjeOI.setText(c.getOiEje());
        txtAdd.setText(c.getAdd());        
       
        popupClientes.hide();
    }

    private double calcularDescuento(Product p) {
        
        double descuentoTotal = 0;
        
        Descuento dCategoria = descuentoDAO.obtenerPorCategoria(p.getCategoria());
        
        if(dCategoria != null){
            if(dCategoria.getTipoValor().equalsIgnoreCase("PORCENTAJE")){
                descuentoTotal += p.getPrecio() *(dCategoria.getValor()/100);
            } else {
                descuentoTotal += dCategoria.getValor();
            }
        }
        return descuentoTotal;
    }
    
   private double calcularTotalVenta(){
        
        double total = 0;
        
        for(SaleItem item : carrito){
            total += item.getSubtotal();
        }
        
        Descuento dGeneral = descuentoDAO.obtenerGeneralActivo();
        
        if(dGeneral != null){
            if(dGeneral.getTipoValor().equalsIgnoreCase("porcentaje")){
            total -= total*(dGeneral.getValor()/100);
        } else {
            total -= dGeneral.getValor();
        }
    }
        return total;
    }
    
    private void actualizarTotal(){
        double totalBruto = 0;
        double descuentoProductos = 0;
        
        for(SaleItem item : carrito){
            totalBruto += item.getProducto().getPrecio() * item.getCantidad();
            descuentoProductos += item.getDescuento() *item.getCantidad();
        }
        
        double totalconDescuento = totalBruto - descuentoProductos;
        double descuentoGeneral = 0;
        Descuento dGeneral = descuentoDAO.obtenerGeneralActivo();
        
        if(dGeneral != null){
            if(dGeneral.getTipoValor().equalsIgnoreCase("PORCENTAJE")){
                descuentoGeneral = totalconDescuento * (dGeneral.getValor()/100);
            } else {
                descuentoGeneral = dGeneral.getValor();
            }
        }
        
        double totalFinal = totalconDescuento - descuentoGeneral;
        
        lblTotalBruto.setText("$" + String.format("%.2f",totalBruto));
        lblDescuento.setText("$" + String.format("%.2f", (descuentoProductos + descuentoGeneral)));
        lblTotal.setText("$" + String.format("%.2f", totalFinal));
    }
          
    @FXML
    private void guardarVenta(){
        
        if(clienteSeleccionado == null){
            mostrarAlerta("Selecciona un Cliente");
            return;
        }
        
        if(vendedorSeleccionado == null){
            mostrarAlerta("Selecciona un Vendedor");
            return;
        }
        
        if(carrito.isEmpty()){
            mostrarAlerta("No Hay Productos en la Venta");
            return;
        }
        
        double total = calcularTotalVenta();
        double monto = 0;
        boolean esBajoPedido = contieneBajoPedido(carrito);
   
        try{
            monto = txtMonto.getText().isEmpty() ? 0 : Double.parseDouble(txtMonto.getText());
        } catch(Exception e){
            mostrarAlerta("Monto Inválido");
            return;
        }
        
        if(esBajoPedido){
            double anticipoMinimo = total * 0.30;
            if(monto < anticipoMinimo){
                mostrarAlerta("Se Requiere Mínimo el 30% de Anticipo: $"
                        + String.format("%.2f",anticipoMinimo));
                return;
            }
        } else{
            if(monto<=0){
                mostrarAlerta("Debe ingresar un monto");
                return;
            }
        }       
                
        //==== ESTADO PAGO ====
        String estadoPago = (monto >= total) ? "COMPLETA":"PENDIENTE";
        //==== ESTADO DE TRABAJO ====
        String estadoTrabajo = esBajoPedido ? "PROCESO":"ENTREGADO";
        
        List<Pago> pagos = new ArrayList<>();
        Pago p = new Pago();
        p.setMetodo(cbMetodoPago.getValue());
        p.setMonto(monto);
        p.setReferencia(null);
        p.setTipoPago("VENTA");
        pagos.add(p);
        
        double totalBruto = calcularTotalBruto();
        double descuentoTotal = calcularDescuento();        
        double totalFinal = calcularTotalVenta();
        
        int idVenta = saleDAO.guardarVenta(
                new ArrayList<>(carrito),
                pagos,
                totalBruto,
                descuentoTotal,
                totalFinal,
                estadoPago,
                clienteSeleccionado.getId(),
                vendedorSeleccionado.getId_vendedor(),
                txtEsfOD.getText(),
                txtCylOD.getText(),
                txtEjeOD.getText(),
                txtEsfOI.getText(),
                txtCylOI.getText(),
                txtEjeOI.getText(),
                txtAdd.getText());
        
        if(idVenta > 0){
            //double cambio = monto - totalFinal;
            //mostrarAlerta("Venta Realizada. Cambio: $" + String.format("%.2f",cambio));
            
            Venta venta = saleDAO.obtenerVentaCompleta(idVenta);
            List<SaleItem> items = saleDAO.obtenerDetalleVenta(idVenta);
            
            TicketService ticketService = new TicketService();
            ticketService.imprimirTicket(venta, items);
            ticketService.imprimirOrdenLaboratorio(venta,items);
            EventBus.publishVenta(clienteSeleccionado.getId());
            List<Integer> productosIds = carrito.stream()
                    .map(item -> item.getProducto().getId_product()).toList();
            EventBus.publishStock(productosIds);
            mostrarAlerta("Venta Realizada Correctamente");
            limpiarVenta();
        } else{
            mostrarAlerta("Error al Guardar Venta");
        }
    }
    
    private boolean contieneBajoPedido(List<SaleItem> carrito){
        return carrito.stream().anyMatch(item -> !item.getProducto().isManejaStock());
    }
    
    private void limpiarVenta(){
        carrito.clear();
        tableProduct.refresh();
        txtMonto.clear();
        lblRestante.setText("$0.00");
        lblCambio.setText("$0.00");
        actualizarTotal();
    }
    
    private void mostrarAlerta(String msg){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    
    private double calcularTotalBruto(){
        return carrito.stream()
            .mapToDouble(i -> i.getProducto().getPrecio() * i.getCantidad())
            .sum();
    }

    private double calcularDescuento(){
        return carrito.stream()
            .mapToDouble(SaleItem::getDescuento)
            .sum();
    }

    private double calcularTotalFinal(){
        return calcularTotalBruto() - calcularDescuento();
    }
    
    private void configurarPopupVendedores(){
        
        listViewVendedores.setItems(resultadosVendedores);
        listViewVendedores.setPrefHeight(150);
        
        popupVendedores.getContent().add(listViewVendedores);
        popupVendedores.setAutoHide(true);
        
        txtColaborador.textProperty().addListener((obs,oldText,newText)->{
            if(newText.isEmpty()){
                popupVendedores.hide();
                return;
            }
            
            List<Empleados> lista = empleadosDAO.buscarPorNombre(newText);
            
            if(lista.isEmpty()){
                popupVendedores.hide();
                return;
            }
            
            resultadosVendedores.setAll(lista);
            
            Bounds bounds = txtColaborador.localToScreen(txtColaborador.getBoundsInLocal());
            popupVendedores.show(txtColaborador, bounds.getMinX(), bounds.getMaxY());
        });
        
        listViewVendedores.setOnMouseClicked(e -> {
            Empleados emp = listViewVendedores.getSelectionModel().getSelectedItem();
            if(emp != null){
                vendedorSeleccionado = emp;
                txtColaborador.setText(emp.getNombre());
                popupVendedores.hide();
            }
        });
        
        listViewVendedores.setCellFactory(param -> new ListCell<>(){
            @Override
            protected void updateItem(Empleados emp,boolean empty){
                super.updateItem(emp, empty);
                
                if(empty || emp == null){
                    setText(null);
                }else{
                    setText(emp.getCodigo() + " - " + emp.getNombre() );
                }
            }
        });
        
    }
       
}
