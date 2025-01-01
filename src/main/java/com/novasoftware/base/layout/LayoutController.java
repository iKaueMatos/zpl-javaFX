package com.novasoftware.base.layout;

import java.io.InputStream;

import com.novasoftware.routes.Routes;
import com.novasoftware.tools.ui.view.NavBarView;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

public class LayoutController extends NavBarView {

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

    @FXML
    private ImageView logoImageView;

    private boolean isMaximized = false;
    private boolean isMenuVisible = false;

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    public void initialize() {
        loadDynamicContent(Routes.TOOL_ZPL_TAG_SCREEN);

        minimizeLabel.setOnMouseClicked(event -> handleMinimize());
        maximizeLabel.setOnMouseClicked(event -> handleMaximize());
        closeLabel.setOnMouseClicked(event -> handleClose());

        titleBar.setOnMousePressed(this::handleMousePressed);
        titleBar.setOnMouseDragged(this::handleMouseDragged);

        InputStream logoImageStream = getClass().getResourceAsStream(Routes.LOGO_PATH_PNG);
        if (logoImageStream != null) {
            Image logoImage = new Image(logoImageStream);
            logoImageView.setImage(logoImage);
        }
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
}
