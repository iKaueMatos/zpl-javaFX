package com.novasoftware.tools.infrastructure.database;

import com.novasoftware.tools.infrastructure.Enum.Column;

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
