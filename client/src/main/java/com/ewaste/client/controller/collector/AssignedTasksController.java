package com.ewaste.client.controller.collector;

import com.ewaste.client.api.CollectorApiClient;
import com.ewaste.client.api.PickupApiClient;
import com.ewaste.client.config.ClientContext;
import com.ewaste.client.controller.BaseController;
import com.ewaste.client.dto.response.CollectorClientResponse;
import com.ewaste.client.dto.response.PickupClientResponse;
import com.ewaste.client.navigation.AppScreen;
import com.ewaste.client.session.UserSession;
import javafx.application.Platform;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class AssignedTasksController extends BaseController {

    @FXML
    private TableView<PickupClientResponse> tasksTable;
    @FXML
    private TableColumn<PickupClientResponse, Number> idColumn;
    @FXML
    private TableColumn<PickupClientResponse, String> addressColumn;
    @FXML
    private TableColumn<PickupClientResponse, String> dateColumn;
    @FXML
    private TableColumn<PickupClientResponse, String> timeColumn;
    @FXML
    private TableColumn<PickupClientResponse, String> statusColumn;

    @FXML
    private Button markCollectedButton;
    @FXML
    private Button markDeliveredButton;
    @FXML
    private TextField centerIdField;
    @FXML
    private Button backButton;
    @FXML
    private Button refreshButton;
    @FXML
    private ProgressIndicator loadingIndicator;

    private PickupApiClient pickupApiClient;
    private CollectorApiClient collectorApiClient;
    private final ObservableList<PickupClientResponse> taskData = FXCollections.observableArrayList();
    private Long collectorId;

    @Override
    protected void onInitialize() {
        pickupApiClient = ClientContext.getInstance().getPickupApiClient();
        collectorApiClient = ClientContext.getInstance().getCollectorApiClient();

        setupTableColumns();
        resolveCollectorAndLoadTasks();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getPickupId()));
        addressColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getAddress() != null ? cellData.getValue().getAddress() : "-"));

        // Use scheduledDate or createdAt instead of preferredDate
        dateColumn.setCellValueFactory(cellData -> {
            String date = cellData.getValue().getScheduledDate() != null ?
                    cellData.getValue().getScheduledDate().toString() :
                    (cellData.getValue().getCreatedAt() != null ?
                            cellData.getValue().getCreatedAt().toString() : "-");
            return new SimpleStringProperty(date);
        });

        timeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getPreferredTime() != null ?
                        cellData.getValue().getPreferredTime() : "-"));

        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getCurrentState() != null ?
                        cellData.getValue().getCurrentState() : "-"));

        tasksTable.setItems(taskData);
        tasksTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> updateButtonStates(newSel));
    }

    private void updateButtonStates(PickupClientResponse selected) {
        if (selected == null) {
            markCollectedButton.setDisable(true);
            markDeliveredButton.setDisable(true);
            return;
        }

        String status = selected.getCurrentState();
        markCollectedButton.setDisable(!"ASSIGNED".equalsIgnoreCase(status));
        markDeliveredButton.setDisable(!"COLLECTED".equalsIgnoreCase(status));
    }

    private void resolveCollectorAndLoadTasks() {
        setLoading(true);
        long userId = userSession.getUserId();

        new Thread(() -> {
            try {
                List<CollectorClientResponse> collectors = collectorApiClient.getAllCollectors();
                CollectorClientResponse profile = collectors.stream()
                        .filter(c -> c.getUserId() != null && c.getUserId().equals(userId))
                        .findFirst()
                        .orElse(null);

                Platform.runLater(() -> {
                    if (profile != null) {
                        collectorId = profile.getCollectorId();
                        loadTasks();
                    } else {
                        setLoading(false);
                        showError("Profile Error", "No collector profile associated with current account.", "");
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    setLoading(false);
                    showError("Network Failure", "Failed to resolve collector profile identity.", e.getMessage());
                });
            }
        }).start();
    }

    private void loadTasks() {
        if (collectorId == null) {
            return;
        }
        setLoading(true);

        new Thread(() -> {
            try {
                List<PickupClientResponse> pickups = pickupApiClient.getPickupsForCollector(collectorId);

                Platform.runLater(() -> {
                    setLoading(false);
                    if (pickups != null) {
                        taskData.setAll(pickups);
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    setLoading(false);
                    showError("Failed to Load Tasks", "Error communicating with backend.", e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    private void handleMarkCollected() {
        PickupClientResponse selected = tasksTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("No Selection", "Please select a task to mark as collected.", "");
            return;
        }

        setLoading(true);

        new Thread(() -> {
            try {
                PickupClientResponse updated = pickupApiClient.updateState(
                        selected.getPickupId(), "COLLECTED", null);

                Platform.runLater(() -> {
                    setLoading(false);
                    showInfo("Pickup Collected", "Pickup #" + selected.getPickupId() + " marked as COLLECTED.", "");
                    loadTasks();
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    setLoading(false);
                    showError("Transition Failed", "Could not update state.", e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    private void handleMarkDelivered() {
        PickupClientResponse selected = tasksTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("No Selection", "Please select a task to mark as delivered.", "");
            return;
        }

        String centerText = centerIdField.getText();
        if (centerText == null || centerText.trim().isEmpty()) {
            showWarning("Input Required", "Please enter a Destination Recycling Center ID.", "");
            return;
        }

        long centerId;
        try {
            centerId = Long.parseLong(centerText.trim());
        } catch (NumberFormatException e) {
            showWarning("Invalid Input", "Recycling Center ID must be a valid numeric value.", "");
            return;
        }

        setLoading(true);

        new Thread(() -> {
            try {
                PickupClientResponse updated = pickupApiClient.updateState(
                        selected.getPickupId(), "DELIVERED", centerId);

                Platform.runLater(() -> {
                    setLoading(false);
                    showInfo("Cargo Delivered", "Pickup #" + selected.getPickupId() +
                            " marked as DELIVERED to center #" + centerId + ".", "");
                    centerIdField.clear();
                    loadTasks();
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    setLoading(false);
                    showError("Delivery Failed", "Could not complete delivery transition.", e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    private void handleBack() {
        navigateTo(AppScreen.COLLECTOR_DASHBOARD);
    }

    @FXML
    private void handleRefresh() {
        loadTasks();
    }

    private void setLoading(boolean isLoading) {
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(isLoading);
        }
        if (refreshButton != null) {
            refreshButton.setDisable(isLoading);
        }
        if (markCollectedButton != null) {
            markCollectedButton.setDisable(isLoading);
        }
        if (markDeliveredButton != null) {
            markDeliveredButton.setDisable(isLoading);
        }
        if (backButton != null) {
            backButton.setDisable(isLoading);
        }
    }
}