package com.malte.boolsolver;

import java.util.ArrayList;

public class Main {
    // Solve an boolean expression:
    public static void main(String[] args) {
        String expression = "a&a|b";
        // Accepts a simple expression with the 3 simple logical expressions AND (&),
        Solver solver = new Solver(expression);
        solver.solve();
    }
}
