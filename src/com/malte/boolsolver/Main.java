package com.malte.boolsolver;

public class Main {
    // Solve an boolean expression:
    public static void main(String[] args) {
        String expression = "!!a+!!b&!x+!y&y&g&h";
        // Accepts a simple expression with the 3 simple logical operators AND (&, *), OR (|, +) and NOT (!).
        BoolSolver solver = new BoolSolver();
        solver.eval(expression, "theonlyobjectivetruth");
    }
}
