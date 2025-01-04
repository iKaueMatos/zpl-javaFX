package com.novasoftware.shared.database.queryBuilder;

import com.novasoftware.shared.Enum.Operator;

public class Condition<T> {
    private final T column; // Agora é um tipo genérico T
    private final Operator operator;
    private final Object value;

    public Condition(T column, Operator operator, Object value) {
        this.column = column;
        this.operator = operator;
        this.value = value;
    }

    public T getColumn() {
        return column;
    }

    public Operator getOperator() {
        return operator;
    }

    public Object getValue() {
        return value;
    }
}
