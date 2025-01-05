package com.novasoftware.user.domain.service;

import com.novasoftware.core.email.SavedEmail;
import com.novasoftware.shared.util.notification.NotificationUtil;
import com.novasoftware.token.application.repository.TokenRepository;
import com.novasoftware.token.infra.repository.TokenRepositoryImpl;
import com.novasoftware.tools.application.repository.UserRepository;
import com.novasoftware.user.application.dto.ForgotUserPassword;
import com.novasoftware.user.domain.model.Users;
import com.novasoftware.user.infra.repository.UserRepositoryImpl;
import org.controlsfx.control.Notifications;
import javafx.scene.control.CheckBox;


import java.util.Optional;

public class UserService {
    private UserRepository userRepository = new UserRepositoryImpl();
    private TokenRepository tokenRepository = new TokenRepositoryImpl();

    public Optional<Users> autheticate(String email, String password, CheckBox rememberEmailCheckBox, Runnable onLoginSuccess) {
        Optional<Users> optionalUser = Optional.of(userRepository.findUserByEmail(email).get());
        optionalUser.filter(user -> isValidCredentials(email, password))
                .ifPresent(user -> {
                    onLoginSuccess.run();
                    Notifications notification = NotificationUtil.pushNotify("Login Bem-Sucedido", "Bem-vindo, " + optionalUser.get().getUsername() + "!");
                    notification.showConfirm();

                    if (rememberEmailCheckBox.isSelected()) {
                        SavedEmail.saveEmail(email);
                    }
                });

        return optionalUser;
    }

    public boolean resetUserPassword(ForgotUserPassword forgotUserPassword) {
        if ((!forgotUserPassword.token().isEmpty() && !forgotUserPassword.newPassword().isEmpty())) {
            Optional<Users> isUser = Optional.ofNullable(tokenRepository.findUserByToken(forgotUserPassword.token())).get();

            Users user = new Users();
            user.setPassword(forgotUserPassword.newPassword());

            userRepository.update(user);
            return true;
        }

        return true;
    }

    private boolean isValidCredentials(String username, String password) {
        return username != null && !username.isEmpty() && password != null && !password.isEmpty();
    }
}
