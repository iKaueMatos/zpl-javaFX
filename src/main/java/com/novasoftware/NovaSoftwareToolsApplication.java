package com.novasoftware;

import java.net.URL;

import com.novasoftware.base.ui.view.MainScreen;
import com.novasoftware.routes.Routes;
import com.novasoftware.shared.database.DatabaseInitializer;
import com.novasoftware.tools.infrastructure.http.controller.auth.LoginController;

import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.flywaydb.core.Flyway;

public class NovaSoftwareToolsApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            runFlywayMigrations();
            configureGlobalTheme();
            configurePrimaryStage(primaryStage);
            showLoginScreen(primaryStage);
        } catch (Exception e) {
            System.err.println("Erro ao iniciar a aplicação: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Configures the global theme and styles for the application.
     */
    private void configureGlobalTheme() {
        UserAgentBuilder.builder()
                .themes(JavaFXThemes.MODENA)
                .themes(MaterialFXStylesheets.forAssemble(true))
                .setDeploy(true)
                .setResolveAssets(true)
                .build()
                .setGlobal();
    }

    /**
     * Configures the main application stage.
     *
     * @param primaryStage the primary stage for the application
     */
    private void configurePrimaryStage(Stage primaryStage) {
        URL logoResource = getClass().getResource(Routes.LOGO_PATH);

        if (logoResource == null) {
            throw new IllegalArgumentException("Recurso de logo não encontrado em: " + Routes.LOGO_PATH);
        }

        primaryStage.getIcons().add(new Image(logoResource.toExternalForm()));
        primaryStage.setTitle("Ferramentas - Nova Software");
        primaryStage.initStyle(StageStyle.UNDECORATED);
    }

    /**
     * Displays the login screen.
     *
     * @param stage the primary stage
     * @throws Exception if an error occurs while loading the login screen
     */
    private void showLoginScreen(Stage stage) throws Exception {
        URL resource = getClass().getResource(Routes.LOGIN_SCREEN_PATH);

        if (resource == null) {
            throw new IllegalArgumentException("Arquivo FXML não encontrado: " + Routes.LOGIN_SCREEN_PATH);
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

    /**
     * Displays the loading screen and initializes the database.
     *
     * @param stage the primary stage
     */
    private void showLoadingScreen(Stage stage) {
        try {
            URL resource = getClass().getResource(Routes.LOADING_SCREEN_PATH);

            if (resource == null) {
                throw new IllegalArgumentException("Arquivo FXML não encontrado: " + Routes.LOADING_SCREEN_PATH);
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

            stage.setScene(scene);
            stage.show();

            Task<Void> initializationTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    DatabaseInitializer.initialize();
                    return null;
                }

                @Override
                protected void succeeded() {
                    showMainScreen(stage);
                }

                @Override
                protected void failed() {
                    System.err.println("Erro durante a inicialização do banco de dados.");
                    getException().printStackTrace();
                }
            };

            new Thread(initializationTask).start();
        } catch (Exception e) {
            System.err.println("Erro ao exibir a tela de carregamento: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Displays the main application screen after successful initialization.
     *
     * @param stage the primary stage
     */
    private void showMainScreen(Stage stage) {
        try {
            MainScreen startupView = new MainScreen();
            startupView.showMainScreen(stage);
        } catch (Exception e) {
            System.err.println("Erro ao exibir a tela principal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void runFlywayMigrations() {
        Flyway flyway = Flyway.configure()
                .dataSource("jdbc:mysql://localhost:3306/dev", "root", "root")
                .load();

        flyway.migrate();
        System.out.println("Migrações aplicadas com sucesso!");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
