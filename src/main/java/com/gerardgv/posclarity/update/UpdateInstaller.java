package com.gerardgv.posclarity.update;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class UpdateInstaller {

    /**
     * Crea un script externo que:
     *
     * 1. Espera a que PoSClarity se cierre.
     * 2. Ejecuta el instalador descargado.
     * 3. Espera a que termine la instalación.
     * 4. Vuelve a iniciar PoSClarity.
     *
     * @param installerPath instalador descargado
     * @param processId PID de PoSClarity
     * @param applicationPath ruta de PoSClarity.exe
     * @return ruta del script creado
     */
    public Path createUpdateScript(
            Path installerPath,
            long processId,
            Path applicationPath
    ) throws IOException {

        validateInstaller(installerPath);
        validateApplication(applicationPath);

        Path scriptDirectory =
                Files.createTempDirectory(
                        "PoSClarity-Updater-"
                );

        Path script =
                scriptDirectory.resolve(
                        "update.cmd"
                );

        String installer =
                installerPath.toAbsolutePath()
                        .toString();

        String application =
                applicationPath.toAbsolutePath()
                        .toString();

        String scriptContent =
                "@echo off\r\n"
                + "setlocal\r\n"
                + "\r\n"
                + "echo Esperando a que PoSClarity se cierre...\r\n"
                + "\r\n"
                + ":WAIT_FOR_APP\r\n"
                + "tasklist /FI \"PID eq "
                + processId
                + "\" | findstr /R /C:\" "
                + processId
                + " \" >nul\r\n"
                + "\r\n"
                + "if not errorlevel 1 (\r\n"
                + "    timeout /t 1 /nobreak >nul\r\n"
                + "    goto WAIT_FOR_APP\r\n"
                + ")\r\n"
                + "\r\n"
                + "echo Ejecutando instalador...\r\n"
                + "\r\n"
                + "start /wait \"\" \""
                + installer
                + "\"\r\n"
                + "\r\n"
                + "echo Instalacion terminada.\r\n"
                + "\r\n"
                + "timeout /t 2 /nobreak >nul\r\n"
                + "\r\n"
                + "echo Iniciando PoSClarity...\r\n"
                + "\r\n"
                + "start \"\" \""
                + application
                + "\"\r\n"
                + "\r\n"
                + "endlocal\r\n"
                + "exit /b 0\r\n";

        Files.writeString(
                script,
                scriptContent,
                StandardCharsets.UTF_8
        );

        return script;
    }

    /**
     * Inicia el script externo.
     */
    public void launchScript(
            Path script
    ) throws IOException {

        if (script == null
                || !Files.exists(script)) {

            throw new IOException(
                    "El script no existe: "
                    + script
            );
        }

        new ProcessBuilder(
                "cmd.exe",
                "/c",
                "start",
                "\"\"",
                "\"" + script.toAbsolutePath() + "\""
        ).start();
    }

    /**
     * Crea el script y lo inicia.
     */
    public void install(
            Path installerPath,
            long processId,
            Path applicationPath
    ) throws IOException {

        Path script =
                createUpdateScript(
                        installerPath,
                        processId,
                        applicationPath
                );

        launchScript(script);
    }

    private void validateInstaller(
            Path installerPath
    ) throws IOException {

        if (installerPath == null
                || !Files.exists(installerPath)) {

            throw new IOException(
                    "El instalador no existe: "
                    + installerPath
            );
        }

        if (!Files.isRegularFile(installerPath)) {

            throw new IOException(
                    "La ruta del instalador no corresponde "
                    + "a un archivo."
            );
        }

        if (!installerPath.toString()
                .toLowerCase()
                .endsWith(".exe")) {

            throw new IOException(
                    "El instalador debe ser un archivo .exe."
            );
        }
    }

    private void validateApplication(
            Path applicationPath
    ) throws IOException {

        if (applicationPath == null
                || !Files.exists(applicationPath)) {

            throw new IOException(
                    "PoSClarity.exe no existe: "
                    + applicationPath
            );
        }

        if (!Files.isRegularFile(applicationPath)) {

            throw new IOException(
                    "La ruta de PoSClarity no corresponde "
                    + "a un archivo."
            );
        }
    }
}