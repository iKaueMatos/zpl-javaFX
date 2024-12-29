package com.novasoftware.tools.infrastructure.http.controller.auth;

import java.io.IOException;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class LoginController {

    public MFXButton minimizeButton;

    public MFXButton closeButton;

    @FXML
    private MFXTextField usernameField;

    @FXML
    private MFXPasswordField passwordField;

    @FXML
    private MFXButton loginButton;

    @FXML
    private Label titleLabel;

    private Stage stage;
    private double xOffset = 0;
    private double yOffset = 0;

    private Runnable onLoginSuccess;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setOnLoginSuccess(Runnable onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
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
    private void handleLogin() throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (isValidCredentials(username, password)) {
            System.out.println("Login bem-sucedido!");
            if (onLoginSuccess != null) {
                onLoginSuccess.run();
            }
        } else {
            System.out.println("Credenciais inválidas!");
        }
    }

    private boolean isValidCredentials(String username, String password) {
        return username != null && !username.isEmpty() &&
                password != null && !password.isEmpty();
    }

    @FXML
    private void handleForgotPassword() {
        System.out.println("Recuperação de senha acionada");
    }


    @FXML
    private void handleSignUp() {
        System.out.println("Botão de cadastro clicado");
    }

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
}
