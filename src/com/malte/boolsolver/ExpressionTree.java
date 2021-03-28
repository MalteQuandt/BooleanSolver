package com.malte.boolsolver;

public class ExpressionTree {
    private Node root;

    public Node getRoot() {
        return root;
    }

    public void setRoot(Token tok) {
        this.root = new Node(tok);
    }

    /**
     * Outputs the tree in infix notation:
     * @param root of the (sub) tree
     */
    public void printIfx(Node root) {
        if (root != null) {
            printIfx(root.getLeft());
            System.out.println(root.getToken().toString());
            printIfx(root.getRight());
        }
    }

    /**
     * Outputs the tree as reverse polish notation:
     * @param root of the (sub) tree
     */
    public void printPfx(Node root) {
        if (root != null) {
            System.out.println(root.getToken().toString());
            printPfx(root.getLeft());
            printPfx(root.getRight());
        }
    }

    /**
     * Outputs the tree in prefix notation:
     * @param root of the (sub) tree
     */
    public void printPrfx(Node root) {
        if (root != null) {
            printPrfx(root.getLeft());
            printPrfx(root.getRight());
            System.out.println(root.getToken().toString());
        }
    }

    public Boolean add(Token tok) {
        if (this.getRoot() == null) {
            // No root, thus trying to access it would give a nasty null-pointer exception:
            setRoot(tok);
            return true;
        } else {
            // There is already a root, thus we can savely add to it:
            return this.getRoot().insert(tok);
        }
    }
    public static Boolean identical(Node first, Node second) {
        if(first == null && second == null) {
            return true;
        }
        if(first!= null && second != null) {
            Boolean equalTokens = first.getToken().equals(second.getToken());
            Boolean equalLeft = identical(first.getLeft(), second.getLeft());
            Boolean equalRight = identical(first.getRight(), second.getRight());
            return equalTokens && equalLeft && equalRight;
        }
        return false;
    }
}

class Node {

    private Node right;
    private Node left;
    private Token token;

    public Node(Token token) {
        setToken(token);
        setLeft(null);
        setRight(null);
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public TokenType getType() {
        return this.getToken().getType();
    }

    // It finally works!!!
    public Boolean insert(Token tok) {
        if(this.getType().isTerminal()) {
            return false;
        }
        // First: Insert right:
        if(this.getRight() == null) {
            this.setRight(new Node(tok));
            return true;
        } else {
            if(this.getRight().insert(tok)) {return true;}
        }
        // Second: insert left:
        if(this.getLeft() == null) {
            if(this.getType().isUnaryOperator()){return false;}
            this.setLeft(new Node(tok));
            return true;
        } else {
            if(this.getLeft().insert(tok)){return true;}
        }
        // Lastly: return false:
        return false;
    }
}
