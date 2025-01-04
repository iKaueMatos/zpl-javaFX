package com.novasoftware.core.config;

import javafx.application.Platform;
import javafx.stage.Stage;
import com.novasoftware.base.ui.view.MainScreen;
import com.novasoftware.core.path.ResourcePaths;
import com.novasoftware.shared.database.environment.DatabaseInitializer;
import com.novasoftware.user.infra.http.controller.auth.LoginController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.StageStyle;
import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;

import java.net.URL;
import java.sql.SQLException;

public class AppInitializer {

    public static void initialize(Stage primaryStage) {
        try {
            DatabaseInitializer.initialize();
            configureGlobalTheme();
            configurePrimaryStage(primaryStage);
            showLoginScreen(primaryStage);
            SystemTrayManager.addToSystemTray(primaryStage);
        } catch (Exception e) {
            System.err.println("Erro ao iniciar a aplicação: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void configureGlobalTheme() {
        UserAgentBuilder.builder()
                .themes(JavaFXThemes.MODENA)
                .themes(MaterialFXStylesheets.forAssemble(true))
                .setDeploy(true)
                .setResolveAssets(true)
                .build()
                .setGlobal();
    }

    private static void configurePrimaryStage(Stage primaryStage) {
        URL logoResource = AppInitializer.class.getResource(ResourcePaths.LOGO_PATH);
        if (logoResource == null) {
            throw new IllegalArgumentException("Recurso de logo não encontrado em: " + ResourcePaths.LOGO_PATH);
        }
        primaryStage.getIcons().add(new Image(logoResource.toExternalForm()));
        primaryStage.setTitle("Ferramentas - Nova Software");
        primaryStage.initStyle(StageStyle.UNDECORATED);
    }

    public static void showLoginScreen(Stage stage) throws Exception {
        URL resource = AppInitializer.class.getResource(ResourcePaths.LOGIN_SCREEN_PATH);
        if (resource == null) {
            throw new IllegalArgumentException("Arquivo FXML não encontrado: " + ResourcePaths.LOGIN_SCREEN_PATH);
        }

        FXMLLoader loader = new FXMLLoader(resource);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

        LoginController controller = loader.getController();
        controller.setStage(stage);
        controller.setOnLoginSuccess(() -> showLoadingScreen(stage));

        stage.setWidth(1200);
        stage.setHeight(600);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private static void showLoadingScreen(Stage stage) {
        try {
            URL resource = AppInitializer.class.getResource(ResourcePaths.LOADING_SCREEN_PATH);
            if (resource == null) {
                throw new IllegalArgumentException("Arquivo FXML não encontrado: " + ResourcePaths.LOADING_SCREEN_PATH);
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

            stage.setScene(scene);
            stage.show();

            new Thread(() -> {
                try {
                    DatabaseInitializer.initialize();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(() -> showMainScreen(stage));
            }).start();
        } catch (Exception e) {
            System.err.println("Erro ao exibir a tela de carregamento: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void showMainScreen(Stage stage) {
        try {
            MainScreen startupView = new MainScreen();
            startupView.showMainScreen(stage);
        } catch (Exception e) {
            System.err.println("Erro ao exibir a tela principal: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
