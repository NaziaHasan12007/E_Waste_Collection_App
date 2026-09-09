package com.ewaste.client.api;

import com.ewaste.client.config.ApiConfig;
import com.ewaste.client.session.UserSession;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Base asynchronous HTTP client proxy abstracting java.net.http.HttpClient.
 * Handles automatic JWT header injection, centralized timeout policies,
 * and JSON serialization/deserialization.
 */
public abstract class ApiClient {

    protected final HttpClient httpClient;
    protected final ObjectMapper objectMapper;
    protected final String baseUrl;

    protected ApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        this.baseUrl = ApiConfig.getBaseUrl();
    }

    protected <T> T get(String endpoint, Class<T> responseType) {
        HttpRequest request = buildRequest(endpoint)
                .GET()
                .build();
        return send(request, responseType);
    }

    protected <T> T get(String endpoint, TypeReference<T> responseType) {
        HttpRequest request = buildRequest(endpoint)
                .GET()
                .build();
        return send(request, responseType);
    }

    protected <T> T post(String endpoint, Object body, Class<T> responseType) {
        try {
            String json = body != null ? objectMapper.writeValueAsString(body) : "";
            HttpRequest request = buildRequest(endpoint)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            return send(request, responseType);
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize request body", e);
        }
    }

    protected <T> T patch(String endpoint, Object body, Class<T> responseType) {
        try {
            String json = body != null ? objectMapper.writeValueAsString(body) : "";
            HttpRequest request = buildRequest(endpoint)
                    .header("Content-Type", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                    .build();
            return send(request, responseType);
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize request body", e);
        }
    }

    protected <T> T delete(String endpoint, Class<T> responseType) {
        HttpRequest request = buildRequest(endpoint)
                .DELETE()
                .build();
        return send(request, responseType);
    }

    private HttpRequest.Builder buildRequest(String endpoint) {
        String fullUrl = endpoint.startsWith("http") ? endpoint : baseUrl + endpoint;
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .timeout(Duration.ofSeconds(15))
                .header("Accept", "application/json");

        String token = UserSession.getInstance().getToken();
        if (token != null && !token.isBlank()) {
            builder.header("Authorization", "Bearer " + token);
        }

        return builder;
    }

    private <T> T send(HttpRequest request, Class<T> responseType) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            validateStatus(response);
            if (responseType == Void.class || response.body() == null || response.body().isBlank()) {
                return null;
            }
            return objectMapper.readValue(response.body(), responseType);
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Network communication failure during HTTP request", e);
        }
    }

    private <T> T send(HttpRequest request, TypeReference<T> responseType) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            validateStatus(response);
            if (response.body() == null || response.body().isBlank()) {
                return null;
            }
            return objectMapper.readValue(response.body(), responseType);
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Network communication failure during HTTP request", e);
        }
    }

    private void validateStatus(HttpResponse<String> response) {
        int status = response.statusCode();
        if (status >= 400) {
            String message = extractErrorMessage(response.body(), status);
            throw new RuntimeException(message);
        }
    }

    private String extractErrorMessage(String body, int status) {
        if (body != null && !body.isBlank()) {
            try {
                var node = objectMapper.readTree(body);
                if (node.has("message")) {
                    return node.get("message").asText();
                }
            } catch (Exception ignored) {
            }
        }
        return "HTTP Error " + status;
    }
}