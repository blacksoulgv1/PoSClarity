package com.gerardgv.posclarity.controllers;

import com.gerardgv.posclarity.api.AuthApiClient;
import com.gerardgv.posclarity.api.dto.auth.LoginRequest;
import com.gerardgv.posclarity.api.dto.auth.LoginResponse;
import com.gerardgv.posclarity.api.dto.seller.SellerResponse;
import com.gerardgv.posclarity.app.App;
import com.gerardgv.posclarity.models.Seller;
import com.gerardgv.posclarity.utils.AuthSession;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController implements Initializable {

    @FXML private TextField txtCodigo;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtPasswordVisible;
    @FXML private Button btnMostrarPassword;
    @FXML private Button btnLogin;
    @FXML private Label lblError;

    private final AuthApiClient authApiClient = new AuthApiClient();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        txtPasswordVisible.textProperty().bindBidirectional(
                txtPassword.textProperty()
        );

        ocultarError();
        txtCodigo.requestFocus();
    }

    @FXML
    private void togglePassword() {

        boolean mostrar = !txtPasswordVisible.isVisible();

        txtPassword.setVisible(!mostrar);
        txtPassword.setManaged(!mostrar);

        txtPasswordVisible.setVisible(mostrar);
        txtPasswordVisible.setManaged(mostrar);

        btnMostrarPassword.setText(mostrar ? "Ocultar" : "Mostrar");

        if (mostrar) {
            txtPasswordVisible.requestFocus();
            txtPasswordVisible.positionCaret(
                    txtPasswordVisible.getText().length()
            );
        } else {
            txtPassword.requestFocus();
            txtPassword.positionCaret(
                    txtPassword.getText().length()
            );
        }
    }

    @FXML
    private void enfocarPassword() {
        txtPassword.requestFocus();
    }

    @FXML
    private void handleLogin() {

        ocultarError();

        String codigoTexto = txtCodigo.getText().trim();
        String password = txtPassword.getText();

        if (codigoTexto.isEmpty()) {
            mostrarError("Ingresa tu código de empleado.");
            txtCodigo.requestFocus();
            return;
        }

        if (!codigoTexto.matches("\\d+")) {
            mostrarError("El código debe contener solo números.");
            txtCodigo.requestFocus();
            return;
        }

        final int codigo;

        try {
            codigo = Integer.parseInt(codigoTexto);
        } catch (NumberFormatException ex) {
            mostrarError("El código de empleado no es válido.");
            txtCodigo.requestFocus();
            return;
        }

        if (password == null || password.isBlank()) {
            mostrarError("Ingresa tu contraseña.");
            enfocarCampoPassword();
            return;
        }

        iniciarAutenticacion(codigo, password);
    }

    private void iniciarAutenticacion(int codigo, String password) {

        btnLogin.setDisable(true);
        btnMostrarPassword.setDisable(true);
        btnLogin.setText("Verificando...");

        Task<LoginResponse> tarea = new Task<>() {
            @Override
            protected LoginResponse call()
                    throws IOException, InterruptedException {

                LoginRequest request = new LoginRequest(codigo, password);

                return authApiClient.login(request);
            }
        };

        tarea.setOnSucceeded(event -> {
            try {
                procesarRespuesta(tarea.getValue());
            } finally {
                restaurarBoton();
            }
        });

        tarea.setOnFailed(event -> {
            Throwable error = tarea.getException();

            if (error != null) {
                System.err.println(
                        "Error al iniciar sesión: " + error.getMessage()
                );
            }

            mostrarError(obtenerMensajeError(error));
            restaurarBoton();
        });

        Thread hilo = new Thread(tarea, "posclarity-login");
        hilo.setDaemon(true);
        hilo.start();
    }

    private void procesarRespuesta(LoginResponse respuesta) {

        if (respuesta == null) {
            mostrarError("El servidor no devolvió una respuesta válida.");
            return;
        }

        SellerResponse sellerResponse = respuesta.getSeller();
        String token = respuesta.getToken();

        if (sellerResponse == null) {
            mostrarError("El servidor no devolvió los datos del empleado.");
            return;
        }

        if (!Boolean.TRUE.equals(sellerResponse.getActive())) {
            mostrarError("El empleado está inactivo. Contacta al administrador.");
            return;
        }

        if (token == null || token.isBlank()) {
            mostrarError("El servidor no devolvió un token de sesión válido.");
            return;
        }

        Seller empleado = sellerResponse.toSeller();

        if (empleado == null || empleado.getRole() == null) {
            mostrarError("Los datos o el rol del empleado no son válidos.");
            return;
        }

        AuthSession.iniciar(empleado, token);

        System.out.println(
                "Autenticación correcta para el empleado: "
                + empleado.getName()
        );
        
        try{
            App.setRoot("main");
        } catch(IOException e){
            AuthSession.cerrarSesion();
            e.printStackTrace();
            mostrarError(
            "La autenticación fue correcta, pero no se pudo abrir "
            + "el menú principal."
            );
        }
    }

    private String obtenerMensajeError(Throwable error) {

        if (error == null) {
            return "No fue posible iniciar sesión.";
        }

        String mensaje = error.getMessage();

        if (mensaje == null || mensaje.isBlank()) {
            return "No fue posible conectar con el servidor. "
                    + "Verifica tu conexión e inténtalo de nuevo.";
        }

        return mensaje;
    }

    private void restaurarBoton() {
        btnLogin.setDisable(false);
        btnMostrarPassword.setDisable(false);
        btnLogin.setText("Iniciar sesión");
    }

    private void enfocarCampoPassword() {
        if (txtPasswordVisible.isVisible()) {
            txtPasswordVisible.requestFocus();
        } else {
            txtPassword.requestFocus();
        }
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setManaged(true);
        lblError.setVisible(true);
    }

    private void ocultarError() {
        lblError.setText("");
        lblError.setManaged(false);
        lblError.setVisible(false);
    }
}
