package com.ewaste.client.config;

import com.ewaste.client.api.ApiClient;
import com.ewaste.client.api.ApiClientImpl;
import com.ewaste.client.session.UserSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Application context container for shared components
 */
public class ClientContext {

    private static ClientContext instance;

    private ApiClient apiClient;
    private ObjectMapper objectMapper;
    private UserSession userSession;

    private ClientContext() {
        initialize();
    }

    public static ClientContext getInstance() {
        if (instance == null) {
            instance = new ClientContext();
        }
        return instance;
    }

    private void initialize() {
        // Initialize ObjectMapper
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Initialize UserSession
        userSession = UserSession.getInstance();

        // Initialize ApiClient
        apiClient = new ApiClientImpl();
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void reset() {
        userSession.endSession();
        // Re-initialize API client if needed
    }
}