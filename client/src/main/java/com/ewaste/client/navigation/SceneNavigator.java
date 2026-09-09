package com.ewaste.client.navigation;

import com.ewaste.client.session.UserSession;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages scene navigation within the application
 */
public class SceneNavigator {

    private static SceneNavigator instance;

    private Stage primaryStage;
    private final Map<AppScreen, Scene> sceneCache = new HashMap<>();
    private AppScreen currentScreen;
    private AppScreen previousScreen;

    private SceneNavigator() {}

    public static SceneNavigator getInstance() {
        if (instance == null) {
            instance = new SceneNavigator();
        }
        return instance;
    }

    public void initialize(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setOnCloseRequest(event -> {
            // Handle application shutdown
            UserSession.getInstance().endSession();
        });
    }

    /**
     * Navigate to a screen
     */
    public void navigateTo(AppScreen screen) {
        navigateTo(screen, true);
    }

    /**
     * Navigate to a screen with option to cache
     */
    public void navigateTo(AppScreen screen, boolean cache) {
        if (primaryStage == null) {
            throw new IllegalStateException("SceneNavigator not initialized. Call initialize() first.");
        }

        try {
            Scene scene;

            if (cache && sceneCache.containsKey(screen)) {
                scene = sceneCache.get(screen);
            } else {
                Parent root = ViewLoader.loadView(screen);
                scene = new Scene(root);
                if (cache) {
                    sceneCache.put(screen, scene);
                }
            }

            // Update screen tracking
            if (currentScreen != null) {
                previousScreen = currentScreen;
            }
            currentScreen = screen;

            // Set scene and show
            primaryStage.setScene(scene);
            primaryStage.setTitle("E-Waste Management - " + screen.getTitle());
            primaryStage.show();

        } catch (Exception e) {
            throw new RuntimeException("Failed to navigate to: " + screen, e);
        }
    }

    /**
     * Navigate back to previous screen
     */
    public void navigateBack() {
        if (previousScreen != null) {
            navigateTo(previousScreen);
        }
    }

    /**
     * Clear the scene cache
     */
    public void clearCache() {
        sceneCache.clear();
    }

    /**
     * Get the current screen
     */
    public AppScreen getCurrentScreen() {
        return currentScreen;
    }

    /**
     * Get the previous screen
     */
    public AppScreen getPreviousScreen() {
        return previousScreen;
    }

    /**
     * Get the primary stage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
}