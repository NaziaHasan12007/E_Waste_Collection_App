package com.ewaste.client.controller.admin;

import com.ewaste.client.api.CollectorApiClient;
import com.ewaste.client.api.PickupApiClient;
import com.ewaste.client.api.ReportApiClient;
import com.ewaste.client.config.ClientContext;
import com.ewaste.client.controller.BaseController;
import com.ewaste.client.dto.response.AnalyticsClientResponse;
import com.ewaste.client.dto.response.CollectorClientResponse;
import com.ewaste.client.dto.response.PickupClientResponse;
import com.ewaste.client.navigation.AppScreen;
import com.ewaste.client.session.UserSession;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

import java.util.List;

public class AdminDashboardController extends BaseController {

    @FXML
    private Label adminNameLabel;
    @FXML
    private Label pendingPickupsCountLabel;
    @FXML
    private Label activeCollectorsCountLabel;
    @FXML
    private Label totalRecycledWeightLabel;

    @FXML
    private Button managePickupsButton;
    @FXML
    private Button manageCollectorsButton;
    @FXML
    private Button inspectProcessingButton;
    @FXML
    private Button viewAnalyticsButton;
    @FXML
    private Button refreshButton;
    @FXML
    private ProgressIndicator loadingIndicator;

    private PickupApiClient pickupApiClient;
    private CollectorApiClient collectorApiClient;
    private ReportApiClient reportApiClient;

    @Override
    protected void onInitialize() {
        pickupApiClient = ClientContext.getInstance().getPickupApiClient();
        collectorApiClient = ClientContext.getInstance().getCollectorApiClient();
        reportApiClient = ClientContext.getInstance().getReportApiClient();

        adminNameLabel.setText("Administrator: " + userSession.getFullName());
        loadDashboardMetrics();
    }

    private void loadDashboardMetrics() {
        setLoading(true);

        new Thread(() -> {
            long pendingCount = 0;
            long availableCollectors = 0;
            double recycledWeight = 0.0;

            try {
                List<PickupClientResponse> pickups = pickupApiClient.getAllPickups(null);
                if (pickups != null) {
                    pendingCount = pickups.stream()
                            .filter(p -> "PENDING".equalsIgnoreCase(p.getCurrentState()) ||
                                    "SUBMITTED".equalsIgnoreCase(p.getCurrentState()))
                            .count();
                }
            } catch (Exception e) {
                // Log error
            }

            try {
                List<CollectorClientResponse> collectors = collectorApiClient.getAllCollectors();
                if (collectors != null) {
                    availableCollectors = collectors.stream()
                            .filter(c -> Boolean.TRUE.equals(c.getIsAvailable()))
                            .count();
                }
            } catch (Exception e) {
                // Log error
            }

            try {
                AnalyticsClientResponse analytics = reportApiClient.getReportsSummary();
                if (analytics != null && analytics.getTotalWeightRecycled() != null) {
                    recycledWeight = analytics.getTotalWeightRecycled();
                }
            } catch (Exception e) {
                // Log error
            }

            final long finalPendingCount = pendingCount;
            final long finalAvailableCollectors = availableCollectors;
            final double finalRecycledWeight = recycledWeight;

            Platform.runLater(() -> {
                setLoading(false);
                pendingPickupsCountLabel.setText(String.valueOf(finalPendingCount));
                activeCollectorsCountLabel.setText(String.valueOf(finalAvailableCollectors));
                totalRecycledWeightLabel.setText(String.format("%.1f kg", finalRecycledWeight));
            });
        }).start();
    }

    @FXML
    private void handleManagePickups() {
        navigateTo(AppScreen.PICKUP_MANAGEMENT);
    }

    @FXML
    private void handleManageCollectors() {
        navigateTo(AppScreen.COLLECTOR_MANAGEMENT);
    }

    @FXML
    private void handleInspectProcessing() {
        navigateTo(AppScreen.INSPECTION_PROCESSING);
    }

    @FXML
    private void handleViewAnalytics() {
        navigateTo(AppScreen.ANALYTICS_REPORTING);
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
        if (managePickupsButton != null) {
            managePickupsButton.setDisable(isLoading);
        }
        if (manageCollectorsButton != null) {
            manageCollectorsButton.setDisable(isLoading);
        }
        if (inspectProcessingButton != null) {
            inspectProcessingButton.setDisable(isLoading);
        }
        if (viewAnalyticsButton != null) {
            viewAnalyticsButton.setDisable(isLoading);
        }
    }
}