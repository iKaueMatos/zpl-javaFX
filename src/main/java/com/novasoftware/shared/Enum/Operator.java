package com.novasoftware.shared.Enum;

public enum Operator {
  EQUALS("="),
  LIKE("LIKE"),
  GREATER_THAN(">"),
  LESS_THAN("<"),
  NOT_EQUALS("!="),
  IN("IN"),
  IS_NULL("IS NULL");

  private final String symbol;

  Operator(String symbol) {
      this.symbol = symbol;
  }

  public String getSymbol() {
      return symbol;
  }
}
