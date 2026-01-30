package com.gerardgv.posclarity.utils;

import javafx.scene.Parent;

public class ViewInf {
    
    private Parent View;
    private Object Controller;
    
    public ViewInf(Parent View, Object Controller){
        this.View = View;
        this.Controller = Controller;
    }
    
    public Parent getView(){
        return View;
    }
    
    public Object getController(){
        return Controller;
    }
}
