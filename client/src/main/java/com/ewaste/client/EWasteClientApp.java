package com.ewaste.client;

import com.ewaste.client.config.ApiConfig;
import com.ewaste.client.config.ClientContext;
import com.ewaste.client.navigation.AppScreen;
import com.ewaste.client.navigation.SceneNavigator;
import com.ewaste.client.session.UserSession;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main JavaFX Application Entry Point
 */
public class EWasteClientApp extends Application {

    private static EWasteClientApp instance;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        instance = this;

        // Initialize application components
        ApiConfig.getInstance(); // Load config
        ClientContext.getInstance(); // Initialize context

        // Set the primary stage in SceneNavigator
        SceneNavigator.getInstance().setPrimaryStage(primaryStage);

        // Set application title and min size
        primaryStage.setTitle("E-Waste Collection App");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

        // Set application icon if available
        // try {
        //     primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/app_icon.png")));
        // } catch (Exception e) {
        //     System.err.println("Could not load application icon: " + e.getMessage());
        // }

        // Check if user has a valid session, otherwise navigate to login
        if (UserSession.getInstance().isAuthenticated()) {
            navigateBasedOnRole();
        } else {
            SceneNavigator.getInstance().navigateTo(AppScreen.LOGIN);
        }

        // Handle window close event
        primaryStage.setOnCloseRequest(event -> {
            // Clean up session if needed
            UserSession.getInstance().endSession();
        });
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

    /**
     * Get the primary stage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}