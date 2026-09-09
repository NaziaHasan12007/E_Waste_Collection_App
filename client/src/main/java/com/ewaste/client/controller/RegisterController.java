package com.ewaste.client.controller;

import com.ewaste.client.api.AuthApiClient;
import com.ewaste.client.dto.request.RegisterClientRequest;
import com.ewaste.client.dto.response.AuthClientResponse;
import com.ewaste.client.navigation.AppScreen;
import com.ewaste.client.navigation.SceneNavigator;
import com.ewaste.client.util.AlertHelper;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

/**
 * Controller managing customer and collector registration.
 */
public class RegisterController extends BaseController {

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> roleComboBox;

    // Collector specific fields
    @FXML private VBox collectorFieldsBox;
    @FXML private ComboBox<String> vehicleTypeComboBox;
    @FXML private TextField maxCapacityField;

    @FXML private Button registerButton;
    @FXML private ProgressIndicator loadingIndicator;

    private final AuthApiClient authApiClient = new AuthApiClient();

    @FXML
    @Override
    public void initialize() {
        super.initialize();
        if (loadingIndicator != null) loadingIndicator.setVisible(false);

        roleComboBox.getItems().addAll("CUSTOMER", "COLLECTOR");
        roleComboBox.setValue("CUSTOMER");

        if (vehicleTypeComboBox != null) {
            vehicleTypeComboBox.getItems().addAll("BICYCLE", "MOTORCYCLE", "VAN", "TRUCK");
            vehicleTypeComboBox.setValue("VAN");
        }

        roleComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isCollector = "COLLECTOR".equalsIgnoreCase(newVal);
            if (collectorFieldsBox != null) {
                collectorFieldsBox.setVisible(isCollector);
                collectorFieldsBox.setManaged(isCollector);
            }
        });
    }

    @FXML
    private void handleRegister() {
        String name = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String pass = passwordField.getText();
        String confirmPass = confirmPasswordField.getText();
        String role = roleComboBox.getValue();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            AlertHelper.showWarning("Validation Error", "All primary credentials are required.");
            return;
        }

        if (!pass.equals(confirmPass)) {
            AlertHelper.showWarning("Validation Error", "Passwords do not match.");
            return;
        }

        RegisterClientRequest request;
        if ("COLLECTOR".equalsIgnoreCase(role)) {
            String vehicle = vehicleTypeComboBox.getValue();
            double capacity;
            try {
                capacity = Double.parseDouble(maxCapacityField.getText().trim());
            } catch (NumberFormatException e) {
                AlertHelper.showWarning("Validation Error", "Max vehicle capacity must be a valid numeric weight.");
                return;
            }
            request = RegisterClientRequest.forCollector(name, email, pass, vehicle, capacity);
        } else {
            request = new RegisterClientRequest(name, email, pass, "CUSTOMER");
        }

        setLoading(true);

        Task<AuthClientResponse> registerTask = new Task<>() {
            @Override
            protected AuthClientResponse call() throws Exception {
                return authApiClient.register(request);
            }
        };

        registerTask.setOnSucceeded(event -> {
            setLoading(false);
            AlertHelper.showInfo("Registration Successful", "Your account has been created. Please log in.");
            SceneNavigator.loadScreen(AppScreen.LOGIN, false);
        });

        registerTask.setOnFailed(event -> {
            setLoading(false);
            Throwable ex = registerTask.getException();
            log.error("Registration failed for {}", email, ex);
            AlertHelper.showError("Registration Failed", ex.getMessage() != null ? ex.getMessage() : "Unable to register.");
        });

        runAsync(registerTask);
    }

    @FXML
    private void handleNavigateLogin() {
        SceneNavigator.loadScreen(AppScreen.LOGIN, false);
    }

    private void setLoading(boolean isLoading) {
        if (loadingIndicator != null) loadingIndicator.setVisible(isLoading);
        registerButton.setDisable(isLoading);
    }
}