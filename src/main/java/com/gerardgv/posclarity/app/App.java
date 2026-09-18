package com.gerardgv.posclarity.app;

import com.gerardgv.posclarity.api.BranchApiClient;
import com.gerardgv.posclarity.models.Branch;
import com.gerardgv.posclarity.utils.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private final BranchApiClient branchApiClient = new BranchApiClient();

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
                Branch sucursal;                
                try {                    
                    sucursal = branchApiClient.getById(idSucursal);                    
                } catch (InterruptedException e){                    
                    Thread.currentThread().interrupt();
                    sucursal = null;                    
                } catch (IOException e){                    
                    e.printStackTrace();
                    sucursal = null;                    
                }
                
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
        scene.getStylesheets().add(
            App.class.getResource(
                "/com/gerardgv/posclarity/css/theme/PosTheme.css"
                ).toExternalForm());
        stage.setScene(scene);
        
        if(primeraInstalacion){
            stage.setWidth(500);
            stage.setHeight(450);
            stage.setResizable(false);
            stage.show();
        }else{
            configurarVentanaPrincipal(stage);
        }       
    }
    
    private static void configurarVentanaPrincipal(Stage stage){
        
     // Área disponible del escritorio, sin cubrir la barra de tareas
    Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();

    stage.setResizable(true);

    stage.setX(visualBounds.getMinX());
    stage.setY(visualBounds.getMinY());
    stage.setWidth(visualBounds.getWidth());
    stage.setHeight(visualBounds.getHeight());

    stage.show();

    // Bloqueamos el redimensionamiento después de establecer el tamaño
    stage.setResizable(false); 
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
        
        Stage stage = (Stage) scene.getWindow();
        
        if(fxml.equals("main")){            
            configurarVentanaPrincipal(stage);
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