package org.example.pattern;

import org.example.model.lexical.*;

public interface Visitor {
    void enterClass();
    void leaveClass();
    void enterClassVarDec();
    void leaveClassVarDec();
    void visitType(Token token);
    void enterSubroutineDec();
    void leaveSubroutineDec();
    void enterParamList();
    void leaveParamList();
    void enterSubroutineBody();
    void leaveSubroutineBody();
    void enterVarDec();
    void leaveVarDec();
    void visitClassName(Identifier token);
    void visitSubroutineName(Identifier token);
    void visitVarName(Identifier token);
    void enterStatements();
    void leaveStatements();
    void enterLetStatement();
    void leaveLetStatement();
    void enterIfStatement();
    void leaveIfStatement();
    void enterWhileStatement();
    void leaveWhileStatement();
    void enterDoStatement();
    void leaveDoStatement();
    void enterReturnStatement();
    void leaveReturnStatement();
    void enterExpressionList();
    void leaveExpressionList();
    void enterExpression();
    void leaveExpression();
    void enterTerm();
    void leaveTerm();
    void enterSubroutineCall();
    void leaveSubroutineCall();
    void visitOp(Symbol token);
    void visitUnaryOp(Symbol token);
    void visitKeywordConstant(Keyword token);
    void visitKeyword(Keyword token);
    void visitSymbol(Symbol token);
    void visitIntegerConstant(IntegerConstant token);
    void visitStringConstant(StringConstant token);

    void visitClassOrVarName(Identifier identifier);
}
