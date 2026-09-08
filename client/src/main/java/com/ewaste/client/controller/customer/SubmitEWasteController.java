package com.ewaste.client.controller.customer;

import com.ewaste.client.api.EWasteApiClient;
import com.ewaste.client.api.PickupApiClient;
import com.ewaste.client.controller.BaseController;
import com.ewaste.client.dto.request.CreatePickupClientRequest;
import com.ewaste.client.dto.request.EWasteItemClientRequest;
import com.ewaste.client.dto.response.EWasteCategoryClientResponse;
import com.ewaste.client.dto.response.EWasteItemClientResponse;
import com.ewaste.client.navigation.AppScreen;
import com.ewaste.client.navigation.SceneNavigator;
import com.ewaste.client.util.AlertHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller allowing customers to specify device attributes and schedule a collection pickup.
 */
public class SubmitEWasteController extends BaseController {

    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TextField brandField;
    @FXML private TextField modelField;
    @FXML private TextField weightField;
    @FXML private ComboBox<String> conditionComboBox;
    @FXML private CheckBox hazardousCheckBox;

    @FXML private TextArea pickupAddressArea;
    @FXML private DatePicker preferredDatePicker;
    @FXML private ComboBox<String> preferredTimeComboBox;

    @FXML private Button submitButton;
    @FXML private ProgressIndicator loadingIndicator;

    private final EWasteApiClient eWasteApiClient = new EWasteApiClient();
    private final PickupApiClient pickupApiClient = new PickupApiClient();

    private final List<EWasteCategoryClientResponse> categoryModels = new ArrayList<>();

    @FXML
    @Override
    public void initialize() {
        super.initialize();
        if (loadingIndicator != null) loadingIndicator.setVisible(false);

        conditionComboBox.getItems().addAll("WORKING", "REPAIRABLE", "NON_FUNCTIONAL", "DAMAGED");
        conditionComboBox.setValue("WORKING");

        preferredTimeComboBox.getItems().addAll("09:00 - 12:00", "12:00 - 15:00", "15:00 - 18:00");
        preferredTimeComboBox.setValue("09:00 - 12:00");

        preferredDatePicker.setValue(LocalDate.now().plusDays(1));

        loadCategories();
    }

    private void loadCategories() {
        Task<List<EWasteCategoryClientResponse>> task = new Task<>() {
            @Override
            protected List<EWasteCategoryClientResponse> call() throws Exception {
                return eWasteApiClient.getCategories();
            }
        };

        task.setOnSucceeded(e -> {
            categoryModels.clear();
            categoryModels.addAll(task.getValue());
            ObservableList<String> names = FXCollections.observableArrayList();
            for (EWasteCategoryClientResponse c : categoryModels) {
                names.add(c.getName());
            }
            categoryComboBox.setItems(names);
            if (!names.isEmpty()) {
                categoryComboBox.setValue(names.get(0));
            }
        });

        runAsync(task);
    }

    @FXML
    private void handleSubmitPickup() {
        String categoryName = categoryComboBox.getValue();
        String brand = brandField.getText().trim();
        String model = modelField.getText().trim();
        String weightText = weightField.getText().trim();
        String condition = conditionComboBox.getValue();
        boolean isHazardous = hazardousCheckBox.isSelected();
        String address = pickupAddressArea.getText().trim();
        LocalDate date = preferredDatePicker.getValue();
        String time = preferredTimeComboBox.getValue();

        if (categoryName == null || weightText.isEmpty() || address.isEmpty() || date == null) {
            AlertHelper.showWarning("Validation Error", "Please fill in all mandatory fields.");
            return;
        }

        double weight;
        try {
            weight = Double.parseDouble(weightText);
            if (weight <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            AlertHelper.showWarning("Validation Error", "Weight must be a positive number.");
            return;
        }

        Long categoryId = 1L;
        for (EWasteCategoryClientResponse cat : categoryModels) {
            if (cat.getName().equalsIgnoreCase(categoryName)) {
                categoryId = cat.getCategoryId();
                break;
            }
        }

        setLoading(true);

        Long finalCategoryId = categoryId;
        Task<Void> submissionPipeline = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // Step 1: Submit EWaste Item
                EWasteItemClientRequest itemReq = new EWasteItemClientRequest(
                        session.getUserId(), finalCategoryId, brand, model, weight, 1, condition, isHazardous
                );
                EWasteItemClientResponse itemRes = eWasteApiClient.submitItem(itemReq);

                // Step 2: Create Pickup Request associating item
                List<Long> itemIds = List.of(itemRes.getItemId());
                CreatePickupClientRequest pickupReq = new CreatePickupClientRequest(
                        session.getUserId(), address, date.toString(), time, itemIds
                );
                pickupApiClient.createPickup(pickupReq);
                return null;
            }
        };

        submissionPipeline.setOnSucceeded(e -> {
            setLoading(false);
            AlertHelper.showInfo("Submission Successful", "Your pickup request has been scheduled.");
            SceneNavigator.loadScreen(AppScreen.CUSTOMER_DASHBOARD, false);
        });

        submissionPipeline.setOnFailed(e -> {
            setLoading(false);
            Throwable err = submissionPipeline.getException();
            log.error("Failed to submit pickup workflow", err);
            AlertHelper.showError("Submission Failed", err.getMessage() != null ? err.getMessage() : "Server rejected request.");
        });

        runAsync(submissionPipeline);
    }

    @FXML
    private void handleBackToDashboard() {
        SceneNavigator.loadScreen(AppScreen.CUSTOMER_DASHBOARD, false);
    }

    private void setLoading(boolean isLoading) {
        if (loadingIndicator != null) loadingIndicator.setVisible(isLoading);
        submitButton.setDisable(isLoading);
    }
}