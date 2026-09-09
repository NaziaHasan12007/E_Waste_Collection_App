package com.ewaste.client;

import com.ewaste.client.config.ApiConfig;
import com.ewaste.client.navigation.AppScreen;
import com.ewaste.client.config.ClientContext;
import com.ewaste.client.navigation.SceneNavigator;
import com.ewaste.client.session.UserSession;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main JavaFX Application Entry Point
 */
public class EWasteClientApp extends Application {

    private static EWasteClientApp instance;

    @Override
    public void start(Stage primaryStage) {
        instance = this;

        // Initialize application components
        ApiConfig.getInstance(); // Load config
        ClientContext.getInstance(); // Initialize context
        SceneNavigator.getInstance().initialize(primaryStage);

        // Set application icon if available
        // primaryStage.getIcons().add(new Image("/images/app-icon.png"));

        // Check if user has a valid session, otherwise navigate to login
        if (UserSession.getInstance().isAuthenticated()) {
            navigateBasedOnRole();
        } else {
            SceneNavigator.getInstance().navigateTo(AppScreen.LOGIN);
        }
    }

    /**
     * Navigate to appropriate dashboard based on user role
     */
    public void navigateBasedOnRole() {
        UserSession session = UserSession.getInstance();

        if (session.isAdmin()) {
            SceneNavigator.getInstance().navigateTo(AppScreen.ADMIN_DASHBOARD);
        } else if (session.isCollector()) {
            SceneNavigator.getInstance().navigateTo(AppScreen.COLLECTOR_DASHBOARD);
        } else if (session.isCustomer()) {
            SceneNavigator.getInstance().navigateTo(AppScreen.CUSTOMER_DASHBOARD);
        } else {
            SceneNavigator.getInstance().navigateTo(AppScreen.LOGIN);
        }
    }

    /**
     * Get the application instance
     */
    public static EWasteClientApp getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        launch(args);
    }
}