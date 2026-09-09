package com.ewaste.client.controller.collector;

import com.ewaste.client.api.CollectorApiClient;
import com.ewaste.client.api.PickupApiClient;
import com.ewaste.client.controller.BaseController;
import com.ewaste.client.dto.response.CollectorClientResponse;
import com.ewaste.client.dto.response.PickupClientResponse;
import com.ewaste.client.navigation.AppScreen;
import com.ewaste.client.navigation.SceneNavigator;
import com.ewaste.client.session.UserSession;
import com.ewaste.client.util.AlertHelper;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ToggleButton;

import java.util.List;

public class CollectorDashboardController extends BaseController {

    @FXML private Label welcomeLabel;
    @FXML private Label vehicleLabel;
    @FXML private Label capacityLabel;
    @FXML private Label activeTasksCountLabel;
    @FXML private ToggleButton availabilityToggle;
    @FXML private Button viewTasksButton;
    @FXML private Button refreshButton;
    @FXML private ProgressIndicator loadingIndicator;

    private final CollectorApiClient collectorApiClient = new CollectorApiClient();
    private final PickupApiClient pickupApiClient = new PickupApiClient();
    private Long collectorId;

    @FXML
    public void initialize() {
        validateSession();
        long userId = UserSession.getInstance().getUserId();
        welcomeLabel.setText("Welcome back, " + UserSession.getInstance().getFullName());
        loadCollectorProfile(userId);
    }

    private void loadCollectorProfile(long userId) {
        setLoading(true);
        Task<CollectorClientResponse> profileTask = new Task<>() {
            @Override
            protected CollectorClientResponse call() {
                List<CollectorClientResponse> collectors = collectorApiClient.getAllCollectors();
                return collectors.stream()
                        .filter(c -> c.getUserId() != null && c.getUserId().equals(userId))
                        .findFirst()
                        .orElse(null);
            }
        };

        profileTask.setOnSucceeded(event -> {
            CollectorClientResponse profile = profileTask.getValue();
            if (profile != null) {
                collectorId = profile.getCollectorId();
                vehicleLabel.setText("Vehicle: " + profile.getVehicleType());
                capacityLabel.setText(String.format("Capacity: %.1f / %.1f kg",
                        profile.getCurrentWorkloadKg(), profile.getMaxCapacityKg()));
                availabilityToggle.setSelected(Boolean.TRUE.equals(profile.getIsAvailable()));
                updateToggleText(availabilityToggle.isSelected());
                loadActiveTaskMetrics(collectorId);
            } else {
                setLoading(false);
                AlertHelper.showError("Profile Error", "Collector profile not found for the active user session.");
            }
        });

        profileTask.setOnFailed(event -> {
            setLoading(false);
            Throwable ex = profileTask.getException();
            AlertHelper.showError("Network Failure", ex != null ? ex.getMessage() : "Unable to retrieve profile.");
        });

        new Thread(profileTask).start();
    }

    private void loadActiveTaskMetrics(long collectorId) {
        Task<List<PickupClientResponse>> taskListTask = new Task<>() {
            @Override
            protected List<PickupClientResponse> call() {
                return pickupApiClient.getPickupsForCollector(collectorId);
            }
        };

        taskListTask.setOnSucceeded(event -> {
            setLoading(false);
            List<PickupClientResponse> tasks = taskListTask.getValue();
            long activeCount = tasks != null ? tasks.stream()
                    .filter(t -> "ASSIGNED".equalsIgnoreCase(t.getStatus()) || "COLLECTED".equalsIgnoreCase(t.getStatus()))
                    .count() : 0;
            activeTasksCountLabel.setText(String.valueOf(activeCount));
        });

        taskListTask.setOnFailed(event -> {
            setLoading(false);
            activeTasksCountLabel.setText("-");
        });

        new Thread(taskListTask).start();
    }

    @FXML
    private void handleToggleAvailability() {
        if (collectorId == null) {
            return;
        }

        boolean newState = availabilityToggle.isSelected();
        updateToggleText(newState);
        setLoading(true);

        Task<CollectorClientResponse> toggleTask = new Task<>() {
            @Override
            protected CollectorClientResponse call() {
                return collectorApiClient.updateAvailability(collectorId, newState);
            }
        };

        toggleTask.setOnSucceeded(event -> {
            setLoading(false);
            AlertHelper.showInfo("Status Updated", "Your availability has been set to: " + (newState ? "Available" : "Unavailable"));
        });

        toggleTask.setOnFailed(event -> {
            setLoading(false);
            availabilityToggle.setSelected(!newState);
            updateToggleText(!newState);
            Throwable ex = toggleTask.getException();
            AlertHelper.showError("Update Failed", ex != null ? ex.getMessage() : "Failed to toggle status.");
        });

        new Thread(toggleTask).start();
    }

    private void updateToggleText(boolean isAvailable) {
        availabilityToggle.setText(isAvailable ? "Status: AVAILABLE" : "Status: UNAVAILABLE");
    }

    @FXML
    private void handleViewTasks() {
        SceneNavigator.loadScreen(AppScreen.COLLECTOR_ASSIGNED_TASKS);
    }

    @FXML
    private void handleRefresh() {
        long userId = UserSession.getInstance().getUserId();
        loadCollectorProfile(userId);
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
    }
}