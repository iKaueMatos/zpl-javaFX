package com.novasoftware;

import com.novasoftware.core.config.AppInitializer;

import javafx.application.Application;
import javafx.stage.Stage;

public class NovaSoftwareToolsApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        AppInitializer.initialize(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
