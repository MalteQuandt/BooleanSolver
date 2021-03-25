package com.malte.boolsolver;

import java.util.HashMap;

public enum TokenType {
    AND,
    OR,
    NOT,
    VARIABLE,
    LITERAL,
    LPAREN,
    RPAREN;

    public static HashMap<TokenType, Boolean> getBinaryOp() {
        return binaryOp;
    }

    public static void setBinaryOp(HashMap<TokenType, Boolean> binaryOp) {
        TokenType.binaryOp = binaryOp;
    }

    public Boolean isOperator() {
        if (this == AND || this == OR || this == NOT) return true;
        else return false;
    }

    public Boolean isRightParen() {
        return this == RPAREN;
    }

    public Boolean isLeftParen() {
        return this == LPAREN;
    }

    public Boolean isParen() {
        return isLeftParen() || isRightParen();
    }

    public Boolean equals(TokenType type) {
        return type == this;
    }

    private static HashMap<TokenType, Integer> precedence = new HashMap<>() {{
        put(AND, 1);
        put(OR, 2);
        put(NOT, 0);
    }};
    private static HashMap<TokenType, Boolean> rightAssociative = new HashMap<>() {{
        put(AND, false);
        put(OR, false);
        put(NOT, true);
    }};
    private static HashMap<TokenType, Boolean> binaryOp = new HashMap<>() {{
        put(AND, true);
        put(OR, true);
        put(NOT, false);
    }};

    public Boolean isBinaryOperator() {
        return TokenType.getBinaryOp().containsKey(this);
    }

    public Boolean isUnaryOperator() {
        return !isBinaryOperator();
    }

    public Boolean isRightAssociative() {
        return rightAssociative.get(this);
    }

    public int getPrecedence() {
        return precedence.get(this);
    }

    public Boolean isValue() {
        return this == LITERAL || this == VARIABLE;
    }
}
