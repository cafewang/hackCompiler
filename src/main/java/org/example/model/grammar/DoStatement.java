package org.example.model.grammar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DoStatement implements Node, Statement {
    private SubroutineCall subroutineCall;

    public void setSubroutineCall(SubroutineCall subroutineCall) {
        this.subroutineCall = subroutineCall;
    }

    public List<String> toInstructions(Map<String, Object> context) {
        List<String> result = new ArrayList<>(subroutineCall.toInstructions(context));
        // ignore return value
        result.add("pop temp 0");
        return result;
    }
}
