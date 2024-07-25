package org.example.model.grammar;

import java.util.List;
import java.util.Map;

public interface Statement {
    List<String> toInstructions(Map<String, Object> context);
}
