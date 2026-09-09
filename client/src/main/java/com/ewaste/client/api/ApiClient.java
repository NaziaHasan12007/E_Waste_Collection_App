package com.ewaste.client.api;

import com.ewaste.client.config.ApiConfig;
import com.ewaste.client.dto.request.LoginClientRequest;
import com.ewaste.client.dto.response.AuthClientResponse;
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

        this.baseUrl = ApiConfig.getInstance().getBaseUrl();
    }

    // ========== SPECIFIC API METHODS ==========

    /**
     * Login user with email and password
     */
    public AuthClientResponse login(LoginClientRequest request) {
        String endpoint = ApiConfig.getInstance().getLoginEndpoint();
        return post(endpoint, request, AuthClientResponse.class);
    }

    /**
     * Register a new user
     */
    public AuthClientResponse register(Object registerRequest) {
        String endpoint = ApiConfig.getInstance().getRegisterEndpoint();
        return post(endpoint, registerRequest, AuthClientResponse.class);
    }

    /**
     * Validate JWT token
     */
    public boolean validateToken(String token) {
        try {
            String endpoint = ApiConfig.getInstance().getValidateTokenEndpoint();
            // Temporarily set token for validation
            String currentToken = UserSession.getInstance().getToken();
            UserSession.getInstance().setToken(token);
            try {
                get(endpoint, Void.class);
                return true;
            } finally {
                // Restore previous token
                UserSession.getInstance().setToken(currentToken);
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get e-waste items
     */
    public <T> T getEWasteItems(Class<T> responseType) {
        String endpoint = ApiConfig.getInstance().getEWasteItemsEndpoint();
        return get(endpoint, responseType);
    }

    /**
     * Get e-waste categories
     */
    public <T> T getEWasteCategories(Class<T> responseType) {
        String endpoint = ApiConfig.getInstance().getEWasteCategoriesEndpoint();
        return get(endpoint, responseType);
    }

    /**
     * Get all pickups
     */
    public <T> T getPickups(Class<T> responseType) {
        String endpoint = ApiConfig.getInstance().getPickupsEndpoint();
        return get(endpoint, responseType);
    }

    /**
     * Get pickup by ID
     */
    public <T> T getPickupById(Long pickupId, Class<T> responseType) {
        String endpoint = ApiConfig.getInstance().getPickupByIdEndpoint(pickupId);
        return get(endpoint, responseType);
    }

    /**
     * Create a new pickup
     */
    public <T> T createPickup(Object pickupRequest, Class<T> responseType) {
        String endpoint = ApiConfig.getInstance().getPickupsEndpoint();
        return post(endpoint, pickupRequest, responseType);
    }

    /**
     * Update pickup status
     */
    public <T> T updatePickup(Long pickupId, Object updateRequest, Class<T> responseType) {
        String endpoint = ApiConfig.getInstance().getPickupByIdEndpoint(pickupId);
        return patch(endpoint, updateRequest, responseType);
    }

    /**
     * Get notifications for a user
     */
    public <T> T getNotifications(Long userId, Class<T> responseType) {
        String endpoint = ApiConfig.getInstance().getNotificationsEndpoint(userId);
        return get(endpoint, responseType);
    }

    /**
     * Get rewards for a customer
     */
    public <T> T getRewards(Long customerId, Class<T> responseType) {
        String endpoint = ApiConfig.getInstance().getRewardsEndpoint(customerId);
        return get(endpoint, responseType);
    }

    /**
     * Get reports summary (admin)
     */
    public <T> T getReportsSummary(Class<T> responseType) {
        String endpoint = ApiConfig.getInstance().getReportsSummaryEndpoint();
        return get(endpoint, responseType);
    }

    /**
     * Get pickups reports (admin)
     */
    public <T> T getReportsPickups(Class<T> responseType) {
        String endpoint = ApiConfig.getInstance().getReportsPickupsEndpoint();
        return get(endpoint, responseType);
    }

    /**
     * Get collector performance
     */
    public <T> T getCollectorPerformance(Long collectorId, Class<T> responseType) {
        String endpoint = ApiConfig.getInstance().getCollectorPerformanceEndpoint(collectorId);
        return get(endpoint, responseType);
    }

    // ========== GENERIC HTTP METHODS ==========

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

    // ========== HELPER METHODS ==========

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
                if (node.has("error")) {
                    return node.get("error").asText();
                }
            } catch (Exception ignored) {
            }
        }
        return "HTTP Error " + status;
    }
}