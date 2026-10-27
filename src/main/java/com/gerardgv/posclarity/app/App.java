
package com.gerardgv.posclarity.app;

import com.gerardgv.posclarity.api.BranchApiClient;
import com.gerardgv.posclarity.models.Branch;
import com.gerardgv.posclarity.utils.*;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;

    private final BranchApiClient branchApiClient =
            new BranchApiClient();

    @Override
    public void start(Stage stage) throws IOException {

        Parent root;
        String vistaInicial;
        boolean primeraInstalacion = false;

        // PRIMERA INSTALACIÓN
        if (!Instalacion.existeSucursal()) {

            primeraInstalacion = true;
            vistaInicial = "ConfiguracionInicial";
            root = loadFXML(vistaInicial);

        } else {

            if (!Configuracion.existeSucursal()) {

                vistaInicial = "SelectBranch";
                root = loadFXML(vistaInicial);

            } else {

                Integer idSucursal = Configuracion.obtenerSucursal();
                Branch sucursal;

                try {

                    sucursal = branchApiClient.getById(idSucursal);

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();
                    sucursal = null;

                } catch (IOException e) {

                    e.printStackTrace();
                    sucursal = null;
                }

                if (sucursal == null) {

                    Configuracion.guardarSucursal(-1);

                    vistaInicial = "SelectBranch";
                    root = loadFXML(vistaInicial);

                } else {

                    Session.setSucursal(sucursal);

                    vistaInicial = "Login";
                    root = loadFXML(vistaInicial);
                }
            }
        }

        scene = new Scene(root);

        scene.getStylesheets().add(
                App.class.getResource(
                        "/com/gerardgv/posclarity/css/theme/PosTheme.css"
                ).toExternalForm()
        );

        stage.setScene(scene);
        stage.setTitle("PoSClarity - Ópticas Clarity");

        if (primeraInstalacion) {

            configurarVentanaInicial(stage);

        } else if ("main".equals(vistaInicial)) {

            configurarVentanaPrincipal(stage);

        } else {

            configurarVentanaAcceso(stage, vistaInicial);
        }
    }

    /**
     * Ventana de configuración de la instalación.
     */
    private static void configurarVentanaInicial(Stage stage) {

        stage.setMaximized(false);
        stage.setFullScreen(false);
        stage.setResizable(true);

        stage.setMinWidth(480);
        stage.setMinHeight(400);

        stage.setWidth(600);
        stage.setHeight(500);

        stage.centerOnScreen();
        stage.show();
    }

    /**
     * Ventanas compactas para login y selección de sucursal.
     */
    private static void configurarVentanaAcceso(
            Stage stage,
            String vista
    ) {

        stage.setMaximized(false);
        stage.setFullScreen(false);
        stage.setResizable(true);

        switch (vista) {

            case "SelectBranch" -> {
                stage.setMinWidth(460);
                stage.setMinHeight(420);

                stage.setWidth(500);
                stage.setHeight(460);
            }

            case "Login" -> {
                stage.setMinWidth(480);
                stage.setMinHeight(480);

                stage.setWidth(560);
                stage.setHeight(600);
            }

            default -> {
                stage.setMinWidth(480);
                stage.setMinHeight(360);

                stage.setWidth(520);
                stage.setHeight(420);
            }
        }

        stage.centerOnScreen();
        stage.show();
    }

    /**
     * Ventana principal del punto de venta.
     */
    private static void configurarVentanaPrincipal(Stage stage) {

        stage.setResizable(true);

        if (!stage.isShowing()) {
            stage.show();
        }

        stage.setMaximized(true);
    }

    /**
     * Cambia la vista actual.
     */
    public static void setRoot(String fxml) throws IOException {

        scene.setRoot(loadFXML(fxml));

        Stage stage = (Stage) scene.getWindow();

        if (stage != null && "main".equals(fxml)) {
            configurarVentanaPrincipal(stage);
        }
    }

    /**
     * Carga una vista FXML.
     */
    private static Parent loadFXML(String fxml) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(
                App.class.getResource(
                        "/com/gerardgv/posclarity/views/"
                                + fxml + ".fxml"
                )
        );

        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
