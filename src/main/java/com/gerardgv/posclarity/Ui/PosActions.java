package com.gerardgv.posclarity.Ui;

import java.util.function.Consumer;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;

public class PosActions {
    
    private PosActions(){
        
    }
    
    private static Button createButton( String icon, String tooltip, String styleClass){
        
        FontIcon fontIcon = new FontIcon(icon);
        fontIcon.setIconSize(15);
        
        Button button = new Button();
        button.setGraphic(fontIcon);
        button.setTooltip(new Tooltip(tooltip));
        button.getStyleClass().addAll("clarity-action",styleClass);
        
        return button;        
    }
    
    public static <T> TableCell <T,Void> delete(
            Consumer<T> action){
        
        return new TableCell<>(){
            
            private final Button btnDelete = 
                    createButton("fas-trash-alt", "Eliminar", "danger");
            
            private final HBox box = new HBox(btnDelete);
            {
                btnDelete.getStyleClass().add("danger");
                box.setAlignment(Pos.CENTER);
                btnDelete.setOnAction(e -> {
                    T item = getTableView().getItems().get(getIndex());
                    action.accept(item);
                });                
            }
            @Override
            protected void updateItem(Void item, boolean empty){
                
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);                
            }
        };
    }
    
    
}
