package org.example.model.lexical;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.regex.Pattern;

public class Identifier implements Token {
    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("^[a-zA-Z_][0-9a-zA-Z_]*");
    private final String identifier;

    public Identifier(String identifier) {
        this.identifier = identifier;
    }

    public static int findFirst(String s) {
        return Token.startWithPattern(s, IDENTIFIER_PATTERN);
    }

    @Override
    public String toString() {
        return identifier;
    }

    @Override
    public Element toElement(Document document) {
        Element element = document.createElement("identifier");
        element.appendChild(document.createTextNode(String.format(" %s ", this)));
        return element;
    }
}
