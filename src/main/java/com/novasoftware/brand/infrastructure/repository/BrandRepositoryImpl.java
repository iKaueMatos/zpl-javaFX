package com.novasoftware.brand.infrastructure.repository;

import com.novasoftware.brand.application.repository.BrandRepository;
import com.novasoftware.brand.domain.model.Brand;
import com.novasoftware.shared.Enum.Brand.BrandEnum;
import com.novasoftware.shared.Enum.Operator;
import com.novasoftware.shared.Enum.product.ProductEnum;
import com.novasoftware.shared.database.environment.DatabaseManager;
import com.novasoftware.shared.database.queryBuilder.QueryBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BrandRepositoryImpl implements BrandRepository {

    @Override
    public List<Brand> getAllBrand() {
        QueryBuilder<BrandEnum> queryBuilder = new QueryBuilder<>(Brand.class);

        queryBuilder.select(
                BrandEnum.ID.getValue(),
                BrandEnum.NAME.getValue(),
                BrandEnum.DESCRIPTION.getValue(),
                BrandEnum.CREATED_AT.getValue(),
                BrandEnum.UPDATE_AT.getValue()
        );

        String sql = queryBuilder.build();
        List<Brand> brandList = new ArrayList<>();

        try (Connection conn = DatabaseManager.getInstance().connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            try (ResultSet resultSet = pstmt.executeQuery()) {
                while (resultSet.next()) {
                    Brand brand = new Brand(resultSet.getLong(ProductEnum.BRAND_ID.getValue()));
                    brand.setId(resultSet.getLong(BrandEnum.ID.getValue()));
                    brand.setName(resultSet.getString(BrandEnum.NAME.getValue()));
                    brand.setDescription(resultSet.getString(BrandEnum.DESCRIPTION.getValue()));
                    brand.setCreated_at(resultSet.getTimestamp(BrandEnum.CREATED_AT.getValue()));
                    brand.setUpdated_at(resultSet.getTimestamp(BrandEnum.UPDATE_AT.getValue()));

                    brandList.add(brand);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return brandList;
    }

    public boolean insert(Brand brand) {
        QueryBuilder<BrandEnum> queryBuilder = new QueryBuilder<>(Brand.class);

        queryBuilder.insertInto(BrandEnum.TABLE.getValue())
                .set(BrandEnum.NAME.getValue(), brand.getName())
                .set(BrandEnum.DESCRIPTION.getValue(), brand.getDescription());

        String sql = queryBuilder.build();

        try (Connection conn = DatabaseManager.getInstance().connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int rowsAffected = pstmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBrand(Brand brand) {
        QueryBuilder<BrandEnum> queryBuilder = new QueryBuilder<>(Brand.class);

        queryBuilder.update(BrandEnum.NAME, brand.getName())
                .update(BrandEnum.DESCRIPTION, brand.getDescription())
                .update(BrandEnum.UPDATE_AT, "CURRENT_TIMESTAMP")
                .where(BrandEnum.ID.getValue(), Operator.EQUALS, brand.getId());

        String sql = queryBuilder.build();

        try (Connection conn = DatabaseManager.getInstance().connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int rowsAffected = pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
