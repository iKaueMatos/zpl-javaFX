package com.zpl.zpl;

import javafx.application.Application;
import javafx.stage.Stage;

import com.zpl.zpl.infrastructure.database.DatabaseInitializer;
import com.zpl.zpl.ui.StageInitializer;

public class ZplApplication extends Application {
    @Override
    public void start(@SuppressWarnings("exports") Stage primaryStage) throws Exception {
        DatabaseInitializer.initialize();
        StageInitializer.initialize(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}