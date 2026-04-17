package com.gerardgv.posclarity.app;

import com.gerardgv.posclarity.database.BranchDAO;
import com.gerardgv.posclarity.models.Branch;
import com.gerardgv.posclarity.utils.Configuracion;
import com.gerardgv.posclarity.utils.Session;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.stage.Modality;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        
        Integer idSucursal = Configuracion.obtenerSucursal();
        Parent root;
        
        if(idSucursal == null){
            root = loadFXML("SelectBranch");
        }else{
            Branch sucursal = BranchDAO.obtenerPorId(idSucursal);
            Session.setSucursal(sucursal);
            root = loadFXML("main");
        }
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
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