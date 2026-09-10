package com.ewaste.client.controller.customer;

import com.ewaste.client.api.RewardApiClient;
import com.ewaste.client.config.ClientContext;
import com.ewaste.client.controller.BaseController;
import com.ewaste.client.dto.response.RewardClientResponse;
import com.ewaste.client.dto.response.RewardHistoryClientResponse;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TextInputDialog;

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
    @FXML
    private Button btnRedeem;

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
        if (btnRedeem != null) {
            btnRedeem.setOnAction(event -> handleRedeem());
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
                List<RewardHistoryClientResponse> history = rewardApiClient.getRewardHistory(userId);

                Platform.runLater(() -> {
                    setLoading(false);
                    if (reward != null) {
                        updateUI(reward, history);
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

    private void updateUI(RewardClientResponse reward, List<RewardHistoryClientResponse> history) {
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

        List<RewardTransactionItem> transactions = toTransactionItems(history);
        tblTransactions.setItems(javafx.collections.FXCollections.observableArrayList(transactions));
    }

    static List<RewardTransactionItem> toTransactionItems(
            List<RewardHistoryClientResponse> history) {
        if (history == null) {
            return new ArrayList<>();
        }
        return history.stream()
                .map(entry -> new RewardTransactionItem(
                        entry.getCreatedAt() != null ? entry.getCreatedAt() : "-",
                        entry.getCalculationBasis() != null
                                ? entry.getCalculationBasis()
                                : "Reward transaction",
                        entry.getPoints() != null ? entry.getPoints() : 0,
                        entry.getPoints() != null && entry.getPoints() >= 0
                                ? "EARNED" : "REDEEMED"))
                .toList();
    }

    @FXML
    private void handleRefresh() {
        loadRewardData();
    }

    @FXML
    private void handleBack() {
        navigateBack();
    }

    @FXML
    private void handleRedeem() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Redeem Points");
        dialog.setHeaderText("Enter the number of points to redeem");
        dialog.setContentText("Points:");

        dialog.showAndWait().ifPresent(value -> {
            try {
                int points = Integer.parseInt(value.trim());
                if (points <= 0) {
                    throw new NumberFormatException();
                }
                if (!showConfirmation("Redeem Points", "Confirm redemption",
                        "Redeem " + points + " points?")) {
                    return;
                }

                setLoading(true);
                new Thread(() -> {
                    try {
                        rewardApiClient.redeemPoints(userSession.getUserId(), points);
                        RewardClientResponse updated = rewardApiClient.getRewardDetails(
                                userSession.getUserId());
                        List<RewardHistoryClientResponse> updatedHistory =
                                rewardApiClient.getRewardHistory(userSession.getUserId());
                        Platform.runLater(() -> {
                            setLoading(false);
                            updateUI(updated, updatedHistory);
                            showInfo("Redemption Complete", "Points redeemed successfully.", "");
                        });
                    } catch (Exception e) {
                        Platform.runLater(() -> {
                            setLoading(false);
                            showError("Redemption Failed", "Unable to redeem points.", e.getMessage());
                        });
                    }
                }).start();
            } catch (NumberFormatException e) {
                showWarning("Invalid Points", "Enter a positive whole number of points.", "");
            }
        });
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