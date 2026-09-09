package com.ewaste.client.controller.customer;

import com.ewaste.client.api.RewardApiClient;
import com.ewaste.client.config.ClientContext;
import com.ewaste.client.controller.BaseController;
import com.ewaste.client.dto.response.RewardClientResponse;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;

public class RewardViewController extends BaseController {

    @FXML
    private Label lblBalance;
    @FXML
    private Label lblTotalEarned;
    @FXML
    private Label lblTier;
    @FXML
    private Label lblCarbonCredits;
    @FXML
    private Label lblProgressText;
    @FXML
    private ProgressBar progressTier;
    @FXML
    private TableView<RewardTransactionItem> tblTransactions;
    @FXML
    private TableColumn<RewardTransactionItem, String> colDate;
    @FXML
    private TableColumn<RewardTransactionItem, String> colDescription;
    @FXML
    private TableColumn<RewardTransactionItem, Number> colPoints;
    @FXML
    private TableColumn<RewardTransactionItem, String> colType;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Button btnRefresh;
    @FXML
    private Button btnBack;

    private RewardApiClient rewardApiClient;

    @Override
    protected void onInitialize() {
        rewardApiClient = ClientContext.getInstance().getRewardApiClient();

        setupTable();
        loadRewardData();

        // Set button actions
        if (btnRefresh != null) {
            btnRefresh.setOnAction(event -> handleRefresh());
        }
        if (btnBack != null) {
            btnBack.setOnAction(event -> handleBack());
        }
    }

    private void setupTable() {
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPoints.setCellValueFactory(new PropertyValueFactory<>("points"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
    }

    private void loadRewardData() {
        setLoading(true);

        new Thread(() -> {
            try {
                Long userId = userSession.getUserId();
                RewardClientResponse reward = rewardApiClient.getRewardDetails(userId);

                Platform.runLater(() -> {
                    setLoading(false);
                    if (reward != null) {
                        updateUI(reward);
                    } else {
                        showError("Error", "Failed to load reward data", "No reward data found for user.");
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    setLoading(false);
                    showError("Error", "Failed to load reward data", e.getMessage());
                });
            }
        }).start();
    }

    private void updateUI(RewardClientResponse reward) {
        // Use the helper methods from RewardClientResponse
        lblBalance.setText(String.valueOf(reward.getBalance() != null ? reward.getBalance() : 0));
        lblTotalEarned.setText(String.valueOf(reward.getPointsEarned() != null ? reward.getPointsEarned() : 0));
        lblTier.setText(reward.getTierDisplay());

        // Carbon credits - if not available, show 0
        lblCarbonCredits.setText("0.00 kg");

        // Update progress to next tier using helper methods
        int pointsToNext = reward.getPointsToNextTier();
        int progress = reward.getProgressToNextTier();
        progressTier.setProgress(progress / 100.0);
        lblProgressText.setText(String.format("%d%% - Points needed: %d", progress, pointsToNext));

        // Create sample transaction data (since your DTO doesn't have transactions)
        // In a real app, you would get this from the API
        List<RewardTransactionItem> transactions = createSampleTransactions();
        tblTransactions.setItems(javafx.collections.FXCollections.observableArrayList(transactions));
    }

    private List<RewardTransactionItem> createSampleTransactions() {
        List<RewardTransactionItem> transactions = new ArrayList<>();
        transactions.add(new RewardTransactionItem("2026-01-15", "E-Waste Drop-off - Laptop", 50, "EARNED"));
        transactions.add(new RewardTransactionItem("2026-01-12", "Recycling Bonus - Batteries", 25, "BONUS"));
        transactions.add(new RewardTransactionItem("2026-01-10", "E-Waste Drop-off - Monitor", 30, "EARNED"));
        transactions.add(new RewardTransactionItem("2026-01-05", "Redeemed Voucher - Coffee Shop", -30, "REDEEMED"));
        transactions.add(new RewardTransactionItem("2025-12-28", "E-Waste Drop-off - Phone", 20, "EARNED"));
        return transactions;
    }

    @FXML
    private void handleRefresh() {
        loadRewardData();
    }

    @FXML
    private void handleBack() {
        navigateBack();
    }

    private void setLoading(boolean loading) {
        if (progressIndicator != null) {
            progressIndicator.setVisible(loading);
        }
        if (btnRefresh != null) {
            btnRefresh.setDisable(loading);
        }
        if (btnBack != null) {
            btnBack.setDisable(loading);
        }
    }

    // Inner class for transaction items
    public static class RewardTransactionItem {
        private final String date;
        private final String description;
        private final int points;
        private final String type;

        public RewardTransactionItem(String date, String description, int points, String type) {
            this.date = date;
            this.description = description;
            this.points = points;
            this.type = type;
        }

        public String getDate() { return date; }
        public String getDescription() { return description; }
        public int getPoints() { return points; }
        public String getType() { return type; }
    }
}