package com.malte.boolsolver;

public class Main {
    // Solve an boolean expression:
    public static void main(String[] args) {
        String expression = "(X1*!X2)|(!X1*X2)|(X4&!X4)&(X3&!X3)";
        expression = "!A+!A";
        // Accepts a simple expression with the 3 simple logical operators AND (&, *), OR (|, +) and NOT (!).
        BoolSolver solver = new BoolSolver();
        solver.eval(expression);
        System.out.println("Original:");
        System.out.println(expression);
        System.out.println("Simplified term:");
        System.out.println(solver.simplify());
    }
}
