package com.ewaste.client.controller.admin;

import com.ewaste.client.api.CollectorApiClient;
import com.ewaste.client.api.PickupApiClient;
import com.ewaste.client.config.ClientContext;
import com.ewaste.client.controller.BaseController;
import com.ewaste.client.dto.response.CollectorClientResponse;
import com.ewaste.client.dto.response.PickupClientResponse;
import com.ewaste.client.navigation.AppScreen;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class PickupManagementController extends BaseController {

    @FXML
    private TableView<PickupClientResponse> pickupsTable;
    @FXML
    private TableColumn<PickupClientResponse, Number> idColumn;
    @FXML
    private TableColumn<PickupClientResponse, Number> customerIdColumn;
    @FXML
    private TableColumn<PickupClientResponse, String> addressColumn;
    @FXML
    private TableColumn<PickupClientResponse, String> statusColumn;
    @FXML
    private TableColumn<PickupClientResponse, Number> priorityColumn;
    @FXML
    private TableColumn<PickupClientResponse, String> collectorColumn;

    @FXML
    private ComboBox<String> stateFilterCombo;
    @FXML
    private ComboBox<CollectorClientResponse> collectorAssignmentCombo;
    @FXML
    private Button assignButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button refreshButton;
    @FXML
    private Button backButton;
    @FXML
    private ProgressIndicator loadingIndicator;

    private PickupApiClient pickupApiClient;
    private CollectorApiClient collectorApiClient;
    private final ObservableList<PickupClientResponse> pickupData = FXCollections.observableArrayList();
    private final ObservableList<CollectorClientResponse> availableCollectors = FXCollections.observableArrayList();

    @Override
    protected void onInitialize() {
        pickupApiClient = ClientContext.getInstance().getPickupApiClient();
        collectorApiClient = ClientContext.getInstance().getCollectorApiClient();

        setupTable();
        setupFilters();
        loadPickups();
        loadCollectors();
    }

    private void setupTable() {
        idColumn.setCellValueFactory(c -> new SimpleLongProperty(c.getValue().getPickupId()));
        customerIdColumn.setCellValueFactory(c -> new SimpleLongProperty(c.getValue().getCustomerId()));
        addressColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAddress()));
        statusColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCurrentState()));
        priorityColumn.setCellValueFactory(c -> new SimpleDoubleProperty(
                c.getValue().getPriorityScore() != null ? c.getValue().getPriorityScore() : 0.0));
        collectorColumn.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getCollectorId() != null ? "#" + c.getValue().getCollectorId() : "Unassigned"));

        pickupsTable.setItems(pickupData);
        pickupsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean disable = (newSel == null) || "COMPLETED".equalsIgnoreCase(newSel.getCurrentState())
                    || "CANCELLED".equalsIgnoreCase(newSel.getCurrentState());
            assignButton.setDisable(disable);
            cancelButton.setDisable(disable);
        });
    }

    private void setupFilters() {
        stateFilterCombo.setItems(FXCollections.observableArrayList(
                "ALL", "PENDING", "ASSIGNED", "IN_PROGRESS", "COMPLETED", "CANCELLED"
        ));
        stateFilterCombo.setValue("ALL");
        stateFilterCombo.valueProperty().addListener((obs, oldV, newV) -> loadPickups());

        collectorAssignmentCombo.setItems(availableCollectors);
        collectorAssignmentCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(CollectorClientResponse item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%s (%.1f/%.1f kg)",
                        item.getFullName(),
                        item.getCurrentWorkloadKg() != null ? item.getCurrentWorkloadKg() : 0.0,
                        item.getMaxCapacityKg() != null ? item.getMaxCapacityKg() : 0.0));
            }
        });
        collectorAssignmentCombo.setButtonCell(collectorAssignmentCombo.getCellFactory().call(null));
    }

    private void loadPickups() {
        setLoading(true);
        String selectedFilter = stateFilterCombo.getValue();
        String filterParam = "ALL".equalsIgnoreCase(selectedFilter) ? null : selectedFilter;

        new Thread(() -> {
            try {
                List<PickupClientResponse> pickups = pickupApiClient.getAllPickups(filterParam);

                Platform.runLater(() -> {
                    setLoading(false);
                    if (pickups != null) {
                        pickupData.setAll(pickups);
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    setLoading(false);
                    showError("Data Error", "Unable to load pickup entries from backend.", e.getMessage());
                });
            }
        }).start();
    }

    private void loadCollectors() {
        new Thread(() -> {
            try {
                List<CollectorClientResponse> collectors = collectorApiClient.getAllCollectors();

                Platform.runLater(() -> {
                    if (collectors != null) {
                        availableCollectors.setAll(collectors.stream()
                                .filter(c -> Boolean.TRUE.equals(c.getIsAvailable()))
                                .collect(java.util.stream.Collectors.toList()));
                    }
                });

            } catch (Exception e) {
                // Log error silently
            }
        }).start();
    }

    @FXML
    private void handleAssignCollector() {
        PickupClientResponse selected = pickupsTable.getSelectionModel().getSelectedItem();
        CollectorClientResponse chosenCollector = collectorAssignmentCombo.getValue();

        if (selected == null || chosenCollector == null) {
            showWarning("Selection Missing", "Select both a target pickup and an eligible collector.", "");
            return;
        }

        setLoading(true);

        new Thread(() -> {
            try {
                PickupClientResponse updated = pickupApiClient.assignCollector(
                        selected.getPickupId(), chosenCollector.getCollectorId());

                Platform.runLater(() -> {
                    setLoading(false);
                    showInfo("Assigned", "Pickup #" + selected.getPickupId() +
                            " successfully assigned to Collector " + chosenCollector.getFullName(), "");
                    loadPickups();
                    loadCollectors();
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    setLoading(false);
                    showError("Assignment Failed", "Error completing assignment.", e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    private void handleCancelPickup() {
        PickupClientResponse selected = pickupsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("No Selection", "Please select a pickup to cancel.", "");
            return;
        }

        if (!showConfirmation("Confirm Cancellation", "Cancel Pickup",
                "Are you sure you want to cancel Pickup #" + selected.getPickupId() + "?")) {
            return;
        }

        setLoading(true);

        new Thread(() -> {
            try {
                PickupClientResponse updated = pickupApiClient.cancelPickup(selected.getPickupId());

                Platform.runLater(() -> {
                    setLoading(false);
                    showInfo("Cancelled", "Pickup #" + selected.getPickupId() + " has been cancelled.", "");
                    loadPickups();
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    setLoading(false);
                    showError("Cancellation Failed", "Could not cancel pickup.", e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    private void handleRefresh() {
        loadPickups();
        loadCollectors();
    }

    @FXML
    private void handleBack() {
        navigateTo(AppScreen.ADMIN_DASHBOARD);
    }

    private void setLoading(boolean isLoading) {
        if (loadingIndicator != null) loadingIndicator.setVisible(isLoading);
        if (refreshButton != null) refreshButton.setDisable(isLoading);
        if (backButton != null) backButton.setDisable(isLoading);
        if (assignButton != null) assignButton.setDisable(isLoading);
        if (cancelButton != null) cancelButton.setDisable(isLoading);
    }
}