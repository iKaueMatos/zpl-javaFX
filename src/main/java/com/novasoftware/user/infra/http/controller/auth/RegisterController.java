package com.novasoftware.user.infra.http.controller.auth;

import com.novasoftware.core.config.AppInitializer;
import com.novasoftware.shared.util.notification.NotificationUtil;
import com.novasoftware.user.domain.model.Users;
import com.novasoftware.user.domain.service.UserService;
import com.novasoftware.user.infra.email.service.EmailService;
import com.novasoftware.user.infra.email.strategy.EmailTemplateStrategy;
import com.novasoftware.user.infra.email.strategy.PasswordResetEmailStrategy;
import com.novasoftware.user.infra.email.strategy.WelcomeEmailStrategy;
import com.novasoftware.user.infra.repository.UserRepositoryImpl;
import com.novasoftware.user.application.repository.UserRepository;
import com.novasoftware.base.layout.BaseController;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class RegisterController extends BaseController {

    @FXML
    private MFXTextField nameField;

    @FXML
    private MFXTextField emailField;

    @FXML
    private MFXPasswordField passwordField;

    @FXML
    private MFXPasswordField confirmPasswordField;

    private static Stage stage;

    @FXML
    private Label titleLabel;


    public RegisterController() {}

    @FXML
    private void handleRegister(ActionEvent event) {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        UserService userService = new UserService();
        Optional<Users> registeredUser = userService.register(name, email, password, confirmPassword);
        if (registeredUser.isPresent()) {
            Notifications notification = NotificationUtil.pushNotify("Sucesso", "Registro realizado com sucesso!");
            notification.showInformation();
            clearFields();
        } else {
            Notifications notification = NotificationUtil.pushNotify("Erro", "Não foi possível registrar o usuário. Verifique os erros e tente novamente.");
            notification.showError();
        }
    }

    private void clearFields() {
        nameField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }

    @FXML
    private void handleBackToLogin() {
        AppInitializer.showLoginScreen();
    }
}
