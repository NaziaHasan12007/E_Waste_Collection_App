package com.ewaste.client.navigation;

import com.ewaste.client.util.ViewLoader;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Centralized navigation manager coordinating JavaFX scenes, root swaps,
 * stage titles, and navigation history.
 */
public final class SceneNavigator {

    private static final Logger log = LoggerFactory.getLogger(SceneNavigator.class);
    private static Stage primaryStage;
    private static final Deque<AppScreen> historyStack = new ArrayDeque<>();
    private static AppScreen currentScreen;

    private SceneNavigator() {}

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static AppScreen getCurrentScreen() {
        return currentScreen;
    }

    /**
     * Swaps the view to the specified AppScreen and pushes the previous screen onto history.
     */
    public static void loadScreen(AppScreen screen) {
        loadScreen(screen, true);
    }

    /**
     * Swaps the view to the specified AppScreen with optional history tracking.
     */
    public static void loadScreen(AppScreen screen, boolean recordHistory) {
        if (primaryStage == null) {
            throw new IllegalStateException("Primary Stage is not initialized in SceneNavigator.");
        }

        Platform.runLater(() -> {
            try {
                Parent root = ViewLoader.load(screen.getFxmlPath());
                Scene scene = primaryStage.getScene();

                if (scene == null) {
                    scene = new Scene(root, 1024, 680);
                    primaryStage.setScene(scene);
                } else {
                    scene.setRoot(root);
                }

                primaryStage.setTitle(screen.getTitle());
                primaryStage.show();

                if (recordHistory && currentScreen != null) {
                    historyStack.push(currentScreen);
                }
                currentScreen = screen;
                log.info("Navigated to screen: {} ({})", screen.name(), screen.getFxmlPath());
            } catch (Exception e) {
                log.error("Failed to navigate to screen {}: {}", screen, e.getMessage(), e);
                throw new RuntimeException("View transition failed: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Navigates back to the previously viewed screen.
     */
    public static boolean navigateBack() {
        if (!historyStack.isEmpty()) {
            AppScreen prev = historyStack.pop();
            loadScreen(prev, false);
            return true;
        }
        return false;
    }

    public static void clearHistory() {
        historyStack.clear();
    }
}