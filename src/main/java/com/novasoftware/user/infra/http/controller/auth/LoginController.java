package com.novasoftware.user.infra.http.controller.auth;

import java.io.IOException;
import java.util.Optional;

import com.novasoftware.base.layout.BaseController;
import com.novasoftware.core.config.AppInitializer;
import com.novasoftware.core.email.SavedEmail;
import com.novasoftware.shared.util.notification.NotificationUtil;
import com.novasoftware.user.domain.model.Users;
import com.novasoftware.user.domain.service.UserService;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;

public class LoginController extends BaseController {

    @FXML
    private MFXTextField emailField;

    @FXML
    private MFXPasswordField passwordField;

    @FXML
    private MFXButton loginButton;

    @FXML
    private CheckBox rememberEmailCheckBox;

    private Runnable onLoginSuccess;

    @FXML
    private Label titleLabel;

    private Stage stage;

    public LoginController() {}

    @FXML
    public void initialize() {
        String savedEmail = SavedEmail.getSavedEmail();
        if (savedEmail != null) {
            emailField.setText(savedEmail);
            rememberEmailCheckBox.setSelected(true);
        }

    }

    public void setOnLoginSuccess(Runnable onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
    }

    @FXML
    private void handleLogin() throws IOException {
        String email = emailField.getText();
        String password = passwordField.getText();
        UserService userService = new UserService();

        Optional<Users> optionalUser = userService.autheticate(email, password, rememberEmailCheckBox, onLoginSuccess);
        if (optionalUser.isEmpty()) {
            Notifications notification = NotificationUtil.pushNotify("Falha no Login", "Credenciais inválidas ou usuário não encontrado.");
            notification.showError();
        }
    }

    @FXML
    public void signIn(ActionEvent event) {
        String username = emailField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() && password.isEmpty()) {
            Notifications notification = NotificationUtil.pushNotify("Campos Vazios", "Por favor, preencha os campos.");
            notification.showError();
            emailField.requestFocus();
        } else if (!username.isEmpty() && password.isEmpty()) {
            Notifications notification = NotificationUtil.pushNotify("Senha Vazia", "Por favor, preencha a senha.");
            notification.showError();
            passwordField.requestFocus();
        } else if (username.isEmpty() && !password.isEmpty()) {
            Notifications notification = NotificationUtil.pushNotify("Usuário Vazio", "Por favor, preencha o usuário.");
            notification.showError();
            emailField.requestFocus();
        } else {
            Image img = new Image(getClass().getResourceAsStream("/images/ok.png"));
            Notifications notification = NotificationUtil.pushNotify("Tudo Ok", "Login realizado com sucesso!");
            notification.graphic(new ImageView(img));
            notification.show();
            clearFields();
        }
    }

    private void clearFields() {
        emailField.setText("");
        passwordField.setText("");
    }

    @FXML
    private void handleForgotPassword() {
        AppInitializer.showForgotPasswordScreen();
    }

    @FXML
    public void handleSignUp() {
        AppInitializer.showRegisterScreen();
    }

    @FXML
    private void handleKeyEvents(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            handleLogin();
        } else if (event.getCode() == KeyCode.DOWN) {
            passwordField.requestFocus();
        }
    }
}
