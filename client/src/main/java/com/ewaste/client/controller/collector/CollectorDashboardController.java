package com.ewaste.client.controller.collector;

import com.ewaste.client.api.CollectorApiClient;
import com.ewaste.client.api.PickupApiClient;
import com.ewaste.client.config.ClientContext;
import com.ewaste.client.controller.BaseController;
import com.ewaste.client.dto.response.CollectorClientResponse;
import com.ewaste.client.dto.response.PickupClientResponse;
import com.ewaste.client.navigation.AppScreen;
import com.ewaste.client.session.UserSession;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ToggleButton;

import java.util.List;

public class CollectorDashboardController extends BaseController {

    @FXML
    private Label welcomeLabel;
    @FXML
    private Label vehicleLabel;
    @FXML
    private Label capacityLabel;
    @FXML
    private Label activeTasksCountLabel;
    @FXML
    private ToggleButton availabilityToggle;
    @FXML
    private Button viewTasksButton;
    @FXML
    private Button refreshButton;
    @FXML
    private ProgressIndicator loadingIndicator;

    private CollectorApiClient collectorApiClient;
    private PickupApiClient pickupApiClient;
    private Long collectorId;

    @Override
    protected void onInitialize() {
        collectorApiClient = ClientContext.getInstance().getCollectorApiClient();
        pickupApiClient = ClientContext.getInstance().getPickupApiClient();

        welcomeLabel.setText("Welcome back, " + userSession.getFullName());

        // Setup availability toggle listener
        availabilityToggle.setOnAction(event -> handleToggleAvailability());
        viewTasksButton.setOnAction(event -> handleViewTasks());
        refreshButton.setOnAction(event -> handleRefresh());

        loadCollectorProfile();
    }

    private void loadCollectorProfile() {
        setLoading(true);
        long userId = userSession.getUserId();

        new Thread(() -> {
            try {
                List<CollectorClientResponse> collectors = collectorApiClient.getAllCollectors();
                CollectorClientResponse profile = collectors.stream()
                        .filter(c -> c.getUserId() != null && c.getUserId().equals(userId))
                        .findFirst()
                        .orElse(null);

                Platform.runLater(() -> {
                    if (profile != null) {
                        collectorId = profile.getCollectorId();
                        updateProfileUI(profile);
                        loadActiveTaskMetrics(collectorId);
                    } else {
                        setLoading(false);
                        showError("Profile Error", "Collector profile not found for the active user session.", "");
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    setLoading(false);
                    showError("Network Failure", "Unable to retrieve profile.", e.getMessage());
                });
            }
        }).start();
    }

    private void updateProfileUI(CollectorClientResponse profile) {
        vehicleLabel.setText("Vehicle: " + (profile.getVehicleType() != null ? profile.getVehicleType() : "Not specified"));

        double currentWorkload = profile.getCurrentWorkloadKg() != null ? profile.getCurrentWorkloadKg() : 0.0;
        double maxCapacity = profile.getMaxCapacityKg() != null ? profile.getMaxCapacityKg() : 0.0;
        capacityLabel.setText(String.format("Capacity: %.1f / %.1f kg", currentWorkload, maxCapacity));

        boolean isAvailable = Boolean.TRUE.equals(profile.getIsAvailable());
        availabilityToggle.setSelected(isAvailable);
        updateToggleText(isAvailable);

        setLoading(false);
    }

    private void loadActiveTaskMetrics(long collectorId) {
        new Thread(() -> {
            try {
                List<PickupClientResponse> tasks = pickupApiClient.getPickupsForCollector(collectorId);

                long activeCount = tasks != null ? tasks.stream()
                        .filter(t -> {
                            String status = t.getCurrentState();
                            return "ASSIGNED".equalsIgnoreCase(status) ||
                                    "COLLECTED".equalsIgnoreCase(status) ||
                                    "IN_PROGRESS".equalsIgnoreCase(status);
                        })
                        .count() : 0;

                Platform.runLater(() -> {
                    activeTasksCountLabel.setText(String.valueOf(activeCount));
                    setLoading(false);
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    activeTasksCountLabel.setText("-");
                    setLoading(false);
                });
            }
        }).start();
    }

    @FXML
    private void handleToggleAvailability() {
        if (collectorId == null) {
            return;
        }

        boolean newState = availabilityToggle.isSelected();
        updateToggleText(newState);
        setLoading(true);

        new Thread(() -> {
            try {
                CollectorClientResponse updated = collectorApiClient.updateCollectorAvailability(
                        collectorId, newState);

                Platform.runLater(() -> {
                    setLoading(false);
                    showInfo("Status Updated", "Your availability has been set to: " +
                            (newState ? "Available" : "Unavailable"), "");
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    setLoading(false);
                    availabilityToggle.setSelected(!newState);
                    updateToggleText(!newState);
                    showError("Update Failed", "Failed to toggle status.", e.getMessage());
                });
            }
        }).start();
    }

    private void updateToggleText(boolean isAvailable) {
        availabilityToggle.setText(isAvailable ? "Status: AVAILABLE" : "Status: UNAVAILABLE");
    }

    @FXML
    private void handleViewTasks() {
        navigateTo(AppScreen.ASSIGNED_TASKS);
    }

    @FXML
    private void handleRefresh() {
        loadCollectorProfile();
    }

    private void setLoading(boolean isLoading) {
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(isLoading);
        }
        if (refreshButton != null) {
            refreshButton.setDisable(isLoading);
        }
        if (availabilityToggle != null) {
            availabilityToggle.setDisable(isLoading);
        }
        if (viewTasksButton != null) {
            viewTasksButton.setDisable(isLoading);
        }
    }
}