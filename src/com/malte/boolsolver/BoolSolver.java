package com.malte.boolsolver;

import java.io.*;
import java.util.*;

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

    public BoolSolver(String expression, String filename) {
        init(expression);
        eval(expression, filename);
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
        if (exists) {
            getList().add(tempora);
            return skip - 1;
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
        if (position == (expression.length() - match.length())) {
            // A lookahead is simply not possible, as there are no further characters in the expression-string,
            // thus we simply return false here.
            return false;
        } else {
            if (match.equals(expression.substring(position, match.length() - 1))) {
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
        for (Token temp : getVariables()) {
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
        for (Token temp : getVariables()) {
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
        for (int i = variables - 1; i >= 0; i--) {
            int mask = 1 << i;
            result.append((number & mask) == 0 ? "0" : "1");
            for (int j = 0; j < (((OperandToken) getVariables().get(BoolSolver.oppositeInRange(0, variables - 1, i))).getValue()).length(); j++) {
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
        if (result == 1) {
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
        return (stop + start) - position;
    }

    private void eval() {
        if (tokenize()) System.exit(0);
        infixToPostfix();
        if (checkRPN()) System.exit(0);
        printTruthTable();
    }

    public void eval(String expression) {
        // Initialization phase:
        if (!(expression != null && expression.length() != 0)) {
            return;
        }
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
        } catch (IOException ioe) {
            System.out.println("The system could not write to " + name + ".tt");
        } finally {
            try {
                writer.close();
            } catch (Exception ex) {
            }
        }
        return temp;
    }

    /**
     * Print the tree in infix notation and add only the nessecarry parenthesis.
     *
     * @param root      root of the subtree
     * @param builder   the string we want to add to
     * @param makeParen if parenthesis need to be added to this subtree
     */
    private void printHelper(Node root, StringBuilder builder, boolean makeParen) {
        boolean righthigher = false, lefthigher = false;
        if (root != null) {
            // HERE: check if left/ right precedence is higher, lower:
            if (root.getLeft() != null && root.getLeft().getType().isOperator() && (root.getLeft().getType().getPrecedence() > root.getType().getPrecedence())) {
                lefthigher = true;
            }
            if (root.getRight() != null && root.getRight().getType().isOperator() && (root.getRight().getType().getPrecedence() > root.getType().getPrecedence())) {
                righthigher = true;
            }

            if (makeParen) builder.append("(");
            printHelper(root.getLeft(), builder, lefthigher);
            builder.append(root.getToken().getValue()).append(" ");
            printHelper(root.getRight(), builder, righthigher);
            if (makeParen) builder.append(")");
        }
    }

    public String printInfixTreeWP() {
        StringBuilder builder = new StringBuilder(32);
        ExpressionTree tree = postfixToExprTree();
        printHelper(tree.getRoot(), builder, false);
        return builder.toString();
    }

    // TODO: this right here:
    public String simplify(String expression) {
        return null;
    }

    public static int bitsSet(int number) {
        Integer bits = 0;
        while (number > 0) {
            bits += (number & 1);
            number >>= 1;
        }
        return bits;
    }

    public static int bitsSet(String number) {
        Integer bits = 0;
        for (int i = number.length() - 1; i >= 0; i--) {
            bits += number.charAt(i) == '1' ? 1 : 0;
        }

        return bits.intValue();
    }

    public static String compare(String implicant1, String implicant2) {
        int len = implicant1.length();
        int pos = 0;
        int differ = 0;
        for (int i = 0; i < len; i++) {
            if (implicant1.charAt(i) != implicant2.charAt(i)) {
                pos = i;
                differ++;
            }
        }
        if (differ == 1) {
            StringBuilder temp = new StringBuilder(implicant1);
            temp.setCharAt(pos, '#');
            return temp.toString();
        }
        return null;
    }

    /* https://inst.eecs.berkeley.edu/~cs282/sp02/readings/moses-simp.pdf
    https://www.erpelstolz.at/gateway/qmo.html
    https://www.allaboutcircuits.com/technical-articles/everything-about-the-quine-mccluskey-method/
    */
    public String simplify() {
        // First, check two cases that crash the algorithm:
        if(getVariables().size()==0) {
            // There are no variables, thus the simplified form is definitely just the evaluation:
            return Integer.toString(evalRPN(0));
        }else if(getVariables().size()==1) {
            // There is only 1 Variable, we can not use the conventional quine-mcclusky algorithm
            if(evalRPN(0)==1) {
                // The var is negated:
                return "!" + getVariables().get(0).getValue().toString();
            } else {
                // The var is not negated:
                return getVariables().get(0).getValue().toString();
            }
        }
        ArrayList<Term>[] minterms = new ArrayList[getVarmount() + 1];
        // Initialize the list with arrays of Terms
        for (int i = 0; i < getVarmount() + 1; i++) {
            minterms[i] = new ArrayList<Term>();
        }
        // Fill the list with the minterms:
        for (int i = 0; i < getMinterms().size(); i++) {
            Integer minterm = getMinterms().get(i);
            minterms[bitsSet(minterm)].add(new Term(minterm, getVarmount()));
        }
        System.out.println("Mccluskey...");

        ArrayList<Term>[] temp = minterms;
        // Perform mccluskey method until there is not a single thing to be done, after that
        // possibly use the quine method to find the prime implicants:
        for (int j = 0; temp != null; j++) {
            temp = mccStep(temp);
            if (arraysEqual(temp, minterms)) {
                break;

            }
            minterms = temp;
        }

        System.out.println("Quine...");
        return quine(minterms);
    }
    /**
     * Performs a single quine step, combining minterms into implicants to find the prime implicants.
     *
     * @return new arraylist, if there was a change, or null, if there was not a single change:
     */
    private ArrayList<Term>[] mccStep(ArrayList<Term>[] minterms) {
        ArrayList<Term>[] output = new ArrayList[minterms.length];
        Term temp = null;
        // Initialize the list:
        for (int i = 0; i < getVarmount() + 1; i++) {
            output[i] = new ArrayList<Term>();
        }
        long startTime = System.currentTimeMillis();
        for (int i = 1; i < minterms.length; i++) {
            // Compare elemets of i-1 with all elements of i+1;
            for (int j = 0; j < minterms[i - 1].size(); j++) {
                for (int k = 0; k < minterms[i].size(); k++) {
                    // If the items only differ by one bit, combine them and add to the new list:
                    if ((temp = minterms[i - 1].get(j).combine(minterms[i].get(k))) != null && !contains(output, temp)) {
                        // They combine, thus we get the new Term object back:
                        output[bitsSet(temp.getBinaryRepresentation())].add(temp);
                    }
                    // They do not combine, thus we get a null back:
                }
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Compare i-1 with i for each two abreast lists: " + (endTime - startTime));
        startTime = System.currentTimeMillis();
        // Check the items that are still marked as false:
        for (int i = 0; i < minterms.length; i++) {
            for (int j = 0; j < minterms[i].size(); j++) {
                temp = minterms[i].get(j);
                if (!temp.getWasAdded()) {
                    output[bitsSet(temp.getBinaryRepresentation())].add(temp);
                } else {
                    // There was a change, thus we set hadChange to true;
                }
            }
        }
        endTime = System.currentTimeMillis();
        System.out.println("Check the list if some of them have not been included " + (endTime - startTime));
        return output;
    }

    private ArrayList<Term>[] mccStepOptimized(ArrayList<Term>[] minterms) {
        return null;
    }
    // Here we use the quine method to find the dominant prime-implicants:
    private String quine(ArrayList<Term>[] terms) {
        Integer termcount = 0;
        Integer mintermpos = 0;
        Integer primepos = 0;
        Term temp = null;
        ArrayList<Term> templist = new ArrayList<Term>();

        for (int i = 0; i < terms.length; i++) {
            termcount += terms[i].size();
        }
        // 1 Dim: minterms,
        // 2 Dim: prime-implicants,
        boolean[][] quineField = new boolean[getMinterms().size()][termcount];

        for (int i = 0; i < terms.length; i++) {
            for (int j = 0; j < terms[i].size(); j++) {
                temp = terms[i].get(j);
                String[] split = temp.getRepresentedTerms().split(", ");
                for (String inner : split) {
                    mintermpos = getPosition(Integer.parseInt(inner));
                    if(mintermpos == -1) {
                        continue;
                    }
                    quineField[mintermpos][primepos] = true;
                    if(!templist.contains(temp)) {
                        templist.add(temp);
                    }
                }
                primepos++;
            }
        }
        ArrayList<Integer> coreprimeimplicantpos = new ArrayList<>();
        // Check for core prime-implicants:
        for (int i = 0; i < quineField.length; i++) {
            if ((primepos = isCorePrimeImplicant(quineField[i])) != -1) {
                // It is a core prime-implicant
                if (!coreprimeimplicantpos.contains(primepos)) {
                    coreprimeimplicantpos.add(primepos);
                }
            }
        }
        StringBuilder builder = new StringBuilder();
        for(int i = 0 ; i < templist.size() ; i++) {
            builder.append(varBackToString(templist.get(i).binaryRepresentation));
            if(i != templist.size() -1) {
                builder.append(" + ");
            }
        }
        return builder.toString();
    }
    //
    private String varBackToString(String chewedvars) {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < getVariables().size();i++) {
            if(chewedvars.charAt(i) == '#') {
                continue;
            }
            if(chewedvars.charAt(i) == '0') {
                builder.append("!");
            }
            builder.append(((OperandToken)getVariables().get(i)).getValue());
            if(i!=(getVariables().size()-1)) {
                builder.append(" * ");
            }
        }
        if(builder.charAt(builder.length()-2) == '*') {
            builder.setLength(builder.length() - 3);
        }
        return builder.toString();
    }

    private Integer getPosition(Integer number) {
        Integer temp = -1;
        Integer length = getMinterms().size();
        for (int i = 0; i < length; i++) {
            temp = number == getMinterms().get(i) ? i : temp;
        }
        return temp;
    }

    private Boolean contains(ArrayList<Term>[] terms, Term term) {
        for (int i = 0; i < terms.length; i++) {
            for (int j = 0; j < terms[i].size(); j++) {
                Term temp = terms[i].get(j);
                if (temp.binaryRepresentationEquals(term)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Boolean arraysEqual(ArrayList<Term>[] termsA, ArrayList<Term>[] termsB) {
        if (termsA.length != termsB.length) {
            return false;
        } else {
            for (int i = 0; i < termsA.length; i++) {
                if (termsA[i].size() != termsB[i].size()) {
                    return false;
                }
                for (int j = 0; j < termsA[i].size(); j++) {
                    if (termsA[i].get(j) != termsB[i].get(j)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private Integer isCorePrimeImplicant(boolean[] dimension) {
        Integer count = 0;
        Integer position = 0;
        for (int i = 0; i < dimension.length; i++) {
            if (dimension[i] == true) {
                position = i;
                count++;
            }
        }
        if (count == 1) {
            return position;
        }
        return -1;

    }

    class Term {
        private String binaryRepresentation;
        private String representedTerms;
        private Boolean wasAdded;
        private Integer setBits;

        public Term(String binaryRepresentation, String representedTerms) {
            setBinaryRepresentation(binaryRepresentation);
            setRepresentedTerms(representedTerms);
            setWasAdded(false);
        }

        public Term(Integer variableNumber, Integer length) {
            this(BoolSolver.intToString(variableNumber, length), Integer.toString(variableNumber));

        }

        @Override
        public String toString() {
            return getBinaryRepresentation();
        }


        public String getBinaryRepresentation() {
            return binaryRepresentation;
        }

        public void setBinaryRepresentation(String binaryRepresentation) {
            this.binaryRepresentation = binaryRepresentation;
        }

        public String getRepresentedTerms() {
            return representedTerms;
        }

        public void setRepresentedTerms(String representedTerms) {
            this.representedTerms = representedTerms;
        }

        public Boolean getWasAdded() {
            return wasAdded;
        }

        public void setWasAdded(Boolean wasAdded) {
            this.wasAdded = wasAdded;
        }

        public String combineIdentifier(Term term) {
            return getRepresentedTerms() + ", " + term.getRepresentedTerms();
        }

        public Term combine(Term term) {
            String temp = null;
            if (null != (temp = compare(getBinaryRepresentation(), term.getBinaryRepresentation()))) {
                this.setWasAdded(true);
                term.setWasAdded(true);
                return new Term(temp, combineIdentifier(term));
            }
            return null;
        }

        public Boolean equalRepresentation(Term term) {
            return this.getBinaryRepresentation().equals(term.getBinaryRepresentation());
        }

        public Boolean binaryRepresentationEquals(Term term) {
            return getBinaryRepresentation().equals(term.getBinaryRepresentation());
        }

        public Boolean contains(Term term) {
            return getRepresentedTerms().contains(term.getRepresentedTerms());
        }

        public Integer getSetBits() {
            return setBits;
        }

        public void setSetBits(Integer setBits) {
            this.setBits = setBits;
        }
    }
}

