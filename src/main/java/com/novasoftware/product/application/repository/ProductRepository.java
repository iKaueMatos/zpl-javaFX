package com.novasoftware.product.application.repository;

import com.novasoftware.product.application.dto.ProductData;
import com.novasoftware.product.domain.model.Product;

import java.sql.SQLException;
import java.util.List;

public interface ProductRepository {
    boolean insert(List<ProductData> productList) throws SQLException;
    boolean updateProduct(List<Product> productList);
    boolean insertBatch(List<ProductData> productList);
    List<ProductData> getProductsFilter(Long brandId, Long productId, String name, String sku);
    List<ProductData> getAllProducts(int page, int pageSize);
}
