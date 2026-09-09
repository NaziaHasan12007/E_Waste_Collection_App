package com.ewaste.client.controller.admin;

import com.ewaste.client.api.ReportApiClient;
import com.ewaste.client.config.ClientContext;
import com.ewaste.client.controller.BaseController;
import com.ewaste.client.dto.response.AnalyticsClientResponse;
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

    @Override
    protected void onInitialize() {
        reportApiClient = ClientContext.getInstance().getReportApiClient();
        loadAnalyticsData();
    }

    private void loadAnalyticsData() {
        setLoading(true);

        new Thread(() -> {
            try {
                AnalyticsClientResponse data = reportApiClient.getReportsSummary();

                Platform.runLater(() -> {
                    setLoading(false);
                    if (data != null) {
                        renderMetrics(data);
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

    private void renderMetrics(AnalyticsClientResponse data) {
        totalVolumeLabel.setText(String.format("%.1f kg",
                data.getTotalWeightRecycled() != null ? data.getTotalWeightRecycled() : 0.0));

        hazardousRatioLabel.setText(String.format("%.1f %%",
                data.getHazardousWasteRatio() != null ? data.getHazardousWasteRatio() : 0.0));

        totalPointsIssuedLabel.setText(String.valueOf(
                data.getTotalPointsEarned() != null ? data.getTotalPointsEarned() : 0));

        double utilization = 0.0;
        if (data.getActiveCollectors() != null && data.getTotalCollectors() != null && data.getTotalCollectors() > 0) {
            utilization = (data.getActiveCollectors() * 100.0) / data.getTotalCollectors();
        }
        activeFleetUtilizationLabel.setText(String.format("%.1f %%", utilization));

        // Populate PieChart
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        pieData.add(new PieChart.Data("Laptops", 30));
        pieData.add(new PieChart.Data("Batteries", 25));
        pieData.add(new PieChart.Data("Displays", 20));
        pieData.add(new PieChart.Data("Circuit Boards", 15));
        pieData.add(new PieChart.Data("Other", 10));
        categoryDistributionChart.setData(pieData);

        // Populate BarChart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Tonnage Processed (kg)");
        series.getData().add(new XYChart.Data<>("Center A", 450));
        series.getData().add(new XYChart.Data<>("Center B", 320));
        series.getData().add(new XYChart.Data<>("Center C", 280));
        series.getData().add(new XYChart.Data<>("Center D", 190));
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