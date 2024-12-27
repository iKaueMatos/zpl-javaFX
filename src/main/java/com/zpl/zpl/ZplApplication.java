package com.zpl.zpl;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

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

    public void openVisualizeZplView() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/VisualizeZplView.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Visualize ZPL");
        stage.show();
    }
}