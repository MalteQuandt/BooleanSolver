package com.malte.boolsolver;

public class TreeNode {
    private TreeNode left;
    private TreeNode right;
    private Token value;

    public TreeNode(TreeNode left, TreeNode right, Token value) {
        setRight(right);
        setLeft(left);
        setValue(value);
    }

    public TreeNode(Token value) {
        setRight(null);
        setLeft(null);
        setValue(value);
    }

    public TokenType getType() {
        if (value != null) return value.getType();
        else return null;
    }

    public TreeNode getLeft() {
        return left;
    }

    public void setLeft(TreeNode left) {
        this.left = left;
    }

    public TreeNode getRight() {
        return right;
    }

    public void setRight(TreeNode right) {
        this.right = right;
    }

    public Token getValue() {
        return value;
    }

    public void setValue(Token value) {
        this.value = value;
    }

    /**
     * Recursively add a token to the tree:
     *
     * @param value the token to be added
     * @return predicate indicating whether the adding was successful:
     */
    public Boolean add(Token value) {
    return false;
    }
    public void print() {
        if(this.getLeft() != null) {
            getLeft().print();
        }
        if(this.getLeft() != null && this.getRight() != null) System.out.println(value.getValue());
        if(this.getRight() != null) {
            getRight().print();
        }
    }

    public Boolean isBinaryOperation() {
        return this.getValue().getType().isBinaryOperator();
    }
    public Boolean isUnaryOperation() {
        return this.getValue().getType().isUnaryOperator();
    }
    public Boolean isTerminal() {
        return this.getValue().getType().isValue();
    }
}