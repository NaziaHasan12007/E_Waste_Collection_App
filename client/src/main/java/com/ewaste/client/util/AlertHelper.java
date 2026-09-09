package com.ewaste.client.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * Standardized dialog utility providing styled modals for errors, confirmations, and alerts.
 */
public final class AlertHelper {

    private AlertHelper() {}

    public static void showInfo(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, message);
    }

    public static void showWarning(String title, String message) {
        showAlert(Alert.AlertType.WARNING, title, message);
    }

    public static void showError(String title, String message) {
        showAlert(Alert.AlertType.ERROR, title, message);
    }

    public static boolean showConfirmation(String title, String message) {
        if (!Platform.isFxApplicationThread()) {
            final boolean[] result = new boolean[1];
            CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(() -> {
                result[0] = showConfirmationDialog(title, message);
                latch.countDown();
            });
            try {
                latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return result[0];
        }
        return showConfirmationDialog(title, message);
    }

    private static boolean showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private static void showAlert(Alert.AlertType type, String title, String message) {
        Runnable showTask = () -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        };

        if (Platform.isFxApplicationThread()) {
            showTask.run();
        } else {
            Platform.runLater(showTask);
        }
    }
}