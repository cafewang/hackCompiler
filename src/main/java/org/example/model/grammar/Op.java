package org.example.model.grammar;

import com.google.common.collect.ImmutableMap;

import java.util.List;

public class Op implements Node {
    private static final ImmutableMap<String, List<String>> SIGN_TO_INSTRUCTIONS = new ImmutableMap.Builder<String, List<String>>()
            .put("+", List.of("add"))
            .put("-", List.of("sub"))
            .put("*", List.of("call Math.multiply 2"))
            .put("/", List.of("call Math.divide 2"))
            .put("=", List.of("eq"))
            .put(">", List.of("gt"))
            .put("<", List.of("lt"))
            .put("&", List.of("and"))
            .put("|", List.of("or"))
            .build();

    private final String sign;


    public Op(String sign) {
        this.sign = sign;
    }

    public List<String> toInstructions() {
        return SIGN_TO_INSTRUCTIONS.get(sign);
    }

    @Override
    public String toString() {
        return sign;
    }
}
