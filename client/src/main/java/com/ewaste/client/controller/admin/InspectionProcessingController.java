package com.ewaste.client.controller.admin;

import com.ewaste.client.api.PickupApiClient;
import com.ewaste.client.api.ProcessingApiClient;
import com.ewaste.client.config.ClientContext;
import com.ewaste.client.controller.BaseController;
import com.ewaste.client.dto.request.ProcessItemClientRequest;
import com.ewaste.client.dto.response.PickupClientResponse;
import com.ewaste.client.dto.response.ProcessingOutcomeClientResponse;
import com.ewaste.client.dto.response.RecyclingCenterClientResponse;
import com.ewaste.client.navigation.AppScreen;
import javafx.application.Platform;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class InspectionProcessingController extends BaseController {

    @FXML
    private TableView<PickupClientResponse> deliveredPickupsTable;
    @FXML
    private TableColumn<PickupClientResponse, Number> pickupIdColumn;
    @FXML
    private TableColumn<PickupClientResponse, String> pickupAddressColumn;
    @FXML
    private TableColumn<PickupClientResponse, String> pickupDateColumn;

    @FXML
    private ComboBox<RecyclingCenterClientResponse> centerComboBox;
    @FXML
    private ComboBox<String> workflowTypeComboBox;
    @FXML
    private TextField targetItemIdField;
    @FXML
    private TextArea inspectionNotesArea;
    @FXML
    private Button executeWorkflowButton;
    @FXML
    private Button backButton;
    @FXML
    private ProgressIndicator loadingIndicator;

    private PickupApiClient pickupApiClient;
    private ProcessingApiClient processingApiClient;
    private final ObservableList<PickupClientResponse> deliveredPickups = FXCollections.observableArrayList();
    private final ObservableList<RecyclingCenterClientResponse> facilityCenters = FXCollections.observableArrayList();

    @Override
    protected void onInitialize() {
        pickupApiClient = ClientContext.getInstance().getPickupApiClient();
        processingApiClient = ClientContext.getInstance().getProcessingApiClient();

        setupTable();
        setupForm();
        loadDeliveredPickups();
        loadCenters();
    }

    private void setupTable() {
        pickupIdColumn.setCellValueFactory(c -> new SimpleLongProperty(c.getValue().getPickupId()));
        pickupAddressColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAddress() != null ? c.getValue().getAddress() : "-"));

        // Use scheduledDate or createdAt instead of preferredDate
        pickupDateColumn.setCellValueFactory(c -> {
            String date = c.getValue().getScheduledDate() != null ?
                    c.getValue().getScheduledDate().toString() :
                    (c.getValue().getCreatedAt() != null ?
                            c.getValue().getCreatedAt().toString() : "-");
            return new SimpleStringProperty(date);
        });

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
                "RECYCLING", "REUSE", "REPAIR", "RECOVERY", "DISPOSAL"
        ));
        workflowTypeComboBox.setValue("RECYCLING");

        centerComboBox.setItems(facilityCenters);
        centerComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(RecyclingCenterClientResponse item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%s - %s", item.getName(), item.getCity()));
            }
        });
        centerComboBox.setButtonCell(centerComboBox.getCellFactory().call(null));
    }

    private void loadDeliveredPickups() {
        setLoading(true);

        new Thread(() -> {
            try {
                List<PickupClientResponse> pickups = pickupApiClient.getAllPickups("DELIVERED");

                Platform.runLater(() -> {
                    setLoading(false);
                    if (pickups != null) {
                        deliveredPickups.setAll(pickups);
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    setLoading(false);
                    showError("Failure", "Unable to load delivered inventory awaiting inspection.", e.getMessage());
                });
            }
        }).start();
    }

    private void loadCenters() {
        new Thread(() -> {
            try {
                List<RecyclingCenterClientResponse> centers = processingApiClient.getRecyclingCenters();

                Platform.runLater(() -> {
                    facilityCenters.setAll(centers);
                    if (!facilityCenters.isEmpty()) {
                        centerComboBox.setValue(facilityCenters.get(0));
                    }
                });

            } catch (Exception e) {
                // Log error but don't show to user
            }
        }).start();
    }

    @FXML
    private void handleExecuteWorkflow() {
        PickupClientResponse selectedPickup = deliveredPickupsTable.getSelectionModel().getSelectedItem();
        if (selectedPickup == null) {
            showWarning("Missing Selection", "Select an active delivered pickup to process.", "");
            return;
        }

        RecyclingCenterClientResponse selectedCenter = centerComboBox.getValue();
        if (selectedCenter == null) {
            showWarning("Missing Facility", "Please designate a certified recycling center.", "");
            return;
        }

        String itemIdStr = targetItemIdField.getText();
        if (itemIdStr == null || itemIdStr.isBlank()) {
            showWarning("Item ID Required", "Please specify the item ID to undergo inspection.", "");
            return;
        }

        long itemId;
        try {
            itemId = Long.parseLong(itemIdStr.trim());
        } catch (NumberFormatException e) {
            showWarning("Invalid Item ID", "Item ID must be a numeric integer.", "");
            return;
        }

        String notes = inspectionNotesArea.getText();
        String workflowType = workflowTypeComboBox.getValue();

        ProcessItemClientRequest request = new ProcessItemClientRequest();
        request.setPickupId(selectedPickup.getPickupId());
        request.setWorkflowType(workflowType);
        request.setCenterId(selectedCenter.getId());
        request.setPointsAwarded(100);

        setLoading(true);

        new Thread(() -> {
            try {
                ProcessingOutcomeClientResponse outcome = processingApiClient.processItem(
                        selectedPickup.getPickupId(), request);

                Platform.runLater(() -> {
                    setLoading(false);
                    showInfo("Execution Succeeded",
                            String.format("Item processed via %s workflow. Points credited: %d",
                                    outcome.getWorkflowDisplay(),
                                    outcome.getPointsAwarded() != null ? outcome.getPointsAwarded() : 0),
                            "");
                    inspectionNotesArea.clear();
                    targetItemIdField.clear();
                    loadDeliveredPickups();
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    setLoading(false);
                    showError("Workflow Failure", "Error processing workflow.", e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    private void handleBack() {
        navigateTo(AppScreen.ADMIN_DASHBOARD);
    }

    private void setLoading(boolean isLoading) {
        if (loadingIndicator != null) loadingIndicator.setVisible(isLoading);
        if (executeWorkflowButton != null) executeWorkflowButton.setDisable(isLoading);
        if (backButton != null) backButton.setDisable(isLoading);
    }
}