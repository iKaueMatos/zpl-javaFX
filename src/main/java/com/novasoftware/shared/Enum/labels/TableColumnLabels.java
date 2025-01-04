package com.novasoftware.shared.Enum.labels;

import com.novasoftware.shared.Enum.user.TableColumn;

public enum TableColumnLabels implements TableColumn {
    ID("id"),
    EAN("ean"),
    SKU("sku"),
    QUANTITY("quantity");

    private final String value;

    TableColumnLabels(String value) {
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
