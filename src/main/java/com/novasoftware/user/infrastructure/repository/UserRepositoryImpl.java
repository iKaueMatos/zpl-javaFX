package com.novasoftware.user.infrastructure.repository;

import com.novasoftware.shared.Enum.user.UserEnum;
import com.novasoftware.shared.Enum.Operator;
import com.novasoftware.shared.database.environment.DatabaseManager;
import com.novasoftware.shared.database.queryBuilder.QueryBuilder;
import com.novasoftware.shared.util.log.DiscordLogger;
import com.novasoftware.user.application.repository.UserRepository;
import com.novasoftware.user.domain.model.User;
import com.novasoftware.user.infrastructure.mapper.UserMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {

    @Override
    public Optional<User> findUserByEmail(String email) {
        QueryBuilder<UserEnum> queryBuilder = new QueryBuilder<>(User.class);
        queryBuilder.select(UserEnum.ALL_COLUMN.getValue()).where(UserEnum.EMAIL.getValue(), Operator.EQUALS, email);

        User user = executeQuery(queryBuilder);
        return Optional.ofNullable(user);
    }

    @Override
    public boolean insertUser(User user) {
        QueryBuilder<UserEnum> queryBuilder = new QueryBuilder<>(User.class);
        queryBuilder.select(UserEnum.USERNAME.getValue(), UserEnum.PASSWORD.getValue(), UserEnum.EMAIL.getValue(),
                UserEnum.CREATED_AT.getValue(), UserEnum.UPDATED_AT.getValue(), UserEnum.IS_ACTIVE.getValue(),
                UserEnum.TOKEN.getValue());

        List<Object> values = new ArrayList<>();
        values.add(user.getUsername());
        values.add(user.getPassword());
        values.add(user.getEmail());
        values.add(user.getCreated_at());
        values.add(user.getUpdated_at());
        values.add(user.getIsActive());

        String sql = buildInsertQuery(values);
        try (Connection conn = DatabaseManager.getInstance().connect();
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

    @Override
    public boolean update(User user) {
        QueryBuilder<UserEnum> queryBuilder = new QueryBuilder<>(User.class);

        User originalUser = findUserByEmail(user.getEmail()).orElse(null);
        if (originalUser == null) {
            throw new IllegalArgumentException("User not found for update.");
        }

        String updateQuery = buildUpdateQuery(user, originalUser);
        try (Connection conn = DatabaseManager.getInstance().connect();
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

            int paramIndex = 1;
            pstmt.setString(paramIndex++, user.getUsername());
            pstmt.setString(paramIndex++, user.getPassword());
            pstmt.setString(paramIndex++, user.getEmail());
            pstmt.setDate(paramIndex++, new java.sql.Date(user.getCreated_at().getTime()));
            pstmt.setDate(paramIndex++, new java.sql.Date(user.getUpdated_at().getTime()));
            pstmt.setInt(paramIndex++, user.getIsActive());

            pstmt.setString(paramIndex++, user.getEmail());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            DiscordLogger.sendLogToDiscord("Erro", "Ocorreu um erro crítico ao processar a atualização", e.toString(), UserRepository.class, DiscordLogger.COLOR_RED);
            return false;
        }
    }

    private String buildInsertQuery(List<Object> values) {
        StringBuilder query = new StringBuilder("INSERT INTO user (username, password, email, created_at, updated_at, is_active) ");
        query.append("VALUES (");

        query.append("?,".repeat(values.size() - 1));
        query.append("?");

        query.append(")");
        return query.toString();
    }

    private String buildUpdateQuery(User user, User originalUser) {
        StringBuilder query = new StringBuilder("UPDATE user SET username = ?, password = ?, email = ?, created_at = ?, updated_at = ?, is_active = ?, token = ? WHERE email = ?");
        return query.toString();
    }

    private User executeQuery(QueryBuilder<UserEnum> queryBuilder) {
        try (Connection conn = DatabaseManager.getInstance().connect();
             PreparedStatement pstmt = queryBuilder.buildPreparedStatement(conn)) {

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return UserMapper.mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
