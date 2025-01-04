package com.novasoftware.user.infra.http.controller.auth;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.prefs.Preferences;

import com.novasoftware.base.layout.BaseController;
import com.novasoftware.core.path.ResourcePaths;
import com.novasoftware.shared.util.notification.NotificationUtil;
import com.novasoftware.tools.application.repository.UserRepository;
import com.novasoftware.user.domain.model.Users;
import com.novasoftware.user.infra.repository.UserRepositoryImpl;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
    private MFXTextField usernameField;

    @FXML
    private MFXPasswordField passwordField;

    @FXML
    private MFXButton loginButton;

    @FXML
    private Label titleLabel;

    @FXML
    private CheckBox rememberEmailCheckBox;

    private Runnable onLoginSuccess;

    private UserRepository userRepository = new UserRepositoryImpl();

    private static final String SAVED_EMAIL_KEY = "savedEmail";

    @FXML
    public void initialize() {
        String savedEmail = getSavedEmail();
        if (savedEmail != null) {
            usernameField.setText(savedEmail);
            rememberEmailCheckBox.setSelected(true);
        }
    }

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

        Optional<Users> optionalUser = userRepository.findUserByEmail(username);
        optionalUser.filter(user -> isValidCredentials(username, password))
                .ifPresent(user -> {
                    onLoginSuccess.run();
                    Notifications notification = NotificationUtil.pushNotify("Login Bem-Sucedido", "Bem-vindo, " + optionalUser.get().getUsername() + "!");
                    notification.showConfirm();

                    if (rememberEmailCheckBox.isSelected()) {
                        saveEmail(username);
                    }
                });

        if (optionalUser.isEmpty() || !isValidCredentials(username, password)) {
            Notifications notification = NotificationUtil.pushNotify("Falha no Login", "Credenciais inválidas ou usuário não encontrado.");
            notification.showError();
        }
    }

    @FXML
    public void handleKeyEvents(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            handleLogin();
        } else if (event.getCode() == KeyCode.DOWN) {
            passwordField.requestFocus();
        }
    }

    @FXML
    public void signIn(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() && password.isEmpty()) {
            Notifications notification = NotificationUtil.pushNotify("Campos Vazios", "Por favor, preencha os campos.");
            notification.showError();
            usernameField.requestFocus();
        } else if (!username.isEmpty() && password.isEmpty()) {
            Notifications notification = NotificationUtil.pushNotify("Senha Vazia", "Por favor, preencha a senha.");
            notification.showError();
            passwordField.requestFocus();
        } else if (username.isEmpty() && !password.isEmpty()) {
            Notifications notification = NotificationUtil.pushNotify("Usuário Vazio", "Por favor, preencha o usuário.");
            notification.showError();
            usernameField.requestFocus();
        } else {
            Image img = new Image(getClass().getResourceAsStream("/images/ok.png"));
            Notifications notification = NotificationUtil.pushNotify("Tudo Ok", "Login realizado com sucesso!");
            notification.graphic(new ImageView(img));
            notification.show();
            clearFields();
        }
    }

    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
    }

    private boolean isValidCredentials(String username, String password) {
        return username != null && !username.isEmpty() && password != null && !password.isEmpty();
    }

    @FXML
    private void handleForgotPassword() {
        System.out.println("Recuperação de senha acionada");
    }

    @FXML
    public void handleSignUp() {
        try {
            URL resource = getClass().getResource(ResourcePaths.REGISTER_SCREEN_PATH);

            if (resource == null) {
                throw new IllegalArgumentException("Arquivo FXML não encontrado: " + ResourcePaths.REGISTER_SCREEN_PATH);
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            Scene scene = new Scene(root);

            RegisterController registerController = loader.getController();
            registerController.setStage(stage);

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

    private void saveEmail(String email) {
        Preferences preferences = Preferences.userNodeForPackage(LoginController.class);
        preferences.put(SAVED_EMAIL_KEY, email);
    }

    private String getSavedEmail() {
        Preferences preferences = Preferences.userNodeForPackage(LoginController.class);
        return preferences.get(SAVED_EMAIL_KEY, null);
    }
}
