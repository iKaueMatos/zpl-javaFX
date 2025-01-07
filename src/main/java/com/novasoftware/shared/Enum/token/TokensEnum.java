package com.novasoftware.shared.Enum.token;

import com.novasoftware.shared.Enum.user.TableColumn;

public enum TokensEnum implements TableColumn {
    ID("tokens.id"),
    TOKEN("tokens.token"),
    USER_ID("tokens.user_id"),
    TYPE("tokens.type"),
    CREATED_AT("tokens.created_at"),
    EXPIRES_AT("tokens.expires_at"),
    USED_AT("tokens.used_at"),
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
