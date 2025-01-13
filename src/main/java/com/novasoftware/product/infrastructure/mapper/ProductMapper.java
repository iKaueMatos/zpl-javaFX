package com.novasoftware.product.infrastructure.mapper;

import com.novasoftware.Supplier.domain.model.Supplier;
import com.novasoftware.brand.domain.model.Brand;
import com.novasoftware.category.domain.model.Category;
import com.novasoftware.product.application.dto.ProductData;
import com.novasoftware.product.domain.model.Product;
import com.novasoftware.shared.Enum.product.ProductEnum;

import java.sql.ResultSet;
import java.sql.SQLException;
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
        values.add(product.brand() != null ? product.brand().getId() : null);
        values.add(product.description());

        return values;
    }

    public static ProductData mapToProductData(ResultSet rs) throws SQLException {
        return new ProductData(
                rs.getString(ProductEnum.NAME.getValue()),
                rs.getString(ProductEnum.SKU.getValue()),
                rs.getString(ProductEnum.VARIATION_SKU.getValue()),
                rs.getString(ProductEnum.EAN.getValue()),
                rs.getInt(ProductEnum.QUANTITY.getValue()),
                rs.getDouble(ProductEnum.SALE_PRICE.getValue()),
                rs.getLong(ProductEnum.CATEGORY_ID.getValue()) != 0 ?
                        new Category(rs.getLong(ProductEnum.CATEGORY_ID.getValue())) : null,
                rs.getDate(ProductEnum.EXPIRY_DATE.getValue()),
                rs.getLong(ProductEnum.SUPPLIER_ID.getValue()) != 0 ?
                        new Supplier(rs.getLong(ProductEnum.SUPPLIER_ID.getValue())) : null,
                rs.getDouble(ProductEnum.WEIGHT.getValue()),
                rs.getString(ProductEnum.IMAGES.getValue()) != null ?
                        List.of(rs.getString(ProductEnum.IMAGES.getValue()).split(",")) : new ArrayList<>(),
                rs.getDate(ProductEnum.CREATION_DATE.getValue()),
                rs.getDate(ProductEnum.LAST_UPDATED_DATE.getValue()),
                rs.getString(ProductEnum.ERROR.getValue()),
                rs.getLong(ProductEnum.BRAND_ID.getValue()) != 0 ?
                new Brand(rs.getLong(ProductEnum.BRAND_ID.getValue())) : null,
                rs.getString(ProductEnum.DESCRIPTION.getValue())
        );
    }

    public static Product mapToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getLong(ProductEnum.ID.getValue()));
        product.setName(rs.getString(ProductEnum.NAME.getValue()));
        product.setSku(rs.getString(ProductEnum.SKU.getValue()));
        product.setVariationSku(rs.getString(ProductEnum.VARIATION_SKU.getValue()));
        product.setEan(rs.getString(ProductEnum.EAN.getValue()));
        product.setQuantity(rs.getInt(ProductEnum.QUANTITY.getValue()));
        product.setSalePrice(rs.getDouble(ProductEnum.SALE_PRICE.getValue()));

        long categoryId = rs.getLong(ProductEnum.CATEGORY_ID.getValue());
        product.setCategory(categoryId != 0 ? new Category(categoryId) : null);

        long supplierId = rs.getLong(ProductEnum.SUPPLIER_ID.getValue());
        product.setSupplier(supplierId != 0 ? new Supplier(supplierId) : null);

        product.setExpiryDate(rs.getDate(ProductEnum.EXPIRY_DATE.getValue()));
        product.setWeight(rs.getDouble(ProductEnum.WEIGHT.getValue()));

        String imagesString = rs.getString(ProductEnum.IMAGES.getValue());
        product.setImages(imagesString != null ? List.of(imagesString.split(",")) : new ArrayList<>());

        product.setCreationDate(rs.getDate(ProductEnum.CREATION_DATE.getValue()));
        product.setLastUpdatedDate(rs.getDate(ProductEnum.LAST_UPDATED_DATE.getValue()));
        product.setError(rs.getString(ProductEnum.ERROR.getValue()));
        product.setBrandId(Long.valueOf(ProductEnum.BRAND_ID.getValue()));
        product.setDescription(ProductEnum.DESCRIPTION.getValue());

        return product;
    }
}
