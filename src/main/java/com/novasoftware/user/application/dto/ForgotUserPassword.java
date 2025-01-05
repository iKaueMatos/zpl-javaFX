package com.novasoftware.user.application.dto;

public record ForgotUserPassword(String token, String newPassword) { }
