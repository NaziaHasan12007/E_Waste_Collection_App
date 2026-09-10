package com.ewaste.client.controller.admin;

import com.ewaste.client.api.CollectorApiClient;
import com.ewaste.client.config.ClientContext;
import com.ewaste.client.controller.BaseController;
import com.ewaste.client.dto.response.CollectorClientResponse;
import com.ewaste.client.navigation.AppScreen;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class CollectorManagementController extends BaseController {

    @FXML
    private TableView<CollectorClientResponse> collectorsTable;
    @FXML
    private TableColumn<CollectorClientResponse, Number> idColumn;
    @FXML
    private TableColumn<CollectorClientResponse, String> nameColumn;
    @FXML
    private TableColumn<CollectorClientResponse, String> vehicleColumn;
    @FXML
    private TableColumn<CollectorClientResponse, Boolean> availableColumn;
    @FXML
    private TableColumn<CollectorClientResponse, Number> workloadColumn;
    @FXML
    private TableColumn<CollectorClientResponse, Number> maxCapacityColumn;

    @FXML
    private Button toggleAvailabilityButton;
    @FXML
    private Button refreshButton;
    @FXML
    private Button backButton;
    @FXML
    private ProgressIndicator loadingIndicator;

    private CollectorApiClient collectorApiClient;
    private final ObservableList<CollectorClientResponse> collectorData = FXCollections.observableArrayList();

    @Override
    protected void onInitialize() {
        collectorApiClient = ClientContext.getInstance().getCollectorApiClient();
        setupTable();
        loadCollectors();
    }

    private void setupTable() {
        idColumn.setCellValueFactory(c -> new SimpleLongProperty(c.getValue().getCollectorId()));
        nameColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFullName() != null ? c.getValue().getFullName() : "-"));
        vehicleColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getVehicleType() != null ? c.getValue().getVehicleType() : "-"));
        availableColumn.setCellValueFactory(c -> new SimpleBooleanProperty(Boolean.TRUE.equals(c.getValue().getIsAvailable())));
        workloadColumn.setCellValueFactory(c -> new SimpleDoubleProperty(
                c.getValue().getCurrentWorkloadKg() != null ? c.getValue().getCurrentWorkloadKg() : 0.0));
        maxCapacityColumn.setCellValueFactory(c -> new SimpleDoubleProperty(
                c.getValue().getMaxCapacityKg() != null ? c.getValue().getMaxCapacityKg() : 0.0));

        collectorsTable.setItems(collectorData);
        collectorsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            toggleAvailabilityButton.setDisable(newSel == null);
            if (newSel != null) {
                boolean active = Boolean.TRUE.equals(newSel.getIsAvailable());
                toggleAvailabilityButton.setText(active ? "Set Unavailable" : "Set Available");
            }
        });
    }

    private void loadCollectors() {
        setLoading(true);

        new Thread(() -> {
            try {
                List<CollectorClientResponse> collectors = collectorApiClient.getAllCollectors();

                Platform.runLater(() -> {
                    setLoading(false);
                    if (collectors != null) {
                        collectorData.setAll(collectors);
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    setLoading(false);
                    showError("Failure", "Could not fetch collector records.", e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    private void handleToggleAvailability() {
        CollectorClientResponse selected = collectorsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("No Selection", "Please select a collector to update.", "");
            return;
        }

        boolean updatedAvailability = !Boolean.TRUE.equals(selected.getIsAvailable());
        setLoading(true);

        new Thread(() -> {
            try {
                CollectorClientResponse updated = collectorApiClient.updateCollectorAvailability(
                        selected.getCollectorId(), updatedAvailability);

                Platform.runLater(() -> {
                    setLoading(false);
                    showInfo("Updated", "Collector #" + selected.getCollectorId() +
                            " is now " + (updatedAvailability ? "ACTIVE" : "INACTIVE"), "");
                    loadCollectors();
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    setLoading(false);
                    showError("Update Failed", "Could not update collector status.", e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    private void handleRefresh() {
        loadCollectors();
    }

    @FXML
    private void handleBack() {
        navigateTo(AppScreen.ADMIN_DASHBOARD);
    }

    private void setLoading(boolean isLoading) {
        if (loadingIndicator != null) loadingIndicator.setVisible(isLoading);
        if (refreshButton != null) refreshButton.setDisable(isLoading);
        if (toggleAvailabilityButton != null && isLoading) toggleAvailabilityButton.setDisable(true);
        if (backButton != null) backButton.setDisable(isLoading);
    }
}