package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.database.*;
import com.gerardgv.posclarity.models.*;
import com.gerardgv.posclarity.utils.EventBus;
import com.gerardgv.posclarity.utils.Session;
import com.gerardgv.posclarity.utils.TableUtils;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

public class GarantiasController implements Initializable {

    @FXML private TextField txtFolioVenta;
    @FXML private Label lblFolio;
    @FXML private Button btnBuscar;
    @FXML private Button btnGuardar;
    @FXML private Label lblCliente;
    @FXML private ComboBox<String> cmbMotivo;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private TextArea txtObservaciones;
    @FXML private TextArea txtProductosVenta;
    @FXML private TextField txtEsfOD,txtEsfOD1;
    @FXML private TextField txtCylOD,txtCylOD1;
    @FXML private TextField txtEjeOD,txtEjeOD1;
    @FXML private TextField txtEsfOI,txtEsfOI1;
    @FXML private TextField txtCylOI,txtCylOI1;
    @FXML private TextField txtEjeOI,txtEjeOI1;
    @FXML private TextField txtAdd,txtAdd1;
    @FXML private TableView<Garantia> tblGarantias;
    @FXML private TableColumn<Garantia, String> colFolio;
    @FXML private TableColumn<Garantia, String> colCliente;
    @FXML private TableColumn<Garantia, String> colProducto;
    @FXML private TableColumn<Garantia, String> colEstado;
    @FXML private TableColumn<Garantia, String> colFecha;
    @FXML private TableColumn<Garantia, Void> colAcciones;
    
    
    private List<SaleItem> productosGarantia = new ArrayList<>();
    private GarantiasDAO garantiaDAO = new GarantiasDAO();
    private SaleDAO saleDAO = new SaleDAO();
    private Venta ventaSeleccionada;
    public ObservableList<Garantia> garantiasActivas = FXCollections.observableArrayList();

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        lblFolio.setText(garantiaDAO.generarFolio());
        cargarCombos();
        configurarTablaGarantias();
        cargarGarantiasActivas();
        btnBuscar.setOnAction(e -> buscarVenta());
        btnGuardar.setOnAction(e -> guardarGarantia() );
    }
    
    public void configurarTablaGarantias(){
        
        colFolio.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFolio()));
        colCliente.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCliente()));
        colProducto.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMotivo()));
        colEstado.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEstado()));
        colFecha.setCellValueFactory(data -> new SimpleStringProperty( data.getValue().getFechaSolicitud()
                            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            ));
        tblGarantias.setItems(garantiasActivas); 
        colAcciones.setCellFactory(param -> TableUtils.createGarantiaAcions(
                this::recepcionGarantia,
                this::entregarGarantia));
    }
    
    private void cargarGarantiasActivas(){
        garantiasActivas.setAll(garantiaDAO.obtenergarantiasActivas());
    }
    
    private void cargarCombos(){
        
        cmbMotivo.getItems().addAll(
            "Defecto de fábrica",
            "Rayadura",
            "Mica maltratada",
            "Error de graduación",
            "Otro"
        );
         
        cmbEstado.getItems().addAll(
            "PROCESO",
            "RECIBIDO",
            "ENTREGADO"
        );
        
        cmbEstado.setValue("PROCESO");
    }

    private void buscarVenta(){   
        
        String folio = txtFolioVenta.getText().trim();

        if(folio.isEmpty()){
            alerta("Ingresa folio");
            return;
        }
        
        ventaSeleccionada = saleDAO.buscarPorFolio(folio);
        
        if(ventaSeleccionada == null){
            limpiarDatosventa();
            alerta("Venta No Encontrada");
            return;
        }
        
        if(!"ENTREGADO".equalsIgnoreCase(ventaSeleccionada.getEstadoTrabajo())){
            limpiarDatosventa();
            alerta("Solo se puede generar Garantías de ventas Entregadas");
            return;
        }
        
        lblCliente.setText(ventaSeleccionada.getCliente().getNombre());
        cargarProductosVenta();
        cargarGraduacionOriginal();
    }
    
    private void guardarGarantia(){
        
        if(ventaSeleccionada == null){
            alerta("Busca una venta primero");
            return;
        }
        
        if(cmbMotivo.getValue() == null){
        alerta("Selecciona un motivo");
        return;
        }

        if(cmbEstado.getValue() == null){
        alerta("Selecciona un estado");
        return;
        }

        Garantia garantia = new Garantia();
        
        garantia.setIdventas(ventaSeleccionada.getId());
        garantia.setIdcliente(ventaSeleccionada.getCliente().getId());
        garantia.setIdsucursal(Session.getSucursal().getId());
        garantia.setMotivo(cmbMotivo.getValue());
        garantia.setEstado(cmbEstado.getValue());
        garantia.setAccion("REHACER_MICA");
        garantia.setObservaciones(txtObservaciones.getText());
        garantia.setUsuario(1);
        
        List<GarantiaDetalle> detalles = new ArrayList<>();
        
        for(SaleItem item : productosGarantia){
            GarantiaDetalle detalle = new GarantiaDetalle();
            detalle.setIdDetalle(item.getIdDetalle());
            detalle.setCantidad(item.getCantidad());
            detalles.add(detalle);
        }
        
        GarantiaGraduacion original = new GarantiaGraduacion();
        
        original.setTipo("ORIGINAL");
        original.setOdEsfera(txtEsfOD.getText());
        original.setOdCilindro(txtCylOD.getText());
        original.setOdEje(txtEjeOD.getText());
        original.setOdAdd(txtAdd.getText());
        original.setOiEsfera(txtEsfOI.getText());
        original.setOiCilindro(txtCylOI.getText());
        original.setOiEje(txtEjeOI.getText());
        original.setOiAdd(txtAdd.getText());
        
        GarantiaGraduacion nueva = new GarantiaGraduacion();
        nueva.setTipo("NUEVA");
        nueva.setOdEsfera(txtEsfOD1.getText());
        nueva.setOdCilindro(txtCylOD1.getText());
        nueva.setOdEje(txtEjeOD1.getText());
        nueva.setOdAdd(txtAdd1.getText());
        nueva.setOiEsfera(txtEsfOI1.getText());
        nueva.setOiCilindro(txtCylOI1.getText());
        nueva.setOiEje(txtEjeOI1.getText());
        nueva.setOiAdd(txtAdd1.getText());
        
        boolean guardado = garantiaDAO.crearGarantia(
            garantia, detalles, original,nueva);
        
        if(guardado){
            alerta("Garantía Registrada Correctamente");
            limpiarFormulario();
            lblFolio.setText(garantiaDAO.generarFolio());
        }else{
            alerta("Error al Registrar Garantía");
        }        
    }
    
    private void alerta(String m){

        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText(m);
        a.showAndWait();
    }

    private void cargarProductosVenta() {
        
        productosGarantia = saleDAO.obtenerDetalleVenta(ventaSeleccionada.getId());
        
        String textoProductos = productosGarantia.stream()
                .map(item -> item.getProducto().getModelo()+" | Cantidad: "+ item.getCantidad())
                .collect(Collectors.joining("\n"));
        
        txtProductosVenta.setText(textoProductos);        
    }

    private void limpiarDatosventa() {
        
        ventaSeleccionada = null;
        lblCliente.setText("");
        txtProductosVenta.clear();        
    }

    private void cargarGraduacionOriginal() {
        
        txtEsfOD.setText(ventaSeleccionada.getOdEsf());
        txtCylOD.setText(ventaSeleccionada.getOdCil());
        txtEjeOD.setText(ventaSeleccionada.getOdEje());
        txtEsfOI.setText(ventaSeleccionada.getOiEsf());
        txtCylOI.setText(ventaSeleccionada.getOiCil());
        txtEjeOI.setText(ventaSeleccionada.getOiEje());
        txtAdd.setText(ventaSeleccionada.getAdd());
        
    }

    private void limpiarFormulario() {

        ventaSeleccionada = null;
        productosGarantia.clear();
        txtFolioVenta.clear();
        lblCliente.setText("");
        txtProductosVenta.clear();
        cmbMotivo.setValue(null);
        cmbEstado.setValue("PROCESO");
        txtObservaciones.clear();
        txtEsfOD.clear();
        txtCylOD.clear();
        txtEjeOD.clear();
        txtEsfOI.clear();
        txtCylOI.clear();
        txtEjeOI.clear();
        txtAdd.clear();
        txtEsfOD1.clear();
        txtCylOD1.clear();
        txtEjeOD1.clear();
        txtEsfOI1.clear();
        txtCylOI1.clear();
        txtEjeOI1.clear();
        txtAdd1.clear();        
    }
    
    private void recepcionGarantia(Garantia g){
        
        boolean ok = garantiaDAO.recepcionGarantia(g.getIdgarantias());
        
        if(ok){
            cargarGarantiasActivas();
            alerta("Producto Recibido en Óptica");
        } else {
            alerta("Error al Actualizar Recepción");
        }        
    }
    
    private void entregarGarantia(Garantia g){        
         
        boolean ok = garantiaDAO.entregarGarantia(g.getIdgarantias());
        
        if(ok){
            cargarGarantiasActivas();
            alerta("Venta Entregada Correctamente");
        }else{
            alerta("Error Al Entregar Venta");
        }
        
        
    }
 
}
