package com.malte.boolsolver;

public class Main {
    // Solve an boolean expression:
    public static void main(String[] args) {
        String expression = "a+b+!c+(d*b)*!b*d|!d";
        // Accepts a simple expression with the 3 simple logical operators AND (&, *), OR (|, +) and NOT (!).
        BoolSolver solver = new BoolSolver();
        solver.eval(expression, "trash.tt");
        System.out.println("Original: \n");
        System.out.println(expression);
        System.out.println("Simplified term: \n");
        System.out.println(solver.simplify());
    }
}
