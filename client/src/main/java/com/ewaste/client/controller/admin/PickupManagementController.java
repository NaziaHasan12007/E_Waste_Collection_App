package com.ewaste.client.controller.admin;

import com.ewaste.client.api.CollectorApiClient;
import com.ewaste.client.api.PickupApiClient;
import com.ewaste.client.controller.BaseController;
import com.ewaste.client.dto.response.CollectorClientResponse;
import com.ewaste.client.dto.response.PickupClientResponse;
import com.ewaste.client.navigation.AppScreen;
import com.ewaste.client.navigation.SceneNavigator;
import com.ewaste.client.util.AlertHelper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class PickupManagementController extends BaseController {

    @FXML private TableView<PickupClientResponse> pickupsTable;
    @FXML private TableColumn<PickupClientResponse, Number> idColumn;
    @FXML private TableColumn<PickupClientResponse, Number> customerIdColumn;
    @FXML private TableColumn<PickupClientResponse, String> addressColumn;
    @FXML private TableColumn<PickupClientResponse, String> statusColumn;
    @FXML private TableColumn<PickupClientResponse, Number> priorityColumn;
    @FXML private TableColumn<PickupClientResponse, String> collectorColumn;

    @FXML private ComboBox<String> stateFilterCombo;
    @FXML private ComboBox<CollectorClientResponse> collectorAssignmentCombo;
    @FXML private Button assignButton;
    @FXML private Button cancelButton;
    @FXML private Button refreshButton;
    @FXML private Button backButton;
    @FXML private ProgressIndicator loadingIndicator;

    private final PickupApiClient pickupApiClient = new PickupApiClient();
    private final CollectorApiClient collectorApiClient = new CollectorApiClient();
    private final ObservableList<PickupClientResponse> pickupData = FXCollections.observableArrayList();
    private final ObservableList<CollectorClientResponse> availableCollectors = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        validateSession();
        setupTable();
        setupFilters();
        loadPickups();
        loadCollectors();
    }

    private void setupTable() {
        idColumn.setCellValueFactory(c -> new SimpleLongProperty(c.getValue().getPickupId()));
        customerIdColumn.setCellValueFactory(c -> new SimpleLongProperty(c.getValue().getUserId()));
        addressColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAddress()));
        statusColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));
        priorityColumn.setCellValueFactory(c -> new SimpleDoubleProperty(
                c.getValue().getPriorityScore() != null ? c.getValue().getPriorityScore() : 0.0));
        collectorColumn.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getCollectorId() != null ? "#" + c.getValue().getCollectorId() : "Unassigned"));

        pickupsTable.setItems(pickupData);
        pickupsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean disable = (newSel == null) || "COMPLETED".equalsIgnoreCase(newSel.getStatus())
                    || "CANCELLED".equalsIgnoreCase(newSel.getStatus());
            assignButton.setDisable(disable);
            cancelButton.setDisable(disable);
        });
    }

    private void setupFilters() {
        stateFilterCombo.setItems(FXCollections.observableArrayList(
                "ALL", "SUBMITTED", "REQUESTED", "ASSIGNED", "COLLECTED", "DELIVERED", "PROCESSING", "COMPLETED", "CANCELLED"
        ));
        stateFilterCombo.setValue("ALL");
        stateFilterCombo.valueProperty().addListener((obs, oldV, newV) -> loadPickups());

        collectorAssignmentCombo.setItems(availableCollectors);
        collectorAssignmentCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(CollectorClientResponse item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("#%d - %s (%s, %.1f kg)",
                        item.getCollectorId(), item.getArea(), item.getVehicleType(), item.getCurrentWorkloadKg()));
            }
        });
        collectorAssignmentCombo.setButtonCell(collectorAssignmentCombo.getCellFactory().call(null));
    }

    private void loadPickups() {
        setLoading(true);
        String selectedFilter = stateFilterCombo.getValue();
        String filterParam = "ALL".equalsIgnoreCase(selectedFilter) ? null : selectedFilter;

        Task<List<PickupClientResponse>> task = new Task<>() {
            @Override
            protected List<PickupClientResponse> call() {
                return pickupApiClient.getAllPickups(filterParam);
            }
        };

        task.setOnSucceeded(e -> {
            setLoading(false);
            pickupData.setAll(task.getValue());
        });

        task.setOnFailed(e -> {
            setLoading(false);
            AlertHelper.showError("Data Error", "Unable to load pickup entries from backend.");
        });

        new Thread(task).start();
    }

    private void loadCollectors() {
        Task<List<CollectorClientResponse>> task = new Task<>() {
            @Override
            protected List<CollectorClientResponse> call() {
                return collectorApiClient.getAvailableCollectors();
            }
        };

        task.setOnSucceeded(e -> availableCollectors.setAll(task.getValue()));
        new Thread(task).start();
    }

    @FXML
    private void handleAssignCollector() {
        PickupClientResponse selected = pickupsTable.getSelectionModel().getSelectedItem();
        CollectorClientResponse chosenCollector = collectorAssignmentCombo.getValue();

        if (selected == null || chosenCollector == null) {
            AlertHelper.showWarning("Selection Missing", "Select both a target pickup and an eligible collector.");
            return;
        }

        setLoading(true);
        Task<PickupClientResponse> assignTask = new Task<>() {
            @Override
            protected PickupClientResponse call() {
                return pickupApiClient.assignCollector(selected.getPickupId(), chosenCollector.getCollectorId());
            }
        };

        assignTask.setOnSucceeded(e -> {
            setLoading(false);
            AlertHelper.showInfo("Assigned", "Pickup #" + selected.getPickupId() + " successfully assigned to Collector #" + chosenCollector.getCollectorId());
            loadPickups();
            loadCollectors();
        });

        assignTask.setOnFailed(e -> {
            setLoading(false);
            Throwable ex = assignTask.getException();
            AlertHelper.showError("Assignment Failed", ex != null ? ex.getMessage() : "Error completing assignment.");
        });

        new Thread(assignTask).start();
    }

    @FXML
    private void handleCancelPickup() {
        PickupClientResponse selected = pickupsTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        if (!AlertHelper.showConfirmation("Confirm Cancellation", "Are you sure you want to cancel Pickup #" + selected.getPickupId() + "?")) {
            return;
        }

        setLoading(true);
        Task<PickupClientResponse> cancelTask = new Task<>() {
            @Override
            protected PickupClientResponse call() {
                return pickupApiClient.cancelPickup(selected.getPickupId());
            }
        };

        cancelTask.setOnSucceeded(e -> {
            setLoading(false);
            AlertHelper.showInfo("Cancelled", "Pickup #" + selected.getPickupId() + " has been cancelled.");
            loadPickups();
        });

        cancelTask.setOnFailed(e -> {
            setLoading(false);
            AlertHelper.showError("Cancellation Failed", cancelTask.getException().getMessage());
        });

        new Thread(cancelTask).start();
    }

    @FXML
    private void handleRefresh() {
        loadPickups();
        loadCollectors();
    }

    @FXML
    private void handleBack() {
        SceneNavigator.loadScreen(AppScreen.ADMIN_DASHBOARD);
    }

    private void setLoading(boolean isLoading) {
        if (loadingIndicator != null) loadingIndicator.setVisible(isLoading);
        if (refreshButton != null) refreshButton.setDisable(isLoading);
    }
}