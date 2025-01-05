package com.novasoftware.token.application.repository;

import com.novasoftware.token.domain.model.Tokens;
import com.novasoftware.user.domain.model.Users;

import java.util.List;
import java.util.Optional;

public interface TokenRepository {
    Optional<List<Tokens>> findTokensByUserId(int userId);
    Optional<Users> findUserByToken(String tokenValue);
    void saveToken(Tokens token);
}
