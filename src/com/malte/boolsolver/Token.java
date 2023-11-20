package com.malte.boolsolver;

public interface Token<V> {
    TokenType getType();

    V getValue();

    Integer getPosition();

    Boolean equals(Token<?> token);

    String toString();
}

class OperandToken implements Token<String> {
    private TokenType type;
    private String value;
    private Integer position;
    private Integer variablePosition;

    public OperandToken(TokenType type, String value, Integer position) {
        setType(type);
        setValue(value);
        setPosition(position);
    }

    public OperandToken(TokenType type, String value, Integer position, Integer varPos) {
        this(type, value, position);
        setVariablePosition(varPos);
    }

    @Override
    public Boolean equals(Token<?> token) {
        return token instanceof OperandToken && token.getType() == getType() && token.getPosition().intValue() == getPosition().intValue() && getValue().equals(token.getValue());
    }

    @Override
    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return this.getValue() + " : " + this.getType() + " : " + this.getPosition();
    }

    public Integer getVariablePosition() {
        return variablePosition;
    }

    private void setVariablePosition(Integer variablePosition) {
        this.variablePosition = variablePosition;
    }
}

class OperatorToken implements Token<TokenType> {
    private TokenType type;
    private Integer position;

    public OperatorToken(TokenType type, String value, Integer position) {
        setType(type);
        setPosition(position);
    }

    @Override
    public Boolean equals(Token<?> token) {
        return token instanceof OperatorToken && token.getType() == getType() && token.getPosition().intValue() == getPosition().intValue() && token.getValue().equals(getValue());
    }

    @Override
    public TokenType getType() {
        return this.type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    @Override
    public TokenType getValue() {
        return this.type;
    }

    @Override
    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Boolean isHigherPrecedenceThan(OperatorToken token) {
        return this.getType().getPrecedence() >= token.getType().getPrecedence();
    }

    public String toString() {
        return this.getValue() + " : " + this.getType() + " : " + this.getPosition();
    }
}
