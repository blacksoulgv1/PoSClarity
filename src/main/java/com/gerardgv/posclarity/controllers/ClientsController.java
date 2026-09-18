package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.Ui.PosNotification;
import com.gerardgv.posclarity.Ui.PosTable;
import com.gerardgv.posclarity.api.ClientApiClient;
import com.gerardgv.posclarity.api.SaleApiClient;
import com.gerardgv.posclarity.models.*;
import com.gerardgv.posclarity.utils.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;


public class ClientsController implements Initializable {
    
    @FXML private Label lblCompras;
    @FXML private Label lblTotalGastado;
    @FXML private Label lblUltimaVisita;
    @FXML private Label lblClienteDesde;
    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtOdEsf;
    @FXML private TextField txtOdCil;
    @FXML private TextField txtOdEje;
    @FXML private TextField txtOiEsf;
    @FXML private TextField txtOiCil;
    @FXML private TextField txtOiEje;
    @FXML private TextField txtAdd;
    @FXML private TextField txtBuscar;
    
    @FXML private Button btnGuardar;
    @FXML private Button btnLimpiar;
    @FXML private StackPane root;
    
    @FXML private TableView<Clients> tblClientes;
    @FXML private TableColumn<Clients, Integer> colId;
    @FXML private TableColumn<Clients, String> colNombre;
    @FXML private TableColumn<Clients, String> colTelefono;
    @FXML private TableColumn<Clients, String> colDireccion;
    @FXML private TableColumn<Clients, String> colGraduacion;
    @FXML private TableColumn<Clients, Void> colAcciones;
    @FXML private TableColumn<Clients,Boolean> colEstado;
    
    private Clients clienteSeleccionado = null;
    private boolean  modoEdicion = false;
     
    private final ClientApiClient clientApi = new ClientApiClient();
    private final SaleApiClient saleApiClient= new SaleApiClient();
    
    private final ObservableList<Clients> listaClientes = FXCollections.observableArrayList();
    private final BooleanProperty formularioDeshabilitado = new SimpleBooleanProperty(false);
    
    
    
     
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        configurarEventos();
        configurarBusqueda();
        configurarGuardado();
        suscribirEventos();
        cargarClientes();
        limpiarFormulario();      

    }    

    private void configurarTabla() {
        
         PosTable.apply(tblClientes);

    // La columna Graduación usa varias líneas
        tblClientes.setFixedCellSize(-1);

    // ==============================
    // DATOS DE LAS COLUMNAS
    // ==============================

        colId.setCellValueFactory(data ->
            new SimpleIntegerProperty(data.getValue().getId()).asObject());

        colNombre.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getName()));

        colTelefono.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getPhone()));

        colDireccion.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getAddress()));

        colGraduacion.setCellValueFactory(data -> {
            Clients cliente = data.getValue();

        String graduacion = String.format(
                "OD: %s / %s x %s%n"
              + "OI: %s / %s x %s%n"
              + "ADD: %s",
                nvl(cliente.getOdEsf()),
                nvl(cliente.getOdCil()),
                nvl(cliente.getOdEje()),
                nvl(cliente.getOiEsf()),
                nvl(cliente.getOiCil()),
                nvl(cliente.getOiEje()),
                nvl(cliente.getAdd())
        );

        return new SimpleStringProperty(graduacion);
    });

    colEstado.setCellValueFactory(data ->
            new SimpleBooleanProperty(data.getValue().isActive()).asObject());

    // ==============================
    // FORMATO DE LAS COLUMNAS
    // ==============================

    PosTable.integer(colId);
    PosTable.text(colNombre);
    PosTable.text(colTelefono);
    PosTable.text(colDireccion);
    PosTable.multiline(colGraduacion);

    colGraduacion.setMinWidth(190);

    // ==============================
    // ACCIONES Y ESTADO
    // ==============================

    colEstado.setCellFactory(column ->
            TableUtils.createActiveToggle(
                    Clients::getId,
                    (id, active) -> {
                        try{
                            clientApi.updateStatus(id, active);
                            cargarClientes();
                            return true;
                        } catch( Exception e ){
                            e.printStackTrace();
                            mostrarError("No Se Puede Actualizar Estado de Cliente");
                            return false;
                        }
                    }));

    colAcciones.setCellFactory(column ->
            TableUtils.createEditButton(
                    this::seleccionarClienteDesdeTabla));
    colEstado.setStyle("-fx-alignment: CENTER;");
    colAcciones.setStyle("-fx-alignment: CENTER;");
    // ==============================
    // COMPORTAMIENTO GENERAL
    // ==============================

    PosTable.placeholder(
            tblClientes,
            "No hay clientes registrados",
            "Agrega clientes desde el formulario de la izquierda",
            "fas-users"
    );

    PosTable.inactiveRows(
            tblClientes,
            Clients::isActive
    );
        
        
    }
    
    private String nvl(Object value){
        return value == null ? "" : value.toString();
    }

    
    private void saveClient(){
        
        if (!validarFormulario()) {
            return;
        }

        Clients cliente = construirCliente();

        boolean resultado = modoEdicion
            ? actualizarCliente(cliente)
            : guardarCliente(cliente);

        if (resultado) {
            mostrarExito(
                modoEdicion
                        ? "Cliente actualizado correctamente."
                        : "Cliente guardado correctamente."
        );

            cargarClientes();
            limpiarFormulario();
            tblClientes.getSelectionModel().clearSelection();

        } else {
            mostrarError(
                modoEdicion
                        ? "No se pudo actualizar el cliente."
                        : "No se pudo guardar el cliente." );
        }
    }
    
    private void cargarClientes(){
        try {
            listaClientes.setAll(clientApi.getAll());
            tblClientes.setItems(listaClientes);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("No se pudieron cargar los clientes.");
        }
    }
    
    private Clients construirCliente() {
        
        Clients cliente = new Clients();

        cliente.setName(txtNombre.getText().trim());
        cliente.setPhone(txtTelefono.getText().trim());
        cliente.setAddress(txtDireccion.getText().trim());

        cliente.setOdEsf(txtOdEsf.getText().trim());
        cliente.setOdCil(txtOdCil.getText().trim());
        cliente.setOdEje(txtOdEje.getText().trim());

        cliente.setOiEsf(txtOiEsf.getText().trim());
        cliente.setOiCil(txtOiCil.getText().trim());
        cliente.setOiEje(txtOiEje.getText().trim());

        cliente.setAdd(txtAdd.getText().trim());

        if (modoEdicion && clienteSeleccionado != null) {
            cliente.setId(clienteSeleccionado.getId());
            cliente.setActive(clienteSeleccionado.isActive());
        } else {
            cliente.setActive(true);
        }
            return cliente;      
    }
    
    private boolean guardarCliente(Clients cliente) {
        try{
            clientApi.create(cliente);
            return true;
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
    
    private boolean actualizarCliente(Clients cliente) {

        if (clienteSeleccionado == null) {
            mostrarError("No hay un cliente seleccionado para actualizar.");
            return false;
        }

        try{
            clientApi.update(cliente);
            return true;
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private void limpiarFormulario() {
        
        txtNombre.clear();
        txtTelefono.clear();
        txtDireccion.clear();
        txtOdEsf.clear();
        txtOdCil.clear();
        txtOdEje.clear();
        txtOiEsf.clear();
        txtOiCil.clear();
        txtOiEje.clear();
        txtAdd.clear();
        clienteSeleccionado = null;
        modoEdicion = false;
        deshabilitarFormulario(false);
        btnGuardar.setText("Guardar");
        tblClientes.getSelectionModel().clearSelection();
        limpiarEstadisticas();
        txtNombre.requestFocus();
    }
       
    private boolean validarFormulario(){
        
        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String direccion = txtDireccion.getText().trim();

        if (nombre.isEmpty()) {
            mostrarError("El nombre es obligatorio.");
            txtNombre.requestFocus();
            return false;
        }
        if (telefono.isEmpty()) {
            mostrarError("El teléfono es obligatorio.");
            txtTelefono.requestFocus();
            return false;
        }
        if (direccion.isEmpty()) {
            mostrarError("La dirección es obligatoria.");
            txtDireccion.requestFocus();
            return false;
        }
        if (!telefono.matches("\\d+")) {
            mostrarError("El teléfono solo debe contener números.");
            txtTelefono.requestFocus();
            return false;
        }
        return true;
    }
    
    private void mostrarError(String msg){
        PosNotification.error(root, "Atención", msg);
    }
    
    private void mostrarExito(String msg){
        PosNotification.success(root, "Listo", msg);
    }
 
    private void deshabilitarFormulario(boolean estado){
        txtNombre.setDisable(estado);
        txtTelefono.setDisable(estado);
        txtDireccion.setDisable(estado);
        txtOdEsf.setDisable(estado);
        txtOdCil.setDisable(estado);
        txtOdEje.setDisable(estado);
        txtOiEsf.setDisable(estado);
        txtOiCil.setDisable(estado);
        txtOiEje.setDisable(estado);
        txtAdd.setDisable(estado);
        formularioDeshabilitado.set(estado);
    }

    private void seleccionarClienteDesdeTabla(Clients cliente){
        if (cliente == null) {
        return;
    }

    clienteSeleccionado = cliente;
    modoEdicion = true;

    txtNombre.setText(nvl(cliente.getName()));
    txtTelefono.setText(nvl(cliente.getPhone()));
    txtDireccion.setText(nvl(cliente.getAddress()));

    txtOdEsf.setText(nvl(cliente.getOdEsf()));
    txtOdCil.setText(nvl(cliente.getOdCil()));
    txtOdEje.setText(nvl(cliente.getOdEje()));

    txtOiEsf.setText(nvl(cliente.getOiEsf()));
    txtOiCil.setText(nvl(cliente.getOiCil()));
    txtOiEje.setText(nvl(cliente.getOiEje()));

    txtAdd.setText(nvl(cliente.getAdd()));

    btnGuardar.setText("Actualizar");

    deshabilitarFormulario(!cliente.isActive());
    cargarEstadisticasCliente(cliente.getId());

    txtNombre.requestFocus();
    }
    
    private void cargarEstadisticasCliente(int idClient){
        
        try{
            ClientStats stats = saleApiClient.getClientStats(idClient);
            actualizarEstadisticas(stats);
        }catch(Exception e){
            e.printStackTrace();
            limpiarEstadisticas();
            mostrarError("No Se Puedieron Cargar Las Estadísticas Del Cliente");
        }
     
    }
    
    public void refrescarStats(int idCliente){
        cargarEstadisticasCliente(idCliente);
    }

    private void configurarEventos() {
        
        btnGuardar.setOnAction(e -> saveClient());
        btnLimpiar.setOnAction(e -> limpiarFormulario());
        
        txtBuscar.sceneProperty().addListener((obs,oldScene,scene)->{
            if(scene != null){
                scene.setOnKeyPressed(e->{
                    if(e.getCode() == KeyCode.ESCAPE){
                        limpiarFormulario();
                    }
                });
            }
        });        
    }

    private void configurarBusqueda() {
        SearchUtils.setupSearch(txtBuscar, tblClientes, listaClientes,
                c-> c.getName(),
                c-> c.getPhone(),
                c-> c.getAddress());
    }

    private void configurarGuardado() {
        btnGuardar.disableProperty().bind(
            formularioDeshabilitado
                    .or(txtNombre.textProperty().isEmpty())
                    .or(txtTelefono.textProperty().isEmpty())
                    .or(txtDireccion.textProperty().isEmpty())
                    );
    }

    private void suscribirEventos() {
        EventBus.subscribeVenta(idCliente -> {
            if(clienteSeleccionado != null && clienteSeleccionado.getId() == idCliente){
                cargarEstadisticasCliente(idCliente);
            }
        });        
    }
    
    private void actualizarEstadisticas(ClientStats stats) {
        
        if(stats == null){
            limpiarEstadisticas();
            return;
        }

        lblCompras.setText(String.valueOf(stats.getTotalPurchases()));
        lblTotalGastado.setText(String.format("$%,.2f", stats.getTotalSpent()));

        lblUltimaVisita.setText(
            formatearFecha(stats.getLastVisit())
        );

        lblClienteDesde.setText(
            formatearFecha(stats.getClientSince())
        );
    
    }

    private void limpiarEstadisticas() {
        lblCompras.setText("0");
        lblTotalGastado.setText("$0.00");
        lblUltimaVisita.setText("-");
        lblClienteDesde.setText("-");    
    }
    
    private String formatearFecha(LocalDateTime fecha){

        return fecha == null
                ? "-"
                : fecha.toLocalDate().toString();
        }
}
