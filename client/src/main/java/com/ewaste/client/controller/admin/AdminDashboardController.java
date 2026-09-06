package com.ewaste.client.controller.admin;

import com.ewaste.client.api.CollectorApiClient;
import com.ewaste.client.api.PickupApiClient;
import com.ewaste.client.api.ReportApiClient;
import com.ewaste.client.controller.BaseController;
import com.ewaste.client.dto.response.AnalyticsClientResponse;
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

import java.util.List;

public class AdminDashboardController extends BaseController {

    @FXML private Label adminNameLabel;
    @FXML private Label pendingPickupsCountLabel;
    @FXML private Label activeCollectorsCountLabel;
    @FXML private Label totalRecycledWeightLabel;

    @FXML private Button managePickupsButton;
    @FXML private Button manageCollectorsButton;
    @FXML private Button inspectProcessingButton;
    @FXML private Button viewAnalyticsButton;
    @FXML private Button refreshButton;
    @FXML private ProgressIndicator loadingIndicator;

    private final PickupApiClient pickupApiClient = new PickupApiClient();
    private final CollectorApiClient collectorApiClient = new CollectorApiClient();
    private final ReportApiClient reportApiClient = new ReportApiClient();

    @FXML
    public void initialize() {
        validateSession();
        adminNameLabel.setText("Administrator: " + UserSession.getInstance().getFullName());
        loadDashboardMetrics();
    }

    private void loadDashboardMetrics() {
        setLoading(true);

        Task<Void> loadTask = new Task<>() {
            private long pendingCount = 0;
            private long availableCollectors = 0;
            private double recycledWeight = 0.0;

            @Override
            protected Void call() {
                // 1. Fetch pending pickups
                try {
                    List<PickupClientResponse> pickups = pickupApiClient.getAllPickups(null);
                    if (pickups != null) {
                        pendingCount = pickups.stream()
                                .filter(p -> "SUBMITTED".equalsIgnoreCase(p.getStatus()) || "REQUESTED".equalsIgnoreCase(p.getStatus()))
                                .count();
                    }
                } catch (Exception ignored) {}

                // 2. Fetch available collectors
                try {
                    List<CollectorClientResponse> collectors = collectorApiClient.getAvailableCollectors();
                    if (collectors != null) {
                        availableCollectors = collectors.size();
                    }
                } catch (Exception ignored) {}

                // 3. Fetch summary metrics
                try {
                    AnalyticsClientResponse analytics = reportApiClient.getSystemAnalytics();
                    if (analytics != null && analytics.getTotalWeightKg() != null) {
                        recycledWeight = analytics.getTotalWeightKg();
                    }
                } catch (Exception ignored) {}

                return null;
            }

            @Override
            protected void succeeded() {
                setLoading(false);
                pendingPickupsCountLabel.setText(String.valueOf(pendingCount));
                activeCollectorsCountLabel.setText(String.valueOf(availableCollectors));
                totalRecycledWeightLabel.setText(String.format("%.1f kg", recycledWeight));
            }

            @Override
            protected void failed() {
                setLoading(false);
                AlertHelper.showError("Metrics Error", "Failed to retrieve real-time operational telemetry.");
            }
        };

        new Thread(loadTask).start();
    }

    @FXML
    private void handleManagePickups() {
        SceneNavigator.loadScreen(AppScreen.ADMIN_PICKUP_MANAGEMENT);
    }

    @FXML
    private void handleManageCollectors() {
        SceneNavigator.loadScreen(AppScreen.ADMIN_COLLECTOR_MANAGEMENT);
    }

    @FXML
    private void handleInspectProcessing() {
        SceneNavigator.loadScreen(AppScreen.ADMIN_INSPECTION_PROCESSING);
    }

    @FXML
    private void handleViewAnalytics() {
        SceneNavigator.loadScreen(AppScreen.ADMIN_ANALYTICS);
    }

    @FXML
    private void handleRefresh() {
        loadDashboardMetrics();
    }

    private void setLoading(boolean isLoading) {
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(isLoading);
        }
        if (refreshButton != null) {
            refreshButton.setDisable(isLoading);
        }
    }
}