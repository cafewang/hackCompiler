package org.example.model.lexical;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.regex.Pattern;

public class Keyword implements Token {
    private static final Pattern KEYWORD_PATTERN = Pattern.compile("class|constructor|function" +
            "|method|field|static|var|int|char|boolean|void|true|false|null|this|let|do" +
            "|if|else|while|return");

    public static boolean isKeyword(String s) {
        return KEYWORD_PATTERN.asMatchPredicate().test(s);
    }

    public static boolean check(Token token, String str) {
        return (token instanceof Keyword) && ((Keyword) token).getKeyword().equals(str);
    }

    public String getKeyword() {
        return keyword;
    }

    private final String keyword;

    public Keyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String toString() {
        return keyword;
    }

    @Override
    public Element toElement(Document document) {
        Element element = document.createElement("keyword");
        element.appendChild(document.createTextNode(String.format(" %s ", this)));
        return element;
    }
}
