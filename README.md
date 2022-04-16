# How to use:

To use the solver in its current state, you declare a string which contains a valid boolean expression (See the EBNF down below).
Then you can create the object like so:

```java
BoolSolver identifier = new BoolSolver(stringExpression);
```

This should print a truth table to the console with the variables that were in the string from left to right being output in the truth table in the opposite order.


If you find a case i have not handled, please write me a message so that i can fix this!

```ebnf
EXP    := TERM {'+' TERM}
TERM   := FACTOR {'*' FACTOR}
FACTOR := VALUE
FACTOR := '!' FACTOR
FACTOR := '(' EXP ')'
VALUE  := 1 | 0 | VARIABLE
```
Variable is just a simple c-like identifier described with:
```
[_a-zA-Z][_a-zA-Z0-9]{0,30}
```
