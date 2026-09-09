package com.ewaste.client.controller.admin;

import com.ewaste.client.api.CollectorApiClient;
import com.ewaste.client.controller.BaseController;
import com.ewaste.client.dto.response.CollectorClientResponse;
import com.ewaste.client.navigation.AppScreen;
import com.ewaste.client.navigation.SceneNavigator;
import com.ewaste.client.util.AlertHelper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class CollectorManagementController extends BaseController {

    @FXML private TableView<CollectorClientResponse> collectorsTable;
    @FXML private TableColumn<CollectorClientResponse, Number> idColumn;
    @FXML private TableColumn<CollectorClientResponse, String> nameColumn;
    @FXML private TableColumn<CollectorClientResponse, String> phoneColumn;
    @FXML private TableColumn<CollectorClientResponse, String> areaColumn;
    @FXML private TableColumn<CollectorClientResponse, String> vehicleColumn;
    @FXML private TableColumn<CollectorClientResponse, Boolean> hazardousColumn;
    @FXML private TableColumn<CollectorClientResponse, Boolean> availableColumn;
    @FXML private TableColumn<CollectorClientResponse, Number> workloadColumn;
    @FXML private TableColumn<CollectorClientResponse, Number> maxCapacityColumn;

    @FXML private Button toggleAvailabilityButton;
    @FXML private Button refreshButton;
    @FXML private Button backButton;
    @FXML private ProgressIndicator loadingIndicator;

    private final CollectorApiClient collectorApiClient = new CollectorApiClient();
    private final ObservableList<CollectorClientResponse> collectorData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        validateSession();
        setupTable();
        loadCollectors();
    }

    private void setupTable() {
        idColumn.setCellValueFactory(c -> new SimpleLongProperty(c.getValue().getCollectorId()));
        nameColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName() != null ? c.getValue().getName() : "-"));
        phoneColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPhone() != null ? c.getValue().getPhone() : "-"));
        areaColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getArea()));
        vehicleColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getVehicleType()));
        hazardousColumn.setCellValueFactory(c -> new SimpleBooleanProperty(Boolean.TRUE.equals(c.getValue().getIsHazardousCapable())));
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

        Task<List<CollectorClientResponse>> task = new Task<>() {
            @Override
            protected List<CollectorClientResponse> call() {
                return collectorApiClient.getAllCollectors();
            }
        };

        task.setOnSucceeded(e -> {
            setLoading(false);
            collectorData.setAll(task.getValue());
        });

        task.setOnFailed(e -> {
            setLoading(false);
            AlertHelper.showError("Failure", "Could not fetch collector records.");
        });

        new Thread(task).start();
    }

    @FXML
    private void handleToggleAvailability() {
        CollectorClientResponse selected = collectorsTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        boolean updatedAvailability = !Boolean.TRUE.equals(selected.getIsAvailable());
        setLoading(true);

        Task<CollectorClientResponse> toggleTask = new Task<>() {
            @Override
            protected CollectorClientResponse call() {
                return collectorApiClient.updateAvailability(selected.getCollectorId(), updatedAvailability);
            }
        };

        toggleTask.setOnSucceeded(e -> {
            setLoading(false);
            AlertHelper.showInfo("Updated", "Collector #" + selected.getCollectorId() + " is now " + (updatedAvailability ? "ACTIVE" : "INACTIVE"));
            loadCollectors();
        });

        toggleTask.setOnFailed(e -> {
            setLoading(false);
            AlertHelper.showError("Update Failed", toggleTask.getException().getMessage());
        });

        new Thread(toggleTask).start();
    }

    @FXML
    private void handleRefresh() {
        loadCollectors();
    }

    @FXML
    private void handleBack() {
        SceneNavigator.loadScreen(AppScreen.ADMIN_DASHBOARD);
    }

    private void setLoading(boolean isLoading) {
        if (loadingIndicator != null) loadingIndicator.setVisible(isLoading);
        if (refreshButton != null) refreshButton.setDisable(isLoading);
        if (toggleAvailabilityButton != null && isLoading) toggleAvailabilityButton.setDisable(true);
    }
}