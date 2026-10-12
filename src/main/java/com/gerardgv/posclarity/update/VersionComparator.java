package com.gerardgv.posclarity.update;

public final class VersionComparator {

    private VersionComparator() {
    }

    /**
     * Compara dos versiones.
     *
     * @return
     * -1 si currentVersion < remoteVersion
     *  0 si son iguales
     *  1 si currentVersion > remoteVersion
     */
    public static int compare(
            String currentVersion,
            String remoteVersion
    ) {

        String current = normalize(currentVersion);
        String remote = normalize(remoteVersion);

        String[] currentParts = current.split("\\.");
        String[] remoteParts = remote.split("\\.");

        int length = Math.max(
                currentParts.length,
                remoteParts.length
        );

        for (int i = 0; i < length; i++) {

            int currentPart = i < currentParts.length
                    ? parsePart(currentParts[i])
                    : 0;

            int remotePart = i < remoteParts.length
                    ? parsePart(remoteParts[i])
                    : 0;

            if (currentPart < remotePart) {
                return -1;
            }

            if (currentPart > remotePart) {
                return 1;
            }
        }

        return 0;
    }

    private static String normalize(String version) {

        if (version == null) {
            return "0";
        }

        String normalized = version.trim();

        if (normalized.startsWith("v")
                || normalized.startsWith("V")) {

            normalized = normalized.substring(1);
        }

        return normalized;
    }

    private static int parsePart(String part) {

        try {
            return Integer.parseInt(part);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}