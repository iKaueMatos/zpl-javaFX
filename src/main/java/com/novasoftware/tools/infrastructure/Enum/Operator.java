package com.novasoftware.tools.infrastructure.Enum;

public enum Operator {
  EQUALS("="),
  LIKE("LIKE"),
  GREATER_THAN(">"),
  LESS_THAN("<"),
  NOT_EQUALS("!="),
  IN("IN");

  private final String symbol;

  Operator(String symbol) {
      this.symbol = symbol;
  }

  public String getSymbol() {
      return symbol;
  }
}
