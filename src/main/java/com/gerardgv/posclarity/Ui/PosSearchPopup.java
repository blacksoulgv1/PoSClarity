package com.gerardgv.posclarity.Ui;

import java.util.List;
import java.util.function.*;
import javafx.animation.*;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import javafx.stage.Screen;
import org.kordamp.ikonli.javafx.FontIcon;

public class PosSearchPopup<T> {
    
    private final PopupControl popup = new PopupControl();
    private final ListView<T> listView = new ListView<>();
    private final VBox root = new VBox(listView);
    private Consumer<T> onSelected;
    private Function<T,String> titleProvider = item -> item == null ? "" : item.toString();
    private Function<T,String> subtitleProvider = item -> "";
    private Function<T,String> iconProvider = item -> "fas-search";
    private String emptyMessage = "Sin Resultados";
    private String searchText = "";
    
    public PosSearchPopup(){
        
        listView.setFixedCellSize(52);
        listView.setPrefHeight(52);
        listView.setPrefWidth(320);

        root.getStyleClass().add("pos-search-popup");
        listView.getStyleClass().add("pos-search-popup-list");

        popup.setAutoHide(true);
        popup.getScene().setRoot(root);
        
        popup.setOnShown(e -> listView.requestFocus());
        
        listView.setCellFactory(lv -> new ListCell<>(){
            
            private final FontIcon icon = new FontIcon();
            private final Label lblTitle = new Label();
            private final Label lblSubtitle = new Label();
            private final VBox texts = new VBox(2,lblTitle,lblSubtitle);
            private final HBox content = new HBox(10, icon, texts);

        {
            icon.setIconSize(18);
            icon.getStyleClass().add("pos-popup-icon");
            lblTitle.getStyleClass().add("pos-popup-title");
            lblSubtitle.getStyleClass().add("pos-popup-subtitle");
            lblTitle.setMaxWidth(Double.MAX_VALUE);
            lblSubtitle.setMaxWidth(Double.MAX_VALUE);
            lblTitle.setTextOverrun(OverrunStyle.ELLIPSIS);
            lblSubtitle.setTextOverrun(OverrunStyle.ELLIPSIS);
            texts.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(texts, Priority.ALWAYS);
            content.getStyleClass().add("pos-popup-row");
        }

        @Override
        protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }

                icon.setIconLiteral(iconProvider.apply(item));
                lblTitle.setText(titleProvider.apply(item));
                lblSubtitle.setText(subtitleProvider.apply(item));
                setGraphic(content);
                setText(null);
            }
        });
        
        listView.setOnMouseClicked(e -> seleccionar());
        listView.setOnKeyPressed(e ->{
            switch (e.getCode()){
                case ENTER,TAB -> seleccionar();
                case ESCAPE -> hide();
            }
        });        
    }
    
    public void setTitleProvider(Function<T, String> titleProvider) {
        this.titleProvider = titleProvider;
    }

    public void setOnSelected(Consumer<T> onSelected) {
        this.onSelected = onSelected;
    }

    public void setItems(List<T> items) {
        
        listView.getItems().setAll(items);
        int count = items == null ? 0 : items.size();
        int visibleRows = Math.min(Math.max(count, 1), 5);
        listView.setPrefHeight(visibleRows * 52 + 6);
        
            if (items == null || items.isEmpty()) {
                
                FontIcon icon = new FontIcon("fas-search");
                icon.setIconSize(24);
                icon.getStyleClass().add("pos-popup-empty-icon");
                Label lbl = new Label(emptyMessage);
                lbl.getStyleClass().add("pos-popup-empty-text");
                VBox emptyBox = new VBox(8, icon, lbl);
                emptyBox.getStyleClass().add("pos-popup-empty");
                listView.setPlaceholder(emptyBox);
            }
    }

    public boolean isEmpty() {
        return listView.getItems().isEmpty();
    }

    public T getFirst() {
        return isEmpty() ? null : listView.getItems().get(0);
    }
    
    public void show(TextField owner){
        
        if (owner == null) {
            hide();
            return;
        }
        
        searchText = owner.getText() == null ? "" : owner.getText();
        listView.refresh();
        Bounds bounds = owner.localToScreen(owner.getBoundsInLocal());
        
        double popupWidth = Math.max(owner.getWidth(), 320);
        double popupHeight = listView.getPrefHeight();
        
        listView.setPrefWidth(popupWidth);
        
        if(!listView.getItems().isEmpty()
            && listView.getSelectionModel().getSelectedIndex() < 0){
            listView.getSelectionModel().selectFirst();
        }
        
        
        
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
        
        double yBelow = bounds.getMaxY() + 4;
        double yAbove = bounds.getMinY() - popupHeight - 4;

        double finalY = (yBelow + popupHeight > screenHeight && yAbove > 0)
            ? yAbove
            : yBelow;

        if (!popup.isShowing()) {
            popup.show(owner, bounds.getMinX(), finalY);
            animarEntrada();
        } else {
            popup.setX(bounds.getMinX());
            popup.setY(finalY);
        }
    }
    
    public void hide(){
        popup.hide();
    }
    
    public void seleccionar(){
         T item = listView.getSelectionModel().getSelectedItem();

        if (item == null && !listView.getItems().isEmpty()) {
            item = listView.getItems().get(0);
        }

        if (item != null && onSelected != null) {
            onSelected.accept(item);
            hide();
        }
    }
    
    public void setSubtitleProvider(Function<T,String> subtitleProvider){
        this.subtitleProvider = subtitleProvider;
    }
    
    public void setIconProvider(Function<T,String> iconProvider){
        this.iconProvider = iconProvider; 
    }
    
    public void setEmptyMessage(String emptyMessage) {
        this.emptyMessage = emptyMessage;
    }
    
    private void animarEntrada(){
        root.setOpacity(0);
        root.setTranslateY(-6);
        FadeTransition fade = new FadeTransition(Duration.millis(160), root);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(160), root);
        slide.setFromY(-6);
        slide.setToY(0);

        fade.play();
        slide.play();
    }
    
}
