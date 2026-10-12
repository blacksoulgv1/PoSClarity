package com.gerardgv.posclarity.update;

import com.fasterxml.jackson.databind.JsonNode;
import com.gerardgv.posclarity.api.BaseApiClient;

import java.io.IOException;

public class UpdateService extends BaseApiClient {

    private static final String GITHUB_API =
            "https://api.github.com/repos/blacksoulgv1/PoSClarity/releases/latest";

    public UpdateService() {
        super();
    }

    /**
     * Consulta la última versión publicada en GitHub.
     */
    public UpdateInfo checkForUpdate()
            throws IOException, InterruptedException {

        JsonNode response = get(
                GITHUB_API,
                JsonNode.class
        );

        if (response == null) {
            throw new IOException(
                    "GitHub no devolvió información de actualización."
            );
        }

        String version =
                response.path("tag_name").asText();

        String name =
                response.path("name").asText();

        String releaseNotes =
                response.path("body").asText("");

        String downloadUrl = null;

        String sha256 = null;

        JsonNode assets = response.path("assets");

        if (assets.isArray()) {

            for (JsonNode asset : assets) {

                String assetName =
                        asset.path("name").asText();

                String browserDownloadUrl =
                        asset.path("browser_download_url").asText();

                String digest =
                        asset.path("digest").asText();

                if (assetName.toLowerCase().endsWith(".exe")) {

                    downloadUrl =
                            browserDownloadUrl;

                    sha256 =
                            extractSha256(digest);

                    break;
                }
            }
        }

        return new UpdateInfo(
                version,
                name,
                releaseNotes,
                downloadUrl,
                sha256
        );
    }

    /**
     * Extrae el SHA-256 del formato publicado por GitHub:
     *
     * sha256:xxxxxxxxxxxxxxxx
     */
    private String extractSha256(String digest) {

        if (digest == null
                || digest.isBlank()) {

            return null;
        }

        String prefix = "sha256:";

        if (digest.startsWith(prefix)) {

            return digest.substring(
                    prefix.length()
            );
        }

        return digest;
    }

    /**
     * Información de la última versión publicada.
     */
    public static class UpdateInfo {

        private final String version;

        private final String name;

        private final String releaseNotes;

        private final String downloadUrl;

        private final String sha256;

        public UpdateInfo(
                String version,
                String name,
                String releaseNotes,
                String downloadUrl,
                String sha256
        ) {

            this.version =
                    version;

            this.name =
                    name;

            this.releaseNotes =
                    releaseNotes;

            this.downloadUrl =
                    downloadUrl;

            this.sha256 =
                    sha256;
        }

        public String getVersion() {
            return version;
        }

        public String getName() {
            return name;
        }

        public String getReleaseNotes() {
            return releaseNotes;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public String getSha256() {
            return sha256;
        }
    }
}