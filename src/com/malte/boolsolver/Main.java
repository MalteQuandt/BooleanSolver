package com.malte.boolsolver;

public class Main {
    // Solve an boolean expression:
    public static void main(String[] args) {
        String expression = "A+(B*C)";
        // Accepts a simple expression with the 3 simple logical operators AND (&, *), OR (|, +) and NOT (!).
        BoolSolver solver = new BoolSolver();
        solver.eval(expression);

        System.out.println(solver.simplify());

    }
}
