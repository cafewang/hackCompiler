package org.example.pattern;

import org.example.SymbolTable;
import org.example.model.lexical.*;
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
import java.util.ArrayDeque;
import java.util.Deque;

public class XMLVisitor implements Visitor {
    private final Document document;
    private final Deque<Element> stack;
    private final SymbolTable symbolTable;
    private String classVarScope, classVarType;
    private int fieldIdx, staticIdx;
    private String subroutineVarType;
    private int varIdx, argIdx;

    public XMLVisitor() {
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        this.document = documentBuilder.newDocument();
        stack = new ArrayDeque<>();
        symbolTable = new SymbolTable();
    }

    @Override
    public String toString() {
        DOMSource dom = new DOMSource(document);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute("indent-number", 2);
        ByteArrayOutputStream outputStream;
        try {
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "html");

            outputStream = new ByteArrayOutputStream();
            transformer.transform(dom, new StreamResult(outputStream));
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
        return outputStream.toString();
    }

    @Override
    public void enterClass() {
        Element element = document.createElement("class");
        stack.addLast(element);
        document.appendChild(element);
    }

    @Override
    public void leaveClass() {
        stack.removeLast();
    }

    @Override
    public void enterClassVarDec() {
        Element element = document.createElement("classVarDec");
        Element parent = stack.peekLast();
        parent.appendChild(element);
        stack.addLast(element);
    }

    @Override
    public void leaveClassVarDec() {
        stack.removeLast();
    }

    @Override
    public void visitType(Token token) {
        stack.peekLast().appendChild(token.toElement(document));
        if (stack.peekLast().getTagName().equals("classVarDec")) {
            classVarType = token.toString();
        } else if (stack.peekLast().getTagName().equals("parameterList")) {
            subroutineVarType = token.toString();
        } else if (stack.peekLast().getTagName().equals("varDec")) {
            subroutineVarType = token.toString();
        }

    }

    @Override
    public void enterSubroutineDec() {
        Element element = document.createElement("subroutineDec");
        Element parent = stack.peekLast();
        parent.appendChild(element);
        stack.addLast(element);
    }

    @Override
    public void leaveSubroutineDec() {
        stack.removeLast();
        varIdx = argIdx = 0;
        symbolTable.clearSubroutineScope();
    }

    @Override
    public void enterParamList() {
        Element element = document.createElement("parameterList");
        Element parent = stack.peekLast();
        parent.appendChild(element);
        stack.addLast(element);
    }

    @Override
    public void leaveParamList() {
        stack.removeLast();
    }

    @Override
    public void enterSubroutineBody() {
        Element element = document.createElement("subroutineBody");
        Element parent = stack.peekLast();
        parent.appendChild(element);
        stack.addLast(element);
    }

    @Override
    public void leaveSubroutineBody() {
        stack.removeLast();
    }

    @Override
    public void enterVarDec() {
        Element element = document.createElement("varDec");
        Element parent = stack.peekLast();
        parent.appendChild(element);
        stack.addLast(element);
    }

    @Override
    public void leaveVarDec() {
        stack.removeLast();
    }

    @Override
    public void visitClassName(Identifier token) {
        stack.peekLast().appendChild(token.toElement(document));
    }

    @Override
    public void visitSubroutineName(Identifier token) {
        stack.peekLast().appendChild(token.toElement(document));
    }

    @Override
    public void visitVarName(Identifier token) {
        stack.peekLast().appendChild(token.toElement(document));
        if (stack.peekLast().getTagName().equals("classVarDec")) {
            if ("field".equals(classVarScope)) {
                symbolTable.putField(token.toString(), classVarType, fieldIdx++);
            } else {
                symbolTable.putStatic(token.toString(), classVarType, staticIdx++);
            }
        } else if (stack.peekLast().getTagName().equals("parameterList")) {
            symbolTable.putArgument(token.toString(), subroutineVarType, argIdx++);
        } else if (stack.peekLast().getTagName().equals("varDec")) {
            symbolTable.putVar(token.toString(), subroutineVarType, varIdx++);
        }
    }

    @Override
    public void enterStatements() {
        Element element = document.createElement("statements");
        Element parent = stack.peekLast();
        parent.appendChild(element);
        stack.addLast(element);
    }

    @Override
    public void leaveStatements() {
        stack.removeLast();
    }

    @Override
    public void enterLetStatement() {
        Element element = document.createElement("letStatement");
        Element parent = stack.peekLast();
        parent.appendChild(element);
        stack.addLast(element);
    }

    @Override
    public void leaveLetStatement() {
        stack.removeLast();
    }

    @Override
    public void enterIfStatement() {
        Element element = document.createElement("ifStatement");
        Element parent = stack.peekLast();
        parent.appendChild(element);
        stack.addLast(element);
    }

    @Override
    public void leaveIfStatement() {
        stack.removeLast();
    }

    @Override
    public void enterWhileStatement() {
        Element element = document.createElement("whileStatement");
        Element parent = stack.peekLast();
        parent.appendChild(element);
        stack.addLast(element);
    }

    @Override
    public void leaveWhileStatement() {
        stack.removeLast();
    }

    @Override
    public void enterDoStatement() {
        Element element = document.createElement("doStatement");
        Element parent = stack.peekLast();
        parent.appendChild(element);
        stack.addLast(element);
    }

    @Override
    public void leaveDoStatement() {
        stack.removeLast();
    }

    @Override
    public void enterReturnStatement() {
        Element element = document.createElement("returnStatement");
        Element parent = stack.peekLast();
        parent.appendChild(element);
        stack.addLast(element);
    }

    @Override
    public void leaveReturnStatement() {
        stack.removeLast();
    }

    @Override
    public void enterExpressionList() {
        Element element = document.createElement("expressionList");
        Element parent = stack.peekLast();
        parent.appendChild(element);
        stack.addLast(element);
    }

    @Override
    public void leaveExpressionList() {
        stack.removeLast();
    }

    @Override
    public void enterExpression() {
        Element element = document.createElement("expression");
        Element parent = stack.peekLast();
        parent.appendChild(element);
        stack.addLast(element);
    }

    @Override
    public void leaveExpression() {
        stack.removeLast();
    }

    @Override
    public void enterTerm() {
        Element element = document.createElement("term");
        Element parent = stack.peekLast();
        parent.appendChild(element);
        stack.addLast(element);
    }

    @Override
    public void leaveTerm() {
        stack.removeLast();
    }

    @Override
    public void enterSubroutineCall() {
        Element element = document.createElement("subroutineCall");
        Element parent = stack.peekLast();
        parent.appendChild(element);
        stack.addLast(element);
    }

    @Override
    public void leaveSubroutineCall() {
        stack.removeLast();
    }

    @Override
    public void visitOp(Symbol token) {
        stack.peekLast().appendChild(token.toElement(document));
    }

    @Override
    public void visitUnaryOp(Symbol token) {
        stack.peekLast().appendChild(token.toElement(document));
    }

    @Override
    public void visitKeywordConstant(Keyword token) {
        stack.peekLast().appendChild(token.toElement(document));
    }

    @Override
    public void visitKeyword(Keyword token) {
        stack.peekLast().appendChild(token.toElement(document));
        if (stack.peekLast().getTagName().equals("classVarDec")) {
            classVarScope = token.toString();
        }
    }

    @Override
    public void visitSymbol(Symbol token) {
        stack.peekLast().appendChild(token.toElement(document));
    }

    @Override
    public void visitIntegerConstant(IntegerConstant token) {
        stack.peekLast().appendChild(token.toElement(document));
    }

    @Override
    public void visitStringConstant(StringConstant token) {
        stack.peekLast().appendChild(token.toElement(document));
    }

    @Override
    public void visitClassOrVarName(Identifier identifier) {

    }
}
