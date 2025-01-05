package com.novasoftware.base.layout;

import com.novasoftware.shared.util.alert.CustomAlert;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;

public class BaseController {
    @FXML
    private Label titleLabel;
    private double xOffset = 0;
    private double yOffset = 0;
    protected Stage stage;

    @FXML
    public void onMousePressed(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    @FXML
    public void onMouseDragged(MouseEvent event) {
        Stage currentStage = (Stage) titleLabel.getScene().getWindow();
        currentStage.setX(event.getScreenX() - xOffset);
        currentStage.setY(event.getScreenY() - yOffset);
    }

    @FXML
    public void handleMinimize() {
        if (stage != null) {
            stage.setIconified(true);
        } else {
            Stage currentStage = (Stage) titleLabel.getScene().getWindow();
            currentStage.setIconified(true);
        }
    }

    @FXML
    public void handleClose() {
        if (stage != null) {
            stage.close();
        } else {
            Stage currentStage = (Stage) titleLabel.getScene().getWindow();
            currentStage.close();
        }
    }
}
