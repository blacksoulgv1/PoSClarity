package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.Ui.*;
import com.gerardgv.posclarity.api.SaleApiClient;
import com.gerardgv.posclarity.api.WarrantyApiClient;
import com.gerardgv.posclarity.models.*;
import com.gerardgv.posclarity.service.TicketService;
import com.gerardgv.posclarity.utils.*;
import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class GarantiasController implements Initializable {

    @FXML private StackPane root;
    @FXML private TextField txtFolioVenta;
    @FXML private Label lblFolio;
    @FXML private Button btnBuscar;
    @FXML private Button btnGuardar;
    @FXML private Label lblCliente;
    @FXML private Label lblEstado;
    
    @FXML private ComboBox<String> cmbMotivo;

    @FXML private TextArea txtProductosVenta;
    
    @FXML private TextField txtEsfOD,txtEsfOD1;
    @FXML private TextField txtCylOD,txtCylOD1;
    @FXML private TextField txtEjeOD,txtEjeOD1;
    @FXML private TextField txtEsfOI,txtEsfOI1;
    @FXML private TextField txtCylOI,txtCylOI1;
    @FXML private TextField txtEjeOI,txtEjeOI1;
    @FXML private TextField txtAdd,txtAdd1;
    
    @FXML private TableView<Warranty> tblGarantias;
    @FXML private TableColumn<Warranty, String> colFolio;
    @FXML private TableColumn<Warranty, String> colCliente;
    @FXML private TableColumn<Warranty, String> colProducto;
    @FXML private TableColumn<Warranty, String> colEstado;
    @FXML private TableColumn<Warranty, String> colFecha;
    @FXML private TableColumn<Warranty, Void> colAcciones;
    
    @FXML private SplitPane splitGarantias;
    @FXML private ScrollPane scrollFormulario;
    
    @FXML private VBox vistaGarantias;
    @FXML private HBox zonaSuperior;

    @FXML private VBox cardInformacion;
    @FXML private VBox panelGraduaciones;

    @FXML private VBox cardGraduacionOriginal;
    @FXML private VBox cardGraduacionNueva;

    @FXML private VBox cardTablaGarantias;
    
    private boolean procesando = false;
    
    private List<SaleItem> productosGarantia = new ArrayList<>();
    private final SaleApiClient saleApiClient = new SaleApiClient();
    private final WarrantyApiClient warrantyApiClient =new WarrantyApiClient();
    
    private Sale ventaSeleccionada;
    private final ObservableList<Warranty> garantiasActivas = FXCollections.observableArrayList();

    private final Map<String,String> accionesPorMotivo = new HashMap<>();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        configurarVista();
        cargarCombos();
        inicializarAcciones();
        configurarTablaGarantias();
        configurarAcciones();
        cargarGarantiasActivas();
        prepararFormulario();       
    }
    
    public void configurarTablaGarantias(){

    colFolio.setCellValueFactory(data ->
            new SimpleStringProperty(
                    valorSeguro(data.getValue().getFolio())
            )
    );

    colCliente.setCellValueFactory(data ->
            new SimpleStringProperty(
                    valorSeguro(data.getValue().getClientName())
            )
    );

    colProducto.setCellValueFactory(data ->
            new SimpleStringProperty(
                    valorSeguro(data.getValue().getReason())
            )
    );

    colEstado.setCellValueFactory(data ->
            new SimpleStringProperty(
                    valorSeguro(data.getValue().getStatus())
            )
    );

    colFecha.setCellValueFactory(data -> {

        if(data.getValue().getRequestDate() == null){
            return new SimpleStringProperty("");
        }

        return new SimpleStringProperty(
                data.getValue()
                        .getRequestDate()
                        .format(
                                DateTimeFormatter.ofPattern(
                                        "dd/MM/yyyy HH:mm"
                                )
                        )
        );
    });

    colAcciones.setCellFactory(param ->
            TableUtils.createGarantiaAcions(
                    this::recepcionGarantia,
                    this::entregarGarantia
            )
    );

    tblGarantias.setItems(garantiasActivas);
}
    
    private void cargarGarantiasActivas(){
        
        try{
            
            int branchId = Session.getSucursal().getId();

            garantiasActivas.setAll(
                warrantyApiClient.getActiveWarranties(branchId)
            );
        } catch(IOException | InterruptedException e){
            
            e.printStackTrace();
            mostrarError("No se pudieron cargar las garantías.");
        }
    }
    
    private void cargarCombos(){
        
        cmbMotivo.getItems().addAll(
            "Defecto de fábrica",
            "Rayadura",
            "Mica maltratada",
            "Error de graduación",
            "Otro"
        );
    }

    private void buscarVenta(){

        String folio = txtFolioVenta.getText().trim();

        if(folio.isEmpty()){
            
            mostrarAdvertencia("Ingresa el folio de la venta.");
            txtFolioVenta.requestFocus();
            return;
        }
        
        try{
            
           ventaSeleccionada = saleApiClient.getByFolio(folio); 
           
        } catch(IOException | InterruptedException e){
            
            e.printStackTrace();
            limpiarDatosVenta();
            mostrarError("No se pudo consultar la venta: " + e.getMessage());
            return;
        }
        
        if (ventaSeleccionada == null) {

            limpiarDatosVenta();
            mostrarError("No se encontró una venta con el folio indicado.");
            return;
        }
        
        if (!"ENTREGADO".equalsIgnoreCase(
            ventaSeleccionada.getWorkStatus())) {

            limpiarDatosVenta();
            mostrarAdvertencia("Solo se pueden generar garantías de ventas entregadas.");
            return;
        }

        if (ventaSeleccionada.getClient() == null) {

            limpiarDatosVenta();
            mostrarError("La venta no tiene un cliente válido.");
            return;
        }

        lblCliente.setText(ventaSeleccionada.getClient().getName());
        cargarProductosVenta();
        cargarGraduacionOriginal();
        btnGuardar.setDisable(productosGarantia.isEmpty());
        mostrarExito("Venta encontrada correctamente.");
    }
    
   private void guardarGarantia(){

    if(procesando){
        return;
    }

    if(ventaSeleccionada == null){

        mostrarAdvertencia(
                "Busca una venta antes de registrar la garantía."
        );

        return;
    }

    if(productosGarantia == null
            || productosGarantia.isEmpty()){

        mostrarAdvertencia(
                "No hay productos seleccionados para la garantía."
        );

        return;
    }

    String motivo = cmbMotivo.getValue();

    if(motivo == null || motivo.isBlank()){

        mostrarAdvertencia(
                "Selecciona un motivo de garantía."
        );

        cmbMotivo.requestFocus();
        return;
    }

    String accion = obtenerAccionPorMotivo(motivo);

    if(accion == null){

        mostrarAdvertencia(
                "Selecciona la acción que se realizará."
        );

        return;
    }

    procesando = true;
    btnGuardar.setDisable(true);

    try{

        Warranty garantia =
                construirGarantia(
                        motivo,
                        accion
                );

        List<WarrantyDetail> detalles =
                construirDetallesGarantia();

        WarrantyGraduation original =
                construirGraduacionOriginal();

        WarrantyGraduation nueva =
                construirGraduacionNueva();

        Warranty garantiaGuardada = 
                warrantyApiClient.createWarranty(garantia, detalles, original, nueva);
        
        if(garantiaGuardada == null){

            mostrarError("No se pudo registrar la garantía.");

            return;
        }
        garantia = garantiaGuardada;
        
            try{
                List<SaleItem> itemsVenta =
                    ventaSeleccionada.getItems();

                TicketService ticketService =
                    new TicketService();                

                ticketService.imprimirOrdenGarantia(
                    garantia,
                    nueva,
                    ventaSeleccionada,
                    itemsVenta
            );
        } catch(Exception exImpresion){
            exImpresion.printStackTrace();
            mostrarAdvertencia(
                    "La garantía se registró correctamente, "
                    + "pero no se pudo generar la orden de laboratorio."
            );
        }

        mostrarExito(
                "Garantía registrada correctamente."
        );

        limpiarFormulario();
        cargarGarantiasActivas();

    }catch(Exception ex){

        ex.printStackTrace();

        mostrarError(
                "Ocurrió un error al registrar la garantía."
        );

    }finally{

        procesando = false;

        btnGuardar.setDisable(
                ventaSeleccionada == null
                || productosGarantia == null
                || productosGarantia.isEmpty()
        );
    }
}
    
    private List<WarrantyDetail> construirDetallesGarantia(){

    List<WarrantyDetail> detalles =
            new ArrayList<>();

    for(SaleItem item : productosGarantia){

        if(item == null){
            continue;
        }

        WarrantyDetail detalle =
                new WarrantyDetail();

        detalle.setDetailId(
                item.getDetailId()
        );

        detalle.setQuantity(
                item.getQuantity()
        );

        detalles.add(detalle);
    }

    return detalles;
}
    
    private WarrantyGraduation construirGraduacionOriginal(){

    WarrantyGraduation original =
            new WarrantyGraduation();

    original.setType("ORIGINAL");

    original.setOdSphere(valorCampo(txtEsfOD));
    original.setOdCylinder(valorCampo(txtCylOD));
    original.setOdAxis(valorCampo(txtEjeOD));
    original.setOdAdd(valorCampo(txtAdd));

    original.setOiSphere(valorCampo(txtEsfOI));
    original.setOiCylinder(valorCampo(txtCylOI));
    original.setOiAxis(valorCampo(txtEjeOI));
    original.setOiAdd(valorCampo(txtAdd));

    return original;
}
    
    private WarrantyGraduation construirGraduacionNueva(){

    WarrantyGraduation nueva =
            new WarrantyGraduation();

    nueva.setType("NUEVA");

    nueva.setOdSphere(valorCampo(txtEsfOD1));
    nueva.setOdCylinder(valorCampo(txtCylOD1));
    nueva.setOdAxis(valorCampo(txtEjeOD1));
    nueva.setOdAdd(valorCampo(txtAdd1));

    nueva.setOiSphere(valorCampo(txtEsfOI1));
    nueva.setOiCylinder(valorCampo(txtCylOI1));
    nueva.setOiAxis(valorCampo(txtEjeOI1));
    nueva.setOiAdd(valorCampo(txtAdd1));

    return nueva;
}
    
    private String valorCampo(TextField campo){
    return campo.getText() != null
            ? campo.getText().trim()
            : "";
}
    
    private void cargarProductosVenta(){

    productosGarantia = ventaSeleccionada.getItems();

    if(productosGarantia == null){
        productosGarantia = new ArrayList<>();
    }

    if(productosGarantia.isEmpty()){
        txtProductosVenta.setText(
                "La venta no tiene productos disponibles."
        );

        btnGuardar.setDisable(true);
        return;
    }

    String textoProductos =
        productosGarantia.stream()
                .filter(item ->
                        item != null
                        && item.getProduct()!= null
                )
                .map(item -> {

                    String modelo =
                            valorSeguro(
                                    item.getProduct().getModel()
                            );

                    String marca =
                            valorSeguro(
                                    item.getProduct().getBrand()
                            );

                    String descripcion =
                            marca.isBlank()
                                    ? modelo
                                    : marca + " - " + modelo;

                    return descripcion
                            + "  |  Cantidad: "
                            + item.getQuantity();
                })
                .collect(
                        Collectors.joining("\n")
                );

if(textoProductos.isBlank()){

    txtProductosVenta.setText(
            "La venta no tiene productos válidos."
    );

    productosGarantia.clear();
    btnGuardar.setDisable(true);
    return;
}

txtProductosVenta.setText(textoProductos);
}

    private void limpiarDatosVenta() {
        
        ventaSeleccionada = null;
    productosGarantia.clear();

    lblCliente.setText(
            "Sin venta seleccionada"
    );

    txtProductosVenta.clear();

    limpiarGraduacionOriginal();

    btnGuardar.setDisable(true);        
    }

    private void cargarGraduacionOriginal() {
        
        txtEsfOD.setText(valorSeguro(ventaSeleccionada.getOdEsf()));
        txtCylOD.setText(valorSeguro(ventaSeleccionada.getOdCil()));
        txtEjeOD.setText(valorSeguro(ventaSeleccionada.getOdEje()));
        txtEsfOI.setText(valorSeguro(ventaSeleccionada.getOiEsf()));
        txtCylOI.setText(valorSeguro(ventaSeleccionada.getOiCil()));
        txtEjeOI.setText(valorSeguro(ventaSeleccionada.getOiEje()));
        txtAdd.setText(valorSeguro(ventaSeleccionada.getAddLens()));
        
    }

    private void limpiarFormulario() {

        ventaSeleccionada = null;
        productosGarantia.clear();

        txtFolioVenta.clear();
        lblCliente.setText("Sin Venta Seleccionada");
        txtProductosVenta.clear();

        cmbMotivo.getSelectionModel().clearSelection();
        mostrarEstadoProceso();
        limpiarGraduacionOriginal();

        txtEsfOD1.clear();
        txtCylOD1.clear();
        txtEjeOD1.clear();

        txtEsfOI1.clear();
        txtCylOI1.clear();
        txtEjeOI1.clear();

        txtAdd1.clear();

        btnGuardar.setDisable(true);       
    }
    
    private void recepcionGarantia(Warranty garantia){
        
        if(garantia == null){
            return;
        }

        if(!"PROCESO".equalsIgnoreCase(garantia.getStatus())){
            mostrarAdvertencia(
                "Solo se pueden recibir garantías en proceso."
            );
            return;
        }

        try {

            warrantyApiClient.receiveWarranty(
                garantia.getId(),
                Session.getSucursal().getId()
            );

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            mostrarError("No se pudo actualizar la recepción.");
            return;
        }
        cargarGarantiasActivas();
        EventBus.publishVenta(garantia.getSaleId());
        mostrarExito("Producto recibido correctamente en óptica.");       
    }
    
    private void entregarGarantia(Warranty garantia){        
         
        if(garantia == null){
            return;
        }

        if(!"RECIBIDO".equalsIgnoreCase(garantia.getStatus())){
            mostrarAdvertencia(
                "La garantía debe estar recibida antes de entregarse."
            );
        return;
        }

        try {

            warrantyApiClient.deliverWarranty(
                garantia.getId(),
                Session.getSucursal().getId()
            );

        } catch (IOException | InterruptedException e) {

            e.printStackTrace();
            mostrarError("No se pudo entregar la garantía.");
            return;
        }

        cargarGarantiasActivas();

        EventBus.publishVenta(
            garantia.getSaleId()
        );

        mostrarExito(
            "Garantía entregada correctamente."
        );       
        
    }

    private void configurarVista() {
        
        PosTable.apply(tblGarantias);
        txtProductosVenta.setEditable(false);
        configurarGraduacionOriginal();        
    }

    private void configurarAcciones() {
        
        btnBuscar.setOnAction(e -> buscarVenta());
        btnGuardar.setOnAction(e -> guardarGarantia());
        txtFolioVenta.setOnAction(e -> buscarVenta());
        
    }

    private void prepararFormulario() {
        
        lblFolio.setText("Se generará al registrar");
        lblCliente.setText("Sin venta seleccionada");
        btnGuardar.setDisable(true);
    }

    private void configurarGraduacionOriginal() {
        
         List<TextField> camposOriginales = List.of(
            txtEsfOD,
            txtCylOD,
            txtEjeOD,
            txtEsfOI,
            txtCylOI,
            txtEjeOI,
            txtAdd
        );

        camposOriginales.forEach(campo -> {
            campo.setEditable(false);
            campo.setFocusTraversable(false);
        });        
    }
    
    private void limpiarGraduacionOriginal(){

        txtEsfOD.clear();
        txtCylOD.clear();
        txtEjeOD.clear();
        txtEsfOI.clear();
        txtCylOI.clear();
        txtEjeOI.clear();
        txtAdd.clear();
    }
    
    private String valorSeguro(String valor){
        return valor == null ? "" : valor;
    }
   
    private void mostrarExito(String mensaje){

        PosNotification.success(root,"Correcto",mensaje);
    }

    private void mostrarError(String mensaje){

        PosNotification.error(root, "Error", mensaje);
    }

    private void mostrarAdvertencia(String mensaje){

        PosNotification.warning( root,"Atención",mensaje);
    }
    
    private Warranty construirGarantia(
        String motivo,
        String accion){

    Warranty garantia = new Warranty();

    garantia.setSaleId(
            ventaSeleccionada.getId()
    );

    garantia.setClientId(
            ventaSeleccionada.getClient().getId()
    );

    garantia.setBranchId(
            Session.getSucursal().getId()
    );

    garantia.setReason(motivo);
    garantia.setAction(accion);
    garantia.setStatus("PROCESO");
    

    // Después lo cambiaremos por el usuario autenticado
    garantia.setUserId(1);

    return garantia;
}

    private void mostrarEstadoProceso() {
        lblEstado.setText("EN PROCESO");

        lblEstado.getStyleClass().setAll(
            "garantia-estado-badge",
            "estado-proceso"
        );
    }

    private void inicializarAcciones() {
        
    accionesPorMotivo.put("Error de graduación", "REHACER_MICA");
    accionesPorMotivo.put("Mica rayada", "REHACER_MICA");
    accionesPorMotivo.put("Defecto de fábrica", "CAMBIO_PRODUCTO");
    accionesPorMotivo.put("Armazón roto", "CAMBIO_PRODUCTO");
    accionesPorMotivo.put("Tornillo flojo", "REPARACION");
    accionesPorMotivo.put("Soldadura dañada", "REPARACION");    
    
    }
    
    private String obtenerAccionPorMotivo(String motivo){
        
        if(motivo == null || motivo.isBlank()){
            return null;            
        }
        
        return switch(motivo){

            case "Error de graduación",
             "Rayadura",
             "Mica maltratada" -> "REHACER_MICA";

            case "Defecto de fábrica",
             "Armazón roto" -> "CAMBIO_PRODUCTO";

            case "Tornillo flojo",
             "Soldadura dañada" -> "REPARACION";

            case "Otro" -> "REHACER_MICA";

            default -> null;
        };
    }
    
}
