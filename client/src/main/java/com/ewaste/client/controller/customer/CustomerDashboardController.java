package com.ewaste.client.controller.customer;

import com.ewaste.client.api.PickupApiClient;
import com.ewaste.client.api.RewardApiClient;
import com.ewaste.client.config.ClientContext;
import com.ewaste.client.controller.BaseController;
import com.ewaste.client.dto.response.PickupClientResponse;
import com.ewaste.client.dto.response.RewardClientResponse;
import com.ewaste.client.navigation.AppScreen;
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

    private PickupApiClient pickupApiClient;
    private RewardApiClient rewardApiClient;

    @Override
    protected void onInitialize() {
        // Initialize specific API clients
        pickupApiClient = ClientContext.getInstance().getPickupApiClient();
        rewardApiClient = ClientContext.getInstance().getRewardApiClient();

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
    }

    private void loadDashboardData() {
        // Show loading state
        setLoading(true);

        new Thread(() -> {
            try {
                Long userId = userSession.getUserId();

                // Load user's reward balance using RewardApiClient
                RewardClientResponse rewardResponse = rewardApiClient.getRewardDetails(userId);
                int balance = rewardResponse != null && rewardResponse.getBalance() != null ?
                        rewardResponse.getBalance() : 0;

                // Load user's pickups using PickupApiClient
                List<PickupClientResponse> pickups = pickupApiClient.getPickupsForCustomer(userId);

                // Count active pickups (not completed or cancelled)
                long activePickups = pickups.stream()
                        .filter(PickupClientResponse::isActive)
                        .count();

                // Calculate total recycled weight from completed pickups
                double totalWeight = pickups.stream()
                        .filter(PickupClientResponse::isCompleted)
                        .mapToDouble(PickupClientResponse::getTotalWeight)
                        .sum();

                // Update UI on JavaFX thread
                Platform.runLater(() -> {
                    setLoading(false);
                    updateDashboard(balance, activePickups, totalWeight);
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    setLoading(false);
                    showError("Error", "Failed to load dashboard data", e.getMessage());
                });
            }
        }).start();
    }

    private void updateDashboard(int points, long activePickups, double totalWeight) {
        // Update labels
        lblPoints.setText(String.valueOf(points));
        lblActivePickups.setText(String.valueOf(activePickups));
        lblTotalRecycled.setText(String.format("%.2f kg", totalWeight));

        // Update pie chart
        updatePieChart();
    }

    private void updatePieChart() {
        chartWasteCategories.getData().clear();

        // Add sample data
        chartWasteCategories.getData().add(new PieChart.Data("Laptops", 30));
        chartWasteCategories.getData().add(new PieChart.Data("Batteries", 25));
        chartWasteCategories.getData().add(new PieChart.Data("Displays", 20));
        chartWasteCategories.getData().add(new PieChart.Data("Circuit Boards", 15));
        chartWasteCategories.getData().add(new PieChart.Data("Other", 10));

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
}