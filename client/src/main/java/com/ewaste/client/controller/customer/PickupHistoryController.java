package com.ewaste.client.controller;

import com.ewaste.client.dto.PickupClientResponse;
import com.ewaste.client.network.ApiClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class PickupHistoryController extends BaseController {

    @FXML
    private TableView<PickupHistoryItem> tablePickups;
    @FXML
    private TableColumn<PickupHistoryItem, Long> colPickupId;
    @FXML
    private TableColumn<PickupHistoryItem, String> colDate;
    @FXML
    private TableColumn<PickupHistoryItem, String> colAddress;
    @FXML
    private TableColumn<PickupHistoryItem, String> colStatus;
    @FXML
    private TableColumn<PickupHistoryItem, Double> colWeight;
    @FXML
    private TableColumn<PickupHistoryItem, Double> colPriority;
    @FXML
    private ComboBox<String> cmbFilter;
    @FXML
    private TextField txtSearch;
    @FXML
    private Button btnRefresh;
    @FXML
    private Label lblTotalPickups;
    @FXML
    private Label lblTotalWeight;
    @FXML
    private ProgressIndicator progressIndicator;

    private ApiClient apiClient;
    private List<PickupClientResponse> allPickups;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    protected void onInitialize() {
        apiClient = ClientContext.getInstance().getApiClient();

        // Setup table columns
        setupTableColumns();

        // Setup filter combo box
        cmbFilter.getItems().addAll("All", "REQUESTED", "ASSIGNED", "COLLECTED",
                "DELIVERED", "PROCESSING", "COMPLETED", "CANCELLED");
        cmbFilter.setValue("All");

        // Setup event handlers
        cmbFilter.setOnAction(event -> applyFilters());
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        btnRefresh.setOnAction(event -> loadPickupHistory());

        // Load data
        loadPickupHistory();
    }

    private void setupTableColumns() {
        colPickupId.setCellValueFactory(new PropertyValueFactory<>("pickupId"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colWeight.setCellValueFactory(new PropertyValueFactory<>("weight"));
        colPriority.setCellValueFactory(new PropertyValueFactory<>("priority"));

        // Custom cell factory for status with color coding
        colStatus.setCellFactory(column -> new TableCell<PickupHistoryItem, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    // Color code based on status
                    String color = getStatusColor(item);
                    setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
                }
            }
        });
    }

    private String getStatusColor(String status) {
        switch (status.toUpperCase()) {
            case "COMPLETED": return "#28a745"; // Green
            case "CANCELLED": return "#dc3545"; // Red
            case "PROCESSING": return "#ffc107"; // Yellow
            case "COLLECTED": return "#17a2b8"; // Cyan
            case "ASSIGNED": return "#007bff"; // Blue
            default: return "#6c757d"; // Gray
        }
    }

    private void loadPickupHistory() {
        setLoading(true);

        new Thread(() -> {
            try {
                Long userId = userSession.getUserId();
                allPickups = apiClient.getPickupsByUser(userId);

                Platform.runLater(() -> {
                    setLoading(false);
                    updateTable();
                    updateStatistics();
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    setLoading(false);
                    showError("Error", "Failed to load pickup history", e.getMessage());
                });
            }
        }).start();
    }

    private void updateTable() {
        List<PickupHistoryItem> items = allPickups.stream()
                .map(this::convertToHistoryItem)
                .toList();

        tablePickups.setItems(FXCollections.observableArrayList(items));
    }

    private PickupHistoryItem convertToHistoryItem(PickupClientResponse pickup) {
        PickupHistoryItem item = new PickupHistoryItem();
        item.setPickupId(pickup.getPickupId());

        // Format date
        if (pickup.getScheduledDate() != null) {
            item.setDate(pickup.getScheduledDate());
        }

        item.setAddress(pickup.getAddress() != null ? pickup.getAddress() : "N/A");
        item.setStatus(pickup.getCurrentState() != null ? pickup.getCurrentState() : "UNKNOWN");
        item.setPriority(pickup.getPriorityScore() != null ? pickup.getPriorityScore() : 0.0);

        // Calculate total weight
        double weight = 0.0;
        if (pickup.getItems() != null) {
            weight = pickup.getItems().stream()
                    .mapToDouble(i -> i.getWeightKg() != null ? i.getWeightKg() : 0.0)
                    .sum();
        }
        item.setWeight(weight);

        return item;
    }

    private void updateStatistics() {
        lblTotalPickups.setText(String.valueOf(allPickups != null ? allPickups.size() : 0));

        double totalWeight = 0.0;
        if (allPickups != null) {
            totalWeight = allPickups.stream()
                    .filter(p -> "COMPLETED".equals(p.getCurrentState()))
                    .mapToDouble(p -> {
                        if (p.getItems() != null) {
                            return p.getItems().stream()
                                    .mapToDouble(i -> i.getWeightKg() != null ? i.getWeightKg() : 0.0)
                                    .sum();
                        }
                        return 0.0;
                    })
                    .sum();
        }
        lblTotalWeight.setText(String.format("%.2f kg", totalWeight));
    }

    private void applyFilters() {
        String filter = cmbFilter.getValue();
        String search = txtSearch.getText().toLowerCase().trim();

        if (allPickups == null) return;

        List<PickupHistoryItem> filtered = allPickups.stream()
                .map(this::convertToHistoryItem)
                .filter(item -> {
                    // Filter by status
                    if (!"All".equals(filter) && !filter.equals(item.getStatus())) {
                        return false;
                    }
                    // Filter by search
                    if (!search.isEmpty()) {
                        return item.getAddress().toLowerCase().contains(search) ||
                                String.valueOf(item.getPickupId()).contains(search);
                    }
                    return true;
                })
                .toList();

        tablePickups.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void handleViewDetails() {
        PickupHistoryItem selected = tablePickups.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("No Selection", "Please select a pickup to view details.", "");
            return;
        }

        // Navigate to pickup details view
        // This would be implemented with a details screen
        showInfo("Pickup Details",
                "Pickup #" + selected.getPickupId(),
                "Status: " + selected.getStatus() + "\n" +
                        "Address: " + selected.getAddress() + "\n" +
                        "Weight: " + String.format("%.2f", selected.getWeight()) + " kg\n" +
                        "Priority: " + String.format("%.2f", selected.getPriority()));
    }

    private void setLoading(boolean loading) {
        progressIndicator.setVisible(loading);
        tablePickups.setDisable(loading);
        btnRefresh.setDisable(loading);
    }

    // Table item class
    public static class PickupHistoryItem {
        private Long pickupId;
        private String date;
        private String address;
        private String status;
        private Double weight;
        private Double priority;

        public Long getPickupId() { return pickupId; }
        public void setPickupId(Long pickupId) { this.pickupId = pickupId; }
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Double getWeight() { return weight; }
        public void setWeight(Double weight) { this.weight = weight; }
        public Double getPriority() { return priority; }
        public void setPriority(Double priority) { this.priority = priority; }
    }

    // Placeholder API interface
    private interface ApiClient {
        List<PickupClientResponse> getPickupsByUser(Long userId) throws Exception;
    }

    private static class ClientContext {
        private static ClientContext instance = new ClientContext();

        public static ClientContext getInstance() {
            return instance;
        }

        public ApiClient getApiClient() {
            return null;
        }
    }
}