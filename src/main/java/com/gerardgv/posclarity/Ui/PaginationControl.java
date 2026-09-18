package com.gerardgv.posclarity.Ui;

import java.util.function.IntConsumer;

public class PaginationControl {
    
    private int totalItems = 0;
    private int itemsPerPage = 10;
    private int currentPage = 0;
    
    private IntConsumer pageChangeListener;

    public void setTotalItems(int totalItems) {
        
    /**
     * Establece el total de registros.
    */

        this.totalItems = Math.max(0, totalItems);

        ajustarPaginaActual();

        notificarCambio();
    }

    /**
     * Obtiene el total de registros.
     */
    public int getTotalItems() {
        return totalItems;
    }

    /**
     * Establece cuántos registros se muestran por página.
     */
    public void setItemsPerPage(int itemsPerPage) {

        if (itemsPerPage <= 0) {
            return;
        }

        this.itemsPerPage = itemsPerPage;

        ajustarPaginaActual();

        notificarCambio();
    }

    /**
     * Obtiene la cantidad de registros por página.
     */
    public int getItemsPerPage() {
        return itemsPerPage;
    }

    /**
     * Establece la página actual.
     *
     * El índice comienza en 0.
     */
    public void setCurrentPage(int page) {

        int ultimaPagina = getTotalPages() - 1;

        if (ultimaPagina < 0) {
            currentPage = 0;
        } else {
            currentPage = Math.max(
                    0,
                    Math.min(page, ultimaPagina)
            );
        }

        notificarCambio();
    }

    /**
     * Obtiene la página actual.
     *
     * El índice comienza en 0.
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * Obtiene el número total de páginas.
     */
    public int getTotalPages() {

        if (totalItems == 0) {
            return 0;
        }

        return (int) Math.ceil(
                (double) totalItems / itemsPerPage
        );
    }

    /**
     * Indica si existe una página anterior.
     */
    public boolean hasPreviousPage() {
        return currentPage > 0;
    }

    /**
     * Indica si existe una página siguiente.
     */
    public boolean hasNextPage() {
        return currentPage < getTotalPages() - 1;
    }

    /**
     * Va a la primera página.
     */
    public void firstPage() {

        if (currentPage != 0) {
            setCurrentPage(0);
        }
    }

    /**
     * Va a la página anterior.
     */
    public void previousPage() {

        if (hasPreviousPage()) {
            setCurrentPage(currentPage - 1);
        }
    }

    /**
     * Va a la página siguiente.
     */
    public void nextPage() {

        if (hasNextPage()) {
            setCurrentPage(currentPage + 1);
        }
    }

    /**
     * Va a la última página.
     */
    public void lastPage() {

        int ultimaPagina = getTotalPages() - 1;

        if (ultimaPagina >= 0 && currentPage != ultimaPagina) {
            setCurrentPage(ultimaPagina);
        }
    }

    /**
     * Reinicia la paginación a la primera página.
     */
    public void reset() {
        setCurrentPage(0);
    }

    /**
     * Obtiene el índice inicial del bloque actual.
     *
     * Ejemplo:
     *
     * Página 0, 10 elementos → 0
     * Página 1, 10 elementos → 10
     * Página 2, 10 elementos → 20
     */
    public int getStartIndex() {
        return currentPage * itemsPerPage;
    }

    /**
     * Obtiene el índice final exclusivo del bloque actual.
     */
    public int getEndIndex() {

        return Math.min(
                getStartIndex() + itemsPerPage,
                totalItems
        );
    }

    /**
     * Registra una función que será ejecutada
     * cuando cambie la página.
     */
    public void setOnPageChange(IntConsumer listener) {
        this.pageChangeListener = listener;
    }

    /**
     * Ajusta la página actual cuando cambia
     * el total de registros o los registros por página.
     */
    private void ajustarPaginaActual() {

        int totalPages = getTotalPages();

        if (totalPages == 0) {
            currentPage = 0;
            return;
        }

        if (currentPage >= totalPages) {
            currentPage = totalPages - 1;
        }
    }

    /**
     * Notifica al controlador que hubo un cambio.
     */
    private void notificarCambio() {

        if (pageChangeListener != null) {
            pageChangeListener.accept(currentPage);
        }
    }
}
