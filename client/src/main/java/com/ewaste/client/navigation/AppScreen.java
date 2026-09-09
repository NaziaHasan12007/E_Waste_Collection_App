package com.ewaste.client.navigation;

/**
 * Enum representing all screens in the application
 */
public enum AppScreen {
    // Auth Screens
    LOGIN("/fxml/auth/login.fxml", "Login"),
    REGISTER("/fxml/auth/register.fxml", "Register"),

    // Customer Screens
    CUSTOMER_DASHBOARD("/fxml/customer/customer_dashboard.fxml", "Customer Dashboard"),
    SUBMIT_EWASTE("/fxml/customer/submit_ewaste.fxml", "Submit E-Waste"),
    PICKUP_HISTORY("/fxml/customer/pickup_history.fxml", "Pickup History"),
    REWARD_LEDGER("/fxml/customer/reward_view.fxml", "Reward Ledger"),

    // Collector Screens
    COLLECTOR_DASHBOARD("/fxml/collector/collector_dashboard.fxml", "Collector Dashboard"),
    ASSIGNED_TASKS("/fxml/collector/assigned_tasks.fxml", "Assigned Tasks"),

    // Admin Screens
    ADMIN_DASHBOARD("/fxml/admin/admin_dashboard.fxml", "Admin Dashboard"),
    PICKUP_MANAGEMENT("/fxml/admin/pickup_management.fxml", "Pickup Management"),
    COLLECTOR_MANAGEMENT("/fxml/admin/collector_management.fxml", "Collector Management"),
    INSPECTION_PROCESSING("/fxml/admin/inspection_processing.fxml", "Inspection & Processing"),
    ANALYTICS_REPORTING("/fxml/admin/analytics_view.fxml", "Analytics & Reporting");

    private final String fxmlPath;
    private final String title;

    AppScreen(String fxmlPath, String title) {
        this.fxmlPath = fxmlPath;
        this.title = title;
    }

    public String getFxmlPath() {
        return fxmlPath;
    }

    public String getTitle() {
        return title;
    }
}