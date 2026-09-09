package com.ewaste.client.controller.customer;

import com.ewaste.client.api.PickupApiClient;
import com.ewaste.client.config.ClientContext;
import com.ewaste.client.controller.BaseController;
import com.ewaste.client.dto.response.PickupClientResponse;
import com.ewaste.client.navigation.AppScreen;
import javafx.application.Platform;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.stream.Collectors;

public class PickupHistoryController extends BaseController {

    @FXML
    private TableView<PickupClientResponse> tblPickups;
    @FXML
    private TableColumn<PickupClientResponse, Number> colId;
    @FXML
    private TableColumn<PickupClientResponse, String> colDate;
    @FXML
    private TableColumn<PickupClientResponse, String> colAddress;
    @FXML
    private TableColumn<PickupClientResponse, String> colStatus;
    @FXML
    private TableColumn<PickupClientResponse, Number> colWeight;
    @FXML
    private TableColumn<PickupClientResponse, Number> colPoints;
    @FXML
    private TableColumn<PickupClientResponse, Void> colActions;

    @FXML
    private ComboBox<String> cmbFilter;
    @FXML
    private TextField txtSearch;
    @FXML
    private Label lblTotalPickups;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Button btnRefresh;
    @FXML
    private Button btnBack;

    private PickupApiClient pickupApiClient;
    private ObservableList<PickupClientResponse> allPickups = FXCollections.observableArrayList();
    private ObservableList<PickupClientResponse> filteredPickups = FXCollections.observableArrayList();

    @Override
    protected void onInitialize() {
        pickupApiClient = ClientContext.getInstance().getPickupApiClient();

        setupTable();
        setupFilters();
        loadPickups();

        // Set button actions
        if (btnRefresh != null) {
            btnRefresh.setOnAction(event -> handleRefresh());
        }
        if (btnBack != null) {
            btnBack.setOnAction(event -> handleBack());
        }
    }

    private void setupTable() {
        // ID column
        colId.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getPickupId()));

        // Date column - using scheduledDate or createdAt
        colDate.setCellValueFactory(cellData -> {
            String date = cellData.getValue().getScheduledDate();
            if (date == null || date.isEmpty()) {
                date = cellData.getValue().getCreatedAt();
            }
            return new SimpleStringProperty(date != null ? date : "-");
        });

        // Address column
        colAddress.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getAddress() != null ?
                        cellData.getValue().getAddress() : "-"));

        // Status column
        colStatus.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCurrentState() != null ?
                        cellData.getValue().getCurrentState() : "-"));

        // Weight column - calculate total weight from items
        colWeight.setCellValueFactory(cellData -> {
            double totalWeight = 0.0;
            List<PickupClientResponse.EWasteItemSummary> items = cellData.getValue().getItems();
            if (items != null) {
                totalWeight = items.stream()
                        .mapToDouble(item -> item.getWeightKg() != null ? item.getWeightKg() : 0.0)
                        .sum();
            }
            return new SimpleLongProperty((long) totalWeight);
        });

        // Points column - you can add this to your DTO or calculate from items
        colPoints.setCellValueFactory(cellData -> {
            // If your DTO has reward points, use it, otherwise show 0
            return new SimpleLongProperty(0);
        });

        // Set up the table with filtered data
        tblPickups.setItems(filteredPickups);
    }

    private void setupFilters() {
        // Filter combo box
        cmbFilter.setItems(FXCollections.observableArrayList(
                "All", "PENDING", "ASSIGNED", "IN_PROGRESS", "COMPLETED", "CANCELLED"
        ));
        cmbFilter.setValue("All");
        cmbFilter.setOnAction(event -> applyFilters());

        // Search text field
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

    private void loadPickups() {
        setLoading(true);

        new Thread(() -> {
            try {
                Long userId = userSession.getUserId();
                List<PickupClientResponse> pickups = pickupApiClient.getPickupsForCustomer(userId);

                Platform.runLater(() -> {
                    setLoading(false);
                    if (pickups != null) {
                        allPickups.setAll(pickups);
                        applyFilters();
                        lblTotalPickups.setText("Total: " + allPickups.size());
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    setLoading(false);
                    showError("Error", "Failed to load pickup history", e.getMessage());
                });
            }
        }).start();
    }

    private void applyFilters() {
        String filter = cmbFilter.getValue();
        String searchText = txtSearch.getText().toLowerCase();

        List<PickupClientResponse> filtered = allPickups.stream()
                .filter(pickup -> {
                    // Apply status filter
                    if (filter != null && !filter.equals("All")) {
                        String status = pickup.getCurrentState();
                        if (status == null || !status.equalsIgnoreCase(filter)) {
                            return false;
                        }
                    }
                    // Apply search filter (search in address)
                    if (searchText != null && !searchText.isEmpty()) {
                        String address = pickup.getAddress();
                        if (address == null || !address.toLowerCase().contains(searchText)) {
                            return false;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());

        filteredPickups.setAll(filtered);
        lblTotalPickups.setText("Total: " + filtered.size());
    }

    @FXML
    private void handleRefresh() {
        loadPickups();
    }

    @FXML
    private void handleBack() {
        navigateBack();
    }

    private void setLoading(boolean loading) {
        if (progressIndicator != null) {
            progressIndicator.setVisible(loading);
        }
        if (btnRefresh != null) {
            btnRefresh.setDisable(loading);
        }
        if (btnBack != null) {
            btnBack.setDisable(loading);
        }
    }

    // Helper method to get status badge style class
    private String getStatusStyleClass(String status) {
        if (status == null) return "status-badge";
        return switch (status.toUpperCase()) {
            case "PENDING" -> "status-badge-pending";
            case "ASSIGNED" -> "status-badge-assigned";
            case "IN_PROGRESS" -> "status-badge-in-progress";
            case "COMPLETED" -> "status-badge-completed";
            case "CANCELLED" -> "status-badge-cancelled";
            default -> "status-badge";
        };
    }
}