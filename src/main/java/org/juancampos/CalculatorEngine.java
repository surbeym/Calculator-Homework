package org.juancampos;

import org.apache.commons.lang3.tuple.Triple;
import org.juancampos.enums.Operators;

import java.util.HashMap;
import java.util.Stack;

public class CalculatorEngine {

    public static final char PLUS = Operators.ADD.getSymbol().charAt(0);
    public static final char MINUS = Operators.SUB.getSymbol().charAt(0);
    public static final char MULTIPLY = Operators.MULT.getSymbol().charAt(0);
    public static final char DIVIDE = Operators.DIV.getSymbol().charAt(0);
    public static final char LET_OPERATOR = Operators.LET.getSymbol().charAt(0);

    /**
     * Calculate function.
     * This problem is a variation on a theme for "basic calculator" problem.
     * Basically it can be approached as a  stack question. Since the expression might have parenthesis,
     * a stack is used to find the value for each sub-expression within a parenthesis.
     * The main expression needs to be delayed until the interim sub-expressions within parenthesis
     * are evaluated and a stack is used to introduce this delay, we use a stack.
     * There is also a precence to the operations namely that multiplication and division happen first.
     * The terms presented here have a variation in that the operation is all enclosed in parenthesis.
     * So working right to left will produce the correct result when greedely solving for multiplication
     * and division first.
     * The other variation is that there is a possible variable expression that needs to be evaluated.
     * The variable names will be saved in a hashmap so if the variable is resolved it will be subsituted
     * for the value.
     * The variable name will also be kept in a stack. If an operation is reduced it will check to see if it
     * matches a variable waiting for the evaluation, if it does it will pop it out of the stack and assign the
     * value to the variable map.
     * @param s Input string
     * @return The result from the calculation.
     */
    public static long calculate(String s) {
        if (s == null || s.length() == 0) return 0;
        Stack<Long> numbers = new Stack<>(); // the stack that stores numbers to be operated on
        Stack<Character> operators = new Stack<>();// the stack that holds the operators
        Stack<String> expressions = new Stack<>();//the stack to hold the expressions
        HashMap<String, Triple<Long,Boolean,String>> variablesMap = new HashMap<>(); //The map containing variables with corresponding values.
        long currentNumber;
        int negate = 1;
        for (int i = 0; i < s.length(); i++) {
            char calculatorChar = s.charAt(i);
            if (calculatorChar == ','){
                continue;
            }
            if (calculatorChar == '-'){
                negate = -1;
                continue;
            }
            if (calculatorChar == LET_OPERATOR) { // The let operator is present, a let expression is parsed
                i++;
                calculatorChar = s.charAt(i);
                if (calculatorChar == '(') {
                    operators.push(calculatorChar);
                    i++;
                    calculatorChar = s.charAt(i);
                }
                StringBuilder variableName = new StringBuilder(String.valueOf(calculatorChar));
                if (Character.isAlphabetic(calculatorChar)){
                    while (i < s.length() - 1 && Character.isAlphabetic(s.charAt(i+1))) {
                        variableName.append(s.charAt(i + 1));
                        i++;
                    }
                }
                i++;
                calculatorChar = s.charAt(i);
                if (calculatorChar != ','){ // A variable name must be immediately followed by a comma to specify the value expression
                    throw new CalculatorException("INVALID ARGUMENT FOR CALCULATOR");
                } else {
                    i++;
                    calculatorChar = s.charAt(i);
                }

                if (calculatorChar == '-'){
                    negate = -1;
                }
                long possibleNumber;
                if (Character.isDigit(calculatorChar)) { //if its a digit, the variable name can have a value assigned immediately
                    possibleNumber = calculatorChar - '0';
                    // iteratively calculate each number
                    while (i < s.length() - 1 && Character.isDigit(s.charAt(i+1))) {
                        possibleNumber = possibleNumber * 10 + (s.charAt(i+1) - '0');
                        i++;
                    }
                    possibleNumber = possibleNumber * negate;
                    negate = 1;
                    variablesMap.put(variableName.toString(), Triple.of(possibleNumber, true, ""));
                    i++;
                    continue;
                }
                variablesMap.put(variableName.toString(), Triple.of(0L, false, ""));
                expressions.push(variableName.toString());
            }

            if (Character.isAlphabetic(calculatorChar)){ //If the variable name is present, is possible to resolve it
                StringBuilder variableName = new StringBuilder(String.valueOf(calculatorChar));
                while (i < s.length() - 1 && Character.isAlphabetic(s.charAt(i+1))) {
                    variableName.append(s.charAt(i + 1));
                    i++;
                }
                if (variablesMap.containsKey(variableName.toString()) && variablesMap.get(variableName.toString()).getMiddle()){
                    numbers.push(variablesMap.get(variableName.toString()).getLeft());
                    i++;
                    calculatorChar = s.charAt(i);
                } else if (numbers.size() == 1){
                    variablesMap.put(variableName.toString(), Triple.of(numbers.peek(), true, ""));
                } else {
                    variablesMap.put(variableName.toString(), Triple.of(0L, false, ""));
                }
            }


            if (Character.isDigit(calculatorChar)) {
                currentNumber = calculatorChar - '0';
                // iteratively calculate each number
                while (i < s.length() - 1 && Character.isDigit(s.charAt(i+1))) {
                    currentNumber = currentNumber * 10 + (s.charAt(i+1) - '0');
                    i++;
                }
                currentNumber = currentNumber * negate;
                negate = 1;
                numbers.push(currentNumber);
            } else if (calculatorChar == '(') {
                operators.push(calculatorChar);
            } else if (calculatorChar == ')') {
                // keep going when we encounter a ')' until we find the opening parenthesis
                if (numbers.size() > 1) {
                    while (numbers.size() > 1 && !operators.isEmpty() && operators.peek() == '(') {
                        operators.pop(); //get rid of the '('
                    }
                    if (!operators.isEmpty()) {
                        numbers.push(operation(operators.pop(), numbers.pop(), numbers.pop()));
                        if (numbers.size() == 1 && !expressions.isEmpty()) {
                            variablesMap.put(expressions.pop(), Triple.of(numbers.peek(), true, ""));
                        }
                    }
                }

            } else if (calculatorChar == PLUS || calculatorChar == MINUS || calculatorChar == MULTIPLY || calculatorChar == DIVIDE) {
                while (!operators.isEmpty() && precedence(calculatorChar, operators.peek())) numbers.push(operation(operators.pop(), numbers.pop(),numbers.pop()));
                operators.push(calculatorChar);
            }
        }
        while (numbers.size() > 1 && !operators.isEmpty()) {
            numbers.push(operation(operators.pop(), numbers.pop(), numbers.pop()));
        }
        return numbers.pop();
    }

    private static long operation(char operation, long secondOperand, long firstOperand) {
        switch (operation) {
            case '+': return firstOperand + secondOperand;
            case '_': return firstOperand - secondOperand;
            case '*': return firstOperand * secondOperand;
            case '/': return firstOperand / secondOperand; // assume secondOperand is not 0
        }
        return 0;
    }
    // helper function to check precedence of current operator and the uppermost operator in the ops stack

    /**
     * Method to verify the precedence of current operator the the uppermost operator in the operations stack
     * @param operator1 A given operator
     * @param operator2 Second operator to compare
     * @return true if the operator 1 has precedence , otherwise
     */
    private static boolean precedence(char operator1, char operator2) {
        if (operator2 == '(' || operator2 == ')') return false;
        if ((operator1 == '*' || operator1 == '/') && (operator2 == '+' || operator2 == '_')) return false;
        return true;
    }
}
