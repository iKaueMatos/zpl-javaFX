package com.zpl.zpl.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

        Button visualizeButton = new Button("Visualize ZPL");
        visualizeButton.setOnAction(event -> {
            try {
                FXMLLoader visualizeLoader = new FXMLLoader(StageInitializer.class.getResource("/view/VisualizeZplView.fxml"));
                VBox visualizeRoot = visualizeLoader.load();
                Scene visualizeScene = new Scene(visualizeRoot, 1024, 768);
                Stage visualizeStage = new Stage();
                visualizeStage.setScene(visualizeScene);
                visualizeStage.setTitle("Visualize ZPL");
                visualizeStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        root.getChildren().add(visualizeButton);

        primaryStage.setScene(scene);
        primaryStage.setTitle("ZPL Label Generator");
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
}
