package com.novasoftware.user.infra.http.controller.auth;

import com.novasoftware.core.path.ResourcePaths;
import com.novasoftware.shared.util.notification.NotificationUtil;
import com.novasoftware.user.domain.model.Users;
import com.novasoftware.user.infra.repository.UserRepositoryImpl;
import com.novasoftware.tools.application.repository.UserRepository;
import com.novasoftware.base.layout.BaseController;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;

import java.net.URL;
import java.util.Date;

public class RegisterController extends BaseController {

    @FXML
    private MFXTextField nameField;

    @FXML
    private MFXTextField emailField;

    @FXML
    private MFXPasswordField passwordField;

    @FXML
    private MFXPasswordField confirmPasswordField;

    @FXML
    private Label titleLabel;

    private Stage stage;
    private UserRepository userRepository = new UserRepositoryImpl();

    public RegisterController(Stage stage) {
        this.stage = stage;
    }

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
        try {
            URL resource = getClass().getResource(ResourcePaths.LOADING_SCREEN_PATH);
            if (resource == null) {
                throw new IllegalArgumentException("Arquivo FXML não encontrado: /view/fxml/login_screen.fxml");
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
            System.err.println("Erro ao carregar a tela de login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
