package com.novasoftware.shared.Enum.Brand;

import com.novasoftware.shared.Enum.user.TableColumn;

public enum BrandEnum implements TableColumn {
    TABLE("brand"),
    ID("id"),
    NAME("marca"),
    DESCRIPTION("description"),
    UPDATE_AT("updated_at"),
    CREATED_AT("created_at");

    private final String value;

    BrandEnum(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getOperatorSymbol() {
        return "";
    }
}
