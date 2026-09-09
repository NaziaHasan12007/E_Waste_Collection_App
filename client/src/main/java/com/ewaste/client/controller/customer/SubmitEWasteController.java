package com.ewaste.client.controller.customer;

import com.ewaste.client.api.EWasteApiClient;
import com.ewaste.client.api.PickupApiClient;
import com.ewaste.client.config.ClientContext;
import com.ewaste.client.controller.BaseController;
import com.ewaste.client.dto.request.CreatePickupClientRequest;
import com.ewaste.client.dto.request.EWasteItemClientRequest;
import com.ewaste.client.dto.response.EWasteCategoryClientResponse;
import com.ewaste.client.dto.response.PickupClientResponse;
import com.ewaste.client.navigation.AppScreen;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SubmitEWasteController extends BaseController {

    @FXML
    private ComboBox<String> cmbCategory;
    @FXML
    private TextField txtItemName;  // Changed from txtModelName to match FXML
    @FXML
    private TextField txtWeight;
    @FXML
    private TextField txtQuantity;  // Added to match FXML
    @FXML
    private CheckBox chkHazardous;
    @FXML
    private ComboBox<String> cmbCondition;
    @FXML
    private TextArea txtDescription;  // Changed from txtSpecificAttributes to match FXML
    @FXML
    private TextField txtAddress;
    @FXML
    private DatePicker dpkPreferredDate;
    @FXML
    private ComboBox<String> cmbPreferredTime;
    @FXML
    private Button btnAddItem;
    @FXML
    private Button btnSubmit;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnRemoveItem;
    @FXML
    private Button btnClearAll;
    @FXML
    private ListView<String> listItems;
    @FXML
    private Label lblTotalWeight;
    @FXML
    private Label lblItemCount;
    @FXML
    private Label lblEstimatedPoints;  // Added to match FXML
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private VBox formContainer;

    private EWasteApiClient eWasteApiClient;
    private PickupApiClient pickupApiClient;
    private List<EWasteItemClientRequest> items = new ArrayList<>();
    private List<EWasteCategoryClientResponse> categories = new ArrayList<>();
    private double totalWeight = 0.0;

    @Override
    protected void onInitialize() {
        eWasteApiClient = ClientContext.getInstance().getEWasteApiClient();
        pickupApiClient = ClientContext.getInstance().getPickupApiClient();

        // Setup condition combo box
        cmbCondition.getItems().addAll("GOOD", "FAIR", "POOR", "BROKEN");
        cmbCondition.setValue("GOOD");

        // Setup time combo box
        cmbPreferredTime.getItems().addAll(
                "09:00 - 10:00", "10:00 - 11:00", "11:00 - 12:00",
                "12:00 - 13:00", "13:00 - 14:00", "14:00 - 15:00",
                "15:00 - 16:00", "16:00 - 17:00"
        );
        cmbPreferredTime.setValue("10:00 - 11:00");

        // Set default date
        dpkPreferredDate.setValue(LocalDate.now().plusDays(2));

        // Setup event handlers
        btnAddItem.setOnAction(event -> handleAddItem());
        btnSubmit.setOnAction(event -> handleSubmit());  // Changed to match FXML
        btnCancel.setOnAction(event -> handleBack());
        btnRemoveItem.setOnAction(event -> handleRemoveItem());
        btnClearAll.setOnAction(event -> handleClearAll());

        // Load categories
        loadCategories();

        // Add listener for estimated points calculation
        txtWeight.textProperty().addListener((obs, oldVal, newVal) -> calculateEstimatedPoints());
        cmbCategory.valueProperty().addListener((obs, oldVal, newVal) -> calculateEstimatedPoints());
    }

    private void loadCategories() {
        // Show loading
        btnAddItem.setDisable(true);

        new Thread(() -> {
            try {
                categories = eWasteApiClient.getCategories();

                Platform.runLater(() -> {
                    // Populate category combo box
                    cmbCategory.getItems().clear();
                    for (EWasteCategoryClientResponse category : categories) {
                        cmbCategory.getItems().add(category.getName());
                    }
                    if (!categories.isEmpty()) {
                        cmbCategory.setValue(categories.get(0).getName());
                    }
                    btnAddItem.setDisable(false);
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    btnAddItem.setDisable(false);
                    showError("Error", "Failed to load categories", e.getMessage());
                });
            }
        }).start();
    }

    private void calculateEstimatedPoints() {
        try {
            double weight = Double.parseDouble(txtWeight.getText().trim());
            String categoryName = cmbCategory.getValue();

            EWasteCategoryClientResponse selectedCategory = categories.stream()
                    .filter(c -> c.getName().equals(categoryName))
                    .findFirst()
                    .orElse(null);

            if (selectedCategory != null && selectedCategory.getRewardPointsPerKg() != null) {
                int points = (int) (weight * selectedCategory.getRewardPointsPerKg());
                lblEstimatedPoints.setText(String.valueOf(points));
            } else {
                lblEstimatedPoints.setText("0");
            }
        } catch (NumberFormatException e) {
            lblEstimatedPoints.setText("0");
        }
    }

    @FXML
    private void handleAddItem() {
        if (!validateItemInput()) {
            return;
        }

        // Get selected category
        String categoryName = cmbCategory.getValue();
        EWasteCategoryClientResponse selectedCategory = categories.stream()
                .filter(c -> c.getName().equals(categoryName))
                .findFirst()
                .orElse(null);

        if (selectedCategory == null) {
            showError("Error", "Invalid category selected", "Please select a valid category.");
            return;
        }

        // Create item DTO
        EWasteItemClientRequest item = new EWasteItemClientRequest();
        item.setCategoryId(selectedCategory.getId());
        item.setName(txtItemName.getText().trim());
        item.setWeightKg(Double.parseDouble(txtWeight.getText().trim()));
        item.setCondition(cmbCondition.getValue());
        item.setDescription(txtDescription.getText().trim());

        // Set quantity
        int quantity = 1;
        try {
            quantity = Integer.parseInt(txtQuantity.getText().trim());
        } catch (NumberFormatException e) {
            // Use default 1
        }
        item.setQuantity(quantity);

        // Add to list
        items.add(item);
        totalWeight += item.getWeightKg() * quantity;

        // Update UI
        updateItemList();
        clearItemForm();

        showInfo("Item Added", "Success", "Item has been added to your pickup list.");
    }

    @FXML
    private void handleSubmit() {  // Changed to match FXML onAction="#handleSubmit"
        if (items.isEmpty()) {
            showWarning("No Items", "Please add at least one item to submit.",
                    "Add items to your pickup request.");
            return;
        }

        if (!validatePickupInput()) {
            return;
        }

        // Show confirmation
        if (!showConfirmation("Submit Pickup", "Confirm Submission",
                "Are you sure you want to submit this pickup request with " + items.size() + " items?")) {
            return;
        }

        setLoading(true);

        new Thread(() -> {
            try {
                // Create pickup request
                CreatePickupClientRequest request = new CreatePickupClientRequest();
                request.setUserId(userSession.getUserId());
                request.setAddress(txtAddress.getText().trim());
                request.setPreferredDate(dpkPreferredDate.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE));
                request.setPreferredTime(cmbPreferredTime.getValue());

                // Submit the pickup with items
                PickupClientResponse response = pickupApiClient.createPickup(request);

                Platform.runLater(() -> {
                    setLoading(false);
                    showInfo("Pickup Submitted", "Success",
                            "Your pickup request has been submitted successfully.\n" +
                                    "Pickup ID: " + response.getPickupId() + "\n" +
                                    "Status: " + response.getCurrentState());

                    // Navigate back to dashboard
                    navigateTo(AppScreen.CUSTOMER_DASHBOARD);
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    setLoading(false);
                    showError("Submission Failed", "Failed to submit pickup request", e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    private void handleClear() {  // Added to match FXML onAction="#handleClear"
        clearItemForm();
        showInfo("Cleared", "Form Cleared", "All input fields have been cleared.");
    }

    @FXML
    private void handleSubmitPickup() {
        handleSubmit();  // Forward to handleSubmit
    }

    private boolean validateItemInput() {
        if (cmbCategory.getValue() == null) {
            showWarning("Category Required", "Please select a waste category.", "");
            cmbCategory.requestFocus();
            return false;
        }

        if (txtItemName.getText().trim().isEmpty()) {
            showWarning("Item Name Required", "Please enter the item name.", "");
            txtItemName.requestFocus();
            return false;
        }

        if (txtWeight.getText().trim().isEmpty()) {
            showWarning("Weight Required", "Please enter the item weight.", "");
            txtWeight.requestFocus();
            return false;
        }

        try {
            double weight = Double.parseDouble(txtWeight.getText().trim());
            if (weight <= 0) {
                showWarning("Invalid Weight", "Weight must be greater than 0.", "");
                txtWeight.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showWarning("Invalid Weight", "Please enter a valid number for weight.", "");
            txtWeight.requestFocus();
            return false;
        }

        if (cmbCondition.getValue() == null) {
            showWarning("Condition Required", "Please select the item condition.", "");
            cmbCondition.requestFocus();
            return false;
        }

        return true;
    }

    private boolean validatePickupInput() {
        if (txtAddress.getText().trim().isEmpty()) {
            showWarning("Address Required", "Please enter your pickup address.", "");
            txtAddress.requestFocus();
            return false;
        }

        if (dpkPreferredDate.getValue() == null) {
            showWarning("Date Required", "Please select a preferred pickup date.", "");
            dpkPreferredDate.requestFocus();
            return false;
        }

        LocalDate selectedDate = dpkPreferredDate.getValue();
        if (selectedDate.isBefore(LocalDate.now())) {
            showWarning("Invalid Date", "Preferred date cannot be in the past.", "");
            dpkPreferredDate.requestFocus();
            return false;
        }

        if (cmbPreferredTime.getValue() == null) {
            showWarning("Time Required", "Please select a preferred pickup time.", "");
            cmbPreferredTime.requestFocus();
            return false;
        }

        return true;
    }

    private void updateItemList() {
        listItems.getItems().clear();
        for (int i = 0; i < items.size(); i++) {
            EWasteItemClientRequest item = items.get(i);
            String display = String.format("%d. %s (%.2f kg x %d) - %s",
                    i + 1, item.getName(), item.getWeightKg(),
                    item.getQuantity() != null ? item.getQuantity() : 1,
                    item.getCondition());
            listItems.getItems().add(display);
        }

        lblTotalWeight.setText(String.format("%.2f kg", totalWeight));
        lblItemCount.setText(String.valueOf(items.size()));
    }

    private void clearItemForm() {
        txtItemName.clear();
        txtWeight.clear();
        txtQuantity.clear();
        chkHazardous.setSelected(false);
        cmbCondition.setValue("GOOD");
        txtDescription.clear();
        lblEstimatedPoints.setText("0");
        txtItemName.requestFocus();
    }

    @FXML
    private void handleRemoveItem() {
        int selectedIndex = listItems.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < items.size()) {
            EWasteItemClientRequest removed = items.remove(selectedIndex);
            int quantity = removed.getQuantity() != null ? removed.getQuantity() : 1;
            totalWeight -= removed.getWeightKg() * quantity;
            updateItemList();
            showInfo("Item Removed", "Success", "Item has been removed from your pickup list.");
        } else {
            showWarning("No Selection", "Please select an item to remove from the list.", "");
        }
    }

    @FXML
    private void handleClearAll() {
        if (items.isEmpty()) {
            showWarning("Empty List", "No items to clear.", "");
            return;
        }

        if (showConfirmation("Clear All", "Confirm Clear",
                "Are you sure you want to clear all items from the list?")) {
            items.clear();
            totalWeight = 0.0;
            updateItemList();
            showInfo("Cleared", "Success", "All items have been removed from the list.");
        }
    }

    @FXML
    private void handleBack() {
        navigateBack();
    }

    private void setLoading(boolean loading) {
        btnAddItem.setDisable(loading);
        btnSubmit.setDisable(loading);
        btnCancel.setDisable(loading);
        btnRemoveItem.setDisable(loading);
        btnClearAll.setDisable(loading);
        progressIndicator.setVisible(loading);
        if (loading) {
            progressIndicator.setProgress(-1.0);
        } else {
            progressIndicator.setProgress(0.0);
        }
    }
}