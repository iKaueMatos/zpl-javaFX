package com.zpl.zpl;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.concurrent.Task;

import com.zpl.zpl.infrastructure.database.DatabaseInitializer;
import com.zpl.zpl.infrastructure.http.controller.LoginScreenController;

public class ZplApplication extends Application {
    @Override
    public void start(@SuppressWarnings("exports") Stage primaryStage) throws Exception {
        showLoadingScreen(primaryStage);
        
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                DatabaseInitializer.initialize();
                return null;
            }

            @Override
            protected void succeeded() {
                try {
                    showLoginScreen(primaryStage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        new Thread(task).start();
    }

    private void showLoadingScreen(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/loading_screen.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    private void showLoginScreen(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login_screen.fxml"));
        Parent root = loader.load();
        LoginScreenController controller = loader.getController();
        controller.setStage(stage);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}