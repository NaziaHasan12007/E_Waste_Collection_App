package com.ewaste.client.controller.admin;

import com.ewaste.client.api.EWasteApiClient;
import com.ewaste.client.api.PickupApiClient;
import com.ewaste.client.api.ProcessingApiClient;
import com.ewaste.client.controller.BaseController;
import com.ewaste.client.dto.request.ProcessItemClientRequest;
import com.ewaste.client.dto.response.PickupClientResponse;
import com.ewaste.client.dto.response.ProcessingOutcomeClientResponse;
import com.ewaste.client.navigation.AppScreen;
import com.ewaste.client.navigation.SceneNavigator;
import com.ewaste.client.util.AlertHelper;
import javafx.application.Platform;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.Map;

public class InspectionProcessingController extends BaseController {

    @FXML private TableView<PickupClientResponse> deliveredPickupsTable;
    @FXML private TableColumn<PickupClientResponse, Number> pickupIdColumn;
    @FXML private TableColumn<PickupClientResponse, String> pickupAddressColumn;
    @FXML private TableColumn<PickupClientResponse, String> pickupDateColumn;

    @FXML private ComboBox<Map<String, Object>> centerComboBox;
    @FXML private ComboBox<String> workflowTypeComboBox;
    @FXML private TextField targetItemIdField;
    @FXML private TextArea inspectionNotesArea;
    @FXML private Button executeWorkflowButton;
    @FXML private Button backButton;
    @FXML private ProgressIndicator loadingIndicator;

    private final PickupApiClient pickupApiClient = new PickupApiClient();
    private final ProcessingApiClient processingApiClient = new ProcessingApiClient();
    private final ObservableList<PickupClientResponse> deliveredPickups = FXCollections.observableArrayList();
    private final ObservableList<Map<String, Object>> facilityCenters = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        validateSession();
        setupTable();
        setupForm();
        loadDeliveredPickups();
        loadCenters();
    }

    private void setupTable() {
        pickupIdColumn.setCellValueFactory(c -> new SimpleLongProperty(c.getValue().getPickupId()));
        pickupAddressColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAddress()));
        pickupDateColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPreferredDate()));

        deliveredPickupsTable.setItems(deliveredPickups);
        deliveredPickupsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null && newSel.getItemIds() != null && !newSel.getItemIds().isEmpty()) {
                targetItemIdField.setText(String.valueOf(newSel.getItemIds().get(0)));
            } else {
                targetItemIdField.clear();
            }
        });
    }

    private void setupForm() {
        workflowTypeComboBox.setItems(FXCollections.observableArrayList(
                "RECYCLE", "REFURBISH", "REPAIR", "HAZARDOUS_DISPOSAL"
        ));
        workflowTypeComboBox.setValue("RECYCLE");

        centerComboBox.setItems(facilityCenters);
        centerComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Map<String, Object> item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("#%s - %s", item.get("centerId"), item.get("name")));
            }
        });
        centerComboBox.setButtonCell(centerComboBox.getCellFactory().call(null));
    }

    private void loadDeliveredPickups() {
        setLoading(true);
        Task<List<PickupClientResponse>> task = new Task<>() {
            @Override
            protected List<PickupClientResponse> call() {
                return pickupApiClient.getAllPickups("DELIVERED");
            }
        };

        task.setOnSucceeded(e -> {
            setLoading(false);
            deliveredPickups.setAll(task.getValue());
        });

        task.setOnFailed(e -> {
            setLoading(false);
            AlertHelper.showError("Failure", "Unable to load delivered inventory awaiting inspection.");
        });

        new Thread(task).start();
    }

    private void loadCenters() {
        Task<List<Map<String, Object>>> task = new Task<>() {
            @Override
            protected List<Map<String, Object>> call() {
                return processingApiClient.getRecyclingCenters();
            }
        };

        task.setOnSucceeded(e -> {
            facilityCenters.setAll(task.getValue());
            if (!facilityCenters.isEmpty()) {
                centerComboBox.setValue(facilityCenters.get(0));
            }
        });

        new Thread(task).start();
    }

    @FXML
    private void handleExecuteWorkflow() {
        PickupClientResponse selectedPickup = deliveredPickupsTable.getSelectionModel().getSelectedItem();
        if (selectedPickup == null) {
            AlertHelper.showWarning("Missing Selection", "Select an active delivered pickup to process.");
            return;
        }

        Map<String, Object> selectedCenter = centerComboBox.getValue();
        if (selectedCenter == null || !selectedCenter.containsKey("centerId")) {
            AlertHelper.showWarning("Missing Facility", "Please designate a certified recycling center.");
            return;
        }

        long centerId = Long.parseLong(selectedCenter.get("centerId").toString());
        String itemIdStr = targetItemIdField.getText();
        if (itemIdStr == null || itemIdStr.isBlank()) {
            AlertHelper.showWarning("Item ID Required", "Please specify the item ID to undergo inspection.");
            return;
        }

        long itemId;
        try {
            itemId = Long.parseLong(itemIdStr.trim());
        } catch (NumberFormatException e) {
            AlertHelper.showWarning("Invalid Item ID", "Item ID must be a numeric integer.");
            return;
        }

        String notes = inspectionNotesArea.getText();
        String resultWorkflow = workflowTypeComboBox.getValue();

        ProcessItemClientRequest request = new ProcessItemClientRequest(
                selectedPickup.getPickupId(), itemId, centerId, notes, resultWorkflow
        );

        setLoading(true);
        Task<ProcessingOutcomeClientResponse> processTask = new Task<>() {
            @Override
            protected ProcessingOutcomeClientResponse call() {
                return processingApiClient.processItem(request);
            }
        };

        processTask.setOnSucceeded(e -> {
            setLoading(false);
            ProcessingOutcomeClientResponse outcome = processTask.getValue();
            AlertHelper.showInfo("Execution Succeeded",
                    String.format("Item #%d processed via %s workflow. Points credited: %d",
                            outcome.getItemId(), outcome.getWorkflowType(), outcome.getPointsAwarded()));
            inspectionNotesArea.clear();
            targetItemIdField.clear();
            loadDeliveredPickups();
        });

        processTask.setOnFailed(e -> {
            setLoading(false);
            Throwable ex = processTask.getException();
            AlertHelper.showError("Workflow Failure", ex != null ? ex.getMessage() : "Error processing workflow.");
        });

        new Thread(processTask).start();
    }

    @FXML
    private void handleBack() {
        SceneNavigator.loadScreen(AppScreen.ADMIN_DASHBOARD);
    }

    private void setLoading(boolean isLoading) {
        if (loadingIndicator != null) loadingIndicator.setVisible(isLoading);
        if (executeWorkflowButton != null) executeWorkflowButton.setDisable(isLoading);
    }
}