package com.novasoftware.shared.Enum.config;

import com.novasoftware.shared.Enum.user.TableColumn;

public enum ConfigEnum implements TableColumn {
    ID("config.id"),
    TYPE("config.config_type"),
    GIVEN("config.config_value"),
    KEY("config.config_key"),
    VALUE("config.config_value"),
    DESCRIPTION("config.description");

    private final String value;

    ConfigEnum(String value) {
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
