package org.example.model.grammar;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class VarDec implements Node {
    private final List<Pair<String, String>> typeAndVarNames;

    public VarDec() {
        typeAndVarNames = new ArrayList<>();
    }

    public List<Pair<String, String>> getTypeAndVarNames() {
        return typeAndVarNames;
    }
}
