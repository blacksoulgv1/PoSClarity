package com.gerardgv.posclarity.utils;

import com.gerardgv.posclarity.api.AuthApiClient;
import com.gerardgv.posclarity.api.SellerApiClient;
import com.gerardgv.posclarity.api.dto.auth.LoginRequest;
import com.gerardgv.posclarity.api.dto.auth.LoginResponse;
import com.gerardgv.posclarity.models.Role;
import com.gerardgv.posclarity.models.Seller;
import java.io.IOException;
import java.util.Optional;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.kordamp.ikonli.javafx.FontIcon;

public final class AuthorizationDialog {

    private AuthorizationDialog() {
    }

    public static Optional<Seller> solicitarGerente() {

        Dialog<Seller> dialog = new Dialog<>();

        dialog.setTitle("Acceso restringido");
        dialog.setHeaderText(null);

        ButtonType btnEntrar = new ButtonType(
                "Autorizar",
                ButtonBar.ButtonData.OK_DONE
        );

        DialogPane pane = dialog.getDialogPane();

        pane.getButtonTypes().addAll(
                btnEntrar,
                ButtonType.CANCEL
        );

        pane.getStyleClass().addAll(
                "pos-theme",
                "pos-dialog"
        );

        cargarCss(
                pane,
                "/com/gerardgv/posclarity/css/theme/PosTheme.css"
        );

        cargarCss(
                pane,
                "/com/gerardgv/posclarity/css/components/pos-textfield.css"
        );

        cargarCss(
                pane,
                "/com/gerardgv/posclarity/css/components/pos-dialog.css"
        );

        TextField txtCodigo = new TextField();
        
        
        
        txtCodigo.setPromptText("Código de empleado");
        txtCodigo.setMaxWidth(Double.MAX_VALUE);

        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Contraseña");
        txtPassword.setMaxWidth(Double.MAX_VALUE);

        Label lblError = new Label();
        lblError.setWrapText(true);
        lblError.getStyleClass().add("authorization-error");

        FontIcon lockIcon = new FontIcon("fas-lock");
        lockIcon.setIconSize(20);
        lockIcon.getStyleClass().add("authorization-lock-icon");

        StackPane iconBox = new StackPane(lockIcon);
        iconBox.getStyleClass().add("authorization-icon-box");

        Label lblTitulo = new Label("Autorización requerida");
        lblTitulo.getStyleClass().add("authorization-title");

        Label lblSubtitulo = new Label(
                "Ingresa las credenciales de un gerente o directivo."
        );
        lblSubtitulo.setWrapText(true);
        lblSubtitulo.getStyleClass().add("authorization-subtitle");

        VBox headerText = new VBox(
                2,
                lblTitulo,
                lblSubtitulo
        );

        HBox header = new HBox(
                12,
                iconBox,
                headerText
        );
        header.setAlignment(Pos.CENTER_LEFT);

        Label lblCodigo = new Label("Código de empleado");
        lblCodigo.getStyleClass().add("authorization-label");
        
        Label lblEmpleado = new Label();
        lblEmpleado.getStyleClass().add("authorization-user");

        Label lblRol = new Label();
        lblRol.getStyleClass().add("authorization-role");

        FontIcon userIcon = new FontIcon("fas-user-check");
        userIcon.setIconSize(18);
        userIcon.getStyleClass().add("authorization-user-icon");

        VBox datosEmpleado = new VBox(
            2,
            lblEmpleado,
            lblRol
        );

        HBox empleadoBox = new HBox(
            12,
            userIcon,
            datosEmpleado
        );

        empleadoBox.setAlignment(Pos.CENTER_LEFT);
        empleadoBox.getStyleClass().add("authorization-user-box");

        empleadoBox.setVisible(false);
        empleadoBox.setManaged(false);
        
        SellerApiClient sellerApi = new SellerApiClient();
        
        txtCodigo.textProperty().addListener((obs, oldValue, newValue) -> {

        empleadoBox.setVisible(false);
        empleadoBox.setManaged(false);

        lblEmpleado.setText("");
        lblRol.setText("");

        lblError.setText("");

        String codigoTexto = newValue == null
            ? ""
            : newValue.trim();

        if (codigoTexto.isEmpty()) {
            return;
        }

        try {

            int codigo = Integer.parseInt(codigoTexto);
            Seller empleado = sellerApi.getByCode(codigo);
                    
            if (empleado == null) {
                return;
            }

            lblEmpleado.setText(empleado.getName());

            lblRol.setText( empleado.getRole() != null 
                ? empleado.getRole().getDisplayName() : "" );

            empleadoBox.setManaged(true);
            empleadoBox.setVisible(true);
        } catch (NumberFormatException ex) {
        // Mientras escribe no mostramos error.
        
        } catch (IOException | InterruptedException ex){
            ex.printStackTrace();
        }
    });
        
        Label lblPassword = new Label("Contraseña");
        lblPassword.getStyleClass().add("authorization-label");

        Label lblNota = new Label(
                "Solo personal autorizado puede continuar."
        );
        lblNota.getStyleClass().add("authorization-note");

        VBox content = new VBox(
                8,
                header,
                new Separator(),
                lblCodigo,
                txtCodigo,
                empleadoBox,
                lblPassword,
                txtPassword,
                lblError,
                lblNota
        );

        content.getStyleClass().add("authorization-content");
        content.setPrefWidth(390);

        pane.setContent(content);

        Button botonEntrar =
                (Button) pane.lookupButton(btnEntrar);

        Button botonCancelar =
                (Button) pane.lookupButton(ButtonType.CANCEL);

        botonEntrar.setDefaultButton(true);
        botonCancelar.setCancelButton(true);

        botonEntrar.disableProperty().bind(
                txtCodigo.textProperty().isEmpty()
                        .or(txtPassword.textProperty().isEmpty())
        );

        botonEntrar.addEventFilter(
                ActionEvent.ACTION,
                event -> validarCredenciales(
                        event,
                        dialog,
                        txtCodigo,
                        txtPassword,
                        lblError
                )
        );

        dialog.setResultConverter(buttonType -> {

            if (buttonType == btnEntrar) {
                return dialog.getResult();
            }

            return null;
        });

        Platform.runLater(txtCodigo::requestFocus);

        return dialog.showAndWait();
    }

    private static void validarCredenciales(
            ActionEvent event,
            Dialog<Seller> dialog,
            TextField txtCodigo,
            PasswordField txtPassword,
            Label lblError) {

        lblError.setText("");

        String codigoTexto = txtCodigo.getText().trim();
        String password = txtPassword.getText();

        int codigo;

        try {
            codigo = Integer.parseInt(codigoTexto);

        } catch (NumberFormatException ex) {

            lblError.setText("El código debe contener solo números.");
            txtCodigo.requestFocus();
            event.consume();
            return;
        }
        
        try{
            
            AuthApiClient authApi = new AuthApiClient();
            
            LoginResponse response = authApi.login(
                new LoginRequest(codigo,password));
            
            Seller empleado = response.getSeller().toSeller();
            
            Role rol = empleado.getRole();
            
            boolean autorizado = rol == Role.MANAGER || rol == Role.DIRECTOR;
            
            if(!autorizado){
                lblError.setText("No cuentas con permisos de gerente o directivo.");
                txtPassword.clear();
                txtPassword.requestFocus();

                event.consume();
                return;
            }
            dialog.setResult(empleado);            
        } catch(Exception e){
            lblError.setText("Código o contraseña incorrectos.");
            txtPassword.clear();
            txtPassword.requestFocus();

            event.consume();
        }
        
    }

    private static void cargarCss(
            DialogPane pane,
            String recurso) {

        var url = AuthorizationDialog.class
                .getResource(recurso);

        if (url != null) {
            pane.getStylesheets()
                    .add(url.toExternalForm());
        } else {
            System.err.println(
                    "No se encontró el CSS: " + recurso
            );
        }
    }
    
    private static String formatearRol(String rol) {

    if (rol == null || rol.isBlank()) {
        return "";
    }

    String texto = rol
            .trim()
            .toLowerCase()
            .replace("_", " ");

    return Character.toUpperCase(texto.charAt(0))
            + texto.substring(1);
}
}