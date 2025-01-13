package com.novasoftware.spreadsheet.infrastructure.repository;

import com.novasoftware.shared.Enum.Operator;
import com.novasoftware.shared.Enum.history.ImportHistoryEnum;
import com.novasoftware.shared.database.environment.DatabaseManager;
import com.novasoftware.shared.database.queryBuilder.QueryBuilder;
import com.novasoftware.spreadsheet.application.repository.ImportHistoryRepository;
import com.novasoftware.spreadsheet.domain.model.ImportHistory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ImportHistoryRepositoryImpl implements ImportHistoryRepository {
    @Override
    public boolean insert(ImportHistory importHistory) {
        QueryBuilder<ImportHistoryEnum> queryBuilder = new QueryBuilder<>(ImportHistoryEnum.class);

        queryBuilder.insertInto(ImportHistoryEnum.TABLE.getValue())
                .set(ImportHistoryEnum.IMPORT_DATE.getValue(), importHistory.getImportDate())
                .set(ImportHistoryEnum.STATUS.getValue(), importHistory.getStatus())
                .set(ImportHistoryEnum.IMPORTED_PRODUCTS_COUNT.getValue(), importHistory.getImportedProductsCount())
                .set(ImportHistoryEnum.ERROR_DETAILS.getValue(), importHistory.getErrorDetails())
                .set(ImportHistoryEnum.CREATED_AT.getValue(), importHistory.getCreatedAt())
                .set(ImportHistoryEnum.UPDATED_AT.getValue(), importHistory.getUpdatedAt());

        String sql = queryBuilder.build();

        try (Connection conn = DatabaseManager.getInstance().connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            pstmt.setObject(paramIndex++, importHistory.getImportDate());
            pstmt.setObject(paramIndex++, importHistory.getStatus());
            pstmt.setObject(paramIndex++, importHistory.getImportedProductsCount());
            pstmt.setObject(paramIndex++, importHistory.getErrorDetails());
            pstmt.setObject(paramIndex++, importHistory.getCreatedAt());
            pstmt.setObject(paramIndex++, importHistory.getUpdatedAt());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<ImportHistory> getAll() {
        QueryBuilder<ImportHistoryEnum> queryBuilder = new QueryBuilder<>("import_history");

        String sql = queryBuilder.select("*")
                .build();

        List<ImportHistory> importHistoryList = new ArrayList<>();

        try (Connection conn = DatabaseManager.getInstance().connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                ImportHistory importHistory = new ImportHistory();
                importHistory.setId(Long.valueOf(rs.getInt("id")));
                importHistory.setImportDate(rs.getDate("import_date"));
                importHistory.setStatus(rs.getString("status"));
                importHistory.setImportedProductsCount(rs.getInt("imported_products_count"));
                importHistory.setErrorDetails(rs.getString("error_details"));
                importHistory.setCreatedAt(rs.getDate("created_at"));
                importHistory.setUpdatedAt(rs.getDate("updated_at"));

                importHistoryList.add(importHistory);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return importHistoryList;
    }

    @Override
    public boolean update(ImportHistory importHistory) {
        QueryBuilder<ImportHistoryEnum> queryBuilder = new QueryBuilder<>(ImportHistoryEnum.class);

        queryBuilder.update(ImportHistoryEnum.IMPORT_DATE, importHistory.getImportDate())
                .update(ImportHistoryEnum.STATUS, importHistory.getStatus())
                .update(ImportHistoryEnum.IMPORTED_PRODUCTS_COUNT, importHistory.getImportedProductsCount())
                .update(ImportHistoryEnum.ERROR_DETAILS, importHistory.getErrorDetails())
                .update(ImportHistoryEnum.CREATED_AT, importHistory.getCreatedAt())
                .update(ImportHistoryEnum.UPDATED_AT, importHistory.getUpdatedAt())
                .where("id", Operator.EQUALS, importHistory.getId());

        String sql = queryBuilder.build();

        try (Connection conn = DatabaseManager.getInstance().connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            pstmt.setObject(paramIndex++, importHistory.getImportDate());
            pstmt.setObject(paramIndex++, importHistory.getStatus());
            pstmt.setObject(paramIndex++, importHistory.getImportedProductsCount());
            pstmt.setObject(paramIndex++, importHistory.getErrorDetails());
            pstmt.setObject(paramIndex++, importHistory.getCreatedAt());
            pstmt.setObject(paramIndex++, importHistory.getUpdatedAt());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}