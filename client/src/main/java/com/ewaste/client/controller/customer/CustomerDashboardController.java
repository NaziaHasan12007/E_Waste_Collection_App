package com.ewaste.client.controller.customer;

import com.ewaste.client.api.PickupApiClient;
import com.ewaste.client.api.RewardApiClient;
import com.ewaste.client.controller.BaseController;
import com.ewaste.client.dto.response.PickupClientResponse;
import com.ewaste.client.dto.response.RewardClientResponse;
import com.ewaste.client.navigation.AppScreen;
import com.ewaste.client.navigation.SceneNavigator;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.List;

/**
 * Main dashboard screen for customer operations and balance summaries.
 */
public class CustomerDashboardController extends BaseController {

    @FXML private Label welcomeLabel;
    @FXML private Label activePickupsCountLabel;
    @FXML private Label rewardPointsLabel;
    @FXML private Label pendingSubmissionsLabel;

    private final PickupApiClient pickupApiClient = new PickupApiClient();
    private final RewardApiClient rewardApiClient = new RewardApiClient();

    @FXML
    @Override
    public void initialize() {
        super.initialize();
        welcomeLabel.setText("Welcome back, " + (session.getUserName() != null ? session.getUserName() : "Customer"));
        refreshDashboardMetrics();
    }

    @FXML
    public void refreshDashboardMetrics() {
        long customerId = session.getUserId();

        Task<Void> loadMetricsTask = new Task<>() {
            private int activePickups = 0;
            private int pendingSubmissions = 0;
            private int totalPoints = 0;

            @Override
            protected Void call() throws Exception {
                // Fetch customer pickups
                List<PickupClientResponse> pickups = pickupApiClient.getPickupsForCustomer(customerId);
                if (pickups != null) {
                    for (PickupClientResponse p : pickups) {
                        String s = p.getStatus().toUpperCase();
                        if ("REQUESTED".equals(s) || "ASSIGNED".equals(s) || "COLLECTED".equals(s)) {
                            activePickups++;
                        } else if ("SUBMITTED".equals(s)) {
                            pendingSubmissions++;
                        }
                    }
                }

                // Fetch customer rewards
                RewardClientResponse rewards = rewardApiClient.getCustomerRewardSummary(customerId);
                if (rewards != null) {
                    totalPoints = rewards.getTotalPoints();
                }
                return null;
            }

            @Override
            protected void succeeded() {
                activePickupsCountLabel.setText(String.valueOf(activePickups));
                pendingSubmissionsLabel.setText(String.valueOf(pendingSubmissions));
                rewardPointsLabel.setText(totalPoints + " pts");
            }

            @Override
            protected void failed() {
                log.warn("Failed to load customer dashboard metrics", getException());
            }
        };

        runAsync(loadMetricsTask);
    }

    @FXML
    private void handleNavigateSubmitEWaste() {
        SceneNavigator.loadScreen(AppScreen.SUBMIT_EWASTE);
    }

    @FXML
    private void handleNavigatePickupHistory() {
        SceneNavigator.loadScreen(AppScreen.PICKUP_HISTORY);
    }

    @FXML
    private void handleNavigateRewards() {
        SceneNavigator.loadScreen(AppScreen.REWARD_VIEW);
    }
}