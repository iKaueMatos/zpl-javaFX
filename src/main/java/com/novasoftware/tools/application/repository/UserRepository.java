package com.novasoftware.tools.application.repository;

import com.novasoftware.user.domain.model.Users;

import java.util.Optional;

public interface UserRepository {
    public Optional<Users> findUserByEmail(String email);
    public boolean insertUser(Users user);
}
