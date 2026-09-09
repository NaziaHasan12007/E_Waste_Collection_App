package com.ewaste.client.navigation;

/**
 * Maps logical screens to their respective FXML view files.
 */
public enum AppScreen {
    LOGIN("/fxml/auth/login.fxml", "E-Waste System - Login"),
    REGISTER("/fxml/auth/register.fxml", "E-Waste System - Create Account"),

    // Customer Views
    CUSTOMER_DASHBOARD("/fxml/customer/customer_dashboard.fxml", "Customer Dashboard"),
    SUBMIT_EWASTE("/fxml/customer/submit_ewaste.fxml", "Submit E-Waste Item"),
    PICKUP_HISTORY("/fxml/customer/pickup_history.fxml", "My Pickups & Requests"),
    REWARD_VIEW("/fxml/customer/reward_view.fxml", "Reward Points & Balance"),

    // Collector Views
    COLLECTOR_DASHBOARD("/fxml/collector/collector_dashboard.fxml", "Collector Control Center"),
    COLLECTOR_ASSIGNED_TASKS("/fxml/collector/assigned_tasks.fxml", "Assigned Tasks & Operations"),

    // Admin Views
    ADMIN_DASHBOARD("/fxml/admin/admin_dashboard.fxml", "Admin Management Console"),
    ADMIN_PICKUP_MANAGEMENT("/fxml/admin/pickup_management.fxml", "Pickup Logistics & Scheduling"),
    ADMIN_COLLECTOR_MANAGEMENT("/fxml/admin/collector_management.fxml", "Collector Fleet Management"),
    ADMIN_INSPECTION_PROCESSING("/fxml/admin/inspection_processing.fxml", "Facility Inspection & Processing"),
    ADMIN_ANALYTICS("/fxml/admin/analytics_view.fxml", "System Analytics & Environmental Reports");

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