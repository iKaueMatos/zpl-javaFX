package com.novasoftware.tools.infrastructure.repository;

import com.novasoftware.shared.Enum.Operator;
import com.novasoftware.shared.Enum.config.ConfigEnum;
import com.novasoftware.shared.database.environment.DatabaseManager;
import com.novasoftware.shared.database.queryBuilder.QueryBuilder;
import com.novasoftware.tools.application.repository.ConfigRepository;
import com.novasoftware.tools.domain.model.Config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ConfigRepositoryImpl implements ConfigRepository {

    @Override
    public void saveConfig(Config config) {
        QueryBuilder<ConfigEnum> queryBuilder = new QueryBuilder<>(Config.class);

        queryBuilder.insertInto("config")
                .set(ConfigEnum.TYPE.getValue(), config.getType())
                .set(ConfigEnum.KEY.getValue(), config.getKey())
                .set(ConfigEnum.VALUE.getValue(), config.getValue())
                .set(ConfigEnum.DESCRIPTION.getValue(), config.getDescription());

        String query = queryBuilder.build();

        try (Connection conn = DatabaseManager.getInstance().connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.executeUpdate();
            System.out.println("Configuração salva com sucesso.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erro ao salvar a configuração no banco de dados.");
        }
    }

    public void updateConfig(Config config) {
        QueryBuilder<ConfigEnum> queryBuilder = new QueryBuilder<>(Config.class);

        queryBuilder.update(ConfigEnum.TYPE, config.getType())
                .set(ConfigEnum.VALUE.getValue(), config.getValue())
                .set(ConfigEnum.DESCRIPTION.getValue(), config.getDescription())
                .where(ConfigEnum.KEY.getValue(), Operator.EQUALS, config.getKey());

        String query = queryBuilder.build();

        try (Connection conn = DatabaseManager.getInstance().connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.executeUpdate();
            System.out.println("Configuração atualizada com sucesso.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erro ao atualizar a configuração no banco de dados.");
        }
    }
}
