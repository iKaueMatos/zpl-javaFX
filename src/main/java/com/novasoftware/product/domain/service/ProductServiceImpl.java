package com.novasoftware.product.domain.service;

import com.novasoftware.product.application.dto.ProductData;
import com.novasoftware.product.application.repository.ProductRepository;
import com.novasoftware.product.infrastructure.repository.ProductRepositoryImpl;

import java.util.List;

public class ProductServiceImpl {
    private ProductRepository productRepository = new ProductRepositoryImpl();
    public boolean save(List<ProductData> productDataList) {
        return productRepository.insertBatch(productDataList);
    }
}
