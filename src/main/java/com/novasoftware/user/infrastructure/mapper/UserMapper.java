package com.novasoftware.user.infrastructure.mapper;

import com.novasoftware.user.domain.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper {
    public static User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setCreated_at(rs.getDate("created_at"));
        user.setUpdated_at(rs.getDate("updated_at"));
        user.setIsActive(rs.getInt("is_active"));
        return user;
    }
}
