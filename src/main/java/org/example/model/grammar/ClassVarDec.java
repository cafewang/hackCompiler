package org.example.model.grammar;

import java.util.ArrayList;
import java.util.List;

public class ClassVarDec implements Node {
    private String scope;
    private String type;
    private final List<String> varNames;

    public ClassVarDec() {
        varNames = new ArrayList<>();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }


    public List<String> getVarNames() {
        return varNames;
    }
}
