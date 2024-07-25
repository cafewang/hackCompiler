package org.example.model.lexical;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class StringConstant implements Token {
    private final String strValue;

    public StringConstant(String strValue) {
        this.strValue = strValue;
    }

    @Override
    public String toString() {
        return strValue;
    }

    public Element toElement(Document document) {
        Element element = document.createElement("stringConstant");
        element.appendChild(document.createTextNode(String.format(" %s ", this)));
        return element;
    }
}
