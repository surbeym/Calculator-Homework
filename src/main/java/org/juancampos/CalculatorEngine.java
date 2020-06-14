package org.juancampos;

import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.juancampos.enums.Operators;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Stack;

public class CalculatorEngine implements ICalculatorEngine{

    public static final char PLUS = Operators.ADD.getSymbol();
    public static final char MINUS = Operators.SUB.getSymbol();
    public static final char MULTIPLY = Operators.MULT.getSymbol();
    public static final char DIVIDE = Operators.DIV.getSymbol();
    public static final char LET_OPERATOR = Operators.LET.getSymbol();
    public static final Logger LOGGER = LogManager.getLogger(CalculatorEngine.class.getName());
    public static final String INVALID_ARGUMENT_FOR_CALCULATOR = "INVALID ARGUMENT FOR CALCULATOR";
    public static final String INPUT_STRING_IS_EMPTY = "Input string is empty";
    public static final String LET_OPERATOR_TO_ASSIGN_VALUE_TO_VARIABLE_BEGINS = "LET operator to assign value to variable begins";
    public static final String VARIABLE_NAME = "Variable name = {0}";
    public static final String VARIABLE_NOT_ASSIGNED_PUSHED_TO_EXPRESSIONS_STACK = "Variable {0} not assigned. Pushed to expressions stack";
    public static final String VARIABLE_ASSIGNED = "Variable assigned. {0} = {1}";
    public static final String NUMBER_STACK_PUSH = "Number stack push = {0}";
    public static final String OPERATORS_STACK_PUSH = "Operators Stack Push: {0}";
    public static final String MISSING_VARIABLES_NOT_ASSIGNED = "Missing Variables Not assigned";

    private CalculatorEngine()
    {
        // private constructor
    }

    // Inner class to provide instance of class
    private static class CalculatorEngineSinglenton
    {
        private static final ICalculatorEngine INSTANCE = new CalculatorEngine();
    }

    public static ICalculatorEngine getInstance()
    {
        return CalculatorEngineSinglenton.INSTANCE;
    }

    /**
     * Calculate function.
     * This problem is a variation on a theme for "basic calculator" problem.
     * Basically it can be approached as a  stack question. Since the expression might have parenthesis,
     * a stack is used to find the value for each sub-expression within a parenthesis.
     * The main expression needs to be delayed until the interim sub-expressions within parenthesis
     * are evaluated and a stack is used to introduce this delay, we use a stack.
     * There is also a precedence to the operations namely that multiplication and division happen first.
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
    @Override
    public long calculate(String s) {
        if (s == null || s.length() == 0) {
            LOGGER.debug(INPUT_STRING_IS_EMPTY);
            return 0;
        }
        Stack<Long> numbers = new Stack<>(); // the stack that stores numbers to be operated on
        Stack<Character> operators = new Stack<>();// the stack that holds the operators
        Stack<String> expressions = new Stack<>();//the stack to hold the expressions
        HashMap<String, Pair<Long,Boolean>> variablesMap = new HashMap<>();
        MutableInt variablesAssignedBal = new MutableInt(0);//map containing variables with corresponding values.
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
            if (calculatorChar == LET_OPERATOR) {  //If the operator is a let operator, process to resolve the variable name
               i = processLetOperator(i,s,operators,expressions,variablesMap,variablesAssignedBal);
              continue;
            }

            if (Character.isAlphabetic(calculatorChar)){ //If the variable name is present, is possible to resolve it. Move the counter accordingly
                i = resolveVariableName(s, numbers, variablesMap, i, calculatorChar,variablesAssignedBal);
                calculatorChar = s.charAt(i);
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
                LOGGER.debug(MessageFormat.format(NUMBER_STACK_PUSH,numbers.peek()));
            } else if (calculatorChar == '(') {
                operators.push(calculatorChar);
                LOGGER.debug(MessageFormat.format(OPERATORS_STACK_PUSH,operators.peek()));
            } else if (calculatorChar == ')') {
                // keep going when we encounter a ')' until we find the opening parenthesis
                if (numbers.size() > 1) {
                    while (numbers.size() > 1 && !operators.isEmpty() && operators.peek() == '(') {
                        operators.pop(); //get rid of the '('
                    }
                    if (!operators.isEmpty()) {
                        long operation = operation(operators.pop(), numbers.pop(), numbers.pop());
                        numbers.push(operation);
                        LOGGER.debug(MessageFormat.format("Operation result = {0} was pushed to numbers stack",numbers.peek()));
                        if (numbers.size() == 1 && !expressions.isEmpty()) {
                            String pop = expressions.pop();
                            variablesMap.put(pop, ImmutablePair.of(numbers.peek(), true));
                            LOGGER.debug(MessageFormat.format("Since only one number left in stack, the value {0} is a result that can be assigned to variable name{1}",numbers.peek(),pop ));
                        }
                    }
                }

            } else if (calculatorChar == PLUS || calculatorChar == MINUS || calculatorChar == MULTIPLY || calculatorChar == DIVIDE) {
                while (!operators.isEmpty() && precedence(calculatorChar, operators.peek())) {
                    numbers.push(operation(operators.pop(), numbers.pop(), numbers.pop()));
                    LOGGER.debug(MessageFormat.format(NUMBER_STACK_PUSH,numbers.peek()));
                }
                operators.push(calculatorChar);
                LOGGER.debug(MessageFormat.format(OPERATORS_STACK_PUSH,operators.peek()));
            }
        }
        for (Pair<Long,Boolean> variableName:variablesMap.values()){
            if (Boolean.FALSE.equals(variableName.getRight())) {
                LOGGER.error(MISSING_VARIABLES_NOT_ASSIGNED);
                throw new CalculatorException(MISSING_VARIABLES_NOT_ASSIGNED);
            }

        }
        while (numbers.size() > 1 && !operators.isEmpty()) {
            numbers.push(operation(operators.pop(), numbers.pop(), numbers.pop()));
            LOGGER.debug(MessageFormat.format(NUMBER_STACK_PUSH,numbers.peek()));
        }
        return numbers.pop();
    }

    protected int resolveVariableName(String s, Stack<Long> numbers, HashMap<String, Pair<Long, Boolean>> variablesMap, int i, char calculatorChar, MutableInt variableAssignedBalance) {
        StringBuilder variableName = new StringBuilder(String.valueOf(calculatorChar));
        while (i < s.length() - 1 && Character.isAlphabetic(s.charAt(i+1))) {
            variableName.append(s.charAt(i + 1));
            i++;
        }
        if (variablesMap.containsKey(variableName.toString()) && variablesMap.get(variableName.toString()).getRight()){
            numbers.push(variablesMap.get(variableName.toString()).getLeft());
            LOGGER.debug(MessageFormat.format(NUMBER_STACK_PUSH,numbers.peek()));
            i++;
        } else if (numbers.size() == 1 && variableAssignedBalance.intValue() > 0){
            variablesMap.put(variableName.toString(), ImmutablePair.of(numbers.peek(), true));
            variableAssignedBalance.decrement();
            LOGGER.debug(MessageFormat.format(VARIABLE_ASSIGNED,variableName.toString(),variablesMap.get(variableName.toString()).getLeft()));
        } else {
            variablesMap.put(variableName.toString(), ImmutablePair.of(0L, false));
            variableAssignedBalance.increment();
            LOGGER.debug(MessageFormat.format(VARIABLE_NOT_ASSIGNED_PUSHED_TO_EXPRESSIONS_STACK,variableName.toString()));
        }
        return i;
    }

    private int processLetOperator(int i, String s, Stack<Character> operators, Stack<String>expressions, HashMap<String, Pair<Long,Boolean>> variablesMap, MutableInt variableUnnasignedBalance) {
        variableUnnasignedBalance.increment();
        LOGGER.debug(LET_OPERATOR_TO_ASSIGN_VALUE_TO_VARIABLE_BEGINS);// The let operator is present, a let expression is parsed
        i++;
        char calculatorChar = s.charAt(i);
        if (calculatorChar == '(') {
            operators.push(calculatorChar);
            LOGGER.debug(MessageFormat.format(OPERATORS_STACK_PUSH,operators.peek()));
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
        LOGGER.debug(MessageFormat.format(VARIABLE_NAME,variableName.toString()));
        i++;
        calculatorChar = s.charAt(i);
        if (calculatorChar != ','){
            LOGGER.error(INVALID_ARGUMENT_FOR_CALCULATOR);// A variable name must be immediately followed by a comma to specify the value expression
            throw new CalculatorException(INVALID_ARGUMENT_FOR_CALCULATOR);
        } else {
            i++;
            calculatorChar = s.charAt(i);
        }
        int negate = 1;
        if (calculatorChar == '-'){
            negate = -1;
            i++;
            calculatorChar = s.charAt(i);
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
            variablesMap.put(variableName.toString(), ImmutablePair.of(possibleNumber, true));
            variableUnnasignedBalance.decrement();
            LOGGER.debug(MessageFormat.format(VARIABLE_ASSIGNED,variableName.toString(),possibleNumber));
            i++;
        }else {
            variablesMap.put(variableName.toString(), ImmutablePair.of(0L, false));
            expressions.push(variableName.toString());
            LOGGER.debug(MessageFormat.format(VARIABLE_NOT_ASSIGNED_PUSHED_TO_EXPRESSIONS_STACK, variableName.toString()));
            i--; //reset counter
        }
        return i;
    }

    private long operation(char operation, long secondOperand, long firstOperand) {
        LOGGER.debug(MessageFormat.format("OPERATION:{0}, FIRST OPERAND:{1}, SECOND OPERAND:{2}", operation, firstOperand,secondOperand));
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
    private boolean precedence(char operator1, char operator2) {
        if (operator2 == '(' || operator2 == ')') return false;
        return (operator1 != '*' && operator1 != '/') || (operator2 != '+' && operator2 != '_');
    }
}
