package com.novasoftware.tools;

import java.net.URL;

import com.novasoftware.tools.infrastructure.database.DatabaseInitializer;
import com.novasoftware.tools.infrastructure.http.controller.auth.LoginController;
import com.novasoftware.tools.ui.view.MainScreen;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class NovaSoftwareToolsApplication extends Application {

    private static final String GLOBAL_CSS = "/view/css/tool-zpl.css";

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            URL resource = getClass().getResource("/view/fxml/login_screen.fxml");
            FXMLLoader loader = new FXMLLoader(resource);

            URL logoResource = getClass().getResource("/view/assets/logo-app.jpg");
            primaryStage.getIcons().add(new Image(logoResource.toExternalForm()));
            primaryStage.setTitle("Ferramentas - Nova Software");

            primaryStage.initStyle(StageStyle.UNDECORATED);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            scene.getStylesheets().add(getClass().getResource(GLOBAL_CSS).toExternalForm());

            showLoginScreen(primaryStage, scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showLoginScreen(Stage stage, Scene scene) throws Exception {
        try {
            URL resource = getClass().getResource("/view/fxml/login_screen.fxml");
            FXMLLoader loader = new FXMLLoader(resource);
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

            stage.setWidth(1200);
            stage.setHeight(600);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.setFullScreen(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showLoadingScreen(Stage stage) throws Exception {
        try {
            URL resource = getClass().getResource("/view/fxml/loading_screen.fxml");
            if (resource == null) {
                throw new RuntimeException("Error: FXML resource '/view/fxml/loading_screen.fxml' not found.");
            }
            FXMLLoader loader = new FXMLLoader(resource);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
