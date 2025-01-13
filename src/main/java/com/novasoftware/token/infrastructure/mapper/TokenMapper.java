package com.novasoftware.token.infrastructure.mapper;

import com.novasoftware.token.domain.model.Token;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TokenMapper {
    public static Token mapResultSetToToken(ResultSet rs) throws SQLException {
        Token token = new Token();
        token.setId(rs.getInt("id"));
        token.setToken(rs.getString("token"));
        token.setUserId(rs.getInt("user_id"));
        token.setType(rs.getString("type"));
        token.setCreatedAt(rs.getTimestamp("created_at"));
        token.setExpiresAt(rs.getTimestamp("expires_at"));
        token.setUsedAt(rs.getTimestamp("used_at"));
        return token;
    }
}
