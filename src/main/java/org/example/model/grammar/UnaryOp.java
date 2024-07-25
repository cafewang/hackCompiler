package org.example.model.grammar;

import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;

public class UnaryOp {
    private static final Map<String, List<String>> INSTRUCTION_MAP = ImmutableMap.of(
            "-", List.of("neg"),
            "~", List.of("not")
    );
    private final String sign;

    public UnaryOp(String sign) {
        this.sign = sign;
    }

    public List<String> toInstructions() {
        return INSTRUCTION_MAP.get(sign);
    }

    @Override
    public String toString() {
        return sign;
    }
}
