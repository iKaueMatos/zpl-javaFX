package com.novasoftware.tools.infrastructure.http.controller.auth;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class BaseScreenAuthController {
    @FXML
    private Label titleLabel;
    
    private double xOffset = 0;
    private double yOffset = 0;
    protected Stage stage;

  
    @FXML
    private void onMousePressed(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    @FXML
    private void onMouseDragged(MouseEvent event) {
        Stage currentStage = (Stage) titleLabel.getScene().getWindow();
        currentStage.setX(event.getScreenX() - xOffset);
        currentStage.setY(event.getScreenY() - yOffset);
    }

        @FXML
        private void handleMinimize() {
            if (stage != null) {
                stage.setIconified(true);
            } else {
                Stage currentStage = (Stage) titleLabel.getScene().getWindow();
                currentStage.setIconified(true);
            }
        }

        @FXML
        private void handleClose() {
            if (stage != null) {
                stage.close();
            } else {
                Stage currentStage = (Stage) titleLabel.getScene().getWindow();
                currentStage.close();
            }
        }
}
