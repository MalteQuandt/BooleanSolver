package com.malte.boolsolver;

import java.util.ArrayList;

public class Main {
    // Solve an boolean expression:
    public static void main(String[] args) {
        String expression = "a&b|c&d|e&f";
        // Accepts a simple expression with the 3 simple logical operators AND (&), OR (|) and NOT (!).
        Solver solver = new Solver(expression);
        solver.eval();
        TreeNode tree = solver.postfixToExprTree();
    }
}
