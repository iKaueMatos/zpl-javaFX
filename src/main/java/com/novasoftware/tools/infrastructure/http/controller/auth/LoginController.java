package com.novasoftware.tools.infrastructure.http.controller.auth;

import java.io.IOException;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private MFXTextField usernameField;

    @FXML
    private MFXPasswordField passwordField;

    @FXML
    private MFXButton loginButton;

    private Stage stage;
    
    private Runnable onLoginSuccess;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setOnLoginSuccess(Runnable onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
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
}
