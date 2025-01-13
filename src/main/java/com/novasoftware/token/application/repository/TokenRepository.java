package com.novasoftware.token.application.repository;

import com.novasoftware.token.domain.model.Token;
import com.novasoftware.user.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface TokenRepository {
    Optional<List<Token>> findTokensByUserId(int userId);
    Optional<User> findUserByToken(String tokenValue);
    void saveToken(Token token);
}
