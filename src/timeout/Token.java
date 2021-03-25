package timeout;

import com.malte.boolsolver.TokenType;

class Token {
    private TokenType type;
    private String value;
    private Integer position;
    public Token(TokenType type, String value, Integer position) {
        setType(type);
        setValue(value);
        setPosition(position);
    }
    public String getValue() {
        return this.value;
    }
    private void setType(TokenType type) {
        this.type = type;
    }
    public TokenType getType() {
        return this.type;
    }
    private void setValue(String value) {
        this.value = value;
    }
    public Integer getPosition() {
        return position;
    }
    private void setPosition(Integer position) {
        this.position = position;
    }
    public Integer comparePrecedence(Token tok) {
        if(this.getType().getPrecedence() > tok.getType().getPrecedence()) {
            return 1;
        } else {
            if(this.getType().getPrecedence() == tok.getType().getPrecedence()) {
                return 0;
            } else {
                return -1;
            }
        }
    }
}