package com.gerardgv.posclarity.utils;

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
            Consumer<T> onEntregar){
        
        return new TableCell<>(){
            
            private final FontIcon iconAbonar = new FontIcon("fas-dollar-sign");
            private final FontIcon iconEntregar = new FontIcon("fas-box");            
            private final Button btnAbonar = new Button();
            private final Button btnEntregar = new Button();            
            private final HBox box = new HBox(8, btnAbonar, btnEntregar);
            
            {
                iconAbonar.setIconSize(16);
                iconAbonar.setIconColor(Color.GREEN);
                iconEntregar.setIconSize(16);
                iconEntregar.setIconColor(Color.DODGERBLUE);
                
                btnAbonar.setGraphic(iconAbonar);
                btnAbonar.setStyle("-fx-background-color: transparent;");
                btnEntregar.setGraphic(iconEntregar);
                btnEntregar.setStyle("-fx-background-color: transparent;");
                box.setAlignment(Pos.CENTER);
                btnAbonar.setOnAction(e -> {
                T item = getTableView().getItems().get(getIndex());
                onAbonar.accept(item);
            });

            btnEntregar.setOnAction(e -> {
                T item = getTableView().getItems().get(getIndex());
                onEntregar.accept(item);
            });
        }
            @Override
        protected void updateItem(Void item, boolean empty){
            super.updateItem(item, empty);

            if(empty){
                setGraphic(null);
            } else {
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
    
}
