package com.novasoftware.shared.database.queryBuilder;

public class InsertColumnValue {
    private final String column;
    private final Object value;

    public InsertColumnValue(String column, Object value) {
        this.column = column;
        this.value = value;
    }

    public String getColumn() {
        return column;
    }

    public Object getValue() {
        return value;
    }
}
