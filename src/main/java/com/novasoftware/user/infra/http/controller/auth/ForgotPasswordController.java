package com.novasoftware.user.infra.http.controller.auth;

import com.novasoftware.base.layout.BaseController;
import com.novasoftware.core.config.AppInitializer;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ForgotPasswordController extends BaseController {
    @FXML
    private VBox emailStep;

    @FXML
    private VBox resetPasswordStep;

    @FXML
    private MFXTextField emailField;

    @FXML
    private MFXTextField tokenField;

    @FXML
    private MFXPasswordField newPasswordField;

    @FXML
    private MFXPasswordField confirmPasswordField;

    @FXML
    private Label titleLabel;

    private static Stage stage;

    public ForgotPasswordController() {}

    @FXML
    public void sendResetToken() {
        String email = emailField.getText();

        if (email == null || email.isEmpty()) {
            titleLabel.setText("Por favor, insira um e-mail válido.");
            return;
        }

        boolean isEmailSent = sendTokenToEmail(email);

        if (isEmailSent) {
            titleLabel.setText("Token enviado para o e-mail: " + email);
            emailStep.setVisible(false);
            emailStep.setManaged(false);
            resetPasswordStep.setVisible(true);
            resetPasswordStep.setManaged(true);
        } else {
            titleLabel.setText("Erro ao enviar o token. Verifique o e-mail.");
        }
    }

    @FXML
    public void resetPassword() {
        String token = tokenField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (token == null || token.isEmpty()) {
            titleLabel.setText("Por favor, insira o token.");
            return;
        }

        if (newPassword == null || newPassword.isEmpty() || confirmPassword == null || confirmPassword.isEmpty()) {
            titleLabel.setText("Por favor, preencha os campos de senha.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            titleLabel.setText("As senhas não coincidem.");
            return;
        }

        boolean isPasswordReset = resetUserPassword(token, newPassword);

        if (isPasswordReset) {
            titleLabel.setText("Senha redefinida com sucesso. Volte ao login.");
            resetPasswordStep.setVisible(false);
            resetPasswordStep.setManaged(false);
            emailStep.setVisible(true);
            emailStep.setManaged(true);
        } else {
            titleLabel.setText("Erro ao redefinir a senha. Verifique o token.");
        }
    }

    private boolean sendTokenToEmail(String email) {
        System.out.println("Simulating sending token to: " + email);
        return true;
    }

    private boolean resetUserPassword(String token, String newPassword) {
        System.out.println("Simulating password reset with token: " + token + " and newPassword: " + newPassword);
        return true;
    }

    @FXML
    private void handleBackToLogin() {
        AppInitializer.showLoginScreen();
    }
}
