package com.novasoftware.tools.application.repository;

import com.novasoftware.user.domain.model.Users;

import java.util.Optional;

public interface UserRepository {
    Optional<Users> findUserByEmail(String email);
    boolean insertUser(Users user);
}
