package com.gerardgv.posclarity.Ui;

import java.text.NumberFormat;
import java.util.Locale;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import java.util.function.Predicate;
import javafx.css.PseudoClass;

public class PosTable {
    
    private static final NumberFormat MXN = 
            NumberFormat.getCurrencyInstance(new Locale("es","MX"));
    
    private static final PseudoClass INACTIVE = PseudoClass.getPseudoClass("inactive");
    
    public PosTable(){
        
    }
    
    // =========================================================
    // CONFIGURACIÓN GENERAL
    // =========================================================
    
    public static <T> void apply(TableView<T> table){
        if(table == null) {
            return;
        }
        
        if(!table.getStyleClass().contains("pos-table")){
            table.getStyleClass().add("pos-table");
        }
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setFixedCellSize(54);
    }
    
    public static <T> void placeholder(
            TableView<T> table,
            String titulo,
            String subtitulo,
            String iconLiteral) {
        
        if(table == null){
            return;
        }

        FontIcon icon = new FontIcon(iconLiteral);
        icon.setIconSize(42);
        icon.getStyleClass().add("clarity-placeholder-icon");

        Label lblTitulo = new Label(titulo);
        lblTitulo.getStyleClass().add("clarity-placeholder-title");

        Label lblSubtitulo = new Label(subtitulo);
        lblSubtitulo.getStyleClass().add("clarity-placeholder-subtitle");

        VBox box = new VBox(8, icon, lblTitulo, lblSubtitulo);
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("clarity-placeholder");

        table.setPlaceholder(box);
    }
    
    public static <T> void multiline(TableColumn<T, String> column) {

        column.setCellFactory(col -> new TableCell<>() {

            private final Label label = new Label();

            {
                label.setWrapText(true);
                label.setMaxWidth(Double.MAX_VALUE);
                label.setAlignment(Pos.CENTER_LEFT);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }

                label.setText(item);
                setGraphic(label);
            }
        });
    }
    
    // =========================================================
    // FILAS
    // =========================================================
    
    public static <T> void animationRows(TableView<T> table){
        configurarFilas(table, null, true);
    }
    
    public static <T> void inactiveRows(
        TableView<T> table,
        Predicate<T> isActive) {

        configurarFilas(table, isActive, true);
    }
    
    public static <T> void inactiveRows(
        TableView<T> table,
        Predicate<T> isActive,
        boolean animate){
        
        configurarFilas(table, isActive, animate);
    }
    
    private static <T> void configurarFilas(
        TableView<T> table,
        Predicate<T> isActive,
        boolean animate) {

        if (table == null) {
            return;
        }

        table.setRowFactory(tv -> new TableRow<>() {

            private T ultimoItemAnimado;

            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);

                boolean inactivo = !empty
                    && item != null
                    && isActive != null
                    && !isActive.test(item);

                pseudoClassStateChanged(INACTIVE, inactivo);

                if (empty || item == null) {
                    ultimoItemAnimado = null;
                    return;
                }

                if (animate && item != ultimoItemAnimado) {
                    ultimoItemAnimado = item;
                    PosAnimations.fadeSlideIn(this);
                }
            }
        });
    }
    
    // =========================================================
    // CELDAS DE TEXTO
    // =========================================================
    
    public static <T> TableCell<T, String> textCell() {

        return new TableCell<>() {
            @Override
            protected void updateItem(String value, boolean empty) {
                super.updateItem(value, empty);

                if (empty || value == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                setText(value);
                setAlignment(Pos.CENTER_LEFT);
            }
        };
    }
    
    public static <T> TableCell<T, String> centeredTextCell() {

        return new TableCell<>() {
            @Override
            protected void updateItem(String value, boolean empty) {
                super.updateItem(value, empty);

                if (empty || value == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                setText(value);
                setAlignment(Pos.CENTER);
            }
        };
    }
    
    public static <T> TableCell<T, String> enumCell() {

        return new TableCell<>() {
            @Override
            protected void updateItem(String value, boolean empty) {
                super.updateItem(value, empty);

                if (empty || value == null || value.isBlank()) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                setText(formatEnum(value));
                setAlignment(Pos.CENTER);
            }
        };
    }
    
    // =========================================================
    // CELDAS NUMÉRICAS
    // =========================================================
    
    public static <T> TableCell<T, Integer> integerCell() {

        return new TableCell<>() {
            @Override
            protected void updateItem(Integer value, boolean empty) {
                super.updateItem(value, empty);

                if (empty || value == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                setText(String.valueOf(value));
                setAlignment(Pos.CENTER);
            }
        };
    }

    public static <T> TableCell<T, Double> doubleCell(int decimals) {

        return new TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);

                if (empty || value == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                setText(String.format(
                        Locale.US,
                        "%." + Math.max(decimals, 0) + "f",
                        value
                ));

                setAlignment(Pos.CENTER_RIGHT);
            }
        };
    }
    
    public static <T> TableCell <T,Double> moneyCell(boolean bold){
        
        return new TableCell<>(){
            
            @Override
            protected void updateItem(Double value, boolean empty){
                super.updateItem(value,empty);
                
                if(empty|| value == null){
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                    return;
                }
                
                setText(MXN.format(value));
                setAlignment(Pos.CENTER_RIGHT);
                setStyle(bold ? "-fx-font-weight: bold;" : "");                
            }
        };        
    }
    
    public static <T> TableCell <T,Double> discountCell() {
        
        return new TableCell<>() {
            
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                
                setAlignment(Pos.CENTER_RIGHT);

                if (empty || value == null || value <= 0) {
                    setText("-");
                    setGraphic(null);
                    setStyle("-fx-text-fill: #94a3b8;");                    
                    return;
                }

                setText("-" + MXN.format(value));
                setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");                
            }
        };
    }

    public static <T> TableCell<T, Double> percentCell() {

        return new TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);

                if (empty || value == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                setText(String.format(Locale.US, "%.2f%%", value));
                setAlignment(Pos.CENTER_RIGHT);
            }
        };
    }
    
    // =========================================================
    // BADGES
    // =========================================================
    
    public static <T> TableCell <T,String> categoryBadgeCell(){
        
        return new TableCell<>(){
            
            private final Label badge = new Label();
            
            {
                badge.getStyleClass().add("badge");
                setAlignment(Pos.CENTER);
            }
            
            @Override
            protected void updateItem(String categoria, boolean empty){
                super.updateItem(categoria, empty);
                
                if(empty || categoria == null || categoria.isBlank()){
                    setText(null);
                    setGraphic(null);
                    return;
                }
                
                badge.setText(formatCategoria(categoria));
                badge.getStyleClass().removeAll(
                        "badge-armazon",
                        "badge-mica",
                        "badge-tratamiento",
                        "badge-servicio",
                        "badge-producto",
                        "badge-lente"
                );

                badge.getStyleClass().add(styleCategoria(categoria));
                setText(null);
                setGraphic(badge);                              
            }
        };        
    }
    
    // =========================================================
    // FORMATEADORES INTERNOS
    // =========================================================
    
    private static String formatEnum(String value) {

        String texto = value
                .trim()
                .toLowerCase()
                .replace("_", " ");

        if (texto.isBlank()) {
            return "";
        }

        return Character.toUpperCase(texto.charAt(0))
                + texto.substring(1);
    }
    
    private static String formatCategoria(String categoria){
        return switch (categoria.toLowerCase()) {
            case "armazon" -> "Armazón";
            case "mica" -> "Mica";
            case "tratamiento" -> "Tratamiento";
            case "servicio" -> "Servicio";
            case "lente_contacto" -> "Lente Contacto";
            default -> categoria;
        };
    }
    
    private static String styleCategoria(String categoria){
        return switch (categoria.toLowerCase()) {
            case "armazon" -> "badge-armazon";
            case "mica" -> "badge-mica";
            case "tratamiento" -> "badge-tratamiento";
            case "servicio" -> "badge-servicio";
            case "lente_contacto" -> "badge-lente";
            default -> "badge-producto";
        };
    }
    // =========================================================
// CONFIGURACIÓN RÁPIDA DE COLUMNAS
// =========================================================

public static <T> void text(TableColumn<T, String> column) {

    if (column == null) {
        return;
    }

    column.setCellFactory(col -> textCell());
}

public static <T> void centeredText(TableColumn<T, String> column) {

    if (column == null) {
        return;
    }

    column.setCellFactory(col -> centeredTextCell());
}

public static <T> void enumColumn(TableColumn<T, String> column) {

    if (column == null) {
        return;
    }

    column.setCellFactory(col -> enumCell());
}

public static <T> void integer(TableColumn<T, Integer> column) {

    if (column == null) {
        return;
    }

    column.setCellFactory(col -> integerCell());
}

public static <T> void decimal(
        TableColumn<T, Double> column,
        int decimals) {

    if (column == null) {
        return;
    }

    column.setCellFactory(col -> doubleCell(decimals));
}

public static <T> void money(
        TableColumn<T, Double> column) {

    money(column, false);
}

public static <T> void money(
        TableColumn<T, Double> column,
        boolean bold) {

    if (column == null) {
        return;
    }

    column.setCellFactory(col -> moneyCell(bold));
}

public static <T> void discount(
        TableColumn<T, Double> column) {

    if (column == null) {
        return;
    }

    column.setCellFactory(col -> discountCell());
}

public static <T> void percent(
        TableColumn<T, Double> column) {

    if (column == null) {
        return;
    }

    column.setCellFactory(col -> percentCell());
}

public static <T> void category(
        TableColumn<T, String> column) {

    if (column == null) {
        return;
    }

    column.setCellFactory(col -> categoryBadgeCell());
}

    

}
