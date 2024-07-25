package org.example.model.grammar;

import org.apache.commons.lang3.tuple.Pair;
import org.example.SymbolTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Term implements Node {
    private Integer integerConstant;
    private String stringConstant;
    private String keywordConstant;
    private SubroutineCall subroutineCall;
    private Pair<UnaryOp, Term> unaryOpAndTerm;
    private String varName;
    private Expression arrayAccessExpression;
    private Expression inParenExpression;

    public void setIntegerConstant(Integer integerConstant) {
        this.integerConstant = integerConstant;
    }


    public void setStringConstant(String stringConstant) {
        this.stringConstant = stringConstant;
    }


    public void setKeywordConstant(String keywordConstant) {
        this.keywordConstant = keywordConstant;
    }


    public void setSubroutineCall(SubroutineCall subroutineCall) {
        this.subroutineCall = subroutineCall;
    }

    public Pair<UnaryOp, Term> getUnaryOpAndTerm() {
        return unaryOpAndTerm;
    }

    public void setUnaryOpAndTerm(Pair<UnaryOp, Term> unaryOpAndTerm) {
        this.unaryOpAndTerm = unaryOpAndTerm;
    }

    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    public void setArrayAccessExpression(Expression arrayAccessExpression) {
        this.arrayAccessExpression = arrayAccessExpression;
    }

    public void setInParenExpression(Expression inParenExpression) {
        this.inParenExpression = inParenExpression;
    }

    public List<String> toInstructions(Map<String, Object> context) {
        SymbolTable symbolTable = (SymbolTable) context.get("symbolTable");
        if (integerConstant != null) {
            return List.of(String.format("push constant %d", integerConstant));
        } else if (stringConstant != null) {
            int len = stringConstant.length();
            List<String> result = new ArrayList<>();
            result.add(String.format("push constant %d", len));
            result.add("call String.new 1");
            for (int i = 0; i < len; i++) {
                result.add(String.format("push constant %d", (int)stringConstant.charAt(i)));
                result.add("call String.appendChar 2");
            }
            return result;
        } else if (keywordConstant != null) {
            if ("false".equals(keywordConstant) || "null".equals(keywordConstant)) {
                return List.of("push constant 0");
            } else if ("true".equals(keywordConstant)) {
                return List.of("push constant 1", "neg");
            } else {
                // handle this
                return List.of("push pointer 0");
            }
        } else if (subroutineCall != null) {
            return subroutineCall.toInstructions(context);
        } else if (unaryOpAndTerm != null) {
            List<String> result = new ArrayList<>();
            result.addAll(unaryOpAndTerm.getRight().toInstructions(context));
            result.addAll(unaryOpAndTerm.getLeft().toInstructions());
            return result;
        } else if (varName != null) {
            List<String> result = new ArrayList<>();
            VariableDef variableDef = symbolTable.find(varName);
            int index = variableDef.getIndex();
            VariableDef.Kind kind = variableDef.getKind();
            if (arrayAccessExpression != null) {
                result.addAll(arrayAccessExpression.toInstructions(context));
                result.add(String.format("push %s %d", VariableDef.KIND_TO_SEGMENT.get(kind), index));
                result.add("add");
                result.add("pop pointer 1"); // set that = array base + index
                result.add("push that 0");
            } else {
                result.add(String.format("push %s %d", VariableDef.KIND_TO_SEGMENT.get(kind), index));
            }
            return result;
        } else {
            // (expression)
            return inParenExpression.toInstructions(context);
        }
    }

    @Override
    public String toString() {
        if (integerConstant != null) {
            return integerConstant.toString();
        } else if (stringConstant != null) {
            return String.format("\"%s\"", stringConstant);
        } else if (keywordConstant != null) {
            return keywordConstant;
        } else if (subroutineCall != null) {
            return subroutineCall.toString();
        } else if (unaryOpAndTerm != null) {
            return unaryOpAndTerm.getRight().toString() + " " + unaryOpAndTerm.getLeft().toString();
        } else if (varName != null) {
            return varName;
        } else {
            // (expression)
            return inParenExpression.toString();
        }
    }
}
