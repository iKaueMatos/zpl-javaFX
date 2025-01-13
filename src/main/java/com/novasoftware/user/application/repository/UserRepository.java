package com.novasoftware.user.application.repository;

import com.novasoftware.user.domain.model.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findUserByEmail(String email);
    boolean insertUser(User user);
    boolean update(User users);
}
