package com.gerardgv.posclarity.Ui;

import com.gerardgv.posclarity.controllers.PaginationController;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

public class TablePagination<T> {
    
    private final TableView<T> table;
    private final PaginationController  pagination;
    private final ObservableList<T> allItems = FXCollections.observableArrayList();
    private final ObservableList<T> filteredItems = FXCollections.observableArrayList();
    private final ObservableList<T> pageItems = FXCollections.observableArrayList();
    
    public TablePagination(TableView<T> table, PaginationController pagination){
        
        this.table = table;
        this.pagination = pagination;
        
        table.setItems(pageItems);

        pagination.setOnPageChange(page ->
                actualizarPagina()
        );        
    }
    
    public void setItems(List<T> items) {

        allItems.setAll(items);
        filteredItems.setAll(allItems);
        pagination.reset();
        pagination.setTotalItems(filteredItems.size());
        actualizarPagina();
    }
    
    public void setFilteredItems(List<T> items) {

        filteredItems.setAll(items);
        pagination.reset();
        pagination.setTotalItems(filteredItems.size());
        actualizarPagina();
    }
    
    public ObservableList<T> getAllItems() {
        return allItems;
    }

    public ObservableList<T> getFilteredItems() {
        return filteredItems;
    }
    
    private void actualizarPagina(){
        
         int total =
                filteredItems.size();

        if (total == 0) {

            pageItems.clear();
            return;
        }


        int inicio =
                pagination.getStartIndex();

        int fin =
                pagination.getEndIndex();


        // --------------------------------------------------------
        // Protección de límites
        // --------------------------------------------------------

        inicio = Math.max(
                0,
                Math.min(inicio, total)
        );

        fin = Math.max(
                inicio,
                Math.min(fin, total)
        );


        pageItems.setAll(
                filteredItems.subList(inicio, fin)
        );
    }
    
    public void refresh(){
        actualizarPagina();
    }
}
