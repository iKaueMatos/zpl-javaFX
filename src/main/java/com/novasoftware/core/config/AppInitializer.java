package com.novasoftware.core.config;

import com.novasoftware.shared.util.alert.CustomAlert;
import com.novasoftware.tools.application.repository.ConfigRepository;
import com.novasoftware.tools.domain.service.PrinterService;
import com.novasoftware.tools.infrastructure.repository.ConfigRepositoryImpl;
import com.novasoftware.user.infra.http.controller.auth.LoginController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import com.novasoftware.base.ui.view.MainScreen;
import com.novasoftware.core.path.ResourcePaths;
import com.novasoftware.shared.database.environment.DatabaseInitializer;
import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;

import java.net.URL;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppInitializer {
    private static StackPane rootContainer = new StackPane();
    private static Stage primaryStage;
    private static PrinterService printerService = new PrinterService();
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static void initialize(Stage stage) {
        primaryStage = stage;
        try {
            DatabaseInitializer.initialize();
            configureGlobalTheme();
            configurePrimaryStage(stage);
            initializeScreens();

            CompletableFuture<Void> initializationTask = CompletableFuture.runAsync(() -> {
                try {
                    detectPrintersAsync();
                } catch (Exception e) {
                    System.err.println("Erro ao inicializar a detecção de impressoras: " + e.getMessage());
                    Platform.runLater(() -> {
                        CustomAlert.showErrorAlert(primaryStage, "Erro na Detecção de Impressoras",
                                "Não foi possível inicializar a detecção de impressoras.");
                    });
                }
            }, executorService);

            initializationTask.thenRunAsync(() -> {
                Platform.runLater(() -> {
                    System.out.println("Detecção de impressoras concluída com sucesso.");
                });
            });
        } catch (Exception e) {
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

    public static void detectPrintersAsync() {
        CompletableFuture.runAsync(() -> {
            try {
                printerService.detectPrinters();
            } catch (Exception e) {
                System.err.println("Erro ao detectar impressoras: " + e.getMessage());
            }
        }).exceptionally(ex -> {
            System.err.println("Erro inesperado ao detectar impressoras: " + ex.getMessage());
            return null;
        });
    }

    private static void configurePrimaryStage(Stage stage) {
        URL logoResource = AppInitializer.class.getResource(ResourcePaths.LOGO_PATH);
        if (logoResource == null) {
            throw new IllegalArgumentException("Recurso de logo não encontrado em: " + ResourcePaths.LOGO_PATH);
        }
        stage.getIcons().add(new Image(logoResource.toExternalForm()));
        stage.setTitle("Ferramentas - Nova Software");
        stage.initStyle(StageStyle.UNDECORATED);
    }

    private static void initializeScreens() throws Exception {
        FXMLLoader loginLoader = new FXMLLoader(AppInitializer.class.getResource(ResourcePaths.LOGIN_SCREEN_PATH));
        Parent loginScreen = loginLoader.load();

        FXMLLoader registerLoader = new FXMLLoader(AppInitializer.class.getResource(ResourcePaths.REGISTER_SCREEN_PATH));
        Parent registerScreen = registerLoader.load();

        FXMLLoader forgotPasswordLoader = new FXMLLoader(AppInitializer.class.getResource(ResourcePaths.FORGOT_PASSWORD_SCREEN_PATH));
        Parent forgotPasswordScreen = forgotPasswordLoader.load();

        LoginController loginController = loginLoader.getController();
        loginController.setOnLoginSuccess(() -> {
            try {
                showLoadingScreen();
            } catch (Exception e) {
                CustomAlert.showErrorAlert(primaryStage, "Ocorreu um erro inesperado.", "Por favor, tente novamente mais tarde.");
            }
        });

        rootContainer.getChildren().addAll(loginScreen, registerScreen, forgotPasswordScreen);
        showScreen(loginScreen);

        Scene scene = new Scene(rootContainer, 1200, 600);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private static Parent loadScreen(String fxmlPath) throws Exception {
        URL resource = AppInitializer.class.getResource(fxmlPath);
        if (resource == null) {
            throw new IllegalArgumentException("Arquivo FXML não encontrado: " + fxmlPath);
        }

        FXMLLoader loader = new FXMLLoader(resource);
        return loader.load();
    }

    public static void showScreen(Parent screen) {
        rootContainer.getChildren().forEach(node -> node.setVisible(false));
        screen.setVisible(true);
    }

    public static void showLoginScreen() {
        Parent loginScreen = (Parent) rootContainer.getChildren().get(0);
        System.out.println(loginScreen);
        showScreen(loginScreen);
    }

    public static void showRegisterScreen() {
        Parent registerScreen = (Parent) rootContainer.getChildren().get(1);
        showScreen(registerScreen);
    }

    public static void showForgotPasswordScreen() {
        Parent forgotPasswordScreen = (Parent) rootContainer.getChildren().get(2);
        showScreen(forgotPasswordScreen);
    }

    private static void showLoadingScreen() {
        try {
            URL resource = AppInitializer.class.getResource(ResourcePaths.LOADING_SCREEN_PATH);
            if (resource == null) {
                throw new IllegalArgumentException("Arquivo FXML não encontrado: " + ResourcePaths.LOADING_SCREEN_PATH);
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            rootContainer.getChildren().add(root);
            showScreen(root);

            new Thread(() -> {
                try {
                    DatabaseInitializer.initialize();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(() -> showMainScreen());
            }).start();
        } catch (Exception e) {
            System.err.println("Erro ao exibir a tela de carregamento: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void showMainScreen() {
        try {
            MainScreen startupView = new MainScreen();
            startupView.showMainScreen(primaryStage);
        } catch (Exception e) {
            System.err.println("Erro ao exibir a tela principal: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
