package com.novasoftware.shared.database;

import com.novasoftware.shared.Enum.Column;

public class OrderByClause {
  private final Column column;
  private final boolean ascending;

  public OrderByClause(Column column, boolean ascending) {
      this.column = column;
      this.ascending = ascending;
  }

  public Column getColumn() {
      return column;
  }

  public boolean isAscending() {
      return ascending;
  }
}
