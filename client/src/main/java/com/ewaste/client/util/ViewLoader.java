package com.ewaste.client.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;

/**
 * Utility responsible for resolving and loading FXML resources cleanly.
 */
public final class ViewLoader {

    private ViewLoader() {}

    public static Parent load(String fxmlPath) throws IOException {
        URL resource = ViewLoader.class.getResource(fxmlPath);
        if (resource == null) {
            throw new IOException("FXML resource file not found at path: " + fxmlPath);
        }
        FXMLLoader loader = new FXMLLoader(resource);
        return loader.load();
    }

    public static <T> LoadedView<T> loadWithController(String fxmlPath) throws IOException {
        URL resource = ViewLoader.class.getResource(fxmlPath);
        if (resource == null) {
            throw new IOException("FXML resource file not found at path: " + fxmlPath);
        }
        FXMLLoader loader = new FXMLLoader(resource);
        Parent root = loader.load();
        T controller = loader.getController();
        return new LoadedView<>(root, controller);
    }

    public static class LoadedView<C> {
        private final Parent root;
        private final C controller;

        public LoadedView(Parent root, C controller) {
            this.root = root;
            this.controller = controller;
        }

        public Parent getRoot() {
            return root;
        }

        public C getController() {
            return controller;
        }
    }
}