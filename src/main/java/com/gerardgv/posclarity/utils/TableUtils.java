package com.gerardgv.posclarity.utils;

import com.gerardgv.posclarity.models.*;
import java.util.function.*;
import javafx.geometry.Pos;
import javafx.scene.control.*;
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
    public static <T> TableCell<T, Void> createVentaAcions(
        Consumer<T> onAbonar,
        Consumer<T> onRecepcion,
        Consumer<T> onEntregar,
        Consumer<T> onCancelar) {

    return new TableCell<>() {

        private final FontIcon iconAbonar =
                new FontIcon("fas-dollar-sign");

        private final FontIcon iconRecepcion =
                new FontIcon("fas-box-open");

        private final FontIcon iconEntregar =
                new FontIcon("fas-box");

        private final FontIcon iconCancelar =
                new FontIcon("fas-times-circle");

        private final Button btnAbonar = new Button();
        private final Button btnRecepcion = new Button();
        private final Button btnEntregar = new Button();
        private final Button btnCancelar = new Button();

        private final HBox box = new HBox(8);

        {
            configurarIconos();
            configurarBotones();
            configurarAcciones();

            box.setAlignment(Pos.CENTER);
            setAlignment(Pos.CENTER);
        }

        private void configurarIconos() {

            iconAbonar.setIconSize(16);
            iconRecepcion.setIconSize(16);
            iconEntregar.setIconSize(16);
            iconCancelar.setIconSize(16);

            iconAbonar.getStyleClass().add("pending-action-icon-pay");
            iconRecepcion.getStyleClass().add("pending-action-icon-receive");
            iconEntregar.getStyleClass().add("pending-action-icon-deliver");
            iconCancelar.getStyleClass().add("pending-action-icon-cancel");
        }

        private void configurarBotones() {

            configurarBoton(
                    btnAbonar,
                    iconAbonar,
                    "Registrar abono"
            );

            configurarBoton(
                    btnRecepcion,
                    iconRecepcion,
                    "Producto recibido en óptica"
            );

            configurarBoton(
                    btnEntregar,
                    iconEntregar,
                    "Entregar producto"
            );

            configurarBoton(
                    btnCancelar,
                    iconCancelar,
                    "Cancelar venta"
            );
        }

        private void configurarBoton(
                Button boton,
                FontIcon icono,
                String tooltip) {

            boton.setGraphic(icono);
            boton.getStyleClass().add("pending-action-button");
            boton.setTooltip(new Tooltip(tooltip));
            boton.setFocusTraversable(false);
        }

        private void configurarAcciones() {

            btnAbonar.setOnAction(event ->
                    ejecutarAccion(onAbonar)
            );

            btnRecepcion.setOnAction(event ->
                    ejecutarAccion(onRecepcion)
            );

            btnEntregar.setOnAction(event ->
                    ejecutarAccion(onEntregar)
            );

            btnCancelar.setOnAction(event ->
                    ejecutarAccion(onCancelar)
            );
        }

        private void ejecutarAccion(Consumer<T> accion) {

            T itemTabla = obtenerItemActual();

            if (itemTabla != null && accion != null) {
                accion.accept(itemTabla);
            }
        }

        private T obtenerItemActual() {

            int index = getIndex();

            if (index < 0
                    || getTableView() == null
                    || index >= getTableView().getItems().size()) {

                return null;
            }

            return getTableView().getItems().get(index);
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);

            box.getChildren().clear();

            if (empty) {
                setGraphic(null);
                return;
            }

            T itemTabla = obtenerItemActual();

            if (!(itemTabla instanceof Sale sale)) {
                setGraphic(null);
                return;
            }

            agregarAccionesValidas(sale);

            setGraphic(
                    box.getChildren().isEmpty()
                            ? null
                            : box
            );
        }

        private void agregarAccionesValidas(Sale sale) {

            String estadoPago = normalizar(
                    sale.getPaymentStatus()
            );

            String estadoTrabajo = normalizar(
                    sale.getWorkStatus()
            );

            boolean pagoCompleto =
                    "COMPLETA".equals(estadoPago);

            boolean entregada =
                    "ENTREGADO".equals(estadoTrabajo);

            if (!pagoCompleto && !entregada) {
                box.getChildren().add(btnAbonar);
            }

            switch (estadoTrabajo) {

                case "PROCESO" -> {

                    box.getChildren().add(btnRecepcion);
                    box.getChildren().add(btnCancelar);
                }

                case "RECIBIDO", "LISTO" -> {

                    if (pagoCompleto) {
                        box.getChildren().add(btnEntregar);
                    }

                    box.getChildren().add(btnCancelar);
                }

                case "ENTREGADO", "CANCELADO" -> {
                    // No se agregan acciones.
                }

                default -> {

                    if (!entregada) {
                        box.getChildren().add(btnCancelar);
                    }
                }
            }
        }

        private String normalizar(String valor) {

            return valor == null
                    ? ""
                    : valor.trim().toUpperCase();
        }
    };
}
    
    //Funcion Button Active
    public static <T> TableCell <T,Boolean> createActiveToggle(
            Function <T,Integer> idGetter,
            ActiveToggleHandler updateAction){
        
        return new TableCell<>(){
            
            private final ToggleButton toggle = new ToggleButton();
            private boolean updating = false;
            
             {
                 toggle.setPrefWidth(45);
                 toggle.setStyle("-fx-background-radius: 20;");
                 
                 toggle.selectedProperty().addListener((obs,oldVal,newVal) -> {
                     
                     if(updating){
                         return;
                     }
                     
                    T item = getTableView().getItems().get(getIndex());
                    
                    if(item == null){
                        return;
                    }
                    
                    int id = idGetter.apply(item);
                     
                    try {
                        boolean ok = updateAction.update(id,newVal);
                        
                        if(ok){
                            actualizarEstilo(newVal);                            
                        } else {
                            updating = true;
                            toggle.setSelected(oldVal);
                            updating = false;
                        }
                    } catch (Exception ex){
                        updating = true;
                        toggle.setSelected(oldVal);
                        updating = false;
                        
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText(null);
                        alert.setContentText(ex.getMessage());
                        alert.showAndWait();
                    }
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
                     updating = false;
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
                    Warranty g = (Warranty) itemTable;
                    btnRecepcionG.setVisible(
                    g.getStatus().equalsIgnoreCase("PROCESO"));
                    btnEntregarG.setVisible(
                    g.getStatus().equalsIgnoreCase("RECIBIDO"));
                     setGraphic(boxG);
                }                
            }
        };
    }
    
    
}
