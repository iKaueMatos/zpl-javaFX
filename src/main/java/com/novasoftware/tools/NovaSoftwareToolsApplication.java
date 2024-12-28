package com.novasoftware.tools;

import com.novasoftware.tools.ui.view.MainScreen;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import com.novasoftware.tools.infrastructure.database.DatabaseInitializer;
import com.novasoftware.tools.infrastructure.http.controller.auth.LoginController;

public class NovaSoftwareToolsApplication extends Application {

    private static final String GLOBAL_CSS = "/css/dark-theme.css";

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login_screen.fxml"));

        String logoPath = getClass().getResource("/assets/icon/logo.jpg").toExternalForm();
        primaryStage.getIcons().add(new Image(logoPath));
        primaryStage.setTitle("Ferramentas - Nova Software");

        primaryStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource(GLOBAL_CSS).toExternalForm());

        showLoginScreen(primaryStage, scene);
    }

    private void showLoginScreen(Stage stage, Scene scene) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login_screen.fxml"));
        Parent root = loader.load();
        scene.setRoot(root);
        LoginController controller = loader.getController();
        controller.setStage(stage);

        controller.setOnLoginSuccess(() -> {
            try {
                showLoadingScreen(stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        stage.setWidth(900);
        stage.setHeight(600);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setFullScreen(false);
        stage.show();
    }

    private void showLoadingScreen(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/loading_screen.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

        scene.getStylesheets().add(getClass().getResource(GLOBAL_CSS).toExternalForm());
        stage.setScene(scene);
        stage.setFullScreen(false);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                DatabaseInitializer.initialize();
                return null;
            }

            @Override
            protected void succeeded() {
                try {
                    MainScreen startupView = new MainScreen();
                    startupView.showMainScreen(stage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void failed() {
                System.err.println("Erro durante a inicialização do banco de dados.");
                getException().printStackTrace();
            }
        };

        new Thread(task).start();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
