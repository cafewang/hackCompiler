package org.example;

import org.example.model.grammar.VariableDef;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private final Map<String, VariableDef> subroutineScope;
    private final Map<String, VariableDef> classScope;

    public SymbolTable() {
        subroutineScope = new HashMap<>();
        classScope = new HashMap<>();
    }

    public VariableDef find(String name) {
        if (subroutineScope.containsKey(name)) {
            return subroutineScope.get(name);
        }
        return classScope.get(name);
    }

    public void putVar(String name, String type, int index) {
        if (subroutineScope.containsKey(name)) {
            throw new IllegalArgumentException(String.format("本地变量%s已存在", name));
        }
        subroutineScope.put(name, new VariableDef(name, type, VariableDef.Kind.VAR, index));
    }

    public void putArgument(String name, String type, int index) {
        if (subroutineScope.containsKey(name)) {
            throw new IllegalArgumentException(String.format("参数%s已存在", name));
        }
        subroutineScope.put(name, new VariableDef(name, type, VariableDef.Kind.ARGUMENT, index));
    }

    public void putStatic(String name, String type, int index) {
        if (classScope.containsKey(name)) {
            throw new IllegalArgumentException(String.format("静态变量%s已存在", name));
        }
        classScope.put(name, new VariableDef(name, type, VariableDef.Kind.STATIC, index));
    }

    public void putField(String name, String type, int index) {
        if (classScope.containsKey(name)) {
            throw new IllegalArgumentException(String.format("成员变量%s已存在", name));
        }
        classScope.put(name, new VariableDef(name, type, VariableDef.Kind.FIELD, index));
    }

    public void clearSubroutineScope() {
        subroutineScope.clear();
    }

    public int getVarCount() {
        return (int) subroutineScope.values().stream()
                .filter(item -> VariableDef.Kind.VAR == item.getKind()).count();
    }

    public int getFieldCount() {
        return (int) classScope.values().stream()
                .filter(item -> VariableDef.Kind.FIELD == item.getKind()).count();
    }
}
