package com.ewaste.client;

import com.ewaste.client.config.ClientContext;
import com.ewaste.client.navigation.AppScreen;
import com.ewaste.client.navigation.SceneNavigator;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * JavaFX desktop application launcher initializing runtime singletons,
 * stage settings, application icons, and bootstrapping the login view.
 */
public class EWasteClientApp extends Application {

    private static final Logger log = LoggerFactory.getLogger(EWasteClientApp.class);

    @Override
    public void init() {
        log.info("Initializing client services and dependencies...");
        // Warm up ClientContext singleton, Jackson modules, and HttpClient
        ClientContext.getInstance();
    }

    @Override
    public void start(Stage primaryStage) {
        log.info("Starting JavaFX client application...");
        primaryStage.setMinWidth(960);
        primaryStage.setMinHeight(640);

        // Load application icon if available
        try (InputStream iconStream = getClass().getResourceAsStream("/images/app_icon.png")) {
            if (iconStream != null) {
                primaryStage.getIcons().add(new Image(iconStream));
            }
        } catch (Exception e) {
            log.warn("Could not load application icon: {}", e.getMessage());
        }

        SceneNavigator.setPrimaryStage(primaryStage);
        SceneNavigator.loadScreen(AppScreen.LOGIN, false);
    }

    public static void main(String[] args) {
        launch(args);
    }
}