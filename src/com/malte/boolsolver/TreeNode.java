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
}