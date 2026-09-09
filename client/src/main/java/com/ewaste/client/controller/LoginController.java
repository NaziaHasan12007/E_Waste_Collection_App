package com.ewaste.client.controller;

import com.ewaste.client.api.AuthApiClient;
import com.ewaste.client.dto.response.AuthClientResponse;
import com.ewaste.client.navigation.AppScreen;
import com.ewaste.client.navigation.SceneNavigator;
import com.ewaste.client.util.AlertHelper;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;

/**
 * Controller handling user authentication and role-based screen routing.
 */
public class LoginController extends BaseController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private ProgressIndicator loadingIndicator;

    private final AuthApiClient authApiClient = new AuthApiClient();

    @FXML
    @Override
    public void initialize() {
        super.initialize();
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(false);
        }
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            AlertHelper.showWarning("Validation Error", "Please provide both email and password.");
            return;
        }

        setLoading(true);

        Task<AuthClientResponse> loginTask = new Task<>() {
            @Override
            protected AuthClientResponse call() throws Exception {
                return authApiClient.login(email, password);
            }
        };

        loginTask.setOnSucceeded(event -> {
            setLoading(false);
            AuthClientResponse response = loginTask.getValue();
            if (response != null && response.getToken() != null) {
                // Initialize session state
                session.setToken(response.getToken());
                session.setUserId(response.getUserId());
                session.setUserName(response.getFullName());
                session.setEmail(response.getEmail());
                session.setRole(response.getRole());

                routeUserByRole(response.getRole());
            } else {
                AlertHelper.showError("Login Failed", "Invalid response received from authentication server.");
            }
        });

        loginTask.setOnFailed(event -> {
            setLoading(false);
            Throwable ex = loginTask.getException();
            log.error("Authentication failed for {}", email, ex);
            AlertHelper.showError("Authentication Failed", ex.getMessage() != null ? ex.getMessage() : "Invalid credentials.");
        });

        runAsync(loginTask);
    }

    private void routeUserByRole(String role) {
        if (role == null) {
            AlertHelper.showError("Authorization Error", "No role assigned to this account.");
            return;
        }

        switch (role.toUpperCase()) {
            case "ADMIN" -> SceneNavigator.loadScreen(AppScreen.ADMIN_DASHBOARD, false);
            case "COLLECTOR" -> SceneNavigator.loadScreen(AppScreen.COLLECTOR_DASHBOARD, false);
            case "CUSTOMER" -> SceneNavigator.loadScreen(AppScreen.CUSTOMER_DASHBOARD, false);
            default -> AlertHelper.showError("Access Denied", "Unrecognized system role: " + role);
        }
    }

    @FXML
    private void handleNavigateRegister() {
        SceneNavigator.loadScreen(AppScreen.REGISTER);
    }

    private void setLoading(boolean isLoading) {
        if (loadingIndicator != null) loadingIndicator.setVisible(isLoading);
        loginButton.setDisable(isLoading);
        emailField.setDisable(isLoading);
        passwordField.setDisable(isLoading);
    }
}