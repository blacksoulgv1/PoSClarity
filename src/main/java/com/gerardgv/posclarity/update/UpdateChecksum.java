package com.gerardgv.posclarity.update;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class UpdateChecksum {

    private UpdateChecksum() {
    }

    /**
     * Calcula el SHA-256 de un archivo.
     *
     * @param file archivo a verificar
     * @return SHA-256 en hexadecimal
     */
    public static String calculateSha256(
            Path file
    ) throws IOException {

        if (file == null) {
            throw new IOException(
                    "El archivo es nulo."
            );
        }

        if (!Files.exists(file)) {
            throw new IOException(
                    "El archivo no existe: "
                    + file
            );
        }

        if (!Files.isRegularFile(file)) {
            throw new IOException(
                    "La ruta no corresponde a un archivo: "
                    + file
            );
        }

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            try (InputStream input =
                         Files.newInputStream(file)) {

                byte[] buffer =
                        new byte[8192];

                int bytesRead;

                while ((bytesRead =
                        input.read(buffer)) != -1) {

                    digest.update(
                            buffer,
                            0,
                            bytesRead
                    );
                }
            }

            return toHex(digest.digest());

        } catch (NoSuchAlgorithmException e) {

            throw new IllegalStateException(
                    "SHA-256 no está disponible.",
                    e
            );
        }
    }

    /**
     * Verifica que el SHA-256 del archivo coincida
     * con el valor esperado.
     *
     * @param file archivo descargado
     * @param expectedSha256 SHA-256 esperado
     * @return true si coincide
     */
    public static boolean verify(
            Path file,
            String expectedSha256
    ) throws IOException {

        if (expectedSha256 == null
                || expectedSha256.isBlank()) {

            throw new IOException(
                    "No se recibió un SHA-256 esperado."
            );
        }

        String actualSha256 =
                calculateSha256(file);

        return actualSha256.equalsIgnoreCase(
                expectedSha256.trim()
        );
    }

    /**
     * Convierte los bytes del hash a hexadecimal.
     */
    private static String toHex(
            byte[] bytes
    ) {

        StringBuilder result =
                new StringBuilder(
                        bytes.length * 2
                );

        for (byte value : bytes) {

            result.append(
                    String.format(
                            "%02x",
                            value
                    )
            );
        }

        return result.toString();
    }
}