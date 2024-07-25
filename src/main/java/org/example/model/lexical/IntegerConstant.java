package org.example.model.lexical;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class IntegerConstant implements Token {
    private final int intValue;

    public IntegerConstant(int intValue) {
        this.intValue = intValue;
    }

    public int getIntValue() {
        return intValue;
    }


    @Override
    public String toString() {
        return String.valueOf(intValue);
    }

    @Override
    public Element toElement(Document document) {
        Element element = document.createElement("integerConstant");
        element.appendChild(document.createTextNode(String.format(" %s ", this)));
        return element;
    }
}
