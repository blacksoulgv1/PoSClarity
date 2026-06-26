package com.gerardgv.posclarity.app;

import com.gerardgv.posclarity.database.BranchDAO;
import com.gerardgv.posclarity.models.Branch;
import com.gerardgv.posclarity.utils.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    

    @Override
    public void start(Stage stage) throws IOException {
        Parent root;
        boolean primeraInstalacion = false;
        
        //PRIMERA INSTALACION
        if(!Instalacion.existeSucursal()){
            primeraInstalacion = true;
            root = loadFXML("ConfiguracionInicial");
            
        } else {
            
            if(!Configuracion.existeSucursal()){
                
                root = loadFXML("SelectBranch");
                
            } else {
                
                Integer idSucursal = Configuracion.obtenerSucursal();
                Branch sucursal = BranchDAO.obtenerPorId(idSucursal);
                
                    if(sucursal == null){
                        Configuracion.guardarSucursal(-1);
                        root = loadFXML("SelectBranch");
                    } else {
                        Session.setSucursal(sucursal);
                        root = loadFXML("main"); 
                    }
            }
        }       
        
        scene = new Scene(root);
        stage.setScene(scene);
        
        if(primeraInstalacion){
            stage.setWidth(500);
            stage.setHeight(450);
            stage.setResizable(false);
        }else{
            stage.setMaximized(true);
        }       
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
        
        Stage stage = (Stage) scene.getWindow();
        
        if(fxml.equals("main")){
            
            stage.setResizable(true);
            stage.setMaximized(true);
            
        }
        
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                App.class.getResource(
                        "/com/gerardgv/posclarity/views/" + fxml + ".fxml")
        );
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}