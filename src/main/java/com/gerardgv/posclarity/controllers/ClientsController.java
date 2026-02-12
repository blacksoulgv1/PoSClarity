package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.database.ClientsDAO;
import com.gerardgv.posclarity.models.Clients;
import java.net.URL;
import java.util.ResourceBundle;
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
    
    @FXML private Button btnNuevo;
    @FXML private Button btnGuardar;
    @FXML private Button btnLimpiar;
    
    @FXML private TableView<Clients> tblClientes;
    @FXML private TableColumn<Clients, Integer> colId;
    @FXML private TableColumn<Clients, String> colNombre;
    @FXML private TableColumn<Clients, String> colTelefono;
    @FXML private TableColumn<Clients, String> colDireccion;
    @FXML private TableColumn<Clients, String> colGraduacion;
    @FXML private TableColumn<Clients, Void> colAcciones;
    @FXML private TableColumn<Clients,String> colEstado;
    
    private Clients clienteSeleccionado = null;
    private boolean  modoEdicion = false;
    
    private ClientsDAO clientsDAO = new ClientsDAO();
    private ObservableList<Clients> listaClientes = FXCollections.observableArrayList();
    
     
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarBusqueda();
        configurarTabla();
        configurarSeleccion();        
        activarguardado();
        cargarClienteDB();
        
        btnGuardar.setDisable(true);
        btnNuevo.setOnAction(e -> newClient());
        btnGuardar.setOnAction(e -> saveClient());
        btnLimpiar.setOnAction(e -> clearform());
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
        
        colEstado.setCellValueFactory(cellData -> {
            boolean activo = cellData.getValue().isActivo();
            return new SimpleStringProperty(activo ? "Activo" : "Inactivo");
        });
            //Columna Acciones
        colAcciones.setCellFactory(col -> new TableCell<Clients, Void>(){
           private final Button btnEditar = new Button("Editar");
           private final Button btnToggle = new Button();
           {
               btnEditar.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
               btnToggle.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
               
               btnEditar.setOnAction(e -> {
                   Clients cliente = getTableView().getItems().get(getIndex());
                   seleccionarClienteDesdeTabla(cliente);
               });
               
               btnToggle.setOnAction(e -> {
                   Clients cliente = getTableView().getItems().get(getIndex());
                   boolean nuevoEstado = !cliente.isActivo();
                   
                   boolean actualiado = clientsDAO.updateEstado(cliente.getId(),nuevoEstado);
                   if(actualiado){
                       cliente.setActivo(nuevoEstado);
                       tblClientes.refresh();
                   }else{
                       mostrarError("No Se Puede Actualizar El Estado De Cliente");
                   }
               });
           }
           @Override
           protected void updateItem(Void item, boolean empty){
               super.updateItem(item, empty);
               
               if(empty){
                   setGraphic(null);
               } else {
                   Clients cliente = getTableView().getItems().get(getIndex());
                   btnToggle.setText(cliente.isActivo() ? "Desactivar" : "Activar");
                   
                   HBox botones = new HBox(5, btnEditar,btnToggle);
                   setGraphic(botones);
               }
           }
        });

    tblClientes.setItems(listaClientes);
    
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
        
    }
    
    private String nvl(Object value){
        return value == null ? "" : value.toString();
    }

    private void newClient(){
        clearform();
        tblClientes.getSelectionModel().clearSelection();
        modoEdicion = false;
        clienteSeleccionado = null;
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

    private void configurarSeleccion() {
        
         tblClientes.getSelectionModel().selectedItemProperty().addListener(
                 (obs, oldSel, cliente) -> {
                                                   
        if (cliente == null) {
            
            clearform();
            clienteSeleccionado = null;
            modoEdicion = false;
            deshabilitarFormulario(false);
            return;           
        }
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
        });
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
        txtNombre.textProperty().isEmpty()
                .or(txtTelefono.textProperty().isEmpty())
                .or(txtDireccion.textProperty().isEmpty()));
    }
    
    private void configurarBusqueda(){
        FilteredList<Clients> filtro = new FilteredList<>(listaClientes, p -> true);
        
        txtBuscar.textProperty().addListener((obs, oldVal, newVal)->{
            filtro.setPredicate(Cliente -> {
                if (newVal == null || newVal.isEmpty()){
                    return true;
                }
                String lower = newVal.toLowerCase();
                return Cliente.getNombre().toLowerCase().contains(lower)
                        || Cliente.getTelefono().toLowerCase().contains(lower);
            });
        }) ;
        
        SortedList<Clients> sorted = new SortedList<>(filtro);
        sorted.comparatorProperty().bind(tblClientes.comparatorProperty());
        
        tblClientes.setItems(sorted);
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
        btnGuardar.setDisable(estado);
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
