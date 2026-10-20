
package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.utils.ViewInf;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainController implements Initializable {

    private final Map<String, ViewInf> showViews = new HashMap<>();

    @FXML private StackPane stackContent;
    @FXML private VBox sideMenu;
    @FXML private HBox brandContainer;
    @FXML private javafx.scene.control.ScrollPane sideMenuScroll;
    @FXML private javafx.scene.image.ImageView brandLogo;

    @FXML private Label currentViewLabel;
    @FXML private Label connectionStatusLabel;
    @FXML private Label branchLabel;
    @FXML private MenuButton userMenuButton;

    @FXML private Button btnVentas;
    @FXML private Button btnPendientes;
    @FXML private Button btnGarantias;
    @FXML private Button btnProductos;
    @FXML private Button btnTraspasos;
    @FXML private Button btnDescuentos;
    @FXML private Button btnClientes;
    @FXML private Button btnSucursales;
    @FXML private Button btnColaboradores;
    @FXML private Button btnReportes;

    private boolean menuCollapsed = false;
    private Button activeButton;

    private final Map<Button, String> buttonLabels = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        registrarEtiquetas();

        // Estos datos se conectarán con la sesión y la API
        // cuando implementemos la autenticación.
        connectionStatusLabel.setText("Estado por verificar");
        branchLabel.setText("Sin verificar");
        userMenuButton.setText("Sin sesión");

        viewVista("Saleview", "Ventas", btnVentas);
    }

    private void registrarEtiquetas() {
        buttonLabels.put(btnVentas, "Ventas");
        buttonLabels.put(btnPendientes, "Ventas Pendientes");
        buttonLabels.put(btnGarantias, "Garantías");
        buttonLabels.put(btnProductos, "Productos");
        buttonLabels.put(btnTraspasos, "Traspasos");
        buttonLabels.put(btnDescuentos, "Descuentos");
        buttonLabels.put(btnClientes, "Clientes");
        buttonLabels.put(btnSucursales, "Sucursales");
        buttonLabels.put(btnColaboradores, "Colaboradores");
        buttonLabels.put(btnReportes, "Reportes");

        buttonLabels.forEach((button, label) ->
                button.setTooltip(new Tooltip(label)));
    }

    @FXML
    private void toggleMenu() {
        menuCollapsed = !menuCollapsed;

        if (menuCollapsed) {
            sideMenuScroll.getStyleClass().add("collapsed");
            sideMenu.getStyleClass().add("collapsed");
        } else {
            sideMenuScroll.getStyleClass().remove("collapsed");
            sideMenu.getStyleClass().remove("collapsed");
        }

        for (Map.Entry<Button, String> entry : buttonLabels.entrySet()) {
            Button button = entry.getKey();
            String label = entry.getValue();

            button.setText(menuCollapsed ? "" : label);
            button.setTooltip(new Tooltip(label));
            button.setMaxWidth(Double.MAX_VALUE);
        }

        sideMenu.getChildren().stream()
                .filter(node -> node instanceof Label)
                .map(node -> (Label) node)
                .filter(label -> label.getStyleClass().contains("menu-section"))
                .forEach(label -> {
                    label.setVisible(!menuCollapsed);
                    label.setManaged(!menuCollapsed);
                });

        if (brandLogo != null) {
            brandLogo.setVisible(!menuCollapsed);
            brandLogo.setManaged(!menuCollapsed);
        }

        sideMenu.getChildren().stream()
                .filter(node -> node instanceof Label)
                .map(node -> (Label) node)
                .filter(label -> label.getStyleClass().contains("brand-title"))
                .forEach(label -> {
                    label.setVisible(!menuCollapsed);
                    label.setManaged(!menuCollapsed);
                });

        if (brandContainer != null) {
            brandContainer.setAlignment(
                    menuCollapsed ? Pos.CENTER : Pos.CENTER
            );
        }
    }

    private void viewVista(String vista, String titulo, Button button) {
        try {
            ViewInf cachedView = showViews.get(vista);
            Parent root;

            if (cachedView != null) {
                root = cachedView.getView();
            } else {
                URL resource = getClass().getResource(
                        "/com/gerardgv/posclarity/views/" + vista + ".fxml"
                );

                if (resource == null) {
                    throw new IOException(
                            "No se encontró el archivo FXML de la vista: " + vista
                    );
                }

                FXMLLoader loader = new FXMLLoader(resource);
                root = loader.load();

                Object controller = loader.getController();

                URL css = getClass().getResource(
                        "/com/gerardgv/posclarity/css/main-layout.css"
                );

                if (css != null) {
                    String cssPath = css.toExternalForm();

                    if (!root.getStylesheets().contains(cssPath)) {
                        root.getStylesheets().add(cssPath);
                    }
                }

                showViews.put(vista, new ViewInf(root, controller));
            }

            mostrarVista(root);
            currentViewLabel.setText(titulo);
            actualizarBotonActivo(button);

        } catch (IOException | RuntimeException e) {
            System.err.println("Error al cargar la vista: " + vista);
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de navegación");
            alert.setHeaderText("No se pudo abrir el módulo");
            alert.setContentText(
                    "Ocurrió un problema al abrir \"" + titulo
                    + "\". Consulta el registro de errores para más detalles."
            );
            alert.showAndWait();
        }
    }

    private void mostrarVista(Parent root) {
        StackPane.setAlignment(root, Pos.TOP_LEFT);
        stackContent.getChildren().setAll(root);
    }

    private void actualizarBotonActivo(Button button) {
        if (activeButton != null) {
            activeButton.getStyleClass().remove("menu-button-active");
        }

        activeButton = button;

        if (activeButton != null
                && !activeButton.getStyleClass().contains("menu-button-active")) {
            activeButton.getStyleClass().add("menu-button-active");
        }
    }

    @FXML
    private void abrirNuevaVenta() {
        viewVista("Saleview", "Ventas", btnVentas);
    }

    @FXML
    private void AbrirGarantias() {
        viewVista("Garantias", "Garantías", btnGarantias);
    }

    @FXML
    private void abrirPendintes() {
        viewVista("Ventas_pendientes", "Ventas pendientes", btnPendientes);
    }

    @FXML
    private void abrirEntregados() {
        viewVista("ventas_entregadas", "Ventas entregadas", btnPendientes);
    }

    @FXML
    private void abrirProductos() {
        viewVista("Productsview", "Productos", btnProductos);
    }

    @FXML
    private void abrirClientes() {
        viewVista("Clientsview", "Clientes", btnClientes);
    }

    @FXML
    private void abrirSucursales() {
        viewVista("Branch", "Sucursales", btnSucursales);
    }

    @FXML
    private void abrirTraspaso() {
        viewVista("Transfer", "Traspasos", btnTraspasos);
    }

    @FXML
    private void abrirDescuento() {
        viewVista("Discount", "Descuentos", btnDescuentos);
    }

    @FXML
    private void abrirAbono() {
        viewVista("Pending", "Ventas pendientes", btnPendientes);
    }

    @FXML
    private void abrirEmpleados() {
        viewVista("Sellers", "Colaboradores", btnColaboradores);
    }

    @FXML
    private void abrirReportes() {
        viewVista("Reports", "Reportes", btnReportes);
    }

    @FXML
    private void abrirPerfil() {
        mostrarAvisoSesion(
                "El perfil estará disponible cuando integremos el inicio de sesión."
        );
    }

    @FXML
    private void cambiarContrasena() {
        mostrarAvisoSesion(
                "El cambio de contraseña se habilitará con la autenticación."
        );
    }

    @FXML
    private void cerrarSesion() {
        mostrarAvisoSesion(
                "El cierre de sesión se implementará al conectar la autenticación."
        );
    }

    private void mostrarAvisoSesion(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cuenta de usuario");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
