package com.ewaste.client.controller.admin;

import com.ewaste.client.api.ReportApiClient;
import com.ewaste.client.api.PickupApiClient;
import com.ewaste.client.config.ClientContext;
import com.ewaste.client.controller.BaseController;
import com.ewaste.client.dto.response.AnalyticsClientResponse;
import com.ewaste.client.dto.response.PickupClientResponse;
import com.ewaste.client.navigation.AppScreen;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

import java.util.Map;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class AnalyticsController extends BaseController {

    @FXML
    private Label totalVolumeLabel;
    @FXML
    private Label hazardousRatioLabel;
    @FXML
    private Label totalPointsIssuedLabel;
    @FXML
    private Label activeFleetUtilizationLabel;

    @FXML
    private PieChart categoryDistributionChart;
    @FXML
    private BarChart<String, Number> facilityThroughputChart;

    @FXML
    private Button refreshButton;
    @FXML
    private Button backButton;
    @FXML
    private ProgressIndicator loadingIndicator;

    private ReportApiClient reportApiClient;
    private PickupApiClient pickupApiClient;

    @Override
    protected void onInitialize() {
        reportApiClient = ClientContext.getInstance().getReportApiClient();
        pickupApiClient = ClientContext.getInstance().getPickupApiClient();
        loadAnalyticsData();
    }

    private void loadAnalyticsData() {
        setLoading(true);

        new Thread(() -> {
            try {
                AnalyticsClientResponse data = reportApiClient.getReportsSummary();
                AnalyticsClientResponse processing = reportApiClient.getProcessingReport();
                List<PickupClientResponse> pickups = pickupApiClient.getAllPickups(null);

                Platform.runLater(() -> {
                    setLoading(false);
                    if (data != null) {
                        renderMetrics(data, processing, pickups);
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    setLoading(false);
                    showError("Reporting Error", "Unable to generate analytics charts.", e.getMessage());
                });
            }
        }).start();
    }

    private void renderMetrics(AnalyticsClientResponse data,
                               AnalyticsClientResponse processing,
                               List<PickupClientResponse> pickups) {
        totalVolumeLabel.setText(String.format("%.1f kg",
                data.getTotalWeightRecycled() != null ? data.getTotalWeightRecycled() : 0.0));

        hazardousRatioLabel.setText(String.format("%.1f %%",
                data.getHazardousWasteRatio() != null
                        ? data.getHazardousWasteRatio() * 100.0 : 0.0));

        totalPointsIssuedLabel.setText(String.valueOf(
                data.getTotalPointsEarned() != null ? data.getTotalPointsEarned() : 0));

        double utilization = 0.0;
        if (data.getActiveCollectors() != null && data.getTotalCollectors() != null && data.getTotalCollectors() > 0) {
            utilization = (data.getActiveCollectors() * 100.0) / data.getTotalCollectors();
        }
        activeFleetUtilizationLabel.setText(String.format("%.1f %%", utilization));

        // Show the actual distribution of facility processing outcomes.
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        if (processing != null && processing.getRecordsByWorkflowType() != null) {
            processing.getRecordsByWorkflowType().forEach((workflow, count) ->
                    pieData.add(new PieChart.Data(workflow, count != null ? count : 0)));
        }
        categoryDistributionChart.setData(pieData);

        // Show the actual pickup lifecycle distribution.
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Pickup Count");
        Map<String, Long> statusCounts = new LinkedHashMap<>();
        if (pickups != null) {
            statusCounts = pickups.stream()
                    .filter(p -> p.getCurrentState() != null)
                    .collect(Collectors.groupingBy(
                            PickupClientResponse::getCurrentState,
                            LinkedHashMap::new,
                            Collectors.counting()));
        }
        statusCounts.forEach((status, count) ->
                series.getData().add(new XYChart.Data<>(status, count)));
        facilityThroughputChart.getData().clear();
        facilityThroughputChart.getData().add(series);
    }

    @FXML
    private void handleRefresh() {
        loadAnalyticsData();
    }

    @FXML
    private void handleBack() {
        navigateTo(AppScreen.ADMIN_DASHBOARD);
    }

    private void setLoading(boolean isLoading) {
        if (loadingIndicator != null) loadingIndicator.setVisible(isLoading);
        if (refreshButton != null) refreshButton.setDisable(isLoading);
        if (backButton != null) backButton.setDisable(isLoading);
    }
}