package com.novasoftware.product.application.repository;

import com.novasoftware.product.application.dto.ProductData;
import com.novasoftware.product.domain.model.Product;

import java.util.List;

public interface ProductRepository {
    boolean insert(List<ProductData> productList);
    boolean updateProduct(List<Product> productList);
    boolean insertBatch(List<ProductData> productList);
}
