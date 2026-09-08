package com.ewaste.client.controller;

import com.ewaste.client.util.AlertHelper;
import com.ewaste.client.navigation.SceneNavigator;
import com.ewaste.client.session.UserSession;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Base controller with common functionality for all controllers
 */
public abstract class BaseController {

    protected UserSession userSession;
    protected SceneNavigator sceneNavigator;

    @FXML
    protected Label lblUserName;
    @FXML
    protected Label lblUserRole;
    @FXML
    protected VBox sidebar;

    public BaseController() {
        this.userSession = UserSession.getInstance();
        this.sceneNavigator = SceneNavigator.getInstance();
    }

    /**
     * Initialize the controller - called after FXML loading
     */
    @FXML
    public void initialize() {
        setupUserInfo();
        setupSidebar();
        onInitialize();
    }

    /**
     * Override this method for custom initialization
     */
    protected void onInitialize() {
        // Override in subclasses
    }

    /**
     * Setup user information in the header
     */
    protected void setupUserInfo() {
        if (lblUserName != null) {
            String name = userSession.getFullName();
            lblUserName.setText(name != null ? name : "Guest");
        }
        if (lblUserRole != null) {
            String role = userSession.getRole();
            lblUserRole.setText(role != null ? role : "Unknown");
            // Apply role-based styling
            if (role != null) {
                lblUserRole.getStyleClass().add("role-" + role.toLowerCase());
            }
        }
    }

    /**
     * Setup sidebar navigation
     */
    protected void setupSidebar() {
        // Override in subclasses for specific sidebar items
    }

    /**
     * Navigate to a specific screen
     */
    protected void navigateTo(com.ewaste.client.core.AppScreen screen) {
        sceneNavigator.navigateTo(screen);
    }

    /**
     * Navigate back to previous screen
     */
    protected void navigateBack() {
        sceneNavigator.navigateBack();
    }

    /**
     * Show information alert
     */
    protected void showInfo(String title, String header, String content) {
        AlertHelper.showInfo(title, header, content);
    }

    /**
     * Show warning alert
     */
    protected void showWarning(String title, String header, String content) {
        AlertHelper.showWarning(title, header, content);
    }

    /**
     * Show error alert
     */
    protected void showError(String title, String header, String content) {
        AlertHelper.showError(title, header, content);
    }

    /**
     * Show confirmation dialog
     */
    protected boolean showConfirmation(String title, String header, String content) {
        return AlertHelper.showConfirmation(title, header, content);
    }

    /**
     * Show error alert from exception
     */
    protected void showException(String title, String header, Throwable throwable) {
        AlertHelper.showException(title, header, throwable);
    }

    /**
     * Show info alert asynchronously on JavaFX thread
     */
    protected void showInfoAsync(String title, String header, String content) {
        Platform.runLater(() -> showInfo(title, header, content));
    }

    /**
     * Show error alert asynchronously on JavaFX thread
     */
    protected void showErrorAsync(String title, String header, String content) {
        Platform.runLater(() -> showError(title, header, content));
    }

    /**
     * Logout the current user
     */
    @FXML
    protected void handleLogout() {
        if (showConfirmation("Logout", "Confirm Logout", "Are you sure you want to logout?")) {
            userSession.endSession();
            navigateTo(com.ewaste.client.core.AppScreen.LOGIN);
        }
    }

    /**
     * Check if user is authenticated, redirect to login if not
     */
    protected boolean checkAuthentication() {
        if (!userSession.isAuthenticated()) {
            navigateTo(com.ewaste.client.core.AppScreen.LOGIN);
            return false;
        }
        return true;
    }

    /**
     * Check if user has required role
     */
    protected boolean checkRole(String requiredRole) {
        if (!checkAuthentication()) {
            return false;
        }
        if (!userSession.hasRole(requiredRole)) {
            showError("Access Denied", "Insufficient Permissions",
                    "You need " + requiredRole + " role to access this feature.");
            return false;
        }
        return true;
    }

    /**
     * Safe string conversion for nullable objects
     */
    protected String safeString(Object obj) {
        return obj != null ? obj.toString() : "";
    }

    /**
     * Safe long conversion for nullable Long objects
     */
    protected long safeLong(Long value) {
        return value != null ? value : 0L;
    }

    /**
     * Safe double conversion for nullable Double objects
     */
    protected double safeDouble(Double value) {
        return value != null ? value : 0.0;
    }

    /**
     * Safe integer conversion for nullable Integer objects
     */
    protected int safeInt(Integer value) {
        return value != null ? value : 0;
    }

    /**
     * Run a task with error handling
     */
    protected void runWithErrorHandling(Runnable task, String errorTitle, String errorHeader) {
        try {
            task.run();
        } catch (Exception e) {
            showException(errorTitle, errorHeader, e);
        }
    }
}