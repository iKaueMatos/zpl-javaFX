package com.novasoftware.product.infrastructure.repository;

import com.novasoftware.product.application.dto.ProductData;
import com.novasoftware.product.application.repository.ProductRepository;
import com.novasoftware.product.domain.model.Product;
import com.novasoftware.product.infrastructure.mapper.ProductMapper;
import com.novasoftware.shared.Enum.product.ProductEnum;
import com.novasoftware.shared.database.environment.DatabaseManager;
import com.novasoftware.shared.database.queryBuilder.QueryBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductRepositoryImpl implements ProductRepository {

    @Override
    public boolean insert(List<ProductData> productList) {
        QueryBuilder<ProductEnum> queryBuilder = new QueryBuilder<>(Product.class);
        queryBuilder.select(
                ProductEnum.ID.getValue(),
                ProductEnum.NAME.getValue(),
                ProductEnum.SKU.getValue(),
                ProductEnum.VARIATION_SKU.getValue(),
                ProductEnum.EAN.getValue(),
                ProductEnum.QUANTITY.getValue(),
                ProductEnum.SALE_PRICE.getValue(),
                ProductEnum.CATEGORY_ID.getValue(),
                ProductEnum.EXPIRY_DATE.getValue(),
                ProductEnum.SUPPLIER_ID.getValue(),
                ProductEnum.WEIGHT.getValue(),
                ProductEnum.IMAGES.getValue(),
                ProductEnum.CREATION_DATE.getValue(),
                ProductEnum.LAST_UPDATED_DATE.getValue(),
                ProductEnum.ERROR.getValue()
        );

        for (ProductData product : productList) {
            List<Object> values = ProductMapper.mapToValues(product);

            String sql = buildInsertQuery(values);
            try (Connection conn = DatabaseManager.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                int paramIndex = 1;
                for (Object value : values) {
                    pstmt.setObject(paramIndex++, value);
                }

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected == 0) {
                    return false;
                }

            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }


    private String buildInsertQuery(List<Object> values) {
        StringBuilder query = new StringBuilder("INSERT INTO product (name, sku, variation_sku, ean, quantity, sale_price, ");
        query.append("category_id, expiry_date, supplier_id, weight, images, creation_date, last_updated_date, error) ");
        query.append("VALUES (");

        query.append("?,".repeat(values.size() - 1));
        query.append("?");

        query.append(")");
        return query.toString();
    }

    @Override
    public boolean updateProduct(List<Product> productList) {
        for (Product product : productList) {
            List<Object> values = new ArrayList<>();
            values.add(product.getName());
            values.add(product.getSku());
            values.add(product.getVariationSku());
            values.add(product.getEan());
            values.add(product.getQuantity());
            values.add(product.getSalePrice());

            values.add(product.getCategory() != null ? product.getCategory().getId() : null);
            values.add(product.getExpiryDate());
            values.add(product.getSupplier() != null ? product.getSupplier().getId() : null);
            values.add(product.getWeight());

            values.add(product.getImages() != null ? String.join(",", product.getImages()) : null);

            values.add(product.getCreationDate());
            values.add(product.getLastUpdatedDate());
            values.add(product.getError());

            String sql = buildUpdateQuery(values, product.getId());

            try (Connection conn = DatabaseManager.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                int paramIndex = 1;
                for (Object value : values) {
                    pstmt.setObject(paramIndex++, value);
                }

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected == 0) {
                    return false;
                }

            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean insertBatch(List<ProductData> productList) {
        String sql = buildInsertQueryBatch();

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);

            int count = 0;
            for (ProductData product : productList) {
                List<Object> values = ProductMapper.mapToValues(product);

                int paramIndex = 1;
                for (Object value : values) {
                    pstmt.setObject(paramIndex++, value);
                }

                pstmt.addBatch();

                if (++count % 100 == 0 || count == productList.size()) {
                    pstmt.executeBatch();
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String buildInsertQueryBatch() {
        return "INSERT INTO product (name, sku, variation_sku, ean, quantity, sale_price, " +
                "category_id, expiry_date, supplier_id, weight, images, creation_date, last_updated_date, error) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    private String buildUpdateQuery(List<Object> values, Long productId) {
        StringBuilder query = new StringBuilder("UPDATE product SET ");

        query.append("name = ?, ");
        query.append("sku = ?, ");
        query.append("variation_sku = ?, ");
        query.append("ean = ?, ");
        query.append("quantity = ?, ");
        query.append("sale_price = ?, ");
        query.append("category_id = ?, ");
        query.append("expiry_date = ?, ");
        query.append("supplier_id = ?, ");
        query.append("weight = ?, ");
        query.append("images = ?, ");
        query.append("creation_date = ?, ");
        query.append("last_updated_date = ?, ");
        query.append("error = ? ");

        query.append("WHERE id = ?");

        values.add(productId);

        return query.toString();
    }
}
