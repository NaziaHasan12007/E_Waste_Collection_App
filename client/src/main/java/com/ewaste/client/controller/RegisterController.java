package com.ewaste.client.controller;

import com.ewaste.client.api.AppScreen;
import com.ewaste.client.api.ClientContext;
import com.ewaste.client.dto.response.AuthClientResponse;
import com.ewaste.client.dto.request.RegisterClientRequest;
import com.ewaste.client.api.ApiClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.io.IOException;

public class RegisterController extends BaseController {

    @FXML
    private TextField txtFullName;
    @FXML
    private TextField txtEmail;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private PasswordField txtConfirmPassword;
    @FXML
    private ComboBox<String> cmbRole;
    @FXML
    private TextField txtVehicleType;
    @FXML
    private TextField txtMaxCapacity;
    @FXML
    private Button btnRegister;
    @FXML
    private Button btnLogin;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Label lblError;
    @FXML
    private VBox collectorFields;

    private ApiClient apiClient;

    @Override
    protected void onInitialize() {
        apiClient = ClientContext.getInstance().getApiClient();

        // Setup role combo box
        cmbRole.getItems().addAll("CUSTOMER", "COLLECTOR");
        cmbRole.setValue("CUSTOMER");

        // Show/hide collector fields based on role selection
        cmbRole.setOnAction(event -> {
            boolean isCollector = "COLLECTOR".equals(cmbRole.getValue());
            collectorFields.setVisible(isCollector);
            collectorFields.setManaged(isCollector);
        });

        // Initially hide collector fields
        collectorFields.setVisible(false);
        collectorFields.setManaged(false);

        // Setup event handlers
        btnRegister.setOnAction(event -> handleRegister());
        btnLogin.setOnAction(event -> navigateTo(AppScreen.LOGIN));

        // Enter key support
        txtFullName.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("ENTER")) txtEmail.requestFocus();
        });
        txtEmail.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("ENTER")) txtPassword.requestFocus();
        });
        txtPassword.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("ENTER")) txtConfirmPassword.requestFocus();
        });
        txtConfirmPassword.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("ENTER")) handleRegister();
        });
    }

    @FXML
    private void handleRegister() {
        clearError();

        if (!validateInput()) {
            return;
        }

        setLoading(true);

        new Thread(() -> {
            try {
                RegisterClientRequest request = createRegistrationRequest();

                AuthClientResponse response = apiClient.register(request);

                Platform.runLater(() -> {
                    setLoading(false);
                    handleRegistrationSuccess(response);
                });

            } catch (IOException e) {
                Platform.runLater(() -> {
                    setLoading(false);
                    showError("Registration Failed", "Network Error",
                            "Unable to connect to server. Please check your connection.\n\n" +
                                    "Error: " + e.getMessage());
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    setLoading(false);
                    showError("Registration Failed", "Registration Error",
                            e.getMessage());
                });
            }
        }).start();
    }

    private RegisterClientRequest createRegistrationRequest() {
        RegisterClientRequest request = new RegisterClientRequest();
        request.setFullName(txtFullName.getText().trim());
        request.setEmail(txtEmail.getText().trim());
        request.setPassword(txtPassword.getText().trim());
        request.setRole(cmbRole.getValue());

        // Collector specific fields
        if ("COLLECTOR".equals(cmbRole.getValue())) {
            request.setVehicleType(txtVehicleType.getText().trim());
            try {
                request.setMaxCapacityKg(Double.parseDouble(txtMaxCapacity.getText().trim()));
            } catch (NumberFormatException e) {
                // Will be handled by validation
            }
        }

        return request;
    }

    private void handleRegistrationSuccess(AuthClientResponse response) {
        // Start user session
        userSession.startSession(
                response.getUserId(),
                response.getFullName(),
                response.getEmail(),
                response.getRole(),
                response.getToken()
        );

        showInfo("Registration Successful",
                "Welcome " + response.getFullName() + "!",
                "Your account has been created successfully.");

        // Navigate to appropriate dashboard
        if (userSession.isAdmin()) {
            navigateTo(AppScreen.ADMIN_DASHBOARD);
        } else if (userSession.isCollector()) {
            navigateTo(AppScreen.COLLECTOR_DASHBOARD);
        } else {
            navigateTo(AppScreen.CUSTOMER_DASHBOARD);
        }
    }

    private boolean validateInput() {
        String fullName = txtFullName.getText().trim();
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();
        String confirmPassword = txtConfirmPassword.getText().trim();

        // Validate full name
        if (fullName.isEmpty()) {
            showError("Full name required", "Please enter your full name.");
            txtFullName.requestFocus();
            return false;
        }

        if (fullName.length() < 2) {
            showError("Invalid name", "Full name must be at least 2 characters.");
            txtFullName.requestFocus();
            return false;
        }

        // Validate email
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

        // Validate password
        if (password.isEmpty()) {
            showError("Password required", "Please enter a password.");
            txtPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            showError("Invalid Password", "Password must be at least 6 characters.");
            txtPassword.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            showError("Password Mismatch", "Passwords do not match.");
            txtConfirmPassword.requestFocus();
            return false;
        }

        // Validate collector fields if role is collector
        if ("COLLECTOR".equals(cmbRole.getValue())) {
            if (txtVehicleType.getText().trim().isEmpty()) {
                showError("Vehicle type required", "Please enter vehicle type.");
                txtVehicleType.requestFocus();
                return false;
            }

            try {
                double capacity = Double.parseDouble(txtMaxCapacity.getText().trim());
                if (capacity <= 0) {
                    showError("Invalid capacity", "Max capacity must be greater than 0.");
                    txtMaxCapacity.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                showError("Invalid capacity", "Please enter a valid number for max capacity.");
                txtMaxCapacity.requestFocus();
                return false;
            }
        }

        return true;
    }

    private void showError(String title, String message) {
        lblError.setText(title + ": " + message);
        lblError.setVisible(true);
        lblError.getStyleClass().add("error-message");
    }

    private void clearError() {
        lblError.setText("");
        lblError.setVisible(false);
        lblError.getStyleClass().remove("error-message");
    }

    private void setLoading(boolean loading) {
        btnRegister.setDisable(loading);
        txtFullName.setDisable(loading);
        txtEmail.setDisable(loading);
        txtPassword.setDisable(loading);
        txtConfirmPassword.setDisable(loading);
        cmbRole.setDisable(loading);
        progressIndicator.setVisible(loading);
        btnRegister.setText(loading ? "Registering..." : "Register");
    }
}