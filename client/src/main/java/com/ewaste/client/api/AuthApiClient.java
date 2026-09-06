package com.ewaste.client.api;

import com.ewaste.client.dto.request.LoginClientRequest;
import com.ewaste.client.dto.request.RegisterClientRequest;
import com.ewaste.client.dto.response.AuthClientResponse;

/**
 * Client for user authentication endpoints (/api/v1/auth).
 */
public class AuthApiClient extends ApiClient {

    private static final String BASE_PATH = "/api/v1/auth";

    public AuthClientResponse login(String email, String password) {
        LoginClientRequest request = new LoginClientRequest(email, password);
        return post(BASE_PATH + "/login", request, AuthClientResponse.class);
    }

    public AuthClientResponse register(RegisterClientRequest request) {
        return post(BASE_PATH + "/register", request, AuthClientResponse.class);
    }
}