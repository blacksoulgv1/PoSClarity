package com.gerardgv.posclarity.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {

    SELLER("VENDEDOR", "Vendedor"),
    MANAGER("GERENTE", "Gerente"),
    DIRECTOR("DIRECTIVO", "Directivo");

    private final String databaseValue;
    private final String displayName;

    Role(String databaseValue, String displayName) {
        this.databaseValue = databaseValue;
        this.displayName = displayName;
    }

    public String getDatabaseValue() {
        return databaseValue;
    }

    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static Role fromValue(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.trim().toUpperCase();

        return switch (normalized) {
            case "VENDEDOR", "SELLER" -> SELLER;
            case "GERENTE", "MANAGER" -> MANAGER;
            case "DIRECTIVO", "DIRECTOR" -> DIRECTOR;

            default -> throw new IllegalArgumentException(
                    "Unknown role: " + value
            );
        };
    }

    @JsonValue
    public String toJsonValue() {
        return name();
    }

    @Override
    public String toString() {
        return displayName;
    }
}