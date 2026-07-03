package com.gerardgv.posclarity.utils;

import com.gerardgv.posclarity.database.*;
import com.gerardgv.posclarity.models.*;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class AuthorizationDialog {
    
    public static boolean solicitarGerente(){
        
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Acceso Restringido");
        dialog.setHeaderText("Ingresa credenciales de gerente");

        ButtonType btnEntrar = new ButtonType("Entrar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnEntrar, ButtonType.CANCEL);

        TextField txtCodigo = new TextField();
        txtCodigo.setPromptText("Código");

        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Contraseña");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Código:"), 0, 0);
        grid.add(txtCodigo, 1, 0);
        grid.add(new Label("Contraseña:"), 0, 1);
        grid.add(txtPassword, 1, 1);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isEmpty() || result.get() != btnEntrar) {
            return false;
        }

        try {
            int codigo = Integer.parseInt(txtCodigo.getText().trim());
            String password = txtPassword.getText().trim();

            EmpleadosDAO dao = new EmpleadosDAO();
            Empleados emp = dao.validarAcceso(codigo, password);

            if (emp == null) {
                mostrarAlerta("Credenciales incorrectas");
                return false;
            }

            if (emp.getRol().equalsIgnoreCase("gerente")
                    || emp.getRol().equalsIgnoreCase("directivo")) {
                return true;
            }

            mostrarAlerta("No cuentas con permisos de gerente");
            return false;

        } catch (NumberFormatException e) {
            mostrarAlerta("El código debe ser numérico");
            return false;
        }
    }

    private static void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Acceso denegado");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
}
