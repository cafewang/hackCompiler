package org.example.model.lexical;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Token {
    static int startWithPattern(String s, Pattern pattern) {
        Matcher matcher = pattern.matcher(s);
        if (!matcher.find()) {
            return -1;
        }
        return matcher.end();
    }

    static String toXML(List<Token> tokens) {
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        Document document = documentBuilder.newDocument();
        Element tokensElement = document.createElement("tokens");
        document.appendChild(tokensElement);
        for (Token token : tokens) {
            tokensElement.appendChild(token.toElement(document));
        }
        DOMSource dom = new DOMSource(document);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute("indent-number", 0);
        ByteArrayOutputStream outputStream;
        try {
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            outputStream = new ByteArrayOutputStream();
            transformer.transform(dom, new StreamResult(outputStream));
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
        return outputStream.toString();
    }

    Element toElement(Document document);
}
