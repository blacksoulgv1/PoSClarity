package com.gerardgv.posclarity.utils;

import java.util.function.Function;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class SearchUtils {
    
    @SafeVarargs
    public static <T> void setupSearch(
            TextField txtBuscar,
            TableView<T> table,
            ObservableList<T> data,
            Function<T, String>...searchFilds){
        
        FilteredList<T> filtered = new FilteredList<>(data, p -> true);
        
        txtBuscar.textProperty().addListener((ObservableValue<? extends String>
               obs,String oldVal,String newVal)->{
            
            filtered.setPredicate(item ->{
                
                if(newVal == null || newVal.isEmpty()){
                    return true;
                }
                
                String lower = newVal.toLowerCase();
                
                for(Function<T,String> field : searchFilds){
                    String value = field.apply(item);
                    if(value != null && value.toLowerCase().contains(lower)){
                        return true;
                    }                    
                }
                return false;
            });
        });
        
        SortedList<T> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sorted);
    }
    
}
