package org.example.model.grammar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReturnStatement implements Node, Statement {
    private Expression returnValue;

    public void setReturnValue(Expression returnValue) {
        this.returnValue = returnValue;
    }

    public List<String> toInstructions(Map<String, Object> context) {
        List<String> result = new ArrayList<>();
        if (returnValue != null) {
            result.addAll(returnValue.toInstructions(context));
        } else {
            result.add("push constant 0");
        }
        result.add("return");
        return result;
    }
}
