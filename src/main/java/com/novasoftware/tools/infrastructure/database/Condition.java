package com.novasoftware.tools.infrastructure.database;

import com.novasoftware.tools.infrastructure.Enum.Column;
import com.novasoftware.tools.infrastructure.Enum.Operator;

public class Condition {
  private final Column column;
  private final Operator operator;
  private final Object value;

  public Condition(Column column, Operator operator, Object value) {
      this.column = column;
      this.operator = operator;
      this.value = value;
  }

  public Column getColumn() {
      return column;
  }

  public Operator getOperator() {
      return operator;
  }

  public Object getValue() {
      return value;
  }
}
