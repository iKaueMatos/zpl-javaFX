package com.novasoftware.tools.infrastructure.http.controller.auth;

import java.io.IOException;
import java.net.URL;

import com.novasoftware.base.layout.BaseController;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class LoginController extends BaseController {

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
    public void handleSignUp() {
        try {
            URL resource = getClass().getResource("/view/fxml/register_screen.fxml");

            if (resource == null) {
                throw new IllegalArgumentException("Arquivo FXML não encontrado: /view/fxml/register_screen.fxml");
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setWidth(1200);
            stage.setHeight(600);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            System.err.println("Erro ao carregar a tela de registro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
