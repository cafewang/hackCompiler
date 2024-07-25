package org.example.model.grammar;

import org.example.SymbolTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SubroutineCall implements Node {
    private String classOrVarName;
    private String subroutineName;
    private ExpressionList expressionList;

    public void setClassOrVarName(String classOrVarName) {
        this.classOrVarName = classOrVarName;
    }


    public void setSubroutineName(String subroutineName) {
        this.subroutineName = subroutineName;
    }


    public void setExpressionList(ExpressionList expressionList) {
        this.expressionList = expressionList;
    }

    public List<String> toInstructions(Map<String, Object> context) {
        List<String> result = new ArrayList<>();
        SymbolTable symbolTable = (SymbolTable) context.get("symbolTable");
        String className = (String) context.get("className");
        if (classOrVarName != null) {
            VariableDef variableDef = (symbolTable).find(classOrVarName);
            if (variableDef != null) {
                // is var
                VariableDef.Kind kind = variableDef.getKind();
                String type = variableDef.getType();
                int index = variableDef.getIndex();
                result.add(String.format("push %s %d", VariableDef.KIND_TO_SEGMENT.get(kind), index));
                expressionList.getExpressions().forEach(exp -> result.addAll(exp.toInstructions(context)));
                result.add(String.format("call %s.%s %d", type, subroutineName,
                        expressionList.getExpressions().size() + 1));
            } else {
                // is class
                expressionList.getExpressions().forEach(exp -> result.addAll(exp.toInstructions(context)));
                result.add(String.format("call %s %d", classOrVarName + "." + subroutineName,
                        expressionList.getExpressions().size()));
            }
        } else {
            // is method
            result.add("push pointer 0");
            expressionList.getExpressions().forEach(exp -> result.addAll(exp.toInstructions(context)));
            result.add(String.format("call %s %d", className + "." + subroutineName,
                    expressionList.getExpressions().size() + 1));
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        expressionList.getExpressions().forEach(exp -> builder.append(exp).append(" "));
        if (classOrVarName != null) {
            builder.append(classOrVarName).append(".");
        }
        builder.append(subroutineName);
        return builder.toString();
    }
}
