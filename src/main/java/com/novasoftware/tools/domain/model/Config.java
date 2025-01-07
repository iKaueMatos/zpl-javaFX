package com.novasoftware.tools.domain.model;

public class Config {
    private String type;
    private String key;
    private String value;
    private String description;

    public Config(String type, String key, String value, String description) {
        this.type = type;
        this.key = key;
        this.value = value;
        this.description = description;
    }

    public Config() {}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
