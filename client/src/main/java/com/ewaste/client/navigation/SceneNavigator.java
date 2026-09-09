package com.ewaste.client.navigation;

import com.ewaste.client.EWasteClientApp;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Stack;

public class SceneNavigator {

    private static SceneNavigator instance;
    private Stage primaryStage;
    private Stack<AppScreen> screenHistory = new Stack<>();
    private AppScreen currentScreen;

    private SceneNavigator() {}

    public static SceneNavigator getInstance() {
        if (instance == null) {
            instance = new SceneNavigator();
        }
        return instance;
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void navigateTo(AppScreen screen) {
        if (screen == null) return;

        try {
            // Store current screen in history if not going back
            if (currentScreen != null) {
                screenHistory.push(currentScreen);
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(screen.getFxmlPath()));
            Pane root = loader.load();

            // Get controller and set stage if it's a BaseController
            Object controller = loader.getController();
            if (controller instanceof com.ewaste.client.controller.BaseController) {
                ((com.ewaste.client.controller.BaseController) controller).setPrimaryStage(primaryStage);
            }

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle(screen.getTitle());
            primaryStage.show();

            currentScreen = screen;

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load screen: " + screen.getFxmlPath(), e);
        }
    }

    public void navigateBack() {
        if (!screenHistory.isEmpty()) {
            AppScreen previousScreen = screenHistory.pop();
            navigateTo(previousScreen);
        }
    }

    public void clearHistory() {
        screenHistory.clear();
    }

    public AppScreen getCurrentScreen() {
        return currentScreen;
    }
}