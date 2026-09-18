package com.gerardgv.posclarity.utils;

import java.util.function.Function;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class SearchUtils {

    // ============================================================
    // BÚSQUEDA PARA TABLAS SIN PAGINACIÓN
    // ============================================================

    @SafeVarargs
    public static <T> void setupSearch(
            TextField txtBuscar,
            TableView<T> table,
            ObservableList<T> data,
            Function<T, String>... searchFields) {

        FilteredList<T> filtered =
                crearFiltro(
                        txtBuscar,
                        data,
                        searchFields
                );

        SortedList<T> sorted =
                new SortedList<>(filtered);

        sorted.comparatorProperty().bind(
                table.comparatorProperty()
        );

        table.setItems(sorted);
    }


    // ============================================================
    // BÚSQUEDA PARA PAGINACIÓN
    // ============================================================

    @SafeVarargs
    public static <T> FilteredList<T> setupSearch(
            TextField txtBuscar,
            ObservableList<T> data,
            Function<T, String>... searchFields) {

        return crearFiltro(
                txtBuscar,
                data,
                searchFields
        );
    }


    // ============================================================
    // CREAR FILTRO
    // ============================================================

    @SafeVarargs
    private static <T> FilteredList<T> crearFiltro(
            TextField txtBuscar,
            ObservableList<T> data,
            Function<T, String>... searchFields) {

        FilteredList<T> filtered =
                new FilteredList<>(data, p -> true);

        txtBuscar.textProperty().addListener(
                (obs, oldVal, newVal) -> {

                    filtered.setPredicate(item -> {

                        if (newVal == null || newVal.isBlank()) {
                            return true;
                        }

                        String lower =
                                newVal.toLowerCase().trim();

                        for (Function<T, String> field : searchFields) {

                            String value =
                                    field.apply(item);

                            if (value != null &&
                                value.toLowerCase().contains(lower)) {

                                return true;
                            }
                        }

                        return false;
                    });
                }
        );

        return filtered;
    }
}