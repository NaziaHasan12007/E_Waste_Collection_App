package com.ewaste.client.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * API configuration manager
 */
public class ApiConfig {

    private static ApiConfig instance;
    private final Properties properties = new Properties();

    private String baseUrl;
    private int connectionTimeout;
    private int readTimeout;

    private ApiConfig() {
        loadConfig();
    }

    public static ApiConfig getInstance() {
        if (instance == null) {
            instance = new ApiConfig();
        }
        return instance;
    }

    private void loadConfig() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application-client.properties")) {
            if (input != null) {
                properties.load(input);
                baseUrl = properties.getProperty("api.base.url", "http://localhost:8080/api/v1");
                connectionTimeout = Integer.parseInt(properties.getProperty("api.connection.timeout", "30000"));
                readTimeout = Integer.parseInt(properties.getProperty("api.read.timeout", "30000"));
            } else {
                // Default values
                baseUrl = "http://localhost:8080/api/v1";
                connectionTimeout = 30000;
                readTimeout = 30000;
            }
        } catch (IOException e) {
            System.err.println("Failed to load API config: " + e.getMessage());
            // Default values
            baseUrl = "http://localhost:8080/api/v1";
            connectionTimeout = 30000;
            readTimeout = 30000;
        }
    }

    public String getAuthEndpoint() {
        return baseUrl + "/auth";
    }

    public String getLoginEndpoint() {
        return baseUrl + "/auth/login";
    }

    public String getRegisterEndpoint() {
        return baseUrl + "/auth/register";
    }

    public String getValidateTokenEndpoint() {
        return baseUrl + "/auth/validate";
    }

    public String getEWasteItemsEndpoint() {
        return baseUrl + "/ewaste/items";
    }

    public String getEWasteCategoriesEndpoint() {
        return baseUrl + "/ewaste/categories";
    }

    public String getPickupsEndpoint() {
        return baseUrl + "/pickups";
    }

    public String getPickupByIdEndpoint(Long pickupId) {
        return baseUrl + "/pickups/" + pickupId;
    }

    public String getNotificationsEndpoint(Long userId) {
        return baseUrl + "/notifications/user/" + userId;
    }

    public String getRewardsEndpoint(Long customerId) {
        return baseUrl + "/rewards/" + customerId;
    }

    public String getRewardRedemptionEndpoint(Long customerId) {
        return getRewardsEndpoint(customerId) + "/redeem";
    }

    public String getReportsSummaryEndpoint() {
        return baseUrl + "/reports/summary";
    }

    public String getReportsPickupsEndpoint() {
        return baseUrl + "/reports/pickups";
    }

    public String getReportsProcessingEndpoint() {
        return baseUrl + "/reports/processing";
    }

    public String getCollectorPerformanceEndpoint(Long collectorId) {
        return baseUrl + "/reports/collector/" + collectorId;
    }

    public String getCollectorEndpoint(Long collectorId) {
        return baseUrl + "/collectors/" + collectorId;
    }

    public String getCollectorsEndpoint() {
        return baseUrl + "/collectors";
    }

    public String getBaseUrl() {
        return getInstance().baseUrl;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }
    /**
     * Get processing base endpoint
     */
    public String getProcessingEndpoint() {
        return baseUrl + "/processing";
    }

    /**
     * Get processing records endpoint for a pickup
     */
    public String getProcessingRecordsEndpoint(long pickupId) {
        return baseUrl + "/processing/records/" + pickupId;
    }

    /**
     * Get recycling centers endpoint
     */
    public String getRecyclingCentersEndpoint() {
        return baseUrl + "/processing/centers";
    }

    /**
     * Get process item endpoint
     */
    public String getProcessItemEndpoint(long pickupId) {
        return baseUrl + "/processing/" + pickupId + "/process";
    }
}