package com.malte.boolsolver;

import java.util.ArrayList;
import java.util.Stack;

final class Solver {
    // List of tokens:
    private ArrayList<Token> tokenlist;
    private ArrayList<Token> rpn;
    // How many variables are in the list:
    private Integer varmount;
    // Points to the token:
    private ArrayList<Token> variables;
    // The original expression is stored in this:
    private String expression;

    // Truth table (only the output column)
    public Solver(String expression) {
        setExpression(expression);
        setVarmount(0);
        setVariables(new ArrayList<>());
    }

    public void printList() {
        for (Token token : tokenlist) {
            System.out.println(token.getValue() + " : " + token.getType() + " : At: " + token.getPosition());
        }
    }

    public ArrayList<Token> getList() {
        return this.tokenlist;
    }

    private String getExpression() {
        return this.expression;
    }

    private void setExpression(String expression) {
        this.expression = expression;
    }

    private void setList(ArrayList<Token> tokens) {
        this.tokenlist = tokens;
    }

    //Consume a variable:
    public int consumeVar(String expression, int position) {
        Integer skip = position;
        Integer varPos = this.getVarmount();
        Token tempora = null;
        for (Integer itr = position; (expression.length() != itr) && (Character.isLetter(expression.charAt(itr)) || (expression.charAt(itr) == '_') || (Character.isDigit(expression.charAt(itr)))); itr++) {
            skip++;
        }
        String temp = expression.substring(position, skip);
        for (Token tok : getList()) {
            if (tok.getValue().equals(temp)) {
                varPos = ((OperandToken) tok).getVariablePosition();
                getList().add(tempora = new OperandToken(TokenType.VARIABLE, temp, position, varPos));
                getVariables().add(tempora);
                return skip - 1;
            }
        }
        getList().add(new OperandToken(TokenType.VARIABLE, temp, position, varPos));
        // Increment varmount:
        setVarmount(getVarmount() + 1);
        return skip - 1;
    }

    // Consume a literal:
    public void consumeLit(Character digit, TokenType type, Integer position) {
        getList().add(new OperandToken(type, Character.toString(digit), position));
    }

    public void consume(Character digit, TokenType type, Integer position) {
        tokenlist.add(new OperatorToken(type, Character.toString(digit), position));
    }

    public void tokenize() {
        setList(new ArrayList<Token>(32));
        int length = expression.length();
        Boolean error = false;
        for (int i = 0; i < length; i++) {
            switch (expression.charAt(i)) {
                case '1':
                case '0':
                    // Logical literal '1'
                    consumeLit(expression.charAt(i), TokenType.LITERAL, i);
                    break;
                case '&':
                    consume(expression.charAt(i), TokenType.AND, i);
                    break;
                case '|':
                    consume(expression.charAt(i), TokenType.OR, i);
                    break;
                case '!':
                    consume(expression.charAt(i), TokenType.NOT, i);
                    break;
                case '(':
                    consume(expression.charAt(i), TokenType.LPAREN, i);
                    break;
                case ')':
                    consume(expression.charAt(i), TokenType.RPAREN, i);
                    break;
                case ' ':
                    // Skip this character as it is just a delimiter.
                    continue;
                case '_':
                    i = consumeVar(expression, i);
                    break;
                default:
                    if ((Character.isLetter(expression.charAt(i)))) {
                        // Check if the current character is a character from the alphabet:
                        i = consumeVar(expression, i);
                    } else {
                        // This is not a valid token!
                        String errormsg = new String("Illegal Token: " + expression.charAt(i) + " at: " + i);
                        System.out.println(errormsg);
                        error = true;
                    }
                    break;
            }
        }
        if(error) System.exit(0);
    }

    private void infixToPostfix() {
        ArrayList<Token> out = new ArrayList<>();
        Stack<Token> stack = new Stack<>();

        if(expression.length() == 0) return;

        for (Token tok : tokenlist) {

            if (tok.getType().isOperator()) {
                // operator:
                while (!stack.isEmpty() && (!stack.peek().getType().isParen() && ((OperatorToken) tok).isHigherPrecedenceThan((OperatorToken) stack.peek()))) {
                    out.add(stack.pop());
                }
                stack.push(tok);
            } else if (tok.getType().isLeftParen()) {
                // Left parenthesis:
                stack.push(tok);
            } else if (tok.getType().isRightParen()) {
                // Right parenthesis:
                while (!stack.peek().getType().equals(TokenType.LPAREN)) {
                    out.add(stack.pop());
                }
                stack.pop();
            } else {
                // Constant/Variable:
                out.add(tok);
            }
        }
        while (!stack.isEmpty()) {
            out.add(stack.pop());
        }
        setRpn(out);
    }

    public TreeNode postfixToExprTree() {
        TreeNode tree = new TreeNode(null);
        // Converts a simple RPN into an expression tree (Binary tree)
        for (int i = getRpn().size()-1; i != 0; i--) {
            // Iterate from the end of the rpn to the start
            tree.add(getRpn().get(i));
        }
        return tree;
    }

    public ArrayList<Token> getRpn() {
        return rpn;
    }

    private void setRpn(ArrayList<Token> rpn) {
        this.rpn = rpn;
    }

    public Integer getVarmount() {
        return varmount;
    }

    private void setVarmount(Integer varmount) {
        this.varmount = varmount;
    }

    public ArrayList<Token> getVariables() {
        return variables;
    }

    public void setVariables(ArrayList<Token> variables) {
        this.variables = variables;
    }

    public void printTruthTable() {
        Integer position = 0;
        // Iterate over the boolean combinations that the variables provide:
        for (int i = 0; i < Math.pow(2, this.getVarmount()); i++) {
            System.out.print("" + intToString(i, getVarmount()));
            // TODO: output the value utilizing the method "solveRPN"
            System.out.println(" : " + evalRPN(i));
        }
    }

    public void printTruthTableWn() {
        Integer position = 0;
        // Iterate over the boolean combinations that the variables provide:
        for (int i = 0; i < Math.pow(2, this.getVarmount()); i++) {
            System.out.print("" + intToString(i, getVarmount(), 3));
            // TODO: output the value utilizing the method "solveRPN"
            System.out.println(" : " + evalRPN(i));
        }
    }

    private static String intToString(int number, int variables) {
        StringBuilder result = new StringBuilder();

        for (int i = variables - 1; i >= 0; i--) {
            int mask = 1 << i;
            result.append((number & mask) == 0 ? "0" : "1");
        }
        return result.toString();
    }

    private static String intToString(int number, int variables, int spaces) {
        StringBuilder result = new StringBuilder();

        for (int i = variables - 1; i >= 0; i--) {
            int mask = 1 << i;
            result.append((number & mask) == 0 ? "0" : "1");
            for(int j = 0; j < spaces; j++) result.append(" ");
        }
        return result.toString();
    }

    private Integer evalRPN(Integer position) {
        if(getRpn() == null) return -1;
        String ttvalues = intToString(position, this.getVarmount());
        Stack<Integer> operandStack = new Stack<>();
        for (Token itr : getRpn()) {
            switch (itr.getType()) {
                case VARIABLE:
                    // I want to fucking die
                    operandStack.push(ttvalues.charAt(((OperandToken) itr).getVariablePosition()) == 48 ? 0 : 1);
                    break;
                case LITERAL:
                    operandStack.push(Integer.parseInt(((OperandToken) itr).getValue()));
                    break;
                case AND:
                    operandStack.push(operandStack.pop().intValue() & operandStack.pop().intValue());
                    break;
                case OR:
                    operandStack.push(operandStack.pop().intValue() | operandStack.pop().intValue());
                    break;
                case NOT:
                    operandStack.push(operandStack.pop().intValue() == 1 ? 0 : 1);
                    break;
            }
        }
        return operandStack.pop();
    }

    private Boolean checkRPN() {
        Boolean error = false;
        Integer elemOnStack = 0;
        if(getRpn() == null) return false;
        for (Token tok : getRpn()) {
            if (tok.getType().isBinaryOperator()) {
                // Is binary operator:
                if (elemOnStack < 2) {
                    // There are not enough items on the stack to do the operation:
                    error = true;
                    System.out.println("Expression error: Not enough operands for binary operation at " + tok.getPosition());
                }
                elemOnStack--;
            } else if (tok.getType().isValue()) {
                // Is value like literal/variable:
                elemOnStack++;
            } else {
                // Is unary operator:
                if (elemOnStack == 0) {
                    // There is not an element on the stack which this operation can use:
                    error = true;
                    System.out.println("Expression error: Not enough operands for unary operation at " + tok.getPosition());
                }
            }

        }
        return error;
    }

    public void eval() {
        tokenize();
        infixToPostfix();
        if (checkRPN()) System.exit(0);
        printTruthTable();
    }
}

