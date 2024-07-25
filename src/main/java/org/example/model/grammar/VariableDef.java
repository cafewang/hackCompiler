package org.example.model.grammar;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class VariableDef {
    public enum Kind {
        STATIC, FIELD, ARGUMENT, VAR
    }

    public static final Map<Kind, String> KIND_TO_SEGMENT = ImmutableMap.of(
            Kind.STATIC, "static",
            Kind.FIELD, "this",
            Kind.ARGUMENT, "argument",
            Kind.VAR, "local"
    );

    private final String name;
    private final String Type;
    private final Kind kind;
    private final int index;

    public VariableDef(String name, String type, Kind kind, int index) {
        this.name = name;
        Type = type;
        this.kind = kind;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return Type;
    }

    public Kind getKind() {
        return kind;
    }

    public int getIndex() {
        return index;
    }

}
