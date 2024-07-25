package org.example.model.grammar;

import org.example.SymbolTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LetStatement implements Node, Statement {
    private String varName;
    private Expression arrayAccessExpression;
    private Expression rightExpression;
    private boolean hasBrackets;

    public boolean isHasBrackets() {
        return hasBrackets;
    }

    public void setHasBrackets(boolean hasBrackets) {
        this.hasBrackets = hasBrackets;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    public void setArrayAccessExpression(Expression arrayAccessExpression) {
        this.arrayAccessExpression = arrayAccessExpression;
    }

    public void setRightExpression(Expression rightExpression) {
        this.rightExpression = rightExpression;
    }

    public Expression getArrayAccessExpression() {
        return arrayAccessExpression;
    }


    public List<String> toInstructions(Map<String, Object> context) {
        List<String> result = new ArrayList<>(rightExpression.toInstructions(context));
        SymbolTable symbolTable = (SymbolTable) context.get("symbolTable");
        VariableDef variableDef = symbolTable.find(varName);
        int index = variableDef.getIndex();
        VariableDef.Kind kind = variableDef.getKind();
        if (arrayAccessExpression != null) {
            result.addAll(arrayAccessExpression.toInstructions(context));
            result.add(String.format("push %s %d", VariableDef.KIND_TO_SEGMENT.get(kind), index));
            result.add("add");
            result.add("pop pointer 1"); // set that = array base + index
            result.add("pop that 0"); // arr[arrayAccessExp]=rightExp
        } else {
            result.add(String.format("pop %s %d", VariableDef.KIND_TO_SEGMENT.get(kind), index));
        }
        return result;
    }
}
