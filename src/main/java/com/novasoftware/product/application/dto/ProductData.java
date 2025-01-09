package com.novasoftware.product.application.dto;

import com.novasoftware.Supplier.domain.model.Supplier;
import com.novasoftware.category.domain.model.Category;

import java.util.Date;
import java.util.List;

public record ProductData(String name, String sku, String variationSku, String ean, Integer quantity, Double salePrice,
                   Category category, Date expiryDate, Supplier supplier, Double weight, List<String> images,
                   Date creationDate, Date lastUpdatedDate, String error) {}
