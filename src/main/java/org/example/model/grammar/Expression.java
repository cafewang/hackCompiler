package org.example.model.grammar;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Expression implements Node {
    private Term fstTerm;
    private final List<Pair<Op, Term>> restTerms;

    public Expression() {
        restTerms = new ArrayList<>();
    }

    public Term getFstTerm() {
        return fstTerm;
    }

    public void setFstTerm(Term fstTerm) {
        this.fstTerm = fstTerm;
    }

    public List<Pair<Op, Term>> getRestTerms() {
        return restTerms;
    }

    public List<String> toInstructions(Map<String, Object> context) {
        List<String> result = new ArrayList<>(fstTerm.toInstructions(context));
        for (Pair<Op, Term> pair : restTerms) {
            result.addAll(pair.getRight().toInstructions(context));
            result.addAll(pair.getLeft().toInstructions());
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(fstTerm.toString());
        for (Pair<Op, Term> pair : restTerms) {
            builder.append(" ").append(pair.getRight().toString());
            builder.append(" ").append(pair.getLeft().toString());
        }
        return builder.toString();
    }
}
