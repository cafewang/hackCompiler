package org.example.model.grammar;

import java.util.ArrayList;
import java.util.List;

public class SubroutineBody implements Node {
    private final List<VarDec> varDecList;
    private Statements statements;

    public Statements getStatements() {
        return statements;
    }

    public void setStatements(Statements statements) {
        this.statements = statements;
    }

    public SubroutineBody() {
        this.varDecList = new ArrayList<>();
    }

    public List<VarDec> getVarDecList() {
        return varDecList;
    }

}
