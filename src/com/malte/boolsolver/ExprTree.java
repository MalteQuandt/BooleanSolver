package com.malte.boolsolver;

public class ExprTree {
    private TreeNode root;

    ExprTree(TreeNode root) {
        setRoot(root);
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }
}
