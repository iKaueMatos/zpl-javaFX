package com.novasoftware.shared.Enum.history;

import com.novasoftware.shared.Enum.user.TableColumn;

public enum ImportHistoryEnum implements TableColumn {
    ID("id"),
    IMPORT_DATE("import_date"),
    STATUS("status"),
    IMPORTED_PRODUCTS_COUNT("imported_products_count"),
    ERROR_DETAILS("error_details"),
    CREATED_AT("created_at"),
    UPDATED_AT("updated_at"),
    TABLE("import_history");

    private final String value;

    ImportHistoryEnum(String value) {
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

