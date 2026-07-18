package com.gerardgv.posclarity.Ui;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class PosAnimations {
    
    private PosAnimations(){
        
    }
    
    public static void fadeSlideIn(Node node){
        
        if(node == null) return;
        
        node.setOpacity(0);
        node.setTranslateY(8);
        
        FadeTransition fade = new FadeTransition(Duration.millis(180),node);
        fade.setFromValue(0);
        fade.setToValue(1);
        
        TranslateTransition slide = new TranslateTransition(Duration.millis(180),node);
        slide.setFromY(8);
        slide.setToY(0);
        fade.play();
        slide.play();        
    }
    
}
