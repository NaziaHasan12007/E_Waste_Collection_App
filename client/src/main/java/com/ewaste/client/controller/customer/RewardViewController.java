package com.ewaste.client.controller.customer;

import com.ewaste.client.api.RewardApiClient;
import com.ewaste.client.controller.BaseController;
import com.ewaste.client.dto.response.RewardClientResponse;
import com.ewaste.client.navigation.AppScreen;
import com.ewaste.client.navigation.SceneNavigator;
import com.ewaste.client.util.AlertHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controller displaying customer reward point balance and calculation history ledger.
 */
public class RewardViewController extends BaseController {

    @FXML private Label totalPointsLabel;
    @FXML private TableView<RewardClientResponse.RewardEntry> ledgerTable;
    @FXML private TableColumn<RewardClientResponse.RewardEntry, String> idColumn;
    @FXML private TableColumn<RewardClientResponse.RewardEntry, String> pickupIdColumn;
    @FXML private TableColumn<RewardClientResponse.RewardEntry, String> pointsColumn;
    @FXML private TableColumn<RewardClientResponse.RewardEntry, String> basisColumn;
    @FXML private TableColumn<RewardClientResponse.RewardEntry, String> dateColumn;

    @FXML private ProgressIndicator loadingIndicator;

    private final RewardApiClient rewardApiClient = new RewardApiClient();
    private final ObservableList<RewardClientResponse.RewardEntry> entries = FXCollections.observableArrayList();

    @FXML
    @Override
    public void initialize() {
        super.initialize();
        if (loadingIndicator != null) loadingIndicator.setVisible(false);

        idColumn.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getRewardId())));
        pickupIdColumn.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getPickupId())));
        pointsColumn.setCellValueFactory(c -> new SimpleStringProperty("+" + c.getValue().getPoints()));
        basisColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCalculationBasis()));
        dateColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCreatedAt()));

        ledgerTable.setItems(entries);
        loadRewardLedger();
    }

    private void loadRewardLedger() {
        setLoading(true);
        Task<RewardClientResponse> task = new Task<>() {
            @Override
            protected RewardClientResponse call() throws Exception {
                return rewardApiClient.getCustomerRewardSummary(session.getUserId());
            }
        };

        task.setOnSucceeded(e -> {
            setLoading(false);
            RewardClientResponse response = task.getValue();
            if (response != null) {
                totalPointsLabel.setText(response.getTotalPoints() + " Pts");
                if (response.getEntries() != null) {
                    entries.setAll(response.getEntries());
                }
            }
        });

        task.setOnFailed(e -> {
            setLoading(false);
            log.error("Failed to load rewards", task.getException());
            AlertHelper.showError("Error", "Could not load reward ledger.");
        });

        runAsync(task);
    }

    @FXML
    private void handleBackToDashboard() {
        SceneNavigator.loadScreen(AppScreen.CUSTOMER_DASHBOARD, false);
    }

    private void setLoading(boolean isLoading) {
        if (loadingIndicator != null) loadingIndicator.setVisible(isLoading);
    }
}