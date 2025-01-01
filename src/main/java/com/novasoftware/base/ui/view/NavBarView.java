package com.novasoftware.tools.ui.view;

import com.novasoftware.routes.Routes;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.core.appender.routing.Route;

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
        loadDynamicContent(Routes.CONFIGURATION_SCREEN);
    }

    @FXML
    public void loadGenerateZPL() {
        loadDynamicContent(Routes.TOOL_ZPL_TAG_SCREEN);
    }

    @FXML
    public void loadImport() {
        loadDynamicContent(Routes.TOOL_IMPORT_SPREADSHEET);
    }
}
