package com.novasoftware.token.application.service;

public interface TokenService {
    boolean sendTokenToEmail(String email);

    static String generateToken() {
        return null;
    }
}

