package com.novasoftware.token.domain.model;

import java.sql.Timestamp;

public class Tokens {
    private int id;
    private String token;
    private int userId;
    private String type;
    private Timestamp createdAt;
    private Timestamp expiresAt;
    private Timestamp usedAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Timestamp expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Timestamp getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(Timestamp usedAt) {
        this.usedAt = usedAt;
    }

    @Override
    public String toString() {
        return "Tokens{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", userId=" + userId +
                ", type='" + type + '\'' +
                ", createdAt=" + createdAt +
                ", expiresAt=" + expiresAt +
                ", usedAt=" + usedAt +
                '}';
    }
}
