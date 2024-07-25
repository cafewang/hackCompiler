package org.example;

import org.example.model.lexical.*;
import org.example.pattern.Visitor;

import java.util.List;


public class SyntaxAnalyzer {
    private List<Token> tokens;
    private int idx;

    public void parseFile(List<Token> tokens, Visitor visitor) {
        this.tokens = tokens;
        idx = 0;
        visitor.enterClass();
        parseClass(visitor);
        visitor.leaveClass();
    }

    private void checkKeyword(String str, String errMsg) {
        if (!Keyword.check(currentTokenNonNull(), str)) {
            throw new IllegalArgumentException(errMsg);
        }
    }

    private void checkIdentifier(String errMsg) {
        if (!(currentTokenNonNull() instanceof Identifier)) {
            throw new IllegalArgumentException(errMsg);
        }
    }

    private void checkSymbol(String str, String errMsg) {
        if (!Symbol.check(currentTokenNonNull(), str)) {
            throw new IllegalArgumentException(errMsg + ":token为" + currentTokenNonNull());
        }
    }

    private void parseClass(Visitor visitor) {
        checkKeyword("class", "关键字class错误");
        visitor.visitKeyword((Keyword) currentTokenNonNull());
        increment();
        checkIdentifier("类名错误");
        visitor.visitClassName((Identifier) currentTokenNonNull());
        increment();
        checkSymbol("{", "类定义错误");
        visitor.visitSymbol((Symbol) currentTokenNonNull());
        increment();
        Token token;
        while (Keyword.check(token = currentTokenNonNull(), "static")
                || Keyword.check(token, "field")) {
            visitor.enterClassVarDec();
            parseClassVarDec(visitor);
            visitor.leaveClassVarDec();
        }
        while (Keyword.check(token = currentTokenNonNull(), "constructor")
                || Keyword.check(token, "function")
                || Keyword.check(token, "method")) {
            visitor.enterSubroutineDec();
            parseSubroutineDec(visitor);
            visitor.leaveSubroutineDec();
        }
        checkSymbol("}", "类定义错误");
        visitor.visitSymbol((Symbol) currentTokenNonNull());
        increment();
    }

    private void parseSubroutineDec(Visitor visitor) {
        Token token;
        if (!(Keyword.check(token = currentTokenNonNull(), "constructor")
                || Keyword.check(token, "function")
                || Keyword.check(token, "method"))) {
            throw new IllegalArgumentException("类成员定义错误");
        }
        visitor.visitKeyword((Keyword) token);
        increment();
        if (Keyword.check(currentTokenNonNull(), "void")) {
            visitor.visitKeyword((Keyword) currentTokenNonNull());
            increment();
        } else {
            parseType(visitor);
        }
        checkIdentifier("类成员定义错误");
        visitor.visitSubroutineName((Identifier) currentTokenNonNull());
        increment();
        checkSymbol("(", "类成员定义错误");
        visitor.visitSymbol((Symbol) currentTokenNonNull());
        increment();
        visitor.enterParamList();
        parseParamList(visitor);
        visitor.leaveParamList();
        checkSymbol(")", "类成员定义错误");
        visitor.visitSymbol((Symbol) currentTokenNonNull());
        increment();
        visitor.enterSubroutineBody();
        parseSubroutineBody(visitor);
        visitor.leaveSubroutineBody();
    }

    private void parseSubroutineBody(Visitor visitor) {
        checkSymbol("{", "类方法体定义错误");
        visitor.visitSymbol((Symbol) currentTokenNonNull());
        increment();
        while (Keyword.check(currentTokenNonNull(), "var")) {
            visitor.enterVarDec();
            parseVarDec(visitor);
            visitor.leaveVarDec();
        }
        visitor.enterStatements();
        parseStatements(visitor);
        visitor.leaveStatements();
        checkSymbol("}", "类方法体定义错误");
        visitor.visitSymbol((Symbol) currentTokenNonNull());
        increment();
    }

    private void parseStatements(Visitor visitor) {
        while (isStatement(currentTokenNonNull())) {
            parseStatement(visitor);
        }
    }

    private void parseStatement(Visitor visitor) {
        Token token = currentTokenNonNull();
        if (Keyword.check(token, "let")) {
            visitor.enterLetStatement();
            parseLetStatement(visitor);
            visitor.leaveLetStatement();
        } else if (Keyword.check(token, "if")) {
            visitor.enterIfStatement();
            parseIfStatement(visitor);
            visitor.leaveIfStatement();
        } else if (Keyword.check(token, "while")) {
            visitor.enterWhileStatement();
            parseWhileStatement(visitor);
            visitor.leaveWhileStatement();
        } else if (Keyword.check(token, "do")) {
            visitor.enterDoStatement();
            parseDoStatement(visitor);
            visitor.leaveDoStatement();
        } else if (Keyword.check(token, "return")) {
            visitor.enterReturnStatement();
            parseReturnStatement(visitor);
            visitor.leaveReturnStatement();
        }
    }

    private void parseReturnStatement(Visitor visitor) {
        checkKeyword("return", "return语句错误");
        visitor.visitKeyword((Keyword) currentTokenNonNull());
        increment();
        if (!Symbol.check(currentTokenNonNull(), ";")) {
            visitor.enterExpression();
            parseExpression(visitor);
            visitor.leaveExpression();
        }
        checkSymbol(";", "return语句错误");
        visitor.visitSymbol((Symbol) currentTokenNonNull());
        increment();
    }

    private void parseDoStatement(Visitor visitor) {
        checkKeyword("do", "do语句错误");
        visitor.visitKeyword((Keyword) currentTokenNonNull());
        increment();
        visitor.enterSubroutineCall();
        parseSubroutineCall(visitor);
        visitor.leaveSubroutineCall();
        checkSymbol(";", "do语句错误");
        visitor.visitSymbol((Symbol) currentTokenNonNull());
        increment();
    }

    private void parseWhileStatement(Visitor visitor) {
        checkKeyword("while", "while语句错误");
        visitor.visitKeyword((Keyword) currentTokenNonNull());
        increment();
        checkSymbol("(", "while语句错误");
        visitor.visitSymbol((Symbol) currentTokenNonNull());
        increment();
        visitor.enterExpression();
        parseExpression(visitor);
        visitor.leaveExpression();
        checkSymbol(")", "while语句错误");
        visitor.visitSymbol((Symbol) currentTokenNonNull());
        increment();
        checkSymbol("{", "while语句错误");
        visitor.visitSymbol((Symbol) currentTokenNonNull());
        increment();
        visitor.enterStatements();
        parseStatements(visitor);
        visitor.leaveStatements();
        checkSymbol("}", "while语句错误");
        visitor.visitSymbol((Symbol) currentTokenNonNull());
        increment();
    }

    private void parseExpression(Visitor visitor) {
        visitor.enterTerm();
        parseTerm(visitor);
        visitor.leaveTerm();
        while (isOP(currentTokenNonNull())) {
            visitor.visitOp((Symbol) currentTokenNonNull());
            increment();
            visitor.enterTerm();
            parseTerm(visitor);
            visitor.leaveTerm();
        }
    }

    private void parseTerm(Visitor visitor) {
        Token token = currentTokenNonNull();
        if ((token instanceof IntegerConstant)
                || (token instanceof StringConstant)) {
            if (token instanceof IntegerConstant) {
                visitor.visitIntegerConstant((IntegerConstant) token);
            } else {
                visitor.visitStringConstant((StringConstant) token);
            }
            increment();
        } else if (token instanceof Keyword) {
            if (!List.of("true", "false", "null", "this")
                    .contains(((Keyword) token).getKeyword())) {
                throw new IllegalArgumentException("表达式格式错误");
            }
            visitor.visitKeywordConstant((Keyword) token);
            increment();
        } else if (token instanceof Symbol) {
            if (isUnaryOP(token)) {
                visitor.visitUnaryOp((Symbol) token);
                increment();
                visitor.enterTerm();
                parseTerm(visitor);
                visitor.leaveTerm();
            } else if (((Symbol) token).getSymbol().equals("(")) {
                visitor.visitSymbol((Symbol) token);
                increment();
                visitor.enterExpression();
                parseExpression(visitor);
                visitor.leaveExpression();
                checkSymbol(")", "表达式格式错误");
                visitor.visitSymbol((Symbol) currentTokenNonNull());
                increment();
            } else {
                throw new IllegalArgumentException("表达式格式错误");
            }
        } else { // Identifier
            // look ahead two
            increment();
            Token snd = currentTokenNonNull();
            if (Symbol.check(snd, "[")) {
                visitor.visitVarName((Identifier) tokens.get(idx - 1));
                visitor.visitSymbol((Symbol) snd);
                increment();
                visitor.enterExpression();
                parseExpression(visitor);
                visitor.leaveExpression();
                checkSymbol("]", "表达式格式错误");
                visitor.visitSymbol((Symbol) currentTokenNonNull());
                increment();
            } else if (Symbol.check(snd, "(") || Symbol.check(snd, ".")) {
                decrement();
                visitor.enterSubroutineCall();
                parseSubroutineCall(visitor);
                visitor.leaveSubroutineCall();
            } else {
                visitor.visitVarName((Identifier) tokens.get(idx - 1));
            }
        }
    }

    private void parseSubroutineCall(Visitor visitor) {
        checkIdentifier("函数调用格式错误");
        increment();
        if (Symbol.check(currentTokenNonNull(), "(")) {
            visitor.visitSubroutineName((Identifier) tokens.get(idx - 1));
            visitor.visitSymbol((Symbol) currentTokenNonNull());
            increment();
        } else if (Symbol.check(currentTokenNonNull(), ".")) {
            visitor.visitClassOrVarName((Identifier) tokens.get(idx - 1));
            visitor.visitSymbol((Symbol) currentTokenNonNull());
            increment();
            checkIdentifier("函数调用格式错误");
            visitor.visitSubroutineName((Identifier) currentTokenNonNull());
            increment();
            checkSymbol("(", "函数调用格式错误");
            visitor.visitSymbol((Symbol) currentTokenNonNull());
            increment();
        }
        visitor.enterExpressionList();
        parseExpressionList(visitor);
        visitor.leaveExpressionList();

        checkSymbol(")", "函数调用格式错误");
        visitor.visitSymbol((Symbol) currentTokenNonNull());
        increment();
    }

    private void parseExpressionList(Visitor visitor) {
        if (Symbol.check(currentTokenNonNull(), ")")) {
            return;
        }
        visitor.enterExpression();
        parseExpression(visitor);
        visitor.leaveExpression();
        while (Symbol.check(currentTokenNonNull(), ",")) {
            visitor.visitSymbol((Symbol) currentTokenNonNull());
            increment();
            visitor.enterExpression();
            parseExpression(visitor);
            visitor.leaveExpression();
        }
    }

    private void decrement() {
        idx--;
    }

    private boolean isUnaryOP(Token token) {
        return (token instanceof Symbol) && "-~".contains(((Symbol) token).getSymbol());
    }

    private boolean isOP(Token token) {
        return (token instanceof Symbol) && "+-*/&|<>=".contains(token.toString());
    }

    private void parseIfStatement(Visitor visitor) {
        checkKeyword("if", "if语句错误");
        visitor.visitKeyword((Keyword) currentTokenNonNull());
        increment();
        checkSymbol("(", "if语句错误");
        visitor.visitSymbol((Symbol) currentTokenNonNull());
        increment();
        visitor.enterExpression();
        parseExpression(visitor);
        visitor.leaveExpression();
        checkSymbol(")", "if语句错误");
        visitor.visitSymbol((Symbol) currentTokenNonNull());
        increment();
        checkSymbol("{", "if语句错误");
        visitor.visitSymbol((Symbol) currentTokenNonNull());
        increment();
        visitor.enterStatements();
        parseStatements(visitor);
        visitor.leaveStatements();
        checkSymbol("}", "if语句错误");
        visitor.visitSymbol((Symbol) currentTokenNonNull());
        increment();
        if (Keyword.check(currentTokenNonNull(), "else")) {
            visitor.visitKeyword((Keyword) currentTokenNonNull());
            increment();
            checkSymbol("{", "else语句错误");
            visitor.visitSymbol((Symbol) currentTokenNonNull());
            increment();
            visitor.enterStatements();
            parseStatements(visitor);
            visitor.leaveStatements();
            checkSymbol("}", "else语句错误");
            visitor.visitSymbol((Symbol) currentTokenNonNull());
            increment();
        }
    }

    private void parseLetStatement(Visitor visitor) {
        checkKeyword("let", "let语句错误");
        visitor.visitKeyword((Keyword) currentTokenNonNull());
        increment();
        checkIdentifier("let语句错误");
        visitor.visitVarName((Identifier) currentTokenNonNull());
        increment();
        if (Symbol.check(currentTokenNonNull(), "[")) {
            visitor.visitSymbol((Symbol) currentTokenNonNull());
            increment();
            visitor.enterExpression();
            parseExpression(visitor);
            visitor.leaveExpression();
            checkSymbol("]", "数组访问语句错误");
            visitor.visitSymbol((Symbol) currentTokenNonNull());
            increment();
        }
        checkSymbol("=", "let语句错误");
        visitor.visitSymbol((Symbol) currentTokenNonNull());
        increment();
        visitor.enterExpression();
        parseExpression(visitor);
        visitor.leaveExpression();
        checkSymbol(";", "let语句错误");
        visitor.visitSymbol((Symbol) currentTokenNonNull());
        increment();
    }

    private void parseVarDec(Visitor visitor) {
        visitor.visitKeyword((Keyword) currentTokenNonNull());
        increment();
        parseType(visitor);
        checkIdentifier("变量定义错误");
        visitor.visitVarName((Identifier) currentTokenNonNull());
        increment();
        while (Symbol.check(currentTokenNonNull(), ",")) {
            visitor.visitSymbol((Symbol) currentTokenNonNull());
            increment();
            checkIdentifier("变量定义错误");
            visitor.visitVarName((Identifier) currentTokenNonNull());
            increment();
        }
        checkSymbol(";", "变量定义错误");
        visitor.visitSymbol((Symbol) currentTokenNonNull());
        increment();
    }

    private boolean isStatement(Token token) {
        return Keyword.check(token, "let")
                || Keyword.check(token, "if")
                || Keyword.check(token, "while")
                || Keyword.check(token, "do")
                || Keyword.check(token, "return");
    }

    private void parseParamList(Visitor visitor) {
        if (Symbol.check(currentTokenNonNull(), ")")) {
            return;
        }
        parseType(visitor);
        checkIdentifier("参数列表格式错误");
        visitor.visitVarName((Identifier) currentTokenNonNull());
        increment();
        while (Symbol.check(currentTokenNonNull(), ",")) {
            visitor.visitSymbol((Symbol) currentTokenNonNull());
            increment();
            parseType(visitor);
            checkIdentifier("参数列表格式错误");
            visitor.visitVarName((Identifier) currentTokenNonNull());
            increment();
        }
    }

    private void increment() {
        idx++;
    }

    private Token currentTokenNonNull() {
        Token token = currentToken();
        if (token == null) {
            throw new IllegalArgumentException("文件格式错误");
        }
        return token;
    }

    private Token currentToken() {
        return idx != tokens.size() ? tokens.get(idx) : null;
    }

    private void parseClassVarDec(Visitor visitor) {
        Token token = currentTokenNonNull();
        if (!(Keyword.check(token, "static") || Keyword.check(token, "field"))) {
            throw new IllegalArgumentException("类变量定义格式错误");
        }
        visitor.visitKeyword((Keyword) token);
        increment();
        parseType(visitor);
        checkIdentifier("类变量定义格式错误");
        visitor.visitVarName((Identifier) currentTokenNonNull());
        increment();
        while (Symbol.check(currentTokenNonNull(), ",")) {
            visitor.visitSymbol((Symbol) currentTokenNonNull());
            increment();
            checkIdentifier("类变量定义格式错误");
            visitor.visitVarName((Identifier) currentTokenNonNull());
            increment();
        }
        checkSymbol(";", "类变量定义格式错误");
        visitor.visitSymbol((Symbol) currentTokenNonNull());
        increment();
    }

    private void parseType(Visitor visitor) {
        Token token = currentTokenNonNull();
        boolean valid = Keyword.check(token, "int") || Keyword.check(token, "char")
                || Keyword.check(token, "boolean") || (token instanceof Identifier);
        if (!valid) {
            throw new IllegalArgumentException("类型格式错误");
        }
        visitor.visitType(token);
        increment();
    }
}
