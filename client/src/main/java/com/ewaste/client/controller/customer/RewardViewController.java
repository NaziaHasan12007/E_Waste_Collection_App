package com.ewaste.client.controller;

import com.ewaste.client.dto.RewardClientResponse;
import com.ewaste.client.network.ApiClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class RewardViewController extends BaseController {

    @FXML
    private Label lblTotalPoints;
    @FXML
    private Label lblCurrentBalance;
    @FXML
    private Label lblTotalTransactions;
    @FXML
    private TableView<RewardTransaction> tableRewards;
    @FXML
    private TableColumn<RewardTransaction, Long> colTransactionId;
    @FXML
    private TableColumn<RewardTransaction, Integer> colPoints;
    @FXML
    private TableColumn<RewardTransaction, Integer> colBalance;
    @FXML
    private TableColumn<RewardTransaction, String> colDate;
    @FXML
    private PieChart chartPointsDistribution;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Button btnRefresh;

    private ApiClient apiClient;
    private List<RewardClientResponse> allRewards;

    @Override
    protected void onInitialize() {
        apiClient = ClientContext.getInstance().getApiClient();

        // Setup table columns
        setupTableColumns();

        // Setup event handlers
        btnRefresh.setOnAction(event -> loadRewardData());

        // Load data
        loadRewardData();
    }

    private void setupTableColumns() {
        colTransactionId.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        colPoints.setCellValueFactory(new PropertyValueFactory<>("pointsEarned"));
        colBalance.setCellValueFactory(new PropertyValueFactory<>("balance"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        // Format points with + sign
        colPoints.setCellFactory(column -> new TableCell<RewardTransaction, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText("+" + item);
                    setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
                }
            }
        });
    }

    private void loadRewardData() {
        setLoading(true);

        new Thread(() -> {
            try {
                Long customerId = userSession.getUserId();
                allRewards = apiClient.getRewardsByCustomer(customerId);

                Platform.runLater(() -> {
                    setLoading(false);
                    updateRewardDisplay();
                    updateTable();
                    updateChart();
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    setLoading(false);
                    showError("Error", "Failed to load reward data", e.getMessage());
                });
            }
        }).start();
    }

    private void updateRewardDisplay() {
        if (allRewards == null || allRewards.isEmpty()) {
            lblTotalPoints.setText("0");
            lblCurrentBalance.setText("0");
            lblTotalTransactions.setText("0");
            return;
        }

        int totalPoints = allRewards.stream()
                .mapToInt(RewardClientResponse::getPointsEarned)
                .sum();

        int currentBalance = allRewards.isEmpty() ? 0 :
                allRewards.get(allRewards.size() - 1).getBalance();

        lblTotalPoints.setText(String.valueOf(totalPoints));
        lblCurrentBalance.setText(String.valueOf(currentBalance));
        lblTotalTransactions.setText(String.valueOf(allRewards.size()));
    }

    private void updateTable() {
        if (allRewards == null) return;

        List<RewardTransaction> transactions = allRewards.stream()
                .map(this::convertToTransaction)
                .toList();

        tableRewards.setItems(FXCollections.observableArrayList(transactions));
    }

    private RewardTransaction convertToTransaction(RewardClientResponse reward) {
        RewardTransaction transaction = new RewardTransaction();
        transaction.setTransactionId(reward.getRewardId());
        transaction.setPointsEarned(reward.getPointsEarned());
        transaction.setBalance(reward.getBalance());
        transaction.setDate("Reward #" + reward.getRewardId());
        return transaction;
    }

    private void updateChart() {
        chartPointsDistribution.getData().clear();

        if (allRewards == null || allRewards.isEmpty()) {
            chartPointsDistribution.getData().add(
                    new PieChart.Data("No Data", 1)
            );
            return;
        }

        // Create distribution chart
        // In real implementation, this would show categories or time-based distribution
        int lowPoints = 0;
        int mediumPoints = 0;
        int highPoints = 0;

        for (RewardClientResponse reward : allRewards) {
            int points = reward.getPointsEarned();
            if (points <= 50) lowPoints++;
            else if (points <= 150) mediumPoints++;
            else highPoints++;
        }

        chartPointsDistribution.getData().addAll(
                new PieChart.Data("Low (≤50)", lowPoints),
                new PieChart.Data("Medium (51-150)", mediumPoints),
                new PieChart.Data("High (>150)", highPoints)
        );

        chartPointsDistribution.setTitle("Reward Distribution");
        chartPointsDistribution.setLegendVisible(true);
        chartPointsDistribution.setLabelsVisible(true);
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
        progressIndicator.setVisible(loading);
        tableRewards.setDisable(loading);
        btnRefresh.setDisable(loading);
    }

    // Table item class
    public static class RewardTransaction {
        private Long transactionId;
        private Integer pointsEarned;
        private Integer balance;
        private String date;

        public Long getTransactionId() { return transactionId; }
        public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }
        public Integer getPointsEarned() { return pointsEarned; }
        public void setPointsEarned(Integer pointsEarned) { this.pointsEarned = pointsEarned; }
        public Integer getBalance() { return balance; }
        public void setBalance(Integer balance) { this.balance = balance; }
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
    }

    // Placeholder API interface
    private interface ApiClient {
        List<RewardClientResponse> getRewardsByCustomer(Long customerId) throws Exception;
    }

    private static class ClientContext {
        private static ClientContext instance = new ClientContext();

        public static ClientContext getInstance() {
            return instance;
        }

        public ApiClient getApiClient() {
            return null;
        }
    }
}