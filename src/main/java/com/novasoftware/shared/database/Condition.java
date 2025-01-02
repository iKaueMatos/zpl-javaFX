package com.novasoftware.shared.database;

import com.novasoftware.shared.Enum.TableColumnUser;
import com.novasoftware.shared.Enum.Operator;

public class Condition {
  private final TableColumnUser column;
  private final Operator operator;
  private final Object value;

  public Condition(TableColumnUser column, Operator operator, Object value) {
      this.column = column;
      this.operator = operator;
      this.value = value;
  }

  public TableColumnUser getColumn() {
      return column;
  }

  public Operator getOperator() {
      return operator;
  }

  public Object getValue() {
      return value;
  }
}
