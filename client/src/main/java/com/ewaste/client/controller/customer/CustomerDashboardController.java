package com.ewaste.client.controller;

import com.ewaste.client.core.AppScreen;
import com.ewaste.client.dto.AnalyticsClientResponse;
import com.ewaste.client.dto.PickupClientResponse;
import com.ewaste.client.network.ApiClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.util.List;

public class CustomerDashboardController extends BaseController {

    @FXML
    private Label lblWelcome;
    @FXML
    private Label lblPoints;
    @FXML
    private Label lblActivePickups;
    @FXML
    private Label lblTotalRecycled;
    @FXML
    private ListView<String> listNotifications;
    @FXML
    private PieChart chartWasteCategories;
    @FXML
    private VBox quickActions;

    private ApiClient apiClient;

    @Override
    protected void onInitialize() {
        apiClient = ClientContext.getInstance().getApiClient();

        // Set welcome message
        String name = userSession.getFullName();
        lblWelcome.setText("Welcome, " + (name != null ? name : "Customer") + "!");

        // Setup quick actions
        setupQuickActions();

        // Load dashboard data
        loadDashboardData();
    }

    private void setupQuickActions() {
        // Add quick action buttons programmatically or use FXML bindings
        // These would be defined in the FXML file
    }

    private void loadDashboardData() {
        // Show loading state
        setLoading(true);

        new Thread(() -> {
            try {
                Long userId = userSession.getUserId();

                // Load user's reward balance
                AnalyticsClientResponse rewardResponse = apiClient.getCustomerRewardSummary(userId);
                int balance = rewardResponse != null && rewardResponse.getRewardCurrentBalance() != null ?
                        rewardResponse.getRewardCurrentBalance() : 0;

                // Load user's pickups
                List<PickupClientResponse> pickups = apiClient.getPickupsByUser(userId);

                // Count active pickups
                long activePickups = pickups.stream()
                        .filter(p -> !"COMPLETED".equals(p.getCurrentState()) &&
                                !"CANCELLED".equals(p.getCurrentState()))
                        .count();

                // Calculate total recycled weight
                double totalWeight = pickups.stream()
                        .filter(p -> "COMPLETED".equals(p.getCurrentState()))
                        .mapToDouble(p -> {
                            if (p.getItems() != null) {
                                return p.getItems().stream()
                                        .mapToDouble(item -> item.getWeightKg() != null ? item.getWeightKg() : 0.0)
                                        .sum();
                            }
                            return 0.0;
                        })
                        .sum();

                // Load notifications
                List<String> notifications = apiClient.getNotifications(userId);

                // Update UI on JavaFX thread
                Platform.runLater(() -> {
                    setLoading(false);
                    updateDashboard(balance, activePickups, totalWeight, notifications, pickups);
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    setLoading(false);
                    showErrorAsync("Error", "Failed to load dashboard data", e.getMessage());
                });
            }
        }).start();
    }

    private void updateDashboard(int points, long activePickups, double totalWeight,
                                 List<String> notifications, List<PickupClientResponse> pickups) {
        // Update labels
        lblPoints.setText(String.valueOf(points));
        lblActivePickups.setText(String.valueOf(activePickups));
        lblTotalRecycled.setText(String.format("%.2f kg", totalWeight));

        // Update notifications list
        listNotifications.getItems().clear();
        if (notifications != null && !notifications.isEmpty()) {
            listNotifications.getItems().addAll(notifications);
        } else {
            listNotifications.getItems().add("No new notifications");
        }

        // Update pie chart
        updatePieChart(pickups);
    }

    private void updatePieChart(List<PickupClientResponse> pickups) {
        chartWasteCategories.getData().clear();

        // Aggregate waste categories from completed pickups
        // This is a simplified version - in production, you'd have category data from the server

        // Add sample data
        chartWasteCategories.getData().add(new PieChart.Data("Laptops", 30));
        chartWasteCategories.getData().add(new PieChart.Data("Batteries", 25));
        chartWasteCategories.getData().add(new PieChart.Data("Displays", 20));
        chartWasteCategories.getData().add(new PieChart.Data("Other", 25));

        // Style the chart
        chartWasteCategories.setTitle("Waste Categories");
        chartWasteCategories.setLegendVisible(true);
        chartWasteCategories.setLabelsVisible(true);
    }

    @FXML
    private void handleSubmitEWaste() {
        navigateTo(AppScreen.SUBMIT_EWASTE);
    }

    @FXML
    private void handleViewHistory() {
        navigateTo(AppScreen.PICKUP_HISTORY);
    }

    @FXML
    private void handleViewRewards() {
        navigateTo(AppScreen.REWARD_LEDGER);
    }

    @FXML
    private void handleRefresh() {
        loadDashboardData();
    }

    private void setLoading(boolean loading) {
        // Show/hide loading indicator
        // This would be implemented with a progress indicator in the FXML
    }

    // Placeholder for ClientContext
    private static class ClientContext {
        private static ClientContext instance = new ClientContext();
        private ApiClient apiClient;

        public static ClientContext getInstance() {
            return instance;
        }

        public ApiClient getApiClient() {
            if (apiClient == null) {
                // apiClient = new ApiClientImpl();
            }
            return apiClient;
        }
    }
}