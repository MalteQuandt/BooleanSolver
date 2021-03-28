# How to use:

To use the solver in its current state, you declare a string which contains a valid boolean expression ,which
is constructred with a combination of parenthesis, AND, OR and NOT operations and literals/variables.
Then you can create the object like so:

```java
BoolSolver identifier = new BoolSolver(stringExpression);
```

This should print a truth table to the console with the variables that were in the string from left to right being outputted in the truth table from right to left.

### Challenge:
Try to break this algorithm! Use the rules of boolean expressions to find one that is 1. valid, and 2. my code does not handle in any correct way.
The reward: Nothing!
