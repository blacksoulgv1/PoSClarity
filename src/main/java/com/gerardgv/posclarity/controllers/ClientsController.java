package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.database.ClientsDAO;
import com.gerardgv.posclarity.models.Clients;
import com.gerardgv.posclarity.utils.SearchUtils;
import com.gerardgv.posclarity.utils.TableUtils;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.collections.transformation.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;


public class ClientsController implements Initializable {

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
    
    private ClientsDAO clientsDAO = new ClientsDAO();
    private ObservableList<Clients> listaClientes = FXCollections.observableArrayList();
    private BooleanProperty formularioDesabilitado = new SimpleBooleanProperty(false);
    
     
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        txtBuscar.requestFocus();
        tblClientes.setSelectionModel(null);
        
        SearchUtils.setupSearch(txtBuscar, tblClientes, listaClientes,
                c-> c.getNombre(),
                c-> c.getTelefono(),
                c-> c.getDireccion());
        
        configurarTabla();
        activarguardado();
        cargarClienteDB();
        
         colEstado.setCellFactory(column ->
                TableUtils.createActiveToggle(
                        Clients::getId,
                        clientsDAO::updateEstado));
         colAcciones.setCellFactory(param ->
            TableUtils.createEditButton(this::seleccionarClienteDesdeTabla));
        
        btnGuardar.setOnAction(e -> saveClient());
        btnLimpiar.setOnAction(e -> clearform());
        
        txtBuscar.sceneProperty().addListener((obs,oldScene,scene)->{
            if(scene != null){
                scene.setOnKeyPressed(e->{
                    if(e.getCode().toString().equals("ESCAPE")){
                        clearform();
                    }
                });
            }
        });
    }    

    private void configurarTabla() {

        colId.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject()
        );

        colNombre.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getNombre())
        );

        colTelefono.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getTelefono())
        );
        
        colDireccion.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getDireccion())
        );
        
        colGraduacion.setCellValueFactory(data -> {
            Clients c = data.getValue();

            String grad = String.format(
                "OD: %s / %s x %s\nOI: %s / %s x %s\nADD: %s",
                nvl(c.getOdEsf()), nvl(c.getOdCil()), nvl(c.getOdEje()),
                nvl(c.getOiEsf()), nvl(c.getOiCil()), nvl(c.getOiEje()),
                nvl(c.getAdd())
        );

        return new SimpleStringProperty(grad);
    });
         // Permite saltos de línea en la celda
        colGraduacion.setCellFactory(tc -> {
        TableCell<Clients, String> cell = new TableCell<>();
        Label label = new Label();
        label.setWrapText(true);
        cell.setGraphic(label);

        cell.itemProperty().addListener((obs, oldText, newText) -> {
            label.setText(newText);
        });

        return cell;
    });
        
        colEstado.setCellValueFactory(data ->
            new SimpleBooleanProperty(data.getValue().isActivo()).asObject());
   
    //Coloca en Gris los Clientes Inactivos
    tblClientes.setRowFactory(tv -> new TableRow<>(){
        @Override
        protected void updateItem(Clients cliente, boolean empty){
            super.updateItem(cliente, empty);
            
            if(cliente == null || empty){
                setStyle("");
            } else if(!cliente.isActivo()){
                setStyle("-fx-background-color: #e0e0e0;");
            } else {
                setStyle("");
            }
        }
    });
    
        tblClientes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colId.setMaxWidth(60);
        colNombre.setPrefWidth(180);
        colTelefono.setPrefWidth(120);
        colDireccion.setPrefWidth(220);
        colGraduacion.setPrefWidth(260);
        colEstado.setMaxWidth(90);
        colAcciones.setMaxWidth(120);

        colId.setStyle("-fx-alignment: CENTER;");
        colEstado.setStyle("-fx-alignment: CENTER;");
        tblClientes.setFixedCellSize(-1);

        
    }
    
    private String nvl(Object value){
        return value == null ? "" : value.toString();
    }

    
    private void saveClient(){
        
        if(!validarFormulario()){
            return;
        }
        
        Clients cliente = new Clients();
        cargarDatos(cliente);
        
        boolean resultado;
        
        if(modoEdicion && clienteSeleccionado != null){
           cliente.setId(clienteSeleccionado.getId());
           cliente.setActivo(clienteSeleccionado.isActivo());
           resultado = clientsDAO.update(cliente);
        } else {
            cliente.setActivo(true);
            resultado = clientsDAO.insert(cliente);
        }
        
        if(resultado){
            mostrarInfo(modoEdicion ? "Cliente Actualizado Correctamente" :
                    "Cliente Guardado Correctamente");
            cargarClienteDB();
            clearform();
            tblClientes.getSelectionModel().clearSelection();
            modoEdicion = false;
            clienteSeleccionado = null;
        } else{
            mostrarError("Ocurrió un Error al Guardar Cliente");
        }

    }
    
    private void cargarClienteDB(){
        listaClientes.clear();
        listaClientes.addAll(clientsDAO.findAll());
    }
    
    private void cargarDatos(Clients c) {
        c.setNombre(txtNombre.getText());
        c.setTelefono(txtTelefono.getText());
        c.setDireccion(txtDireccion.getText());

        c.setOdEsf(txtOdEsf.getText());
        c.setOdCil(txtOdCil.getText());
        c.setOdEje(txtOdEje.getText());

        c.setOiEsf(txtOiEsf.getText());
        c.setOiCil(txtOiCil.getText());
        c.setOiEje(txtOiEje.getText());

        c.setAdd(txtAdd.getText());
        
    }

    private void clearform() {
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
        txtNombre.requestFocus();
    }
       
    private boolean validarFormulario(){
        
        if (txtNombre.getText().trim().isEmpty()){
            mostrarError("El Nombre es Obligatorio");
            txtNombre.requestFocus();
            return false;
        }
        
        if (txtTelefono.getText().trim().isEmpty()){
            mostrarError("El Telefono es Obligatorio");
            txtTelefono.requestFocus();
            return false;
        }
        
        if (txtDireccion.getText().trim().isEmpty()){
            mostrarError("La Dirección es Obligatoria");
            txtDireccion.requestFocus();
            return false;
        }
        
        if(!txtTelefono.getText().matches("\\d+")){
            mostrarError("El Teléfono solo debe Contener Números");
            txtTelefono.requestFocus();
            return false;
        }
        return true;
    }
    
    private void mostrarError(String mensaje){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private void mostrarInfo(String mensaje){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private void activarguardado(){
        btnGuardar.disableProperty().bind(
            formularioDesabilitado
                    .or(txtNombre.textProperty().isEmpty())
                    .or(txtTelefono.textProperty().isEmpty())
                    .or(txtDireccion.textProperty().isEmpty())
                    );
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
        formularioDesabilitado.set(estado);
    }

    private void seleccionarClienteDesdeTabla(Clients cliente){
        clienteSeleccionado = cliente;
        modoEdicion = true;
        txtNombre.setText(cliente.getNombre());
        txtTelefono.setText(cliente.getTelefono());
        txtDireccion.setText(cliente.getDireccion());

        txtOdEsf.setText(cliente.getOdEsf());
        txtOdCil.setText(cliente.getOdCil());
        txtOdEje.setText(cliente.getOdEje());

        txtOiEsf.setText(cliente.getOiEsf());
        txtOiCil.setText(cliente.getOiCil());
        txtOiEje.setText(cliente.getOiEje());

        txtAdd.setText(cliente.getAdd());

        deshabilitarFormulario(!cliente.isActivo());
        
    }
}
