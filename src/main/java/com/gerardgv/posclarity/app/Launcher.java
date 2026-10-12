package com.gerardgv.posclarity.app;

import com.gerardgv.posclarity.update.UpdateChecker;
import com.gerardgv.posclarity.update.UpdateChecker.UpdateResult;
import com.gerardgv.posclarity.update.UpdateChecksum;
import com.gerardgv.posclarity.update.UpdateDownloader;
import com.gerardgv.posclarity.update.UpdateInstaller;
import com.gerardgv.posclarity.update.UpdateService;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class Launcher {

    private static UpdateResult updateResult;
    private static Exception updateCheckException;

    public static void main(String[] args) {

        try {

            UpdateChecker checker =
                    new UpdateChecker();

            updateResult =
                    checker.check();

            System.out.println(
                    "Versión instalada: "
                    + updateResult.getCurrentVersion()
            );

            System.out.println(
                    "Versión remota: "
                    + updateResult.getRemoteVersion()
            );

            System.out.println(
                    "Actualización disponible: "
                    + updateResult.isUpdateAvailable()
            );

        } catch (Exception e) {

            updateCheckException = e;

            System.err.println(
                    "No fue posible comprobar actualizaciones."
            );

            e.printStackTrace();
        }

        Application.launch(
                UpdateApplication.class,
                args
        );
    }

    public static class UpdateApplication
            extends Application {

        @Override
        public void start(Stage stage) {

            try {

                if (updateCheckException != null) {

                    System.out.println(
                            "Continuando con PoSClarity "
                            + "sin comprobación de actualización."
                    );

                    launchApp(stage);

                    return;
                }

                if (updateResult != null
                        && updateResult.isUpdateAvailable()) {

                    showUpdateDialog(
                            stage,
                            updateResult
                    );

                    return;
                }

                System.out.println(
                        "PoSClarity está actualizado."
                );

                launchApp(stage);

            } catch (Exception e) {

                System.err.println(
                        "Error al iniciar PoSClarity."
                );

                e.printStackTrace();

                launchApp(stage);
            }
        }

        private void showUpdateDialog(
                Stage stage,
                UpdateResult result
        ) {

            Alert alert =
                    new Alert(
                            Alert.AlertType.INFORMATION
                    );

            alert.setTitle(
                    "Actualización disponible"
            );

            alert.setHeaderText(
                    "Hay una nueva versión de PoSClarity"
            );

            String releaseNotes =
                    result.getUpdateInfo()
                            .getReleaseNotes();

            StringBuilder message =
                    new StringBuilder();

            message.append(
                    "Versión instalada: "
            );

            message.append(
                    result.getCurrentVersion()
            );

            message.append("\n");

            message.append(
                    "Nueva versión: "
            );

            message.append(
                    result.getRemoteVersion()
            );

            if (releaseNotes != null
                    && !releaseNotes.isBlank()) {

                message.append(
                        "\n\nNotas de la versión:\n"
                );

                message.append(
                        releaseNotes
                );
            }

            message.append(
                    "\n\n¿Deseas actualizar PoSClarity?"
            );

            alert.setContentText(
                    message.toString()
            );

            ButtonType actualizar =
                    new ButtonType(
                            "Actualizar"
                    );

            ButtonType masTarde =
                    new ButtonType(
                            "Más tarde"
                    );

            alert.getButtonTypes().setAll(
                    actualizar,
                    masTarde
            );

            Optional<ButtonType> respuesta =
                    alert.showAndWait();

            if (respuesta.isPresent()
                    && respuesta.get() == actualizar) {

                System.out.println(
                        "Usuario seleccionó actualizar."
                );

                try {

                    performUpdate(
                            result
                    );

                } catch (Exception e) {

                    System.err.println(
                            "No fue posible realizar "
                            + "la actualización."
                    );

                    e.printStackTrace();

                    showUpdateError();

                    launchApp(stage);
                }

            } else {

                System.out.println(
                        "Usuario seleccionó Más tarde."
                );

                launchApp(stage);
            }
        }

        private void launchApp(
                Stage stage
        ) {

            try {

                App app =
                        new App();

                app.start(stage);

            } catch (Exception e) {

                e.printStackTrace();

                Platform.exit();
            }
        }

        private void performUpdate(
                UpdateResult result
        ) throws Exception {

            UpdateService.UpdateInfo info =
                    result.getUpdateInfo();

            if (info.getDownloadUrl() == null
                    || info.getDownloadUrl().isBlank()) {

                throw new Exception(
                        "GitHub no proporcionó "
                        + "una URL de descarga."
                );
            }

            if (info.getSha256() == null
                    || info.getSha256().isBlank()) {

                throw new Exception(
                        "GitHub no proporcionó "
                        + "un SHA-256."
                );
            }

            System.out.println(
                    "Descargando actualización..."
            );

            Path updateDirectory =
                    Files.createTempDirectory(
                            "PoSClarity-Update-"
                    );

            Path installer =
                    updateDirectory.resolve(
                            "PoSClarity-"
                            + info.getVersion()
                            + ".exe"
                    );

            UpdateDownloader downloader =
                    new UpdateDownloader();

            downloader.download(
                    info.getDownloadUrl(),
                    installer
            );

            System.out.println(
                    "Descarga completada."
            );

            System.out.println(
                    "Verificando SHA-256..."
            );

            boolean valid =
                    UpdateChecksum.verify(
                            installer,
                            info.getSha256()
                    );

            if (!valid) {

                Files.deleteIfExists(
                        installer
                );

                throw new Exception(
                        "El SHA-256 del instalador "
                        + "no coincide."
                );
            }

            System.out.println(
                    "SHA-256 válido."
            );

            Path application =
                    findApplicationPath();

            long processId =
                    ProcessHandle.current().pid();

            System.out.println(
                    "PID actual: "
                    + processId
            );

            System.out.println(
                    "Aplicación: "
                    + application
            );

            UpdateInstaller updateInstaller =
                    new UpdateInstaller();

            Path script =
                    updateInstaller.createUpdateScript(
                            installer,
                            processId,
                            application
                    );

            System.out.println(
                    "Script de actualización:"
            );

            System.out.println(
                    script
            );

            updateInstaller.launchScript(
                    script
            );

            System.out.println(
                    "Actualizador externo iniciado."
            );

            System.out.println(
                    "Cerrando PoSClarity..."
            );

            Platform.exit();

            System.exit(0);
        }

        private Path findApplicationPath()
                throws Exception {

            String javaHome =
                    System.getProperty(
                            "java.home"
                    );

            if (javaHome == null
                    || javaHome.isBlank()) {

                throw new Exception(
                        "No fue posible determinar "
                        + "JAVA_HOME."
                );
            }

            Path runtime =
                    Path.of(javaHome);

            Path installationDirectory =
                    runtime.getParent();

            if (installationDirectory == null) {

                throw new Exception(
                        "No fue posible determinar "
                        + "el directorio de instalación."
                );
            }

            Path application =
                    installationDirectory.resolve(
                            "PoSClarity.exe"
                    );

            if (!Files.exists(application)) {

                throw new Exception(
                        "No se encontró PoSClarity.exe en: "
                        + application
                );
            }

            return application;
        }

        private void showUpdateError() {

            Alert alert =
                    new Alert(
                            Alert.AlertType.ERROR
                    );

            alert.setTitle(
                    "Error de actualización"
            );

            alert.setHeaderText(
                    "No fue posible actualizar PoSClarity"
            );

            alert.setContentText(
                    "El sistema continuará iniciando "
                    + "con la versión actual."
            );

            alert.showAndWait();
        }
    }
}