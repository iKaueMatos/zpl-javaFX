package com.novasoftware.shared.Enum.product;

import com.novasoftware.shared.Enum.user.TableColumn;

public enum ProductEnum implements TableColumn {
    ID("id"),
    NAME("name"),
    SKU("sku"),
    VARIATION_SKU("variation_sku"),
    EAN("ean"),
    QUANTITY("quantity"),
    SALE_PRICE("sale_price"),
    CATEGORY_ID("category_id"),
    EXPIRY_DATE("expiry_date"),
    SUPPLIER_ID("supplier_id"),
    WEIGHT("weight"),
    IMAGES("images"),
    CREATION_DATE("creation_date"),
    LAST_UPDATED_DATE("last_updated_date"),
    ERROR("error"),
    BRAND_ID("brand_id"),
    ALL_COLUMN("*"),
    DESCRIPTION("description");

    private final String value;

    ProductEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String getOperatorSymbol() {
        return "";
    }
}

