package com.novasoftware.tools.ui.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class MainLayoutView {

    @FXML
    public ScrollPane scrollPane;

    @FXML
    private VBox contentPane;

    @FXML
    private javafx.scene.control.Label minimizeLabel;

    @FXML
    private javafx.scene.control.Label maximizeLabel;

    @FXML
    private javafx.scene.control.Label closeLabel;

    @FXML
    private HBox titleBar;

    @FXML
    private VBox menuPane;

    private boolean isMaximized = false;
    private boolean isMenuVisible = true;

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    public void initialize() {
        loadDynamicContent("/view/tool_zpl_tag.fxml");

        minimizeLabel.setOnMouseClicked(event -> handleMinimize());
        maximizeLabel.setOnMouseClicked(event -> handleMaximize());
        closeLabel.setOnMouseClicked(event -> handleClose());

        titleBar.setOnMousePressed(this::handleMousePressed);
        titleBar.setOnMouseDragged(this::handleMouseDragged);
    }

    @FXML
    private void handleMenuButton(MouseEvent event) {
        toggleMenuVisibility();
    }

    private void toggleMenuVisibility() {
        if (isMenuVisible) {
            menuPane.setManaged(false);
            menuPane.setVisible(false);
        } else {
            menuPane.setManaged(true);
            menuPane.setVisible(true);
        }
        isMenuVisible = !isMenuVisible;
    }

    private void loadDynamicContent(String fxmlPath) {
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
    private void handleMinimize() {
        Window window = contentPane.getScene().getWindow();
        if (window instanceof Stage) {
            ((Stage) window).setIconified(true);
        }
    }

    @FXML
    private void handleMaximize() {
        Window window = contentPane.getScene().getWindow();
        if (window instanceof Stage) {
            Stage stage = (Stage) window;
            if (isMaximized) {
                stage.setMaximized(false);
                isMaximized = false;
            } else {
                stage.setMaximized(true);
                isMaximized = true;
            }
        }
    }

    @FXML
    private void handleClose() {
        Window window = contentPane.getScene().getWindow();
        if (window instanceof Stage) {
            ((Stage) window).close();
        }
    }

    private void handleMousePressed(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    private void handleMouseDragged(MouseEvent event) {
        Window window = contentPane.getScene().getWindow();
        if (window instanceof Stage) {
            Stage stage = (Stage) window;
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        }
    }

    @FXML
    private void loadConfig() {
        loadDynamicContent("/view/configuration_screen.fxml");
    }

    @FXML
    private void loadGenerateZPL() {
        loadDynamicContent("/view/tool_zpl_tag.fxml");
    }

    @FXML
    private void loadImport() {
        loadDynamicContent("/view/tool_import_spreadsheet.fxml");
    }
}
