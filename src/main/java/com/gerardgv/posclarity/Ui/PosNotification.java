package com.gerardgv.posclarity.Ui;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

public class PosNotification {
    
    private PosNotification(){
        
    }
    
    public enum Type{
        SUCCESS,
        DANGER,
        WARNING,
        INFO
    }
    
    public static void success(StackPane root, String title, String message){
        show(root,title,message,Type.SUCCESS);
    }
    
    public static void error(StackPane root, String title, String message){
        show(root,title,message,Type.DANGER);
    }
    
    public static void warning(StackPane root,String title,String message){
        show(root,title,message,Type.WARNING);
    }

    public static void info(StackPane root,String title,String message){
        show(root,title,message,Type.INFO);
    }
    
    public static void show(StackPane root, String title, String message, Type type){
        
        if(root == null) return;
        
        Label lblTitle = new Label(title);
        lblTitle.getStyleClass().add("pos-toast-title");

        Label lblMessage = new Label(message);
        lblMessage.getStyleClass().add("pos-toast-message");
        lblMessage.setWrapText(true);
        FontIcon icon = new FontIcon();
        
        switch(type){
            
        case SUCCESS -> icon.setIconLiteral("fas-check-circle");
        case DANGER -> icon.setIconLiteral("fas-times-circle");
        case WARNING -> icon.setIconLiteral("fas-exclamation-triangle");
        case INFO -> icon.setIconLiteral("fas-info-circle");
        
        }
        
        icon.setIconSize(22);
        icon.getStyleClass().add("pos-toast-icon");

        VBox texts = new VBox(3, lblTitle, lblMessage);
        texts.setFillWidth(true);

        HBox toastContent = new HBox(12,icon, texts);
        toastContent.setAlignment(Pos.CENTER_LEFT);
        
        Region progress = new Region();
        progress.getStyleClass().add("pos-toast-progress");

        VBox toastBox = new VBox(toastContent, progress);
        toastBox.getStyleClass().addAll("pos-toast", cssType(type));

        toastBox.setMinWidth(340);
        toastBox.setPrefWidth(340);
        toastBox.setMaxWidth(340);

        StackPane.setAlignment(toastBox, Pos.TOP_RIGHT);
        toastBox.setTranslateX(-20);
        toastBox.setTranslateY(20);

        root.getChildren().add(toastBox);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(180), toastBox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(180), toastBox);
        slideIn.setFromY(0);
        slideIn.setToY(20);

        PauseTransition wait = new PauseTransition(Duration.seconds(2.2));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(220), toastBox);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        fadeIn.play();
        slideIn.play();

        wait.setOnFinished(e -> {
            fadeOut.play();
            fadeOut.setOnFinished(ev -> root.getChildren().remove(toastBox));
        });
        
        ScaleTransition progressAnim = new ScaleTransition(Duration.seconds(2.2), progress);
        progressAnim.setFromX(1);
        progressAnim.setToX(0);
        progressAnim.play();

        wait.play();
    }   
    
    private static String cssType(Type type){
        return switch (type) {
            case SUCCESS -> "pos-toast-success";
            case DANGER -> "pos-toast-danger";
            case WARNING -> "pos-toast-warning";
            case INFO -> "pos-toast-info";
        };
    }
}
