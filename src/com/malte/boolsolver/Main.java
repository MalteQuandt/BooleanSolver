package com.malte.boolsolver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    // Solve an boolean expression:
    public static void main(String[] args) throws IOException {
        String expression = "(X1*!X2)|(!X1*X2)|(X4&!X4)&(X3&!X3)";

        InputStreamReader ir = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(ir);

        // Read user input
        String s = br.readLine();
        System.out.println("Please input a boolean expression: ");
        if (!s.isEmpty()) {
            expression = s;
            System.out.println(expression);
        }

        // Accepts a simple expression with the 3 simple logical operators AND (&, *), OR (|, +) and NOT (!).
        BoolSolver solver = new BoolSolver();
        solver.eval(expression);
        System.out.println("Original:");
        System.out.println(expression);
        System.out.println("Simplified term:");
//        System.out.println(solver.simplify());
    }
}
