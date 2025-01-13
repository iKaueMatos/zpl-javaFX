package com.novasoftware.token.infrastructure.repository;

import com.novasoftware.shared.Enum.Operator;
import com.novasoftware.shared.Enum.token.TokensEnum;
import com.novasoftware.shared.Enum.user.UserEnum;
import com.novasoftware.shared.database.environment.DatabaseManager;
import com.novasoftware.shared.database.queryBuilder.QueryBuilder;
import com.novasoftware.token.application.repository.TokenRepository;
import com.novasoftware.token.domain.model.Token;
import com.novasoftware.token.infrastructure.mapper.TokenMapper;
import com.novasoftware.user.domain.model.User;
import com.novasoftware.user.infrastructure.mapper.UserMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TokenRepositoryImpl implements TokenRepository {

    @Override
    public Optional<List<Token>> findTokensByUserId(int userId) {
        QueryBuilder<TokensEnum> queryBuilder = buildTokensByUserIdQuery(userId);
        return executeQuery(queryBuilder, TokenMapper::mapResultSetToToken);
    }

    @Override
    public Optional<User> findUserByToken(String tokenValue) {
        QueryBuilder<TokensEnum> queryBuilder = buildUserByTokenQuery(tokenValue);
        return executeQuery(queryBuilder, UserMapper::mapResultSetToUser).map(users -> users.get(0));
    }

    private QueryBuilder<TokensEnum> buildTokensByUserIdQuery(int userId) {
        QueryBuilder<TokensEnum> queryBuilder = new QueryBuilder<>(Token.class);
        queryBuilder
                .select(String.valueOf(TokensEnum.ALL_COLUMN.getValue()))
                .where(TokensEnum.USER_ID.getValue(), Operator.EQUALS, userId)
                .and(TokensEnum.USED_AT.getValue(), Operator.IS_NULL, "")
                .and(TokensEnum.EXPIRES_AT.getValue(), Operator.GREATER_THAN, new Timestamp(System.currentTimeMillis()));
        return queryBuilder;
    }

    private QueryBuilder<TokensEnum> buildUserByTokenQuery(String tokenValue) {
        QueryBuilder<TokensEnum> queryBuilder = new QueryBuilder<>(Token.class);
        queryBuilder
                .select(UserEnum.ALL_COLUMN.getValue())
                .join(User.class, "token.user_id", "user.id")
                .where(TokensEnum.TOKEN.getValue(), Operator.EQUALS, tokenValue)
                .and(TokensEnum.EXPIRES_AT.getValue(), Operator.GREATER_THAN, new Timestamp(System.currentTimeMillis()));
        return queryBuilder;
    }

    private <T> Optional<List<T>> executeQuery(QueryBuilder<TokensEnum> queryBuilder, ResultSetMapper<T> mapper) {
        String sql = queryBuilder.build();

        try (Connection conn = DatabaseManager.getInstance().connect();
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
    public void saveToken(Token token) {
        QueryBuilder<TokensEnum> queryBuilder = new QueryBuilder<>(Token.class);
        queryBuilder
                .insertInto("token")
                .set(TokensEnum.TOKEN.getValue(), token.getToken())
                .set(TokensEnum.USER_ID.getValue(), token.getUserId())
                .set(TokensEnum.CREATED_AT.getValue(), token.getCreatedAt())
                .set(TokensEnum.EXPIRES_AT.getValue(), token.getExpiresAt())
                .set(TokensEnum.USED_AT.getValue(), token.getUsedAt());

        String sql = queryBuilder.build();

        try (Connection conn = DatabaseManager.getInstance().connect();
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

    @FunctionalInterface
    private interface ResultSetMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }
}
