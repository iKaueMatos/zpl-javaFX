package com.novasoftware.token.infra.repository;

import com.novasoftware.shared.Enum.Operator;
import com.novasoftware.shared.Enum.token.TokensEnum;
import com.novasoftware.shared.Enum.user.UsersEnum;
import com.novasoftware.shared.database.environment.DatabaseManager;
import com.novasoftware.shared.database.queryBuilder.QueryBuilder;
import com.novasoftware.token.application.repository.TokenRepository;
import com.novasoftware.token.domain.model.Tokens;
import com.novasoftware.user.domain.model.Users;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TokenRepositoryImpl implements TokenRepository {

    @Override
    public Optional<List<Tokens>> findTokensByUserId(int userId) {
        QueryBuilder<TokensEnum> queryBuilder = buildTokensByUserIdQuery(userId);
        return executeQuery(queryBuilder, this::mapResultSetToToken);
    }

    @Override
    public Optional<Users> findUserByToken(String tokenValue) {
        QueryBuilder<TokensEnum> queryBuilder = buildUserByTokenQuery(tokenValue);
        return executeQuery(queryBuilder, this::mapResultSetToUser).map(users -> users.get(0));
    }

    private QueryBuilder<TokensEnum> buildTokensByUserIdQuery(int userId) {
        QueryBuilder<TokensEnum> queryBuilder = new QueryBuilder<>(Tokens.class);
        queryBuilder
                .select(String.valueOf(TokensEnum.ALL_COLUMN.getValue()))
                .where(TokensEnum.USER_ID.getValue(), Operator.EQUALS, userId)
                .and(TokensEnum.USED_AT.getValue(), Operator.IS_NULL, "")
                .and(TokensEnum.EXPIRES_AT.getValue(), Operator.GREATER_THAN, new Timestamp(System.currentTimeMillis()));
        return queryBuilder;
    }

    private QueryBuilder<TokensEnum> buildUserByTokenQuery(String tokenValue) {
        QueryBuilder<TokensEnum> queryBuilder = new QueryBuilder<>(Tokens.class);
        queryBuilder
                .select(UsersEnum.ALL_COLUMN.getValue())
                .join(Users.class, "tokens.user_id", "users.id")
                .where(TokensEnum.TOKEN.getValue(), Operator.EQUALS, tokenValue)
                .and(TokensEnum.EXPIRES_AT.getValue(), Operator.GREATER_THAN, new Timestamp(System.currentTimeMillis()));
        return queryBuilder;
    }

    private <T> Optional<List<T>> executeQuery(QueryBuilder<TokensEnum> queryBuilder, ResultSetMapper<T> mapper) {
        String sql = queryBuilder.build();

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = queryBuilder.buildPreparedStatement(conn);
             ResultSet rs = pstmt.executeQuery()) {

            List<T> results = new ArrayList<>();
            while (rs.next()) {
                results.add(mapper.map(rs));
            }

            return results.isEmpty() ? Optional.empty() : Optional.of(results);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public void saveToken(Tokens token) {
        QueryBuilder<TokensEnum> queryBuilder = new QueryBuilder<>(Tokens.class);
        queryBuilder
                .insertInto("tokens")
                .set(TokensEnum.TOKEN.getValue(), token.getToken())
                .set(TokensEnum.USER_ID.getValue(), token.getUserId())
                .set(TokensEnum.CREATED_AT.getValue(), token.getCreatedAt())
                .set(TokensEnum.EXPIRES_AT.getValue(), token.getExpiresAt())
                .set(TokensEnum.USED_AT.getValue(), token.getUsedAt());

        String sql = queryBuilder.build();

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            pstmt.setString(paramIndex++, token.getToken());
            pstmt.setInt(paramIndex++, token.getUserId());
            pstmt.setTimestamp(paramIndex++, token.getCreatedAt());
            pstmt.setTimestamp(paramIndex++, token.getExpiresAt());

            if (token.getUsedAt() != null) {
                pstmt.setTimestamp(paramIndex++, token.getUsedAt());
            } else {
                pstmt.setNull(paramIndex++, java.sql.Types.TIMESTAMP);
            }

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar o token no banco de dados", e);
        }
    }

    private Tokens mapResultSetToToken(ResultSet rs) throws SQLException {
        Tokens token = new Tokens();
        token.setId(rs.getInt("id"));
        token.setToken(rs.getString("token"));
        token.setUserId(rs.getInt("user_id"));
        token.setType(rs.getString("type"));
        token.setCreatedAt(rs.getTimestamp("created_at"));
        token.setExpiresAt(rs.getTimestamp("expires_at"));
        token.setUsedAt(rs.getTimestamp("used_at"));
        return token;
    }

    private Users mapResultSetToUser(ResultSet rs) throws SQLException {
        Users user = new Users();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setCreated_at(rs.getTimestamp("created_at"));
        user.setUpdated_at(rs.getTimestamp("updated_at"));
        user.setIsActive(rs.getInt("is_active"));
        user.setToken(rs.getString("token"));
        return user;
    }

    @FunctionalInterface
    private interface ResultSetMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }
}
