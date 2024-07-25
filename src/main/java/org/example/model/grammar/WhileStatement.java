package org.example.model.grammar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class WhileStatement implements Node, Statement {
    private Expression predicate;
    private Statements statements;

    public void setStatements(Statements statements) {
        this.statements = statements;
    }

    public void setPredicate(Expression predicate) {
        this.predicate = predicate;
    }

    public List<String> toInstructions(Map<String, Object> context) {
        List<String> result = new ArrayList<>();
        AtomicInteger whileIdx = (AtomicInteger) context.get("whileIdx");
        int idx = whileIdx.getAndIncrement();
        result.add(String.format("label WHILE_START_%d", idx));
        result.addAll(predicate.toInstructions(context));
        result.add(String.format("if-goto WHILE_TRUE_%d", idx));
        result.add(String.format("goto WHILE_FALSE_%d", idx));
        result.add(String.format("label WHILE_TRUE_%d", idx));
        result.addAll(statements.toInstructions(context));
        result.add(String.format("goto WHILE_START_%d", idx));
        result.add(String.format("label WHILE_FALSE_%d", idx));
        return result;
    }
}
