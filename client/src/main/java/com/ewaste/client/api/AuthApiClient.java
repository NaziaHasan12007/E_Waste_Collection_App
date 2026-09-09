package com.ewaste.client.api;

import com.ewaste.client.config.ApiConfig;
import com.ewaste.client.dto.request.LoginClientRequest;
import com.ewaste.client.dto.request.RegisterClientRequest;
import com.ewaste.client.dto.response.AuthClientResponse;
import com.ewaste.client.session.UserSession;

/**
 * Client for user authentication endpoints (/api/v1/auth).
 */
public class AuthApiClient extends ApiClient {

    private static AuthApiClient instance;
    private AuthApiClient() {
        super();
    }

    public static AuthApiClient getInstance() {
        if (instance == null) {
            instance = new AuthApiClient();
        }
        return instance;
    }

    public AuthClientResponse login(String email, String password) {
        LoginClientRequest request = new LoginClientRequest(email, password);
        return post(ApiConfig.getInstance().getLoginEndpoint(), request, AuthClientResponse.class);
    }

    public AuthClientResponse login(LoginClientRequest request) {
        return post(ApiConfig.getInstance().getLoginEndpoint(), request, AuthClientResponse.class);
    }

    public AuthClientResponse register(RegisterClientRequest request) {
        return post(ApiConfig.getInstance().getRegisterEndpoint(), request, AuthClientResponse.class);
    }

    // ========== ADDITIONAL HELPER METHODS ==========

    public boolean validateToken(String token) {
        try {
            String endpoint = ApiConfig.getInstance().getValidateTokenEndpoint();
            String currentToken = UserSession.getInstance().getToken();
            UserSession.getInstance().setToken(token);
            try {
                get(endpoint, Void.class);
                return true;
            } finally {
                UserSession.getInstance().setToken(currentToken);
            }
        } catch (Exception e) {
            return false;
        }
    }

    public void logout() {
        UserSession.getInstance().endSession();
    }

    public boolean isAuthenticated() {
        return UserSession.getInstance().isAuthenticated();
    }

    public AuthClientResponse refreshToken() {
        // Implementation for token refresh if needed
        String currentToken = UserSession.getInstance().getToken();
        if (currentToken == null || currentToken.isBlank()) {
            throw new RuntimeException("No token to refresh");
        }
        // Add refresh endpoint call here
        return null;
    }
}