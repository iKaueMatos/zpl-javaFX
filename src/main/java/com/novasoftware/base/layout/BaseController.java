package com.novasoftware.base.layout;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class BaseController {
    @FXML
    private static Label titleLabel;
    private static double xOffset = 0;
    private static double yOffset = 0;
    protected static Stage stage;

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

    public boolean isFieldEmptyMessage(TextField textField, String errorMessage) {
        if (textField.getText() == null || textField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campo Obrigatório", errorMessage);
            return true;
        }
        return false;
    }

    public boolean isComboBoxEmptyMessage(MFXComboBox<String> comboBox, String errorMessage) {
        if (comboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Campo Obrigatório", errorMessage);
            return true;
        }
        return false;
    }

    public void clearTextFields(TextField... fields) {
        for (TextField field : fields) {
            field.clear();
        }
    }

    public void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
