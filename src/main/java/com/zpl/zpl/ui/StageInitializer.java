package com.zpl.zpl.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;

public class StageInitializer {
    public static void initialize(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(StageInitializer.class.getResource("/view/StartupView.fxml"));
        VBox root = loader.load();
        Scene scene = new Scene(root, 1024, 768);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        scene.getStylesheets().add(StageInitializer.class.getResource("/css/dark-theme.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setTitle("ZPL Label Generator");
        primaryStage.setFullScreen(true);
        primaryStage.getIcons().add(new Image(StageInitializer.class.getResourceAsStream("/assets/icon/nova-software-logo.jpg")));
        primaryStage.show();
    }
}
