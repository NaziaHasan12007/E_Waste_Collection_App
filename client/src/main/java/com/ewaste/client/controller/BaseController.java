package com.ewaste.client.controller;

import com.ewaste.client.navigation.AppScreen;
import com.ewaste.client.navigation.SceneNavigator;
import com.ewaste.client.session.UserSession;
import com.ewaste.client.util.AlertHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base abstract controller providing session access, logging,
 * thread-safe alerts, and unified navigation lifecycle methods.
 */
public abstract class BaseController {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final UserSession session = UserSession.getInstance();

    @FXML
    public void initialize() {
        log.debug("Initializing controller: {}", getClass().getSimpleName());
    }

    /**
     * Centralized logout procedure clearing the active session and returning to Login screen.
     */
    @FXML
    protected void handleLogout() {
        log.info("User {} logged out", session.getEmail());
        session.clear();
        SceneNavigator.loadScreen(AppScreen.LOGIN, false);
    }

    /**
     * Executes an asynchronous task off the JavaFX Application Thread.
     */
    protected void runAsync(Runnable backgroundTask) {
        new Thread(backgroundTask).start();
    }

    /**
     * Runs an update on the JavaFX UI Application Thread.
     */
    protected void runOnUiThread(Runnable uiTask) {
        if (Platform.isFxApplicationThread()) {
            uiTask.run();
        } else {
            Platform.runLater(uiTask);
        }
    }

    /**
     * Shows a modal error dialogue safely from any thread.
     */
    protected void showErrorAlert(String title, String message) {
        runOnUiThread(() -> AlertHelper.showError(title, message));
    }

    /**
     * Shows an informational dialogue safely from any thread.
     */
    protected void showInfoAlert(String title, String message) {
        runOnUiThread(() -> AlertHelper.showInfo(title, message));
    }
}