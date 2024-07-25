package org.example.model.grammar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class IfStatement implements Node, Statement {
    private Expression predicate;
    private Statements trueStatements;
    private Statements falseStatements;

    public void setPredicate(Expression predicate) {
        this.predicate = predicate;
    }


    public Statements getTrueStatements() {
        return trueStatements;
    }

    public void setTrueStatements(Statements trueStatements) {
        this.trueStatements = trueStatements;
    }


    public void setFalseStatements(Statements falseStatements) {
        this.falseStatements = falseStatements;
    }

    public List<String> toInstructions(Map<String, Object> context) {
        AtomicInteger ifIdx = (AtomicInteger) context.get("ifIdx");
        int idx = ifIdx.getAndIncrement();
        List<String> result = new ArrayList<>(predicate.toInstructions(context));
        result.add(String.format("if-goto IF_TRUE_%d", idx));
        if (falseStatements != null) {
            result.addAll(falseStatements.toInstructions(context));
        }
        result.add(String.format("goto IF_END_%d", idx));
        result.add(String.format("label IF_TRUE_%d", idx));
        result.addAll(trueStatements.toInstructions(context));
        result.add(String.format("label IF_END_%d", idx));
        return result;
    }
}
