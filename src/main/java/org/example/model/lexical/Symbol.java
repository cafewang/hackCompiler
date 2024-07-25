package org.example.model.lexical;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.regex.Pattern;

public class Symbol implements Token {
    private static final Pattern SYMBOL_PATTERN = Pattern.compile("^[{}()\\[\\].,;+\\-*/&|<>=~]");

    public String getSymbol() {
        return symbol;
    }

    private final String symbol;

    public static int findFirst(String s) {
        return Token.startWithPattern(s, SYMBOL_PATTERN);
    }
    public Symbol(String symbol) {
        this.symbol = symbol;
    }

    public static boolean check(Token token, String s) {
        return token instanceof Symbol && ((Symbol) token).getSymbol().equals(s);
    }

    @Override
    public String toString() {
        return symbol;
    }

    public Element toElement(Document document) {
        Element element = document.createElement("symbol");
        element.appendChild(document.createTextNode(String.format(" %s ", this)));
        return element;
    }
}
