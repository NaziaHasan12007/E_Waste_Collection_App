package com.ewaste.client.controller.admin;

import com.ewaste.client.api.ReportApiClient;
import com.ewaste.client.controller.BaseController;
import com.ewaste.client.dto.response.AnalyticsClientResponse;
import com.ewaste.client.navigation.AppScreen;
import com.ewaste.client.navigation.SceneNavigator;
import com.ewaste.client.util.AlertHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

import java.util.Map;

public class AnalyticsController extends BaseController {

    @FXML private Label totalVolumeLabel;
    @FXML private Label hazardousRatioLabel;
    @FXML private Label totalPointsIssuedLabel;
    @FXML private Label activeFleetUtilizationLabel;

    @FXML private PieChart categoryDistributionChart;
    @FXML private BarChart<String, Number> facilityThroughputChart;

    @FXML private Button refreshButton;
    @FXML private Button backButton;
    @FXML private ProgressIndicator loadingIndicator;

    private final ReportApiClient reportApiClient = new ReportApiClient();

    @FXML
    public void initialize() {
        validateSession();
        loadAnalyticsData();
    }

    private void loadAnalyticsData() {
        setLoading(true);

        Task<AnalyticsClientResponse> task = new Task<>() {
            @Override
            protected AnalyticsClientResponse call() {
                return reportApiClient.getSystemAnalytics();
            }
        };

        task.setOnSucceeded(e -> {
            setLoading(false);
            AnalyticsClientResponse data = task.getValue();
            if (data != null) {
                renderMetrics(data);
            }
        });

        task.setOnFailed(e -> {
            setLoading(false);
            Throwable ex = task.getException();
            AlertHelper.showError("Reporting Error", ex != null ? ex.getMessage() : "Unable to generate analytics charts.");
        });

        new Thread(task).start();
    }

    private void renderMetrics(AnalyticsClientResponse data) {
        totalVolumeLabel.setText(String.format("%.1f kg", data.getTotalWeightKg() != null ? data.getTotalWeightKg() : 0.0));
        hazardousRatioLabel.setText(String.format("%.1f %%", data.getHazardousPercentage() != null ? data.getHazardousPercentage() : 0.0));
        totalPointsIssuedLabel.setText(String.valueOf(data.getTotalPointsIssued() != null ? data.getTotalPointsIssued() : 0));
        activeFleetUtilizationLabel.setText(String.format("%.1f %%", data.getFleetUtilizationRate() != null ? data.getFleetUtilizationRate() : 0.0));

        // 1. Populate PieChart: Material Distribution
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        if (data.getCategoryBreakdown() != null) {
            for (Map.Entry<String, Double> entry : data.getCategoryBreakdown().entrySet()) {
                pieData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
            }
        }
        categoryDistributionChart.setData(pieData);

        // 2. Populate BarChart: Facility Throughput
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Tonnage Processed (kg)");
        if (data.getFacilityThroughputKg() != null) {
            for (Map.Entry<String, Double> entry : data.getFacilityThroughputKg().entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
        }
        facilityThroughputChart.getData().clear();
        facilityThroughputChart.getData().add(series);
    }

    @FXML
    private void handleRefresh() {
        loadAnalyticsData();
    }

    @FXML
    private void handleBack() {
        SceneNavigator.loadScreen(AppScreen.ADMIN_DASHBOARD);
    }

    private void setLoading(boolean isLoading) {
        if (loadingIndicator != null) loadingIndicator.setVisible(isLoading);
        if (refreshButton != null) refreshButton.setDisable(isLoading);
    }
}