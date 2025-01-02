package com.novasoftware.user.infra.repository;

import com.novasoftware.shared.Enum.TableColumnUser;
import com.novasoftware.shared.Enum.Operator;
import com.novasoftware.shared.database.DatabaseManager;
import com.novasoftware.shared.database.QueryBuilder;
import com.novasoftware.tools.application.repository.UserRepository;
import com.novasoftware.user.domain.model.Users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {
    public Optional<Users> findUserByEmail(String email) {
        QueryBuilder<Users> queryBuilder = new QueryBuilder<>(Users.class);
        queryBuilder.select(TableColumnUser.ALL_COLUMN);
        queryBuilder.where(TableColumnUser.EMAIL, Operator.EQUALS, email);

        return Optional.ofNullable(executeQuery(queryBuilder));
    }

    public boolean insertUser(Users user) {
        QueryBuilder<Users> queryBuilder = new QueryBuilder<>(Users.class);
        queryBuilder.select(TableColumnUser.USERNAME, TableColumnUser.PASSWORD, TableColumnUser.EMAIL,
                TableColumnUser.CREATED_AT, TableColumnUser.UPDATED_AT, TableColumnUser.IS_ACTIVE,
                TableColumnUser.TOKEN);

        List<Object> values = new ArrayList<>();
        values.add(user.getUsername());
        values.add(user.getPassword());
        values.add(user.getEmail());
        values.add(user.getCreated_at());
        values.add(user.getUpdated_at());
        values.add(user.getIsActive());
        values.add(user.getToken());

        String sql = buildInsertQuery(values);
        System.out.println("Consulta SQL gerada: " + sql);

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            for (Object value : values) {
                pstmt.setObject(paramIndex++, value);
            }

            // Executando a inserção
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String buildInsertQuery(List<Object> values) {
        StringBuilder query = new StringBuilder("INSERT INTO ");
        query.append("users (username, password, email, created_at, updated_at, is_active, token) ");
        query.append("VALUES (");

        query.append("?,".repeat(values.size() - 1));
        query.append("?");
        query.append(")");
        return query.toString();
    }

    private static Users executeQuery(QueryBuilder<Users> queryBuilder) {
        String sql = queryBuilder.build();
        System.out.println("Consulta SQL gerada: " + sql);

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = queryBuilder.buildPreparedStatement(conn)) {

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
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
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
