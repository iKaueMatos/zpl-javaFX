package com.novasoftware.tools.ui.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainScreen extends Application {

    private static final String GLOBAL_CSS = "/css/style.css";

    @Override
    public void start(Stage primaryStage) throws Exception {
        showMainScreen(primaryStage);
    }

    public void showMainScreen(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main_layout_screen.fxml"));
        Scene scene = new Scene(loader.load(), 1600, 768);
        scene.getStylesheets().add(getClass().getResource(GLOBAL_CSS).toExternalForm());
        stage.setScene(scene);
        stage.setFullScreen(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
