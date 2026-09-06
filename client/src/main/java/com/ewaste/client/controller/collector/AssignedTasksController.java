package com.ewaste.client.controller.collector;

import com.ewaste.client.api.CollectorApiClient;
import com.ewaste.client.api.PickupApiClient;
import com.ewaste.client.controller.BaseController;
import com.ewaste.client.dto.response.CollectorClientResponse;
import com.ewaste.client.dto.response.PickupClientResponse;
import com.ewaste.client.navigation.AppScreen;
import com.ewaste.client.navigation.SceneNavigator;
import com.ewaste.client.session.UserSession;
import com.ewaste.client.util.AlertHelper;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class AssignedTasksController extends BaseController {

    @FXML private TableView<PickupClientResponse> tasksTable;
    @FXML private TableColumn<PickupClientResponse, Number> idColumn;
    @FXML private TableColumn<PickupClientResponse, String> addressColumn;
    @FXML private TableColumn<PickupClientResponse, String> dateColumn;
    @FXML private TableColumn<PickupClientResponse, String> timeColumn;
    @FXML private TableColumn<PickupClientResponse, String> statusColumn;

    @FXML private Button markCollectedButton;
    @FXML private Button markDeliveredButton;
    @FXML private TextField centerIdField;
    @FXML private Button backButton;
    @FXML private Button refreshButton;
    @FXML private ProgressIndicator loadingIndicator;

    private final PickupApiClient pickupApiClient = new PickupApiClient();
    private final CollectorApiClient collectorApiClient = new CollectorApiClient();
    private final ObservableList<PickupClientResponse> taskData = FXCollections.observableArrayList();
    private Long collectorId;

    @FXML
    public void initialize() {
        validateSession();
        setupTableColumns();
        resolveCollectorAndLoadTasks();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getPickupId()));
        addressColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress()));
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPreferredDate()));
        timeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPreferredTime()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));

        tasksTable.setItems(taskData);
        tasksTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> updateButtonStates(newSel));
    }

    private void updateButtonStates(PickupClientResponse selected) {
        if (selected == null) {
            markCollectedButton.setDisable(true);
            markDeliveredButton.setDisable(true);
            return;
        }

        String status = selected.getStatus();
        markCollectedButton.setDisable(!"ASSIGNED".equalsIgnoreCase(status));
        markDeliveredButton.setDisable(!"COLLECTED".equalsIgnoreCase(status));
    }

    private void resolveCollectorAndLoadTasks() {
        setLoading(true);
        long userId = UserSession.getInstance().getUserId();

        Task<CollectorClientResponse> profileTask = new Task<>() {
            @Override
            protected CollectorClientResponse call() {
                List<CollectorClientResponse> collectors = collectorApiClient.getAllCollectors();
                return collectors.stream()
                        .filter(c -> c.getUserId() != null && c.getUserId().equals(userId))
                        .findFirst()
                        .orElse(null);
            }
        };

        profileTask.setOnSucceeded(event -> {
            CollectorClientResponse profile = profileTask.getValue();
            if (profile != null) {
                collectorId = profile.getCollectorId();
                loadTasks();
            } else {
                setLoading(false);
                AlertHelper.showError("Profile Error", "No collector profile associated with current account.");
            }
        });

        profileTask.setOnFailed(event -> {
            setLoading(false);
            AlertHelper.showError("Network Failure", "Failed to resolve collector profile identity.");
        });

        new Thread(profileTask).start();
    }

    private void loadTasks() {
        if (collectorId == null) {
            return;
        }
        setLoading(true);

        Task<List<PickupClientResponse>> task = new Task<>() {
            @Override
            protected List<PickupClientResponse> call() {
                return pickupApiClient.getPickupsForCollector(collectorId);
            }
        };

        task.setOnSucceeded(event -> {
            setLoading(false);
            taskData.setAll(task.getValue());
        });

        task.setOnFailed(event -> {
            setLoading(false);
            Throwable ex = task.getException();
            AlertHelper.showError("Failed to Load Tasks", ex != null ? ex.getMessage() : "Error communicating with backend.");
        });

        new Thread(task).start();
    }

    @FXML
    private void handleMarkCollected() {
        PickupClientResponse selected = tasksTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        setLoading(true);
        Task<PickupClientResponse> updateTask = new Task<>() {
            @Override
            protected PickupClientResponse call() {
                return pickupApiClient.updateState(selected.getPickupId(), "COLLECT", null);
            }
        };

        updateTask.setOnSucceeded(event -> {
            setLoading(false);
            AlertHelper.showInfo("Pickup Collected", "Pickup #" + selected.getPickupId() + " marked as COLLECTED.");
            loadTasks();
        });

        updateTask.setOnFailed(event -> {
            setLoading(false);
            Throwable ex = updateTask.getException();
            AlertHelper.showError("Transition Failed", ex != null ? ex.getMessage() : "Could not update state.");
        });

        new Thread(updateTask).start();
    }

    @FXML
    private void handleMarkDelivered() {
        PickupClientResponse selected = tasksTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        String centerText = centerIdField.getText();
        if (centerText == null || centerText.trim().isEmpty()) {
            AlertHelper.showWarning("Input Required", "Please enter a Destination Recycling Center ID.");
            return;
        }

        long centerId;
        try {
            centerId = Long.parseLong(centerText.trim());
        } catch (NumberFormatException e) {
            AlertHelper.showWarning("Invalid Input", "Recycling Center ID must be a valid numeric value.");
            return;
        }

        setLoading(true);
        Task<PickupClientResponse> updateTask = new Task<>() {
            @Override
            protected PickupClientResponse call() {
                return pickupApiClient.updateState(selected.getPickupId(), "DELIVER", centerId);
            }
        };

        updateTask.setOnSucceeded(event -> {
            setLoading(false);
            AlertHelper.showInfo("Cargo Delivered", "Pickup #" + selected.getPickupId() + " marked as DELIVERED to center #" + centerId + ".");
            centerIdField.clear();
            loadTasks();
        });

        updateTask.setOnFailed(event -> {
            setLoading(false);
            Throwable ex = updateTask.getException();
            AlertHelper.showError("Delivery Failed", ex != null ? ex.getMessage() : "Could not complete delivery transition.");
        });

        new Thread(updateTask).start();
    }

    @FXML
    private void handleBack() {
        SceneNavigator.loadScreen(AppScreen.COLLECTOR_DASHBOARD);
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
    }
}