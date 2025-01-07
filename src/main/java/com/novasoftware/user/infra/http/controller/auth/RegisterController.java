package com.novasoftware.user.infra.http.controller.auth;

import com.novasoftware.core.config.AppInitializer;
import com.novasoftware.shared.util.notification.NotificationUtil;
import com.novasoftware.user.domain.model.Users;
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

    private UserRepository userRepository = new UserRepositoryImpl();

    private EmailService emailService = new EmailService();

    public RegisterController() {}

    @FXML
    private void handleRegister(ActionEvent event) {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Notifications notification = NotificationUtil.pushNotify("Erro", "Todos os campos são obrigatórios.");
            notification.showError();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Notifications notification = NotificationUtil.pushNotify("Erro", "As senhas não coincidem.");
            notification.showError();
            return;
        }

        if (userRepository.findUserByEmail(email).isPresent()) {
            Notifications notification = NotificationUtil.pushNotify("Erro", "Este usuário já está cadastrado.");
            notification.showError();
            return;
        }

        Users newUser = new Users();
        newUser.setUsername(name);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setCreated_at(new Date());
        newUser.setUpdated_at(new Date());
        newUser.setIsActive(1);
        newUser.setToken("token_gerado");

        boolean isRegistered = userRepository.insertUser(newUser);


        if (isRegistered) {
            Notifications notification = NotificationUtil.pushNotify("Sucesso", "Registro realizado com sucesso!");
            notification.showInformation();
            clearFields();

            CompletableFuture.runAsync(() -> {
                Map<String, Object> data = Map.of(
                        "name", newUser.getUsername()
                );

                EmailTemplateStrategy strategy = new WelcomeEmailStrategy();
                emailService.sendEmail(email, strategy, data);
            });
        } else {
            Notifications notification = NotificationUtil.pushNotify("Erro", "Erro ao registrar o usuário.");
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
