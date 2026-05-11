
package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.database.SaleDAO;
import com.gerardgv.posclarity.models.Venta;
import com.gerardgv.posclarity.utils.EventBus;
import com.gerardgv.posclarity.utils.TableUtils;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PendingController implements Initializable {
    
    @FXML private TextField txtBuscarVenta;
    
    @FXML private TableView<Venta> tblVentas;
    @FXML private TableColumn<Venta,Integer> colId;
    @FXML private TableColumn<Venta,String> colCliente;
    @FXML private TableColumn<Venta,String> colFecha;
    @FXML private TableColumn<Venta,Double> colTotal;
    @FXML private TableColumn<Venta,Double> colPagado;
    @FXML private TableColumn<Venta,Double> colRestante;
    @FXML private TableColumn<Venta,String> colEstadoPago;
    @FXML private TableColumn<Venta,String> colEstadoTrabajo;
    @FXML private TableColumn<Venta,Void> colAcciones;
    
    private ObservableList<Venta> listaVentas = FXCollections.observableArrayList();
    private ObservableList<Venta> listaFiltrada = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarVentasPendientes();
        activarBusqueda();
        
        colAcciones.setCellFactory(param -> TableUtils.createVentaAcions(
                this::abonarVenta,
                this::recepcionarVenta,
                this::entregarVenta,
                this::cancelarVenta)
        );
        
        EventBus.subscribeVenta(id -> {
        cargarVentasPendientes();
        });
    }    

    private void configurarColumnas(){

    colId.setCellValueFactory(data ->
        new SimpleIntegerProperty(data.getValue().getId()).asObject());
    colCliente.setCellValueFactory(data ->
        new SimpleStringProperty(data.getValue().getCliente().getNombre()));
    
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyy HH:mm");
    colFecha.setCellValueFactory(data ->
        new SimpleStringProperty(
            data.getValue().getFecha().format(formatter)
        ));
    colTotal.setCellValueFactory(data ->
        new SimpleDoubleProperty(data.getValue().getTotal()).asObject());
    colPagado.setCellValueFactory(data ->
        new SimpleDoubleProperty(data.getValue().getPagado()).asObject());
    colRestante.setCellValueFactory(data ->
        new SimpleDoubleProperty(data.getValue().getRestante()).asObject());
    colEstadoPago.setCellValueFactory(data ->
        new SimpleStringProperty(data.getValue().getEstadoPago()));
    colEstadoTrabajo.setCellValueFactory(data ->
        new SimpleStringProperty(data.getValue().getEstadoTrabajo()));
    }

    private void cargarVentasPendientes() {
        SaleDAO dao = new SaleDAO();
        
        listaVentas.clear();
        listaVentas.addAll(dao.obtenerTrabajosActivos());
        System.out.println("ventas cargadas" + listaVentas.size());

        listaFiltrada.setAll(listaVentas);
        tblVentas.setItems(listaFiltrada);
    }

    private void activarBusqueda() {
        
        SaleDAO dao = new SaleDAO();
        
        txtBuscarVenta.textProperty().addListener((obs,oldText,newText) -> {
            if(newText == null || newText.isEmpty()){
                listaFiltrada.setAll(listaVentas);
            }else{
                listaFiltrada.setAll(dao.buscarVentasPendientesPorCliente(newText));
            }
        });
        tblVentas.setItems(listaVentas);
    }
        
    private void entregarVenta(Venta v){
        
        if(!v.getEstadoPago().equalsIgnoreCase("COMPLETA")){
            mostrarAlerta("No puedes Entregar un Venta No Liquidada");
            return;
        }
        
        boolean ok = new SaleDAO().entregarVenta(v.getId());
        
        if(ok){
            EventBus.publishVenta(v.getId());
            mostrarAlerta("Venta Entregada Correctamente");
        }else{
            mostrarAlerta("Error Al Entregar Venta");
        }
        
        
    }
    
    private void abonarVenta(Venta v){
    try{
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/com/gerardgv/posclarity/views/Abonos.fxml")
        );

        Parent root = loader.load();

        AbonosController controller = loader.getController();
        controller.setVenta(v);

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Abonos");
        stage.setOnHidden(e ->{
            cargarVentasPendientes();
        });
        stage.show();

    }catch(Exception e){
        e.printStackTrace();
    }
}

    private void recepcionarVenta(Venta v){
        
        boolean ok = new SaleDAO().recepcionarVenta(v.getId());
        
        if(ok){
            EventBus.publishVenta(v.getId());
            mostrarAlerta("Producto Recibido en Óptica");
        } else {
            mostrarAlerta("Error al Actualizar Recepción");
        }        
    }
    
    private void cancelarVenta(Venta v){
        
        boolean ok = new SaleDAO().cancelarVenta(v.getId());
        
        if(ok){
            EventBus.publishVenta(v.getId());
            mostrarAlerta("Venta Cancelada");
        } else {
            mostrarAlerta("Error al Cancelar Venta");
        }   
    }
    
    private void mostrarAlerta(String msg){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
