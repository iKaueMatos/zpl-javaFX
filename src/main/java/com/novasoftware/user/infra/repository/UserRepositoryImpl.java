package com.novasoftware.user.infra.repository;

import com.novasoftware.shared.Enum.user.Users;
import com.novasoftware.shared.Enum.Operator;
import com.novasoftware.shared.database.environment.DatabaseManager;
import com.novasoftware.shared.database.queryBuilder.QueryBuilder;
import com.novasoftware.tools.application.repository.UserRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {

    @Override
    public Optional<com.novasoftware.user.domain.model.Users> findUserByEmail(String email) {
        QueryBuilder<Users> queryBuilder = new QueryBuilder<>(Users.class);
        queryBuilder.select(Users.ALL_COLUMN)
                .where(Users.EMAIL, Operator.EQUALS, email);

        return Optional.ofNullable(executeQuery(queryBuilder));
    }

    @Override
    public boolean insertUser(com.novasoftware.user.domain.model.Users user) {
        QueryBuilder<Users> queryBuilder = new QueryBuilder<>(Users.class);
        queryBuilder.select(Users.USERNAME, Users.PASSWORD, Users.EMAIL,
                Users.CREATED_AT, Users.UPDATED_AT, Users.IS_ACTIVE,
                Users.TOKEN);

        List<Object> values = new ArrayList<>();
        values.add(user.getUsername());
        values.add(user.getPassword());
        values.add(user.getEmail());
        values.add(user.getCreated_at());
        values.add(user.getUpdated_at());
        values.add(user.getIsActive());
        values.add(user.getToken());

        String sql = buildInsertQuery(values);

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            for (Object value : values) {
                pstmt.setObject(paramIndex++, value);
            }

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String buildInsertQuery(List<Object> values) {
        StringBuilder query = new StringBuilder("INSERT INTO users (username, password, email, created_at, updated_at, is_active, token) ");
        query.append("VALUES (");

        query.append("?,".repeat(values.size() - 1));
        query.append("?");

        query.append(")");
        return query.toString();
    }

    private com.novasoftware.user.domain.model.Users executeQuery(QueryBuilder<Users> queryBuilder) {
        String sql = queryBuilder.build();

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = queryBuilder.buildPreparedStatement(conn)) {

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                com.novasoftware.user.domain.model.Users user = new com.novasoftware.user.domain.model.Users();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setCreated_at(rs.getDate("created_at"));
                user.setUpdated_at(rs.getDate("updated_at"));
                user.setIsActive(rs.getInt("is_active"));
                user.setToken(rs.getString("token"));
                return user;
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
