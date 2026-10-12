package com.gerardgv.posclarity.update;

public class UpdateChecker {

    private final UpdateService updateService;

    public UpdateChecker() {
        this.updateService = new UpdateService();
    }

    /**
     * Consulta GitHub y determina si existe una versión
     * superior a la versión instalada.
     *
     * @return UpdateResult con la información disponible.
     */
    public UpdateResult check()
            throws Exception {

        String currentVersion =
                AppVersion.getCurrentVersion();

        UpdateService.UpdateInfo remoteInfo =
                updateService.checkForUpdate();

        String remoteVersion =
                remoteInfo.getVersion();

        int comparison =
                VersionComparator.compare(
                        currentVersion,
                        remoteVersion
                );

        boolean updateAvailable =
                comparison < 0;

        return new UpdateResult(
                currentVersion,
                remoteVersion,
                updateAvailable,
                remoteInfo
        );
    }

    /**
     * Resultado de la comprobación de actualización.
     */
    public static class UpdateResult {

        private final String currentVersion;
        private final String remoteVersion;
        private final boolean updateAvailable;
        private final UpdateService.UpdateInfo updateInfo;

        public UpdateResult(
                String currentVersion,
                String remoteVersion,
                boolean updateAvailable,
                UpdateService.UpdateInfo updateInfo
        ) {
            this.currentVersion = currentVersion;
            this.remoteVersion = remoteVersion;
            this.updateAvailable = updateAvailable;
            this.updateInfo = updateInfo;
        }

        public String getCurrentVersion() {
            return currentVersion;
        }

        public String getRemoteVersion() {
            return remoteVersion;
        }

        public boolean isUpdateAvailable() {
            return updateAvailable;
        }

        public UpdateService.UpdateInfo getUpdateInfo() {
            return updateInfo;
        }
    }
}