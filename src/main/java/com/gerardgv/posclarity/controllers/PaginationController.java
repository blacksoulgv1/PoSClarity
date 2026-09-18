package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.Ui.PaginationControl;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.IntConsumer;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

public class PaginationController implements Initializable {

    // ============================================================
    // CONTROLES FXML
    // ============================================================

    @FXML private HBox root;
    @FXML private ComboBox<Integer> cbFilasPorPagina;
    @FXML private Button btnPrimeraPagina;
    @FXML private Button btnPaginaAnterior;
    @FXML private Button btnPaginaSiguiente;
    @FXML private Button btnUltimaPagina;
    @FXML private Label lblPaginaActual;


    // ============================================================
    // PAGINACIÓN
    // ============================================================

    private final PaginationControl pagination =
            new PaginationControl();


    // ============================================================
    // EVENTO PARA EL CONTROLADOR PADRE
    // ============================================================

    private IntConsumer onPageChange;


    // ============================================================
    // INICIALIZACIÓN
    // ============================================================

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        configurarFilasPorPagina();
        configurarEventos();
        actualizarVista();

    }


    // ============================================================
    // CONFIGURAR FILAS POR PÁGINA
    // ============================================================

    private void configurarFilasPorPagina() {

        cbFilasPorPagina.setItems(
                FXCollections.observableArrayList(
                        6,
                        12,
                        24,
                        36,
                        48
                )
        );

        cbFilasPorPagina.setValue(
                pagination.getItemsPerPage()
        );

    }


    // ============================================================
    // EVENTOS
    // ============================================================

    private void configurarEventos() {

        btnPrimeraPagina.setOnAction(e ->
                pagination.firstPage()
        );

        btnPaginaAnterior.setOnAction(e ->
                pagination.previousPage()
        );

        btnPaginaSiguiente.setOnAction(e ->
                pagination.nextPage()
        );

        btnUltimaPagina.setOnAction(e ->
                pagination.lastPage()
        );


        cbFilasPorPagina.setOnAction(e -> {

            Integer filas =
                    cbFilasPorPagina.getValue();

            if (filas != null) {

                pagination.setItemsPerPage(filas);

                actualizarVista();

                if (onPageChange != null) {
                    onPageChange.accept(
                            pagination.getCurrentPage()
                    );
                }

            }

        });


        pagination.setOnPageChange(page -> {

            actualizarVista();

            if (onPageChange != null) {

                onPageChange.accept(page);

            }

        });

    }


    // ============================================================
    // ACTUALIZAR INTERFAZ
    // ============================================================

    private void actualizarVista() {

        int totalPages =
                pagination.getTotalPages();

        int paginaActual =
                pagination.getCurrentPage();


        // --------------------------------------------------------
        // Número de página
        // --------------------------------------------------------

        if (totalPages == 0) {

            lblPaginaActual.setText("0");

        } else {

            lblPaginaActual.setText(
                    String.valueOf(paginaActual + 1)
            );

        }


        // --------------------------------------------------------
        // Estados de navegación
        // --------------------------------------------------------

        boolean hayAnterior =
                pagination.hasPreviousPage();

        boolean haySiguiente =
                pagination.hasNextPage();


        btnPrimeraPagina.setDisable(
                !hayAnterior
        );

        btnPaginaAnterior.setDisable(
                !hayAnterior
        );

        btnPaginaSiguiente.setDisable(
                !haySiguiente
        );

        btnUltimaPagina.setDisable(
                !haySiguiente
        );

    }


    // ============================================================
    // API PÚBLICA DEL COMPONENTE
    // ============================================================

    /**
     * Establece el total de registros.
     */
    public void setTotalItems(int totalItems) {

        pagination.setTotalItems(totalItems);

        actualizarVista();
    }


    /**
     * Obtiene el total de registros.
     */
    public int getTotalItems() {

        return pagination.getTotalItems();

    }


    /**
     * Establece la cantidad de elementos por página.
     *
     * Cada vista puede utilizar una cantidad diferente.
     */
    public void setItemsPerPage(int itemsPerPage) {

        if (itemsPerPage <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad de elementos por página debe ser mayor que 0."
            );
        }

        pagination.setItemsPerPage(itemsPerPage);

        cbFilasPorPagina.setValue(itemsPerPage);

        actualizarVista();
    }


    /**
     * Obtiene la cantidad de elementos por página.
     */
    public int getItemsPerPage() {

        return pagination.getItemsPerPage();

    }


    /**
     * Muestra u oculta el selector de filas por página.
     *
     * Cuando está oculto también deja de ocupar espacio.
     */
    public void setShowItemsPerPageSelector(boolean mostrar) {

        cbFilasPorPagina.setVisible(mostrar);
        cbFilasPorPagina.setManaged(mostrar);

    }


    /**
     * Obtiene la página actual.
     *
     * El índice comienza en 0.
     */
    public int getCurrentPage() {

        return pagination.getCurrentPage();

    }


    /**
     * Obtiene el índice inicial de los registros
     * correspondientes a la página actual.
     */
    public int getStartIndex() {

        return pagination.getStartIndex();

    }


    /**
     * Obtiene el índice final exclusivo.
     */
    public int getEndIndex() {

        return pagination.getEndIndex();

    }


    /**
     * Reinicia la paginación a la primera página.
     */
    public void reset() {

        pagination.reset();

        actualizarVista();

    }


    /**
     * Permite al controlador padre reaccionar
     * cuando cambia la página.
     */
    public void setOnPageChange(IntConsumer listener) {

        this.onPageChange = listener;

    }

}