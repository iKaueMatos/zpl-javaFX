package com.novasoftware.tools.infrastructure.Enum;

public enum Column {
  EAN("ean"),
  SKU("sku"),
  QUANTITY("quantity");

  private final String name;

  Column(String name) {
      this.name = name;
  }

  public String getName() {
      return name;
  }
}
