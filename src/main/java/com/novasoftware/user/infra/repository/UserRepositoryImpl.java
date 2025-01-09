package com.novasoftware.user.infra.repository;

import com.novasoftware.shared.Enum.user.UsersEnum;
import com.novasoftware.shared.Enum.Operator;
import com.novasoftware.shared.database.environment.DatabaseManager;
import com.novasoftware.shared.database.queryBuilder.QueryBuilder;
import com.novasoftware.shared.util.log.DiscordLogger;
import com.novasoftware.user.application.repository.UserRepository;
import com.novasoftware.user.domain.model.Users;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {

    @Override
    public Optional<Users> findUserByEmail(String email) {
        QueryBuilder<UsersEnum> queryBuilder = new QueryBuilder<>(Users.class);
        queryBuilder.select(UsersEnum.ALL_COLUMN.getValue()).where(UsersEnum.EMAIL.getValue(), Operator.EQUALS, email);

        Users user = executeQuery(queryBuilder);
        return Optional.ofNullable(user);
    }

    @Override
    public boolean insertUser(Users user) {
        QueryBuilder<UsersEnum> queryBuilder = new QueryBuilder<>(Users.class);
        queryBuilder.select(UsersEnum.USERNAME.getValue(), UsersEnum.PASSWORD.getValue(), UsersEnum.EMAIL.getValue(),
                UsersEnum.CREATED_AT.getValue(), UsersEnum.UPDATED_AT.getValue(), UsersEnum.IS_ACTIVE.getValue(),
                UsersEnum.TOKEN.getValue());

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

    @Override
    public boolean update(Users user) {
        QueryBuilder<UsersEnum> queryBuilder = new QueryBuilder<>(Users.class);

        Users originalUser = findUserByEmail(user.getEmail()).orElse(null);
        if (originalUser == null) {
            throw new IllegalArgumentException("User not found for update.");
        }

        String updateQuery = buildUpdateQuery(user, originalUser);
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

            int paramIndex = 1;
            pstmt.setString(paramIndex++, user.getUsername());
            pstmt.setString(paramIndex++, user.getPassword());
            pstmt.setString(paramIndex++, user.getEmail());
            pstmt.setDate(paramIndex++, new java.sql.Date(user.getCreated_at().getTime()));
            pstmt.setDate(paramIndex++, new java.sql.Date(user.getUpdated_at().getTime()));
            pstmt.setInt(paramIndex++, user.getIsActive());
            pstmt.setString(paramIndex++, user.getToken());

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
        StringBuilder query = new StringBuilder("INSERT INTO users (username, password, email, created_at, updated_at, is_active, token) ");
        query.append("VALUES (");

        query.append("?,".repeat(values.size() - 1));
        query.append("?");

        query.append(")");
        return query.toString();
    }

    private String buildUpdateQuery(Users user, Users originalUser) {
        StringBuilder query = new StringBuilder("UPDATE users SET username = ?, password = ?, email = ?, created_at = ?, updated_at = ?, is_active = ?, token = ? WHERE email = ?");
        return query.toString();
    }

    private Users executeQuery(QueryBuilder<UsersEnum> queryBuilder) {
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = queryBuilder.buildPreparedStatement(conn)) {

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Users mapResultSetToUser(ResultSet rs) throws SQLException {
        Users user = new Users();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setCreated_at(rs.getDate("created_at"));
        user.setUpdated_at(rs.getDate("updated_at"));
        user.setIsActive(rs.getInt("is_active"));
        user.setToken(rs.getString("token"));
        return user;
    }
}
