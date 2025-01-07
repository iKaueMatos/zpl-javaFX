package com.novasoftware.token.domain.service;

import com.novasoftware.token.application.repository.TokenRepository;
import com.novasoftware.token.domain.model.Tokens;
import com.novasoftware.token.infra.repository.TokenRepositoryImpl;
import com.novasoftware.user.application.repository.UserRepository;
import com.novasoftware.user.domain.model.Users;
import com.novasoftware.user.infra.email.service.EmailService;
import com.novasoftware.user.infra.email.strategy.EmailTemplateStrategy;
import com.novasoftware.user.infra.email.strategy.PasswordResetEmailStrategy;
import com.novasoftware.user.infra.repository.UserRepositoryImpl;

import java.sql.Timestamp;
import java.util.Map;
import java.util.Random;

public class TokenService {
    private final EmailService emailService;
    private final UserRepository userRepository = new UserRepositoryImpl();
    private final TokenRepository tokenRepository = new TokenRepositoryImpl();

    public TokenService(EmailService emailService) {
        this.emailService = emailService;
    }

    public boolean sendTokenToEmail(String email) {
        try {
            String token = generateToken();
            Users user = userRepository.findUserByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o e-mail: " + email));

            Tokens tokenEntity = new Tokens();
            tokenEntity.setToken(token);
            tokenEntity.setUserId(user.getId());
            tokenEntity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            tokenEntity.setExpiresAt(new Timestamp(System.currentTimeMillis() + 3600000));
            tokenEntity.setUsedAt(null);

            tokenRepository.saveToken(tokenEntity);
            Map<String, Object> data = Map.of(
                    "name", user.getUsername(),
                    "token", token
            );

            EmailTemplateStrategy strategy = new PasswordResetEmailStrategy();
            emailService.sendEmail(email, strategy, data);
            System.out.println("E-mail enviado com sucesso para " + email);
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao enviar o e-mail: " + e.getMessage());
            return false;
        }
    }

    public static String generateToken() {
        Random random = new Random();
        int token = 100000 + random.nextInt(900000);
        return String.valueOf(token);
    }
}
