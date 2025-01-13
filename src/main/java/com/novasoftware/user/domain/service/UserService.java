package com.novasoftware.user.domain.service;

import com.novasoftware.core.email.SavedEmail;
import com.novasoftware.shared.util.notification.NotificationUtil;
import com.novasoftware.token.application.repository.TokenRepository;
import com.novasoftware.token.infrastructure.repository.TokenRepositoryImpl;
import com.novasoftware.user.application.repository.UserRepository;
import com.novasoftware.user.application.dto.ForgotUserPassword;
import com.novasoftware.user.domain.model.User;
import com.novasoftware.user.infrastructure.email.service.EmailService;
import com.novasoftware.user.infrastructure.email.strategy.EmailTemplateStrategy;
import com.novasoftware.user.infrastructure.email.strategy.WelcomeEmailStrategy;
import com.novasoftware.user.infrastructure.repository.UserRepositoryImpl;
import org.controlsfx.control.Notifications;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javafx.scene.control.CheckBox;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class UserService {
    private final UserRepository userRepository = new UserRepositoryImpl();
    private final TokenRepository tokenRepository = new TokenRepositoryImpl();
    private final EmailService emailService = new EmailService();
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Optional<User> login(String email, String password, CheckBox rememberEmailCheckBox, Runnable onLoginSuccess) {
        Optional<User> optionalUser = userRepository.findUserByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (!isEncrypted(user.getPassword())) {
                String encryptedPassword = passwordEncoder.encode(user.getPassword());
                user.setPassword(encryptedPassword);
                userRepository.update(user);
            }

            if (passwordMatches(password, user.getPassword())) {
                onLoginSuccess.run();
                sendNotification("Login Bem-Sucedido", "Bem-vindo, " + user.getUsername() + "!", true);

                if (rememberEmailCheckBox.isSelected()) {
                    SavedEmail.saveEmail(email);
                }

                return Optional.of(user);
            }
        }

        sendNotification("Erro", "Credenciais inválidas.", false);
        return Optional.empty();
    }

    public Optional<User> register(String name, String email, String password, String confirmPassword) {
        if (!areFieldsValid(name, email, password, confirmPassword)) {
            return Optional.empty();
        }

        if (userRepository.findUserByEmail(email).isPresent()) {
            sendNotification("Erro", "Este usuário já está cadastrado.", false);
            return Optional.empty();
        }

        User newUser = createNewUser(name, email, password);
        if (userRepository.insertUser(newUser)) {
            sendNotification("Sucesso", "Registro realizado com sucesso!", true);
            sendWelcomeEmailAsync(newUser);
            return Optional.of(newUser);
        } else {
            sendNotification("Erro", "Erro ao registrar o usuário.", false);
            return Optional.empty();
        }
    }

    public boolean resetUserPassword(ForgotUserPassword forgotUserPassword) {
        if (!isPasswordResetRequestValid(forgotUserPassword)) {
            return false;
        }

        Optional<User> userOptional = tokenRepository.findUserByToken(forgotUserPassword.token());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(encodePassword(forgotUserPassword.newPassword()));
            userRepository.update(user);
            return true;
        }

        sendNotification("Erro", "Token inválido ou expirado.", false);
        return false;
    }

    private boolean areFieldsValid(String name, String email, String password, String confirmPassword) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            sendNotification("Erro", "Todos os campos são obrigatórios.", false);
            return false;
        }

        if (!password.equals(confirmPassword)) {
            sendNotification("Erro", "As senhas não coincidem.", false);
            return false;
        }

        return true;
    }

    private boolean passwordMatches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    private boolean isEncrypted(String password) {
        return password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$");
    }

    private User createNewUser(String name, String email, String password) {
        User user = new User();
        user.setUsername(name);
        user.setEmail(email);
        user.setPassword(encodePassword(password));
        user.setCreated_at(new Date());
        user.setUpdated_at(new Date());
        user.setIsActive(1);
        return user;
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private void sendWelcomeEmailAsync(User newUser) {
        CompletableFuture.runAsync(() -> {
            Map<String, Object> data = Map.of("name", newUser.getUsername());
            EmailTemplateStrategy strategy = new WelcomeEmailStrategy();
            emailService.sendEmail(newUser.getEmail(), strategy, data);
        });
    }

    private boolean isPasswordResetRequestValid(ForgotUserPassword forgotUserPassword) {
        return forgotUserPassword != null &&
                forgotUserPassword.token() != null &&
                !forgotUserPassword.token().isEmpty() &&
                forgotUserPassword.newPassword() != null &&
                !forgotUserPassword.newPassword().isEmpty();
    }

    private void sendNotification(String title, String message, boolean isSuccess) {
        Notifications notification = NotificationUtil.pushNotify(title, message);
        if (isSuccess) {
            notification.showInformation();
        } else {
            notification.showError();
        }
    }
}
