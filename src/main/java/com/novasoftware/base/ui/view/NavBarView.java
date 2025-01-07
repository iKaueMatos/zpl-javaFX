package com.novasoftware.base.ui.view;

import com.novasoftware.core.path.ResourcePaths;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class NavBarView {
    @FXML
    public VBox contentPane;

    public void loadDynamicContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();

            contentPane.getChildren().clear();
            contentPane.getChildren().add(content);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erro ao carregar o conteúdo dinâmico: " + fxmlPath);
        }
    }

    @FXML
    public void loadConfig() {
        loadDynamicContent(ResourcePaths.CONFIGURATION_SCREEN);
    }

    @FXML
    public void loadGenerateZPL() {
        loadDynamicContent(ResourcePaths.TOOL_ZPL_TAG_SCREEN);
    }

    @FXML
    public void loadImport() {
        loadDynamicContent(ResourcePaths.TOOL_IMPORT_SPREADSHEET);
    }

    @FXML
    private void exitApplication() {
        Platform.exit();
        System.exit(0);
    }
}
