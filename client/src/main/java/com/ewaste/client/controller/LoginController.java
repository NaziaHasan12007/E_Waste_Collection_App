package com.ewaste.client.controller;

import com.ewaste.client.api.AuthApiClient;
import com.ewaste.client.config.ClientContext;
import com.ewaste.client.dto.request.LoginClientRequest;
import com.ewaste.client.dto.response.AuthClientResponse;
import com.ewaste.client.navigation.AppScreen;
import com.ewaste.client.session.UserSession;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import java.util.prefs.Preferences;

public class LoginController extends BaseController {

    @FXML
    private TextField txtEmail;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Button btnLogin;
    @FXML
    private Button btnRegister;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Label lblError;
    @FXML
    private VBox loginContainer;
    @FXML
    private CheckBox chkRememberMe;

    private AuthApiClient authApiClient;
    private UserSession userSession;

    @Override
    protected void onInitialize() {
        // Initialize user session
        userSession = UserSession.getInstance();

        // Initialize API client
        authApiClient = ClientContext.getInstance().getAuthApiClient();

        // Setup event handlers
        setupEventHandlers();

        // Clear any previous session
        userSession.endSession();

        // Set focus to email field
        Platform.runLater(() -> txtEmail.requestFocus());

        // Apply any saved credentials if remember me is enabled
        loadSavedCredentials();
    }

    private void setupEventHandlers() {
        // Enter key support for password field
        txtPassword.setOnKeyPressed(this::handleEnterKey);

        // Enter key support for email field
        txtEmail.setOnKeyPressed(this::handleEnterKey);

        // Login button action
        btnLogin.setOnAction(event -> handleLogin());

        // Register button action
        btnRegister.setOnAction(event -> navigateTo(AppScreen.REGISTER));

        // Clear error on typing
        txtEmail.textProperty().addListener((obs, oldVal, newVal) -> clearError());
        txtPassword.textProperty().addListener((obs, oldVal, newVal) -> clearError());
    }

    private void handleEnterKey(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleLogin();
        }
    }

    @FXML
    private void handleLogin() {
        // Clear previous error
        clearError();

        // Validate input
        if (!validateInput()) {
            return;
        }

        // Show loading state
        setLoading(true);

        // Perform login in background thread
        new Thread(() -> {
            try {
                LoginClientRequest request = new LoginClientRequest(
                        txtEmail.getText().trim(),
                        txtPassword.getText().trim()
                );

                AuthClientResponse response = authApiClient.login(request);

                // Update UI on JavaFX thread
                Platform.runLater(() -> {
                    setLoading(false);
                    handleLoginSuccess(response);
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    setLoading(false);
                    showDetailedError("Login Failed", "Authentication Error",
                            "Invalid email or password.\n\n" +
                                    "Please try again or register if you don't have an account.");
                });
            }
        }).start();
    }

    private void handleLoginSuccess(AuthClientResponse authResponse) {
        // Start user session
        userSession.startSession(
                authResponse.getUserId(),
                authResponse.getFullName(),
                authResponse.getEmail(),
                authResponse.getRole(),
                authResponse.getToken()
        );

        // Save credentials if remember me is checked
        if (chkRememberMe.isSelected()) {
            saveCredentials(txtEmail.getText().trim());
        }

        // Navigate to appropriate dashboard based on role
        if (userSession.isAdmin()) {
            navigateTo(AppScreen.ADMIN_DASHBOARD);
        } else if (userSession.isCollector()) {
            navigateTo(AppScreen.COLLECTOR_DASHBOARD);
        } else if (userSession.isCustomer()) {
            navigateTo(AppScreen.CUSTOMER_DASHBOARD);
        } else {
            showError("Error", "Unknown Role", "User role not recognized.");
        }
    }

    private boolean validateInput() {
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();

        if (email.isEmpty()) {
            showError("Email required", "Please enter your email address.");
            txtEmail.requestFocus();
            return false;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Invalid Email", "Please enter a valid email address.");
            txtEmail.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            showError("Password required", "Please enter your password.");
            txtPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            showError("Invalid Password", "Password must be at least 6 characters.");
            txtPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void showError(String title, String message) {
        lblError.setText(title + ": " + message);
        lblError.setVisible(true);
        lblError.getStyleClass().add("error-message");
    }

    private void showDetailedError(String title, String subtitle, String message) {
        lblError.setText(title + ": " + subtitle + " - " + message);
        lblError.setVisible(true);
        lblError.getStyleClass().add("error-message");
    }

    private void clearError() {
        lblError.setText("");
        lblError.setVisible(false);
        lblError.getStyleClass().remove("error-message");
    }

    private void setLoading(boolean loading) {
        btnLogin.setDisable(loading);
        txtEmail.setDisable(loading);
        txtPassword.setDisable(loading);
        progressIndicator.setVisible(loading);
        btnLogin.setText(loading ? "Logging in..." : "Login");
    }

    private void saveCredentials(String email) {
        try {
            Preferences prefs = Preferences.userNodeForPackage(LoginController.class);
            prefs.put("remembered_email", email);
            prefs.putBoolean("remember_me", true);
        } catch (Exception e) {
            // Log error but don't fail login
            System.err.println("Could not save credentials: " + e.getMessage());
        }
    }

    private void loadSavedCredentials() {
        try {
            Preferences prefs = Preferences.userNodeForPackage(LoginController.class);
            if (prefs.getBoolean("remember_me", false)) {
                txtEmail.setText(prefs.get("remembered_email", ""));
                chkRememberMe.setSelected(true);
            }
        } catch (Exception e) {
            System.err.println("Could not load credentials: " + e.getMessage());
        }
    }
}