package com.novasoftware.shared.Enum.user;

public enum Users implements TableColumn {
    ID("id"),
    USERNAME("username"),
    PASSWORD("password"),
    EMAIL("email"),
    CREATED_AT("created_at"),
    UPDATED_AT("updated_at"),
    IS_ACTIVE("is_active"),
    TOKEN("token"),
    ALL_COLUMN("*");

    private final String value;

    Users(String value) {
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
