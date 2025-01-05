package com.novasoftware.user.domain.service;

import com.novasoftware.core.email.SavedEmail;
import com.novasoftware.shared.util.notification.NotificationUtil;
import com.novasoftware.tools.application.repository.UserRepository;
import com.novasoftware.user.domain.model.Users;
import com.novasoftware.user.infra.repository.UserRepositoryImpl;
import org.controlsfx.control.Notifications;
import javafx.scene.control.CheckBox;


import java.util.Optional;

public class UserService {
    private UserRepository userRepository = new UserRepositoryImpl();

    public Optional<Users> autheticate(String email, String password, CheckBox rememberEmailCheckBox, Runnable onLoginSuccess) {
        Optional<Users> optionalUser = userRepository.findUserByEmail(email);
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

    private boolean isValidCredentials(String username, String password) {
        return username != null && !username.isEmpty() && password != null && !password.isEmpty();
    }
}
