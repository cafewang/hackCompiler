package org.example.pattern;

import org.apache.commons.lang3.tuple.Pair;
import org.example.SymbolTable;
import org.example.model.grammar.*;
import org.example.model.lexical.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CodeGenerationVisitor implements Visitor {
    private final List<String> vmInstructions;
    private final Deque<Node> stack;
    private final SymbolTable symbolTable;
    private int varIdx, argIdx, staticIdx, fieldIdx;
    private final Map<String, Object> context;

    public CodeGenerationVisitor() {
        stack = new ArrayDeque<>();
        symbolTable = new SymbolTable();
        vmInstructions = new ArrayList<>();
        context = new HashMap<>();
        context.put("symbolTable", symbolTable);
        context.put("ifIdx", new AtomicInteger(0));
        context.put("whileIdx", new AtomicInteger(0));
    }

    @Override
    public String toString() {
        return String.join("\n", vmInstructions);
    }

    @Override
    public void enterClass() {
        stack.addLast(new Klass());
    }

    @Override
    public void leaveClass() {
        stack.removeLast();
    }

    @Override
    public void enterClassVarDec() {
        stack.addLast(new ClassVarDec());
    }

    @Override
    public void leaveClassVarDec() {
        stack.removeLast();
    }

    @Override
    public void visitType(Token token) {
        if (stack.peekLast() instanceof ClassVarDec) {
            ((ClassVarDec) stack.peekLast()).setType(token.toString());
        } else if (stack.peekLast() instanceof ParameterList) {
            List<Pair<String, String>> typeAndVarNames = ((ParameterList) stack.peekLast()).getTypeAndVarNames();
            typeAndVarNames.add(Pair.of(token.toString(), null));
        } else if (stack.peekLast() instanceof VarDec) {
            List<Pair<String, String>> typeAndVarNames = ((VarDec) stack.peekLast()).getTypeAndVarNames();
            typeAndVarNames.add(Pair.of(token.toString(), null));
        } else if (stack.peekLast() instanceof SubroutineDec) {
            ((SubroutineDec) stack.peekLast()).setReturnType(token.toString());
        }

    }

    @Override
    public void enterSubroutineDec() {
        stack.addLast(new SubroutineDec());
    }

    @Override
    public void leaveSubroutineDec() {
        stack.removeLast();
        symbolTable.clearSubroutineScope();
        varIdx = argIdx = 0;
    }

    @Override
    public void enterParamList() {
        if (stack.peekLast() instanceof SubroutineDec) {
            String className = (String) context.get("className");
            String type = ((SubroutineDec) stack.peekLast()).getType();
            if ("method".equals(type)) {
                symbolTable.putArgument("ARG_THIS", className, argIdx++);
            }
        }
        stack.addLast(new ParameterList());
    }

    @Override
    public void leaveParamList() {
        stack.removeLast();
    }

    @Override
    public void enterSubroutineBody() {
        stack.addLast(new SubroutineBody());
    }

    @Override
    public void leaveSubroutineBody() {
        vmInstructions.addAll(((SubroutineBody)stack.peekLast())
                .getStatements().toInstructions(context));
        stack.removeLast();
    }

    @Override
    public void enterVarDec() {
        VarDec varDec = new VarDec();
        ((SubroutineBody)stack.peekLast()).getVarDecList().add(varDec);
        stack.addLast(varDec);
    }

    @Override
    public void leaveVarDec() {
        stack.removeLast();
    }

    @Override
    public void visitClassName(Identifier token) {
        Node parent = stack.peekLast();
        if (parent instanceof Klass) {
            context.put("className", token.toString());
        } else if (parent instanceof SubroutineCall) {
            ((SubroutineCall) parent).setClassOrVarName(token.toString());
        }
    }

    @Override
    public void visitSubroutineName(Identifier token) {
        if (stack.peekLast() instanceof SubroutineDec) {
            ((SubroutineDec) stack.peekLast()).setSubroutineName(token.toString());
        } else if (stack.peekLast() instanceof SubroutineCall) {
            ((SubroutineCall) stack.peekLast()).setSubroutineName(token.toString());
        }
    }

    @Override
    public void visitVarName(Identifier token) {
        if (stack.peekLast() instanceof ClassVarDec) {
            ClassVarDec classVarDec = (ClassVarDec) stack.peekLast();
            classVarDec.getVarNames().add(token.toString());
            if ("field".equals(classVarDec.getScope())) {
                symbolTable.putField(token.toString(), classVarDec.getType(), fieldIdx++);
            } else {
                symbolTable.putStatic(token.toString(), classVarDec.getType(), staticIdx++);
            }
        } else if (stack.peekLast() instanceof ParameterList) {
            List<Pair<String, String>> typeAndVarNames = ((ParameterList) stack.peekLast()).getTypeAndVarNames();
            String type = typeAndVarNames.get(typeAndVarNames.size() - 1).getLeft();
            typeAndVarNames.remove(typeAndVarNames.size() - 1);
            typeAndVarNames.add(Pair.of(type, token.toString()));
            symbolTable.putArgument(token.toString(), type, argIdx++);
        } else if (stack.peekLast() instanceof VarDec) {
            List<Pair<String, String>> typeAndVarNames = ((VarDec) stack.peekLast()).getTypeAndVarNames();
            Pair<String, String> pair = typeAndVarNames.get(typeAndVarNames.size() - 1);
            String type = pair.getLeft();
            String lastVarName = pair.getRight();
            if (lastVarName == null) {
                typeAndVarNames.remove(typeAndVarNames.size() - 1);
            }
            typeAndVarNames.add(Pair.of(type, token.toString()));

            symbolTable.putVar(token.toString(), type, varIdx++);
        } else if (stack.peekLast() instanceof Term) {
            ((Term) stack.peekLast()).setVarName(token.toString());
        } else if (stack.peekLast() instanceof SubroutineCall) {
            ((SubroutineCall) stack.peekLast()).setClassOrVarName(token.toString());
        } else if (stack.peekLast() instanceof LetStatement) {
            ((LetStatement) stack.peekLast()).setVarName(token.toString());
        }
    }

    @Override
    public void enterStatements() {
        Statements statements = new Statements();
        if (stack.peekLast() instanceof SubroutineBody) {
            SubroutineBody subroutineBody = (SubroutineBody) stack.pollLast();
            SubroutineDec subroutineDec = (SubroutineDec) stack.pollLast();
            stack.addLast(subroutineDec);
            stack.addLast(subroutineBody);
            subroutineBody.setStatements(statements);
            String type = subroutineDec.getType();
            vmInstructions.add(String.format("function %s %s",
                    context.get("className") + "." + subroutineDec.getSubroutineName(),
                    symbolTable.getVarCount()));
            if ("method".equals(type)) {
                // set this
                vmInstructions.add("push argument 0");
                vmInstructions.add("pop pointer 0");
            } else if ("constructor".equals(type)) {
                // set this to allocated space
                vmInstructions.add(String.format("push constant %d", symbolTable.getFieldCount()));
                vmInstructions.add("call Memory.alloc 1");
                vmInstructions.add("pop pointer 0");
            }
        } else if (stack.peekLast() instanceof IfStatement) {
            IfStatement ifStatement = (IfStatement) stack.peekLast();
            if (ifStatement.getTrueStatements() == null) {
                ifStatement.setTrueStatements(statements);
            } else {
                ifStatement.setFalseStatements(statements);
            }
        } else if (stack.peekLast() instanceof WhileStatement) {
            ((WhileStatement) stack.peekLast()).setStatements(statements);
        }

        stack.addLast(statements);
    }

    @Override
    public void leaveStatements() {
        stack.removeLast();
    }

    @Override
    public void enterLetStatement() {
        LetStatement letStatement = new LetStatement();
        if (stack.peekLast() instanceof Statements) {
            ((Statements) stack.peekLast()).getStatementList()
                    .add(letStatement);
        }
        stack.addLast(letStatement);
    }

    @Override
    public void leaveLetStatement() {
        LetStatement letStatement = (LetStatement) stack.peekLast();
        stack.removeLast();
    }

    @Override
    public void enterIfStatement() {
        IfStatement ifStatement = new IfStatement();
        if (stack.peekLast() instanceof Statements) {
            ((Statements) stack.peekLast()).getStatementList()
                    .add(ifStatement);
        }
        stack.addLast(ifStatement);
    }

    @Override
    public void leaveIfStatement() {
        stack.removeLast();
    }

    @Override
    public void enterWhileStatement() {
        WhileStatement whileStatement = new WhileStatement();
        if (stack.peekLast() instanceof Statements) {
            ((Statements) stack.peekLast()).getStatementList()
                    .add(whileStatement);
        }
        stack.addLast(whileStatement);
    }

    @Override
    public void leaveWhileStatement() {
        stack.removeLast();
    }

    @Override
    public void enterDoStatement() {
        DoStatement doStatement = new DoStatement();
        if (stack.peekLast() instanceof Statements) {
            ((Statements) stack.peekLast()).getStatementList()
                    .add(doStatement);
        }
        stack.addLast(doStatement);
    }

    @Override
    public void leaveDoStatement() {
        stack.removeLast();
    }

    @Override
    public void enterReturnStatement() {
        ReturnStatement returnStatement = new ReturnStatement();
        if (stack.peekLast() instanceof Statements) {
            ((Statements) stack.peekLast()).getStatementList()
                    .add(returnStatement);
        }
        stack.addLast(returnStatement);
    }

    @Override
    public void leaveReturnStatement() {
        stack.removeLast();
    }

    @Override
    public void enterExpressionList() {
        SubroutineCall subroutineCall = (SubroutineCall) stack.peekLast();
        ExpressionList expressionList = new ExpressionList();
        subroutineCall.setExpressionList(expressionList);
        stack.addLast(expressionList);
    }

    @Override
    public void leaveExpressionList() {
        stack.removeLast();
    }

    @Override
    public void enterExpression() {
        Expression expression = new Expression();
        Node parent = stack.peekLast();
        if (parent instanceof Term) {
            Term term = (Term) parent;
            if (term.getVarName() != null) {
                term.setArrayAccessExpression(expression);
            } else {
                term.setInParenExpression(expression);
            }
        } else if (parent instanceof ExpressionList) {
            ((ExpressionList) parent).getExpressions().add(expression);
        } else if (parent instanceof LetStatement) {
            LetStatement letStatement = (LetStatement) parent;
            if (letStatement.isHasBrackets() && letStatement.getArrayAccessExpression() == null) {
                letStatement.setArrayAccessExpression(expression);
            } else {
                letStatement.setRightExpression(expression);
            }
        } else if (parent instanceof IfStatement) {
            ((IfStatement) parent).setPredicate(expression);
        } else if (parent instanceof WhileStatement) {
            ((WhileStatement) parent).setPredicate(expression);
        } else if (parent instanceof ReturnStatement) {
            ((ReturnStatement) parent).setReturnValue(expression);
        }
        stack.addLast(expression);
    }

    @Override
    public void leaveExpression() {
        stack.removeLast();
    }

    @Override
    public void enterTerm() {
        Node parent = stack.peekLast();
        Term term = new Term();
        if (parent instanceof Expression) {
            Expression expression = (Expression) parent;
            Term fstTerm = expression.getFstTerm();
            if (fstTerm == null) {
                expression.setFstTerm(term);
            } else {
                List<Pair<Op, Term>> restTerms = expression.getRestTerms();
                Pair<Op, Term> pair = restTerms.get(restTerms.size() - 1);
                Op op = pair.getLeft();
                restTerms.remove(restTerms.size() - 1);
                restTerms.add(Pair.of(op, term));
            }
        } else if (parent instanceof Term) {
            Term parentTerm = (Term) parent;
            Pair<UnaryOp, Term> pair = parentTerm.getUnaryOpAndTerm();
            UnaryOp unaryOp = pair.getLeft();
            parentTerm.setUnaryOpAndTerm(Pair.of(unaryOp, term));
        }
        stack.addLast(term);
    }

    @Override
    public void leaveTerm() {
        stack.removeLast();
    }

    @Override
    public void enterSubroutineCall() {
        Node parent = stack.peekLast();
        SubroutineCall subroutineCall = new SubroutineCall();
        if (parent instanceof Term) {
            ((Term) parent).setSubroutineCall(subroutineCall);
        } else if (parent instanceof DoStatement) {
            ((DoStatement) parent).setSubroutineCall(subroutineCall);
        }
        stack.addLast(subroutineCall);
    }

    @Override
    public void leaveSubroutineCall() {
        stack.removeLast();
    }

    @Override
    public void visitOp(Symbol token) {
        Expression expression = (Expression) stack.peekLast();
        expression.getRestTerms().add(Pair.of(new Op(token.toString()), null));
    }

    @Override
    public void visitUnaryOp(Symbol token) {
        Term term = (Term) stack.peekLast();
        term.setUnaryOpAndTerm(Pair.of(new UnaryOp(token.toString()), null));
    }

    @Override
    public void visitKeywordConstant(Keyword token) {
        Term term = (Term) stack.peekLast();
        term.setKeywordConstant(token.toString());
    }

    @Override
    public void visitKeyword(Keyword token) {
        if (stack.peekLast() instanceof ClassVarDec) {
            ((ClassVarDec) stack.peekLast()).setScope(token.toString());
        } else if (stack.peekLast() instanceof SubroutineDec) {
            if ("void".equals(token.toString())) {
                ((SubroutineDec) stack.peekLast()).setReturnType("void");
            } else {
                ((SubroutineDec) stack.peekLast()).setType(token.toString());
            }
        }
    }

    @Override
    public void visitSymbol(Symbol token) {
        Node parent = stack.peekLast();
        if (parent instanceof LetStatement) {
            if ("[".equals(token.toString())) {
                ((LetStatement) parent).setHasBrackets(true);
            }
        }
    }

    @Override
    public void visitIntegerConstant(IntegerConstant token) {
        ((Term)stack.peekLast()).setIntegerConstant(token.getIntValue());
    }

    @Override
    public void visitStringConstant(StringConstant token) {
        ((Term)stack.peekLast()).setStringConstant(token.toString());
    }

    @Override
    public void visitClassOrVarName(Identifier identifier) {
        SubroutineCall parent = (SubroutineCall)stack.peekLast();
        parent.setClassOrVarName(identifier.toString());
    }
}
