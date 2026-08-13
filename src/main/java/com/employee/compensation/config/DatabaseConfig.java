package com.employee.compensation.config;

public class DatabaseConfig {

    private DatabaseConfig() {
    }

    public static String getConnectionUrl() {

        String value = System.getenv("DB_CONNECTION_URL");

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "DB_CONNECTION_URL environment variable is not configured."
            );
        }

        return value;
    }

    public static String getUsername() {

        String value = System.getenv("DB_USERNAME");

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "DB_USERNAME environment variable is not configured."
            );
        }

        return value;
    }

    public static String getPassword() {

        String value = System.getenv("DB_PASSWORD");

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "DB_PASSWORD environment variable is not configured."
            );
        }

        return value;
    }
}
