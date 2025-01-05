package com.novasoftware.shared.Enum.token;

import com.novasoftware.shared.Enum.user.TableColumn;

public enum TokensEnum implements TableColumn {
    ID("id"),
    TOKEN("token"),
    USER_ID("user_id"),
    TYPE("type"),
    CREATED_AT("created_at"),
    EXPIRES_AT("expires_at"),
    USED_AT("used_at"),
    ALL_COLUMN("*");

    private final String value;

    TokensEnum(String value) {
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
