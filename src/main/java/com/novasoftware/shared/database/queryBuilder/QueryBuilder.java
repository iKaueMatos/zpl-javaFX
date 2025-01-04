package com.novasoftware.shared.database.queryBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.novasoftware.shared.Enum.Operator;
import com.novasoftware.shared.Enum.user.TableColumn;

public class QueryBuilder<T extends Enum<T> & TableColumn> {
    private String table;
    private final List<Condition> conditions = new ArrayList<>();
    private final List<String> columnsToSelect = new ArrayList<>();
    private final List<OrderByClause> orderByClauses = new ArrayList<>();
    private int limit = -1;

    private final Class<T> modelClass;
    private final List<ColumnDefinition> columnsToCreate = new ArrayList<>();

    public QueryBuilder(Class<T> modelClass) {
        this.modelClass = modelClass;
        this.table = modelClass.getSimpleName().toLowerCase();
    }

    public QueryBuilder<T> select(T... columns) {
        for (T column : columns) {
            if (column.getValue().equals("*")) {
                columnsToSelect.add("*");
                return this;
            }
            columnsToSelect.add(column.getValue());
        }
        return this;
    }

    public QueryBuilder<T> where(T column, Operator operator, Object value) {
        conditions.add(new Condition(column, operator, value));
        return this;
    }

    public QueryBuilder<T> orderBy(T column, boolean ascending) {
        orderByClauses.add(new OrderByClause(column, ascending));
        return this;
    }

    public QueryBuilder<T> limit(int limit) {
        this.limit = limit;
        return this;
    }

    public String build() {
        if (table == null) {
            throw new IllegalStateException("Table name must be specified.");
        }

        StringBuilder query = new StringBuilder("SELECT ");
        if (columnsToSelect.isEmpty()) {
            query.append("*");
        } else {
            query.append(String.join(", ", columnsToSelect));
        }

        query.append(" FROM ").append(table);

        if (!conditions.isEmpty()) {
            query.append(" WHERE ");
            List<String> conditionStrings = new ArrayList<>();
            for (Condition condition : conditions) {
                String columnValue = condition.getColumn().toString(); // Adjust this if needed to get the column's actual value

                if (condition.getOperator() == Operator.IN) {
                    conditionStrings.add(columnValue + " " + condition.getOperator().getSymbol() + " (?)");
                } else {
                    conditionStrings.add(columnValue + " " + condition.getOperator().getSymbol() + " ?");
                }
            }
            query.append(String.join(" AND ", conditionStrings));
        }

        if (!orderByClauses.isEmpty()) {
            query.append(" ORDER BY ");
            List<String> orderByStrings = new ArrayList<>();
            for (OrderByClause orderBy : orderByClauses) {
                orderByStrings.add(orderBy.getColumn().getClass() + (orderBy.isAscending() ? " ASC" : " DESC"));
            }
            query.append(String.join(", ", orderByStrings));
        }

        if (limit > -1) {
            query.append(" LIMIT ").append(limit);
        }

        return query.toString();
    }

    public PreparedStatement buildPreparedStatement(Connection conn) throws SQLException {
        String sql = build();
        PreparedStatement pstmt = conn.prepareStatement(sql);

        int paramIndex = 1;
        for (Condition condition : conditions) {
            if (condition.getOperator() == Operator.IN) {
                pstmt.setString(paramIndex++, String.valueOf(condition.getValue()));
            } else {
                pstmt.setObject(paramIndex++, condition.getValue());
            }
        }

        return pstmt;
    }

    public String buildCreateTable() {
        if (modelClass == null) {
            throw new IllegalStateException("Model class must be specified.");
        }

        StringBuilder query = new StringBuilder("CREATE TABLE ").append(table).append(" (");

        var fields = modelClass.getDeclaredFields();
        List<String> columnDefinitions = new ArrayList<>();
        for (var field : fields) {
            String columnName = field.getName();
            String columnType = getSqlType(field.getType());

            columnDefinitions.add(columnName + " " + columnType);
        }

        query.append(String.join(", ", columnDefinitions));
        query.append(")");

        return query.toString();
    }

    private String getSqlType(Class<?> fieldType) {
        if (fieldType == String.class) {
            return "VARCHAR(255)";
        } else if (fieldType == int.class || fieldType == Integer.class) {
            return "INT";
        } else if (fieldType == boolean.class || fieldType == Boolean.class) {
            return "BOOLEAN";
        } else if (fieldType == double.class || fieldType == Double.class) {
            return "DOUBLE";
        } else if (fieldType == float.class || fieldType == Float.class) {
            return "FLOAT";
        }
        return "VARCHAR(255)";
    }
}
