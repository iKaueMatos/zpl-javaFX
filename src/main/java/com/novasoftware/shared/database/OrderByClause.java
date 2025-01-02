package com.novasoftware.shared.database;

import com.novasoftware.shared.Enum.TableColumnUser;

public class OrderByClause {
  private final TableColumnUser column;
  private final boolean ascending;

  public OrderByClause(TableColumnUser column, boolean ascending) {
      this.column = column;
      this.ascending = ascending;
  }

  public TableColumnUser getColumn() {
      return column;
  }

  public boolean isAscending() {
      return ascending;
  }
}
