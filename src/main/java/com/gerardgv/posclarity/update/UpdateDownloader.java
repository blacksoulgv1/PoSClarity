package com.gerardgv.posclarity.update;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

public class UpdateDownloader {

    private final HttpClient httpClient;

    public UpdateDownloader() {

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .followRedirects(
                        HttpClient.Redirect.NORMAL
                )
                .build();
    }

    /**
     * Descarga el instalador de una actualización.
     *
     * @param downloadUrl URL del instalador.
     * @param destination Ruta donde se guardará.
     * @return archivo descargado.
     */
    public Path download(
            String downloadUrl,
            Path destination
    ) throws IOException, InterruptedException {

        if (downloadUrl == null
                || downloadUrl.isBlank()) {

            throw new IOException(
                    "La URL de descarga está vacía."
            );
        }

        if (destination == null) {

            throw new IOException(
                    "La ruta de destino es inválida."
            );
        }

        Path parent =
                destination.getParent();

        if (parent != null) {

            Files.createDirectories(parent);
        }

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(downloadUrl))
                        .timeout(
                                Duration.ofMinutes(10)
                        )
                        .header(
                                "User-Agent",
                                "PoSClarity-Updater"
                        )
                        .GET()
                        .build();

        HttpResponse<InputStream> response =
                httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofInputStream()
                );

        int statusCode =
                response.statusCode();

        if (statusCode < 200
                || statusCode >= 300) {

            response.body().close();

            throw new IOException(
                    "Error al descargar actualización. "
                    + "HTTP "
                    + statusCode
            );
        }

        try (InputStream input =
                     response.body();
             OutputStream output =
                     Files.newOutputStream(destination)) {

            input.transferTo(output);
        }

        if (!Files.exists(destination)) {

            throw new IOException(
                    "El instalador no fue descargado."
            );
        }

        if (Files.size(destination) == 0) {

            Files.deleteIfExists(destination);

            throw new IOException(
                    "El instalador descargado está vacío."
            );
        }

        return destination;
    }
}