package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.database.DescuentoDAO;
import com.gerardgv.posclarity.models.Descuento;
import com.gerardgv.posclarity.utils.SearchUtils;
import com.gerardgv.posclarity.utils.TableUtils;
import org.kordamp.ikonli.javafx.FontIcon;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.paint.Color;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;



public class DescuentoController implements Initializable {
    
    @FXML private TextField txtNombre;
    @FXML private TextField txtDescuento;
    @FXML private TextField txtValor;
    @FXML private TextField txtCupon;
    @FXML private TextField txtPrioridad;
    @FXML private TextField txtBuscar;
    @FXML private TextField txtModeloProducto;
    @FXML private TextField txtDioptriaMax;
    @FXML private CheckBox chkRequiereArmazon;
    
    @FXML private ComboBox<String> cbCategoria;
    @FXML private ComboBox<String> cbTipoAplicacion;
    @FXML private ComboBox<String> cbTipoValor;
    
    @FXML private DatePicker dpInicio;
    @FXML private DatePicker dpFin;   
    
    @FXML private Button btnGuardar;
    
    @FXML private TableView<Descuento> tabla;
    @FXML private TableColumn<Descuento,Integer> colId;
    @FXML private TableColumn<Descuento, String> colNombre;
    @FXML private TableColumn<Descuento, String> colTAplicacion;
    @FXML private TableColumn<Descuento, String> colTValor;
    @FXML private TableColumn<Descuento, Double> colValor; 
    @FXML private TableColumn<Descuento, String> colcupon;
    @FXML private TableColumn<Descuento, String> colCategoria;
    @FXML private TableColumn<Descuento, String> colFi;
    @FXML private TableColumn<Descuento, String> colFf;    
    @FXML private TableColumn<Descuento, Integer> colPrioridad;
    @FXML private TableColumn<Descuento, Boolean> colActivo;
    @FXML private TableColumn<Descuento, Void> colAccion;
    
    private final DescuentoDAO dao = new DescuentoDAO();
    private Descuento descuentoSeleccionado;
    private ObservableList<Descuento> listaDescuentos = FXCollections.observableArrayList();

    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        configurarColumnas();
        cargarTabla();
        cargarCombos();
        formaterCombos();
        
        SearchUtils.setupSearch(txtBuscar, tabla, listaDescuentos,
                d -> d.getNombre(),
                d ->d.getCategoria());
        
        colActivo.setCellFactory(column ->
                TableUtils.createActiveToggle(
                        Descuento::getId,
                        dao::actualizarActivo));
        
        colAccion.setCellFactory(param ->
            TableUtils.createEditButton(this::editarDescuento));
        
        txtBuscar.sceneProperty().addListener((obs,oldScene,scene)->{
            if(scene != null){
                scene.setOnKeyPressed(e->{
                    if(e.getCode().toString().equals("ESCAPE")){
                        limpiar();
                    }
                });
            }
        });
        tabla.setSelectionModel(null);
    }
    
    private void cargarCombos(){
        cbTipoAplicacion.setItems(FXCollections.observableArrayList(
                "GENERAL",
                "CATEGORIA",
                "CUPON"
        ));

        cbTipoValor.setItems(FXCollections.observableArrayList(
                "PORCENTAJE",
                "FIJO"
        ));

        cbCategoria.setItems(FXCollections.observableArrayList(
                "armazon",
                "lente_contacto",
                "producto",
                "servicio",
                "mica",
                "tratamiento"
        )); 
    }
    
    
    private void cargarTabla() {
        
        listaDescuentos.clear();
        listaDescuentos.addAll(dao.getAll());
        tabla.setItems(listaDescuentos);

    }
    
    private void configurarColumnas(){
        
        colId.setCellValueFactory(data ->
            new SimpleIntegerProperty(data.getValue().getId()).asObject());

        colNombre.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getNombre()));

        colTAplicacion.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getTipoAplicacion()));

        colTValor.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getTipoValor()));

        colValor.setCellValueFactory(data ->
            new SimpleDoubleProperty(data.getValue().getValor()).asObject());

        colcupon.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getCodigoCupon()));

        colCategoria.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getCategoria()));

        colFi.setCellValueFactory(data ->
            new SimpleObjectProperty<>(data.getValue().getFechaInicio().toString()));

        colFf.setCellValueFactory(data ->
            new SimpleObjectProperty<>(data.getValue().getFechaFin().toString()));

        colPrioridad.setCellValueFactory(data ->
            new SimpleIntegerProperty(data.getValue().getPrioridad()).asObject());

        colActivo.setCellValueFactory(data ->
            new SimpleBooleanProperty(data.getValue().isActivo()).asObject());

        
    }
    
    private void editarDescuento(Descuento d){
        
        descuentoSeleccionado = d;
        
        txtNombre.setText(d.getNombre());
        cbTipoAplicacion.setValue(d.getTipoAplicacion());
        cbTipoValor.setValue(d.getTipoValor());
        txtValor.setText(String.valueOf(d.getValor()));
        txtCupon.setText(d.getCodigoCupon());
        cbCategoria.setValue(d.getCategoria());
        txtModeloProducto.setText(d.getModeloProducto());
        chkRequiereArmazon.setSelected(d.isRequiereArmazon());

        if(d.getDiotriaMax()!= null){
            txtDioptriaMax.setText(String.valueOf(d.getDiotriaMax()));
        }
        
        dpInicio.setValue(d.getFechaInicio());
        dpFin.setValue(d.getFechaFin());
        txtPrioridad.setText(String.valueOf(d.getPrioridad()));
        btnGuardar.setText("Actualizar");
    }
    
    @FXML
    private void guardar(){
        try{
            Descuento d = new Descuento();
             
            d.setNombre(txtNombre.getText());
            d.setTipoAplicacion(cbTipoAplicacion.getValue());
            d.setTipoValor(cbTipoValor.getValue());
            d.setValor(Double.parseDouble(txtValor.getText()));
            d.setCodigoCupon(txtCupon.getText());
            d.setCategoria(cbCategoria.getValue());
            d.setModeloProducto( txtModeloProducto.getText());
            d.setRequiereArmazon(chkRequiereArmazon.isSelected());

            if(!txtDioptriaMax.getText().isBlank()){
                 d.setDiotriaMax(
                    Double.parseDouble(
                    txtDioptriaMax.getText()));
            }
             d.setFechaInicio(dpInicio.getValue());
             d.setFechaFin(dpFin.getValue());
             d.setPrioridad(Integer.parseInt(txtPrioridad.getText()));
             d.setActivo(true);
             
             boolean ok;
             
             if(descuentoSeleccionado == null){
                 ok = dao.insert(d);
             } else {
                 d.setId(descuentoSeleccionado.getId());
                 ok = dao.update(d);
             }
             if(ok){
                 cargarTabla();
                 limpiar();
                 mostrarMensaje("Descuento Guardado Correctamente");
             }else {
                 mostrarMensaje("Error al Guardar");
             }
        } catch(Exception e){
            mostrarMensaje("Datos Inválidos");
        }
    }

    private void limpiar(){
        txtNombre.clear();
        txtValor.clear();
        txtCupon.clear();
        txtPrioridad.clear();
        txtModeloProducto.clear();
        txtDioptriaMax.clear();
        chkRequiereArmazon.setSelected(false);
        cbTipoAplicacion.setValue(null);
        cbTipoValor.setValue(null);
        cbCategoria.setValue(null);        
        dpInicio.setValue(null);
        dpFin.setValue(null);
        descuentoSeleccionado = null;
        btnGuardar.setText("Guardar");
    }
    
    private void mostrarMensaje(String mensaje){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private void formaterCombos(){
        cbCategoria.setCellFactory(lv -> new ListCell<>(){
            @Override
        protected void updateItem(String item,boolean empty){
                super.updateItem(item, empty);
                setText(empty || item == null ? null: formatearTexto(item));
            }
        });
        
        cbCategoria.setButtonCell(new ListCell<>(){
            @Override
        protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formatearTexto(item));
            }
        });
    }
    
    private String formatearTexto(String valor){
        return switch(valor){            
            case"armazon" -> "Armazón";
            case "lente_contacto" -> "Lente de contacto";
            case "producto" -> "Producto";
            case "servicio" -> "Servicio";
            case "mica" -> "Mica";
            case "tratamiento" -> "Tratamiento";
            default -> valor;                
        };
    }    
   
}
