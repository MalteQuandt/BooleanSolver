package com.malte.boolsolver;

import java.io.*;
import java.nio.Buffer;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

final class BoolSolver {
    // List of tokens:
    private ArrayList<Token> tokenlist;
    private ArrayList<Token> rpn;
    private ArrayList<Integer> minterms;
    private ArrayList<Integer> maxterms;
    // How many variables are in the list:
    private Integer varmount;
    // Points to the tokens with the variables in them:
    private ArrayList<Token> variables;
    // The original expression is stored in this:
    private String expression;

    // Truth table (only the output column)
    public BoolSolver(String expression) {
        init(expression);
        eval();
    }
    public BoolSolver() {
        // Literally nothing to be done here!
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
    private int consumeVar(String expression, int position) {
        Integer skip = position;
        Integer varPos = this.getVarmount();
        Token tempora = null;
        Boolean exists = false;
        for (Integer itr = position; (expression.length() != itr) && (Character.isLetter(expression.charAt(itr)) || (expression.charAt(itr) == '_') || (Character.isDigit(expression.charAt(itr)))); itr++) {
            skip++;
        }
        String temp = expression.substring(position, skip);
        for (Token tok : getList()) {
            if (temp.equals(tok.getValue())) {
                varPos = ((OperandToken) tok).getVariablePosition();

                exists = true;
            }
        }
        tempora = new OperandToken(TokenType.VARIABLE, temp, position, varPos);
        if(exists) {
            getList().add(tempora);
            return skip -1;
        } else {
            // The variable is not in the list yet, thus we add it
            getVariables().add(tempora);
        }
        getList().add(tempora);
        // Increment varmount:
        setVarmount(getVarmount() + 1);
        return skip - 1;
    }

    // Consume a literal:
    private void consumeLit(Character digit, TokenType type, Integer position) {
        getList().add(new OperandToken(type, Character.toString(digit), position));
    }

    private Boolean consumeLong(String expression, String match, TokenType type, Integer position) {
        // Use a simple 1-character lookahead:
        if(position == (expression.length()-match.length())) {
            // A lookahead is simply not possible, as there are no further characters in the expression-string,
            // thus we simply return false here.
            return false;
        } else {
            if(match.equals(expression.substring(position, match.length()-1))) {
                // It does match, thus we can just create the token and get one with our day:
                // TODO: if i ever plan on fully implementing this method, then i have to implement the token
                // creation process / token insertion process here and insert a new tokentype into the tokentype
                // enum

                return true;
            } else {
                // It does not match:
                return false;
            }
        }
    }

    private void consume(Character digit, TokenType type, Integer position) {
        tokenlist.add(new OperatorToken(type, Character.toString(digit), position));
    }

    private Boolean tokenize() {
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
                case '*':
                    consume(expression.charAt(i), TokenType.AND, i);
                    break;
                case '|':
                case '+':
                    consume(expression.charAt(i), TokenType.OR, i);
                    break;
                case '!':
                case '-':
                    consume(expression.charAt(i), TokenType.NOT, i);
                    break;
                case '(':
                case '[':
                case '{':
                    consume(expression.charAt(i), TokenType.LPAREN, i);
                    break;
                case ')':
                case '}':
                case ']':
                    consume(expression.charAt(i), TokenType.RPAREN, i);
                    break;
                case ' ':
                case '\r':
                case '\n':
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
        return error;
    }

    /**
     * Simple shunting-yard implementation, which converts a boolean expression in infix-notation
     * into reverse polish notation, or postfix.
     */
    private void infixToPostfix() {
        ArrayList<Token> out = new ArrayList<>();
        Stack<Token> stack = new Stack<>();

        if (expression.length() == 0) return;

        for (Token tok : getList()) {
            if (tok.getType().isBinaryOperator()) {
                // operator:
                while (!stack.isEmpty() && (!stack.peek().getType().isParen() && ((OperatorToken) tok).isHigherPrecedenceThan((OperatorToken) stack.peek()))) {
                    Token temp = stack.pop();
                    out.add(temp);
                }
                stack.push(tok);
            } else if (tok.getType().isUnaryOperator()) {
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

    public ExpressionTree postfixToExprTree() {
        if (this.getRpn() == null) {
            return null;
        }
        ExpressionTree tree = new ExpressionTree();
        for (int i = this.getRpn().size() - 1; i >= 0; i--) {
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

    public ArrayList<Integer> getMinterms() {
        return minterms;
    }

    public void setMinterms(ArrayList<Integer> minterms) {
        this.minterms = minterms;
    }

    public ArrayList<Integer> getMaxterms() {
        return maxterms;
    }

    public void setMaxterms(ArrayList<Integer> maxterms) {
        this.maxterms = maxterms;
    }

    public ArrayList<Token> getVariables() {
        return variables;
    }

    private void setVariables(ArrayList<Token> variables) {
        this.variables = variables;
    }

    public void printTruthTable() {
        Integer position = 0;
        Integer varPos = 0;
        for(Token temp : getVariables()) {
            System.out.print(temp.getValue() + " ");
        }
        System.out.println(" : %");
        // Iterate over the boolean combinations that the variables provide:
        for (int i = 0; i < Math.pow(2, this.getVarmount()); i++) {
            System.out.print("" + this.varString(i, getVarmount()));
            System.out.println(" : " + evalRPN(i));
        }
    }

    public String truthTableToString() {
        StringBuilder sb = new StringBuilder(16);
        Integer position = 0;
        Integer varPos = 0;
        for(Token temp : getVariables()) {
            sb.append(temp.getValue() + " ");
        }
        sb.append(" : %\n");
        // Iterate over the boolean combinations that the variables provide:
        for (int i = 0; i < Math.pow(2, this.getVarmount()); i++) {
            sb.append("" + this.varString(i, getVarmount()));
            sb.append(" : " + evalRPN(i) + "\n");
        }
        return sb.toString();
    }

    public static String intToString(int number, int variables) {
        StringBuilder result = new StringBuilder();

        for (int i = variables - 1; i >= 0; i--) {
            int mask = 1 << i;
            result.append((number & mask) == 0 ? "0" : "1");
        }
        return result.toString();
    }

    private String varString(int number, int variables) {
        StringBuilder result = new StringBuilder();
        for (int i = variables-1; i >=0; i--) {
            int mask = 1 << i;
            result.append((number & mask) == 0 ? "0" : "1");
            for(int j = 0; j < (((OperandToken)getVariables().get(BoolSolver.oppositeInRange(0, variables-1, i))).getValue()).length(); j++) {
                result.append(" ");
            }
        }
        return result.toString();
    }

    private Integer solveTree(Node tree, String positionstring) {
        if (tree != null) {
            switch (tree.getType()) {
                case VARIABLE:
                    return (positionstring.charAt(((OperandToken) tree.getToken()).getVariablePosition())) == 48 ? 0 : 1;
                case LITERAL:
                    return Integer.parseInt(((OperandToken) tree.getToken()).getValue());
                case AND:
                    return solveTree(tree.getRight(), positionstring) & solveTree(tree.getLeft(), positionstring);
                case OR:
                    return solveTree(tree.getRight(), positionstring) | solveTree(tree.getLeft(), positionstring);
                case NOT:
                    return solveTree(tree.getRight(), positionstring);
            }
        }
        return null;
    }

    private Integer evalRPN(Integer position) {
        if (getRpn() == null) return -1;
        Integer result = -1;
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
        result = operandStack.pop();
        if(result==1) {
            getMinterms().add(position);
        } else {
            getMaxterms().add(position);
        }
        return result;
    }

    private Boolean checkRPN() {
        Boolean error = false;
        Integer elemOnStack = 0;
        if (getRpn() == null) return false;
        for (Token tok : getRpn()) {
            if (tok.getType().isBinaryOperator()) {
                // Is binary operator:
                // Error at this token:
                if (elemOnStack < 2) {
                    // There are not enough items on the stack to do the operation:
                    error = true;
                    System.out.println("Expression error: Not enough operands for binary operation at " + tok.getPosition());
                    System.out.println("Stack only has " + elemOnStack + " element(s)");
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
                    System.out.println("Stack only has " + elemOnStack + " element(s)");
                }
            }

        }
        return error;
    }

    private static int oppositeInRange(Integer start, Integer stop, Integer position) {
        return (stop+start)-position;
    }

    private void eval() {
        if (tokenize()) System.exit(0);
        infixToPostfix();
        if (checkRPN()) System.exit(0);
        printTruthTable();
    }
    public void eval(String expression) {
        // Initialization phase:
        init(expression);
        // evalutate it:
        eval();
    }

    private void init(String expression) {
        setExpression(expression);
        setVarmount(0);
        setVariables(new ArrayList<Token>());
        setMaxterms(new ArrayList<Integer>());
        setMinterms(new ArrayList<Integer>());
    }
    public String eval(String expression, String name) {
        init(expression);
        if (tokenize()) System.exit(0);
        infixToPostfix();
        if (checkRPN()) System.exit(0);
        String temp = truthTableToString();
        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(name + ".tt"), "utf-8"));
            writer.write(temp);
        } catch(IOException ioe) {
            System.out.println("The system could not write to " + name + ".tt");
        } finally {
            try {
                writer.close();
            } catch(Exception ex) {
            }
        }
        return temp;
    }
    public ArrayList<Token> printInfixTreeWP() {
        return null;
    }

    public String simplify(String expression) {
        return null;
    }
    public String simplify() {
        StringBuilder expression = new StringBuilder(16);
        if(getVariables().size()==0) {
            // Only literals, thus we just calculate the value and spit it out:
        } else {
            // There are variables, thus we use quine-mcclusky to solve this:

        }
        return expression.toString();
    }
}

