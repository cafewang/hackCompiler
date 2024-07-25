package org.example.model.grammar;

import java.util.ArrayList;
import java.util.List;

public class ExpressionList implements Node {
    public List<Expression> getExpressions() {
        return expressions;
    }

    private final List<Expression> expressions;

    public ExpressionList() {
        this.expressions = new ArrayList<>();
    }

}
