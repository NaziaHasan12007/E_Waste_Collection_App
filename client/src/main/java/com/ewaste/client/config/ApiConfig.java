package com.ewaste.client.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Reads HTTP API endpoint settings, base URLs, and timeout configurations.
 */
public final class ApiConfig {

    private static final Properties properties = new Properties();
    private static final String DEFAULT_BASE_URL = "http://localhost:8080/api/v1";
    private static final int DEFAULT_TIMEOUT_SECONDS = 15;

    public static final int REQUEST_TIMEOUT_SECONDS;
    private static final String BASE_URL;

    static {
        try (InputStream is = ApiConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (is != null) {
                properties.load(is);
            }
        } catch (IOException ignored) {
            // Fall back to defaults if not found
        }

        String envUrl = System.getenv("API_BASE_URL");
        if (envUrl != null && !envUrl.isBlank()) {
            BASE_URL = envUrl.replaceAll("/+$", "");
        } else {
            BASE_URL = properties.getProperty("api.base.url", DEFAULT_BASE_URL).replaceAll("/+$", "");
        }

        int timeout = DEFAULT_TIMEOUT_SECONDS;
        String timeoutProp = properties.getProperty("api.timeout.seconds");
        if (timeoutProp != null) {
            try {
                timeout = Integer.parseInt(timeoutProp);
            } catch (NumberFormatException ignored) {
                timeout = DEFAULT_TIMEOUT_SECONDS;
            }
        }
        REQUEST_TIMEOUT_SECONDS = timeout;
    }

    private ApiConfig() {
        // Prevent instantiation
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }
}