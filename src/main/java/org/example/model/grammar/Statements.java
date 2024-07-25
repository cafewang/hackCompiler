package org.example.model.grammar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Statements implements Node {
    private final List<Statement> statementList;

    public Statements() {
        statementList = new ArrayList<>();
    }

    public List<String> toInstructions(Map<String, Object> context) {
        return statementList.stream().map(item -> item.toInstructions(context))
                .flatMap(Collection::stream).collect(Collectors.toList());
    }

    public List<Statement> getStatementList() {
        return statementList;
    }
}
