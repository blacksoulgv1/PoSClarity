package com.gerardgv.posclarity.update;

import java.nio.file.Path;

public class UpdateInstallerTest {

    public static void main(String[] args) {

        try {

            System.out.println(
                    "=== PRUEBA DE UPDATE INSTALLER ==="
            );

            UpdateService service =
                    new UpdateService();

            UpdateService.UpdateInfo info =
                    service.checkForUpdate();

            System.out.println(
                    "Versión remota: "
                    + info.getVersion()
            );

            System.out.println(
                    "URL: "
                    + info.getDownloadUrl()
            );

            Path installer =
                    Path.of(
                            "C:\\Users\\Clarity\\AppData\\Local\\Temp\\PoSClarity-UpdateTest-3797349083383008108\\PoSClarity-v1.0.1.exe"
                    );

            Path application =
                    Path.of(
                            "C:\\Program Files\\PoSClarity\\PoSClarity.exe"
                    );

            long pid =
                    ProcessHandle.current().pid();

            System.out.println(
                    "PID actual: "
                    + pid
            );

            System.out.println(
                    "Instalador:"
            );

            System.out.println(
                    installer
            );

            System.out.println(
                    "Aplicación:"
            );

            System.out.println(
                    application
            );

            UpdateInstaller updateInstaller =
                    new UpdateInstaller();

            Path script =
                    updateInstaller.createUpdateScript(
                            installer,
                            pid,
                            application
                    );

            System.out.println(
                    "Script creado:"
            );

            System.out.println(
                    script
            );

            System.out.println(
                    "=== PRUEBA TERMINADA ==="
            );

        } catch (Exception e) {

            System.err.println(
                    "ERROR EN LA PRUEBA"
            );

            e.printStackTrace();
        }
    }
}