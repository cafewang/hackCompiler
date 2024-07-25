package org.example;

import org.example.model.lexical.*;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Tokenizer {
    private static final Pattern COMMENT_PATTERN = Pattern.compile("(//[^\n]*)|(/\\*.*?\\*/)", Pattern.DOTALL);
    private static final Pattern CANDIDATE_PATTERN = Pattern.compile("[^\\s\"]+|\"[^\"]*\"");

    private static List<Token> toTokens(String s) {
        if (s.isEmpty()) {
            return Collections.emptyList();
        }

        int i = 0;
        List<Token> result = new ArrayList<>();
        while (true) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                int n = 0;
                char digit;
                while (i != s.length() && Character.isDigit(digit = s.charAt(i))) {
                    n = n * 10 + (digit - '0');
                    i++;
                }
                result.add(new IntegerConstant(n));
            } else if (c == '"') {
                i++;
                StringBuilder builder = new StringBuilder();
                char sndQuote;
                while (i != s.length() && (sndQuote = s.charAt(i)) != '"') {
                    builder.append(sndQuote);
                    i++;
                }
                if (i == s.length()) {
                    throw new IllegalArgumentException("双引号未结束");
                }
                result.add(new StringConstant(builder.toString()));
                i++;
            } else {
                int symbolIdx = Symbol.findFirst(s.substring(i));
                if (symbolIdx != -1) {
                    String symbol = s.substring(i, i + symbolIdx);
                    result.add(new Symbol(symbol));
                    i += symbolIdx;
                } else {
                    int identifierIdx = Identifier.findFirst(s.substring(i));
                    if (identifierIdx == -1) {
                        throw new IllegalArgumentException("标识符格式错误");
                    }
                    if (Keyword.isKeyword(s.substring(i, i + identifierIdx))) {
                        result.add(new Keyword(s.substring(i, i + identifierIdx)));
                    } else {
                        result.add(new Identifier(s.substring(i, i + identifierIdx)));
                    }
                    i += identifierIdx;
                }
            }

            if (i == s.length()) {
                return result;
            }
        }
    }

    private final BufferedReader br;

    public Tokenizer(BufferedReader br) {
        this.br = br;
    }

    public List<Token> tokenize() {
        String content = br.lines().collect(Collectors.joining("\n"));
        Matcher matcher = COMMENT_PATTERN.matcher(content);
        List<String> lines = matcher.replaceAll("").lines()
                .filter(str -> !str.isBlank()).collect(Collectors.toList());
        List<String> pieces = lines.stream().map(this::split).flatMap(Collection::stream)
                .filter(str -> !str.isEmpty()).collect(Collectors.toList());
        return pieces.stream().map(Tokenizer::toTokens).flatMap(Collection::stream).collect(Collectors.toList());
    }

    private List<String> split(String s) {
        Matcher matcher = CANDIDATE_PATTERN.matcher(s);
        List<String> result = new ArrayList<>();
        while (matcher.find()) {
            result.add(s.substring(matcher.start(), matcher.end()));
        }
        return result;
    }

}
