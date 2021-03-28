# How to use:

To use the solver in its current state, you declare a string which contains a valid boolean expression (EBNF not included, use your imagination!) which
is constructred with a combination of parenthesis, AND, OR and NOT operations and literals/variables.
Then you can create the object like so:

```java
BoolSolver identifier = new BoolSolver(stringExpression);
```

This should print a truth table to the console with the variables that were in the string from left to right being outputted in the truth table from right to left.

If you find any expression that do not return the correct truth table, or that slip through the error detection methods, do not hesitate to inform me.
