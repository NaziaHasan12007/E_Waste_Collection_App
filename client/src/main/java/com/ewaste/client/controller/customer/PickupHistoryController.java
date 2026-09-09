package com.ewaste.client.controller.customer;

import com.ewaste.client.api.PickupApiClient;
import com.ewaste.client.controller.BaseController;
import com.ewaste.client.dto.response.PickupClientResponse;
import com.ewaste.client.navigation.AppScreen;
import com.ewaste.client.navigation.SceneNavigator;
import com.ewaste.client.util.AlertHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

/**
 * Controller displaying customer pickup history and tracking workflow statuses.
 */
public class PickupHistoryController extends BaseController {

    @FXML private TableView<PickupClientResponse> pickupTable;
    @FXML private TableColumn<PickupClientResponse, String> idColumn;
    @FXML private TableColumn<PickupClientResponse, String> dateColumn;
    @FXML private TableColumn<PickupClientResponse, String> addressColumn;
    @FXML private TableColumn<PickupClientResponse, String> statusColumn;
    @FXML private TableColumn<PickupClientResponse, String> priorityColumn;

    @FXML private Button cancelPickupButton;
    @FXML private ProgressIndicator loadingIndicator;

    private final PickupApiClient pickupApiClient = new PickupApiClient();
    private final ObservableList<PickupClientResponse> dataList = FXCollections.observableArrayList();

    @FXML
    @Override
    public void initialize() {
        super.initialize();
        if (loadingIndicator != null) loadingIndicator.setVisible(false);

        idColumn.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getPickupId())));
        dateColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPreferredDate() + " " + c.getValue().getPreferredTime()));
        addressColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAddress()));
        statusColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));
        priorityColumn.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.1f", c.getValue().getPriorityScore())));

        pickupTable.setItems(dataList);
        loadPickups();
    }

    private void loadPickups() {
        setLoading(true);
        Task<List<PickupClientResponse>> task = new Task<>() {
            @Override
            protected List<PickupClientResponse> call() throws Exception {
                return pickupApiClient.getPickupsForCustomer(session.getUserId());
            }
        };

        task.setOnSucceeded(e -> {
            setLoading(false);
            dataList.setAll(task.getValue());
        });

        task.setOnFailed(e -> {
            setLoading(false);
            log.error("Failed to load customer pickups", task.getException());
            AlertHelper.showError("Data Error", "Unable to load pickup history.");
        });

        runAsync(task);
    }

    @FXML
    private void handleCancelSelectedPickup() {
        PickupClientResponse selected = pickupTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Selection Error", "Please select a pickup to cancel.");
            return;
        }

        if ("COMPLETED".equalsIgnoreCase(selected.getStatus()) || "DELIVERED".equalsIgnoreCase(selected.getStatus())) {
            AlertHelper.showWarning("Action Prohibited", "Completed or delivered pickups cannot be cancelled.");
            return;
        }

        setLoading(true);
        Task<Void> cancelTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                pickupApiClient.cancelPickup(selected.getPickupId());
                return null;
            }
        };

        cancelTask.setOnSucceeded(e -> {
            setLoading(false);
            AlertHelper.showInfo("Cancelled", "Pickup request has been cancelled.");
            loadPickups();
        });

        cancelTask.setOnFailed(e -> {
            setLoading(false);
            AlertHelper.showError("Cancellation Failed", cancelTask.getException().getMessage());
        });

        runAsync(cancelTask);
    }

    @FXML
    private void handleBackToDashboard() {
        SceneNavigator.loadScreen(AppScreen.CUSTOMER_DASHBOARD, false);
    }

    private void setLoading(boolean isLoading) {
        if (loadingIndicator != null) loadingIndicator.setVisible(isLoading);
    }
}