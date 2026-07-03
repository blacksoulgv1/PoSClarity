package com.gerardgv.posclarity.utils;

import com.gerardgv.posclarity.models.Garantia;
import com.gerardgv.posclarity.models.Venta;
import java.util.function.*;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

public class TableUtils {
    
    // Funcion Button Edit
    public static <T> TableCell <T,Void> createEditButton(
            Consumer<T> action){
        
        return new TableCell<>(){
             private final FontIcon iconEdit = new FontIcon("fas-edit");
             private final Button btnEdit = new Button();
             private final HBox box = new HBox(btnEdit);
             {
                 iconEdit.setIconSize(18);
                 iconEdit.setIconColor(Color.DODGERBLUE);
                 
                 btnEdit.setGraphic(iconEdit);
                 btnEdit.setStyle("-fx-background-color: transparent;");
                 
                 box.setAlignment(Pos.CENTER);
                 
                 btnEdit.setOnAction(e -> {
                     T item = getTableView().getItems().get(getIndex());
                     action.accept(item);
                 });
             }
             @Override
             protected void updateItem(Void item,boolean empty){
                 super.updateItem(item, empty);
                 
                 if(empty){
                     setGraphic(null);
                 } else {
                     setGraphic(box);
                 }
             }
        };
    }
    
    //funcion de Abonos
    public static <T> TableCell <T,Void>createVentaAcions(
            Consumer<T> onAbonar,
            Consumer<T> onRecepcion,
            Consumer<T> onEntregar,
            Consumer<T> onCancelar){
        
        return new TableCell<>(){
            
            private final FontIcon iconAbonar = new FontIcon("fas-dollar-sign");
            private final FontIcon iconRecepcion = new FontIcon("fas-box-open");
            private final FontIcon iconEntregar = new FontIcon("fas-box");
            private final FontIcon iconCancelar = new FontIcon("fas-times-circle"); 
            
            private final Button btnAbonar = new Button();
            private final Button btnRecepcion = new Button();
            private final Button btnEntregar = new Button();
            private final Button btnCancelar = new Button();              
            private final HBox box = new HBox(8, btnAbonar,btnRecepcion, btnEntregar,btnCancelar);
            
            {
                iconAbonar.setIconSize(16);
                iconAbonar.setIconColor(Color.GREEN);
                iconRecepcion.setIconSize(16);
                iconRecepcion.setIconColor(Color.ORANGE);
                iconEntregar.setIconSize(16);
                iconEntregar.setIconColor(Color.DODGERBLUE);
                iconCancelar.setIconSize(16);
                iconCancelar.setIconColor(Color.RED);
                
                btnAbonar.setGraphic(iconAbonar);
                btnAbonar.setStyle("-fx-background-color: transparent;");
                btnRecepcion.setGraphic(iconRecepcion);
                btnRecepcion.setStyle("-fx-background-color: transparent;");
                btnEntregar.setGraphic(iconEntregar);
                btnEntregar.setStyle("-fx-background-color: transparent;");
                btnCancelar.setGraphic(iconCancelar);
                btnCancelar.setStyle("-fx-background-color: transparent;");
                                
                btnAbonar.setTooltip(new javafx.scene.control.Tooltip("Registrar Abono"));
                btnRecepcion.setTooltip(
                    new javafx.scene.control.Tooltip("Producto recibido en óptica"));
                btnEntregar.setTooltip(
                    new javafx.scene.control.Tooltip("Entregar producto"));
                btnCancelar.setTooltip(
                    new javafx.scene.control.Tooltip("Cancelar venta"));
                
                btnAbonar.setOnAction(e -> {
                T item = getTableView().getItems().get(getIndex());
                onAbonar.accept(item);
                });
                btnRecepcion.setOnAction(e -> {
                T item = getTableView().getItems().get(getIndex());
                onRecepcion.accept(item);
                });
                btnEntregar.setOnAction(e -> {
                T item = getTableView().getItems().get(getIndex());
                onEntregar.accept(item);
                });
                btnCancelar.setOnAction(e -> {
                T item = getTableView().getItems().get(getIndex());
                onCancelar.accept(item);
                });
                box.setAlignment(Pos.CENTER);
        }
            @Override
        protected void updateItem(Void item, boolean empty){
            super.updateItem(item, empty);

            if(empty){
                setGraphic(null);
                return;
            } else {
                
                T itemTable = getTableView().getItems().get(getIndex());
                Venta v = (Venta) itemTable;
                
                btnAbonar.setVisible(
                    !v.getEstadoPago().equalsIgnoreCase("COMPLETA"));
                btnRecepcion.setVisible(
                    v.getEstadoTrabajo().equalsIgnoreCase("PROCESO"));
                btnEntregar.setVisible(
                    v.getEstadoTrabajo().equalsIgnoreCase("RECIBIDO"));
                btnCancelar.setVisible(
                    !v.getEstadoTrabajo().equalsIgnoreCase("ENTREGADO"));
                
                setGraphic(box);
            }
        }
    };
}
    
    //Funcion Button Active
    public static <T> TableCell <T,Boolean> createActiveToggle(
            Function <T,Integer> idGetter,
            BiConsumer<Integer,Boolean> updateAction){
        
        return new TableCell<>(){
            
            private final ToggleButton toggle = new ToggleButton();          
             {
                 toggle.setPrefWidth(45);
                 toggle.setStyle("-fx-background-radius: 20;");
                 toggle.selectedProperty().addListener((obs,oldVal,newVal)->{
                     T item = getTableView().getItems().get(getIndex());
                     int id = idGetter.apply(item);
                     updateAction.accept(id,newVal);
                     actualizarEstilo(newVal);
                 });                 
             }
             
             private void actualizarEstilo(boolean activo){
                 if(activo){
                     toggle.setText("ON");
                     toggle.setStyle(
                        "-fx-background-color:#4CAF50;"+
                        "-fx-text-fill:white;"+
                        "-fx-background-radius:20;");
                 } else{
                     toggle.setText("OFF");
                     toggle.setStyle(
                        "-fx-background-color:#b0b0b0;"+
                        "-fx-text-fill:white;"+
                        "-fx-background-radius:20;");
                 }
             }
             
             @Override
             protected void updateItem(Boolean activo,boolean empty){
                 super.updateItem(activo, empty);
                 
                 if(empty || activo == null){
                     setGraphic(null);
                 } else {
                     toggle.setSelected(activo);
                     actualizarEstilo(activo);
                     setGraphic(toggle);
                 }
             }
        };
    }
    
    //GARANTIAS 
    public static <T> TableCell <T,Void>createGarantiaAcions(
            Consumer<T> onRecepcionG,
            Consumer<T> onEntregarG){
        
        return new TableCell<>(){
            
            private final FontIcon iconRecepcionG = new FontIcon("fas-box-open");
            private final FontIcon iconEntregarG = new FontIcon("fas-box");
            private final Button btnRecepcionG = new Button();
            private final Button btnEntregarG = new Button();
            
            private final HBox boxG = new HBox(5, btnRecepcionG,btnEntregarG);
            {
                iconRecepcionG.setIconSize(16);
                iconRecepcionG.setIconColor(Color.ORANGE);
                iconEntregarG.setIconSize(16);
                iconEntregarG.setIconColor(Color.DODGERBLUE);
                btnRecepcionG.setGraphic(iconRecepcionG);
                btnRecepcionG.setStyle("-fx-background-color: transparent;");
                btnEntregarG.setGraphic(iconEntregarG);
                btnEntregarG.setStyle("-fx-background-color: transparent;");
                
                btnRecepcionG.setTooltip(
                    new javafx.scene.control.Tooltip("Producto recibido en óptica"));
                btnEntregarG.setTooltip(
                    new javafx.scene.control.Tooltip("Entregar producto"));
                btnRecepcionG.setOnAction(e -> {
                T item = getTableView().getItems().get(getIndex());
                onRecepcionG.accept(item);
                });
                btnEntregarG.setOnAction(e -> {
                T item = getTableView().getItems().get(getIndex());
                onEntregarG.accept(item);
                });
                boxG.setAlignment(Pos.CENTER);
            }
            @Override
            protected void updateItem(Void item, boolean empty){
                super.updateItem(item, empty);
                
                if(empty){
                    setGraphic(null);
                    return;
                } else {
                    T itemTable = getTableView().getItems().get(getIndex());
                    Garantia g = (Garantia) itemTable;
                    btnRecepcionG.setVisible(
                    g.getEstado().equalsIgnoreCase("PROCESO"));
                    btnEntregarG.setVisible(
                    g.getEstado().equalsIgnoreCase("RECIBIDO"));
                     setGraphic(boxG);
                }                
            }
        };
    }
    
    
}
