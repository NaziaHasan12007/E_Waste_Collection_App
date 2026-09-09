package com.ewaste.client.util;

import com.ewaste.client.navigation.AppScreen;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Utility class for loading FXML views
 */
public class ViewLoader {

    private static final String FXML_BASE_PATH = "/fxml";

    /**
     * Load a view by screen enum
     */
    public static Parent loadView(AppScreen screen) throws IOException {
        return loadView(screen.getFxmlPath());
    }

    /**
     * Load a view by FXML path
     */
    public static Parent loadView(String fxmlPath) throws IOException {
        URL resource = ViewLoader.class.getResource(fxmlPath);
        if (resource == null) {
            throw new IOException("FXML resource not found: " + fxmlPath);
        }
        return FXMLLoader.load(resource);
    }

    /**
     * Load a view with a custom controller
     */
    public static <T> T loadViewWithController(AppScreen screen, Class<T> controllerClass) throws IOException {
        return loadViewWithController(screen.getFxmlPath(), controllerClass);
    }

    /**
     * Load a view with a custom controller
     */
    public static <T> T loadViewWithController(String fxmlPath, Class<T> controllerClass) throws IOException {
        URL resource = ViewLoader.class.getResource(fxmlPath);
        if (resource == null) {
            throw new IOException("FXML resource not found: " + fxmlPath);
        }
        FXMLLoader loader = new FXMLLoader(resource);
        loader.load();
        return loader.getController();
    }

    /**
     * Load a view and return both the Parent and the Controller
     */
    public static ViewLoadResult loadViewWithController(AppScreen screen) throws IOException {
        return loadViewWithController(screen.getFxmlPath());
    }

    /**
     * Load a view and return both the Parent and the Controller
     */
    public static ViewLoadResult loadViewWithController(String fxmlPath) throws IOException {
        URL resource = ViewLoader.class.getResource(fxmlPath);
        if (resource == null) {
            throw new IOException("FXML resource not found: " + fxmlPath);
        }
        FXMLLoader loader = new FXMLLoader(resource);
        Parent parent = loader.load();
        Object controller = loader.getController();
        return new ViewLoadResult(parent, controller);
    }

    /**
     * Result wrapper for view loading with controller
     */
    public static class ViewLoadResult {
        private final Parent parent;
        private final Object controller;

        public ViewLoadResult(Parent parent, Object controller) {
            this.parent = parent;
            this.controller = controller;
        }

        public Parent getParent() {
            return parent;
        }

        public Object getController() {
            return controller;
        }

        @SuppressWarnings("unchecked")
        public <T> T getController(Class<T> controllerClass) {
            return (T) controller;
        }
    }
}