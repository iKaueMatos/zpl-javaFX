package com.novasoftware.product.infrastructure.mapper;

import com.novasoftware.product.application.dto.ProductData;

import java.util.ArrayList;
import java.util.List;

public class ProductMapper {

    public static List<Object> mapToValues(ProductData product) {
        List<Object> values = new ArrayList<>();
        values.add(product.name());
        values.add(product.sku());
        values.add(product.variationSku());
        values.add(product.ean());
        values.add(product.quantity());
        values.add(product.salePrice());

        values.add(product.category() != null ? product.category().getId() : null);
        values.add(product.expiryDate());
        values.add(product.supplier() != null ? product.supplier().getId() : null);
        values.add(product.weight());

        values.add(product.images() != null ? String.join(",", product.images()) : null);
        values.add(product.creationDate());
        values.add(product.lastUpdatedDate());
        values.add(product.error());

        return values;
    }
}
