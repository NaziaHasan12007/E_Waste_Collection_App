package com.ewaste.client.controller.customer;

import com.ewaste.client.config.ClientContext;
import com.ewaste.client.controller.BaseController;
import com.ewaste.client.core.AppScreen;
import com.ewaste.client.dto.request.CreatePickupClientRequest;
import com.ewaste.client.dto.response.EWasteCategoryResponse;
import com.ewaste.client.dto.request.EWasteItemRequestDto;
import com.ewaste.client.network.ApiClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SubmitEWasteController extends BaseController {

    @FXML
    private ComboBox<String> cmbCategory;
    @FXML
    private TextField txtModelName;
    @FXML
    private TextField txtWeight;
    @FXML
    private CheckBox chkHazardous;
    @FXML
    private ComboBox<String> cmbCondition;
    @FXML
    private TextArea txtSpecificAttributes;
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
    private ListView<String> listItems;
    @FXML
    private Label lblTotalWeight;
    @FXML
    private Label lblItemCount;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private VBox formContainer;

    private ApiClient apiClient;
    private List<EWasteItemRequestDto> items = new ArrayList<>();
    private List<EWasteCategoryResponse> categories = new ArrayList<>();
    private double totalWeight = 0.0;

    @Override
    protected void onInitialize() {
        apiClient = ClientContext.getInstance().getApiClient();

        // Setup condition combo box
        cmbCondition.getItems().addAll("WORKING", "DAMAGED", "NON_FUNCTIONAL", "PARTIALLY_WORKING");
        cmbCondition.setValue("WORKING");

        // Setup time combo box
        cmbPreferredTime.getItems().addAll("09:00 - 10:00", "10:00 - 11:00", "11:00 - 12:00",
                "12:00 - 13:00", "13:00 - 14:00", "14:00 - 15:00", "15:00 - 16:00", "16:00 - 17:00");
        cmbPreferredTime.setValue("10:00 - 11:00");

        // Set default date
        dpkPreferredDate.setValue(LocalDate.now().plusDays(2));

        // Setup event handlers
        btnAddItem.setOnAction(event -> handleAddItem());
        btnSubmit.setOnAction(event -> handleSubmitPickup());
        btnCancel.setOnAction(event -> navigateBack());

        // Load categories
        loadCategories();
    }

    private void loadCategories() {
        // Show loading
        btnAddItem.setDisable(true);

        new Thread(() -> {
            try {
                categories = apiClient.getCategories();

                Platform.runLater(() -> {
                    // Populate category combo box
                    cmbCategory.getItems().clear();
                    for (EWasteCategoryResponse category : categories) {
                        cmbCategory.getItems().add(category.getCategoryName());
                    }
                    if (!categories.isEmpty()) {
                        cmbCategory.setValue(categories.get(0).getCategoryName());
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

    @FXML
    private void handleAddItem() {
        if (!validateItemInput()) {
            return;
        }

        // Get selected category
        String categoryName = cmbCategory.getValue();
        EWasteCategoryResponse selectedCategory = categories.stream()
                .filter(c -> c.getCategoryName().equals(categoryName))
                .findFirst()
                .orElse(null);

        if (selectedCategory == null) {
            showError("Error", "Invalid category selected");
            return;
        }

        // Create item DTO
        EWasteItemRequestDto item = new EWasteItemRequestDto();
        item.setCategoryId(selectedCategory.getCategoryId());
        item.setModelName(txtModelName.getText().trim());
        item.setWeightKg(Double.parseDouble(txtWeight.getText().trim()));
        item.setIsHazardous(chkHazardous.isSelected());
        item.setWasteCondition(cmbCondition.getValue());
        item.setSpecificAttributes(txtSpecificAttributes.getText().trim());

        // Add to list
        items.add(item);
        totalWeight += item.getWeightKg();

        // Update UI
        updateItemList();
        clearItemForm();

        showInfo("Item Added", "Success", "Item has been added to your pickup list.");
    }

    @FXML
    private void handleSubmitPickup() {
        if (items.isEmpty()) {
            showWarning("No Items", "Please add at least one item to submit.", "Add items to your pickup request.");
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

                // First, create the items
                List<Long> itemIds = new ArrayList<>();
                for (EWasteItemRequestDto itemDto : items) {
                    Long itemId = apiClient.createItem(itemDto);
                    itemIds.add(itemId);
                }
                request.setItemIds(itemIds);

                // Submit pickup
                PickupClientResponse response = apiClient.createPickup(request);

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

    private boolean validateItemInput() {
        if (cmbCategory.getValue() == null) {
            showWarning("Category Required", "Please select a waste category.");
            cmbCategory.requestFocus();
            return false;
        }

        if (txtModelName.getText().trim().isEmpty()) {
            showWarning("Model Name Required", "Please enter the model name.");
            txtModelName.requestFocus();
            return false;
        }

        if (txtWeight.getText().trim().isEmpty()) {
            showWarning("Weight Required", "Please enter the item weight.");
            txtWeight.requestFocus();
            return false;
        }

        try {
            double weight = Double.parseDouble(txtWeight.getText().trim());
            if (weight <= 0) {
                showWarning("Invalid Weight", "Weight must be greater than 0.");
                txtWeight.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showWarning("Invalid Weight", "Please enter a valid number for weight.");
            txtWeight.requestFocus();
            return false;
        }

        if (cmbCondition.getValue() == null) {
            showWarning("Condition Required", "Please select the item condition.");
            cmbCondition.requestFocus();
            return false;
        }

        return true;
    }

    private boolean validatePickupInput() {
        if (txtAddress.getText().trim().isEmpty()) {
            showWarning("Address Required", "Please enter your pickup address.");
            txtAddress.requestFocus();
            return false;
        }

        if (dpkPreferredDate.getValue() == null) {
            showWarning("Date Required", "Please select a preferred pickup date.");
            dpkPreferredDate.requestFocus();
            return false;
        }

        LocalDate selectedDate = dpkPreferredDate.getValue();
        if (selectedDate.isBefore(LocalDate.now())) {
            showWarning("Invalid Date", "Preferred date cannot be in the past.");
            dpkPreferredDate.requestFocus();
            return false;
        }

        if (cmbPreferredTime.getValue() == null) {
            showWarning("Time Required", "Please select a preferred pickup time.");
            cmbPreferredTime.requestFocus();
            return false;
        }

        return true;
    }

    private void updateItemList() {
        listItems.getItems().clear();
        for (int i = 0; i < items.size(); i++) {
            EWasteItemRequestDto item = items.get(i);
            String display = String.format("%d. %s (%.2f kg) - %s",
                    i + 1, item.getModelName(), item.getWeightKg(), item.getWasteCondition());
            listItems.getItems().add(display);
        }

        lblTotalWeight.setText(String.format("%.2f kg", totalWeight));
        lblItemCount.setText(String.valueOf(items.size()));
    }

    private void clearItemForm() {
        txtModelName.clear();
        txtWeight.clear();
        chkHazardous.setSelected(false);
        cmbCondition.setValue("WORKING");
        txtSpecificAttributes.clear();
        txtModelName.requestFocus();
    }

    @FXML
    private void handleRemoveItem() {
        int selectedIndex = listItems.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < items.size()) {
            EWasteItemRequestDto removed = items.remove(selectedIndex);
            totalWeight -= removed.getWeightKg();
            updateItemList();
            showInfo("Item Removed", "Success", "Item has been removed from your pickup list.");
        } else {
            showWarning("No Selection", "Please select an item to remove from the list.");
        }
    }

    @FXML
    private void handleClearAll() {
        if (items.isEmpty()) {
            showWarning("Empty List", "No items to clear.");
            return;
        }

        if (showConfirmation("Clear All", "Confirm Clear",
                "Are you sure you want to clear all items from the list?")) {
            items.clear();
            totalWeight = 0.0;
            updateItemList();
            showInfo("Cleared", "Success", "All items have
