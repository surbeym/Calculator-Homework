package org.juancampos.services;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.juancampos.enums.Operators;
import org.juancampos.exceptions.CalculatorException;
import org.juancampos.utils.VariableExpression;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Stack;

public class CalculatorService implements ICalculatorService {

    public static final char PLUS = Operators.ADD.getSymbol();
    public static final char MINUS = Operators.SUB.getSymbol();
    public static final char MULTIPLY = Operators.MULT.getSymbol();
    public static final char DIVIDE = Operators.DIV.getSymbol();
    public static final char LET_OPERATOR = Operators.LET.getSymbol();
    public static final Logger LOGGER = LogManager.getLogger(CalculatorService.class.getName());
    public static final String INVALID_ARGUMENT_FOR_CALCULATOR = "INVALID ARGUMENT FOR CALCULATOR";
    public static final String INPUT_STRING_IS_EMPTY = "Input string is empty";
    public static final String LET_OPERATOR_TO_ASSIGN_VALUE_TO_VARIABLE_BEGINS = "LET operator to assign value to variable begins";
    public static final String VARIABLE_NAME = "Variable name = {0}";
    public static final String VARIABLE_NOT_ASSIGNED_PUSHED_TO_EXPRESSIONS_STACK = "Variable {0} not assigned. Pushed to expressions stack";
    public static final String VARIABLE_ASSIGNED = "Variable assigned. {0} = {1}";
    public static final String NUMBER_STACK_PUSH = "Number stack push = {0}";
    public static final String OPERATORS_STACK_PUSH = "Operators Stack Push: {0}";
    public static final String MISSING_VARIABLES_NOT_ASSIGNED = "Missing Variables Not assigned";

    private CalculatorService()
    {
        // private constructor
    }

    // Inner class to provide instance of class
    private static class CalculatorEngineSinglenton
    {
        private static final ICalculatorService INSTANCE = new CalculatorService();
    }

    public static ICalculatorService getInstance()
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
        HashMap<String, VariableExpression> variablesMap = new HashMap<>(); //variables map to store values
        MutableInt variablesAssignedBalance = new MutableInt(0); //Checks the variables have been assigned
        MutableInt letParenthesis = new MutableInt(0); //counter for the number of parenthesis to be inside a LET operator
        MutableBoolean  complexLet = new MutableBoolean(false);
        boolean openLet = false;// flag to check process is inside a LET operator

        long currentNumber;
        int negate = 1;
        for (int i = 0; i < s.length(); i++) {
            char calculatorChar = s.charAt(i);
            if (calculatorChar == ','){//commas are skipped as they are not operators
                continue;
            }
            if (calculatorChar == '-'){//a negative number can be presented
                negate = -1;
                continue;
            }
            if (calculatorChar == LET_OPERATOR) {  //If the operator is a LET operator, process to resolve the variable name
               openLet =  true; //if the first operation is a LET operator, the flag is not set so the closing parenthesis can start the operations process
               i = processLetOperator(i,s,operators,expressions,variablesMap,variablesAssignedBalance,complexLet);
              continue;
            }

            if (Character.isAlphabetic(calculatorChar)){ //If the variable name is present, is possible to resolve it. Move the counter accordingly
                i = resolveVariableName(s, numbers,expressions, variablesMap, i,variablesAssignedBalance);
                calculatorChar = s.charAt(i);
            }


            if (Character.isDigit(calculatorChar)) { //If a number appears it gets pushed to the stack of numbers to process
                currentNumber = calculatorChar - '0';
                // iteratively calculate each number
                while (i < s.length() - 1 && Character.isDigit(s.charAt(i+1))) {
                    currentNumber = currentNumber * 10 + (s.charAt(i+1) - '0');
                    i++;
                }
                currentNumber = currentNumber * negate;//if there was a - before the digit, it will become negative number
                negate = 1;
                numbers.push(currentNumber);
                LOGGER.debug(MessageFormat.format(NUMBER_STACK_PUSH,numbers.peek()));
            } else if (calculatorChar == '(') {
                if (openLet){   //if inside a LET operation, increase the count of internal LET parenthesis
                    letParenthesis.increment();
                }
                operators.push(calculatorChar);
                LOGGER.debug(MessageFormat.format(OPERATORS_STACK_PUSH,operators.peek()));
            } else if (calculatorChar == ')') {
                //If LET operation is open, skip closing parenthesis until it hits closing LET parenthesis
                if (openLet){
                    if (letParenthesis.decrementAndGet() == 0){
                        openLet = false;
                        if (complexLet.isTrue()){
                            complexLet.setFalse();
                            if (expressions.isEmpty())
                                continue;
                        }
                    }
                }
                // keep going when we encounter a ')' until we find the opening parenthesis in operator stack
                if (numbers.size() > 1) {
                    while (numbers.size() > 1 && !operators.isEmpty() && operators.peek() == '(') {
                        operators.pop(); //get rid of the '('
                    }
                    if (!operators.isEmpty()) { //find the operator to execute with operands
                        long operation = operation(operators.pop(), numbers.pop(), numbers.pop());
                        if ( !expressions.isEmpty() && (letParenthesis.intValue() == 0)) { //the expression at top of stack will be the variable name that needs value assigned.
                            String pop = expressions.pop();
                            variablesAssignedBalance.decrement();
                            LOGGER.debug(MessageFormat.format("Since only one number left in stack, the value {0} is a result that can be assigned to variable name{1}",operation,pop ));
                            variablesMap.put(pop, VariableExpression.of(operation, true));
                        } else {
                            numbers.push(operation); //push result of collapsed operation
                            LOGGER.debug(MessageFormat.format("Operation result = {0} was pushed to numbers stack", numbers.peek()));
                        }

                    }
                }

            } else if (calculatorChar == PLUS || calculatorChar == MINUS || calculatorChar == MULTIPLY || calculatorChar == DIVIDE) {
                operators.push(calculatorChar);
                LOGGER.debug(MessageFormat.format(OPERATORS_STACK_PUSH,operators.peek()));
            }
        }
        for (VariableExpression variable:variablesMap.values()){  //If at the end of the operations we still have unnasigned variables, its an issue.
            if (Boolean.FALSE.equals(variable.isValueAssigned())) {
                LOGGER.error(MISSING_VARIABLES_NOT_ASSIGNED);
                throw new CalculatorException(MISSING_VARIABLES_NOT_ASSIGNED);
            }

        }
        while (numbers.size() > 1 && !operators.isEmpty()) { //resolve all remaining operations.
            numbers.push(operation(operators.pop(), numbers.pop(), numbers.pop()));
            LOGGER.debug(MessageFormat.format(NUMBER_STACK_PUSH,numbers.peek()));
        }
        return numbers.pop();  //last calculated number in stack is the result.
    }

    /**
     * Resolve a variable name. If a variable name is present, check that it has a value assigned an if so add the number to the numbers stack to
     * have the operation executed
     * @param s Input string
     * @param numbers Numbers stack
     * @param variablesMap Variables mao to find if current value for variable name exists.
     * @param i Current character counter
     * @param variableAssignedBalance The counter to make sure all variables are assigned.
     * @return The current position in the command string.
     */
    protected int resolveVariableName(String s, Stack<Long> numbers,Stack<String> expressions, HashMap<String, VariableExpression> variablesMap, int i,  MutableInt variableAssignedBalance) {
        StringBuilder variableName = new StringBuilder(String.valueOf(s.charAt(i)));
        while (i < s.length() - 1 && Character.isAlphabetic(s.charAt(i+1))) {
            variableName.append(s.charAt(i + 1));
            i++;
        }
        if ( s.length() > i+1 && !(s.charAt(i+1) == ',' || s.charAt(i+1) == ')'))
        {
            LOGGER.error(INVALID_ARGUMENT_FOR_CALCULATOR);
            throw new CalculatorException(INVALID_ARGUMENT_FOR_CALCULATOR);
        }
        if (variablesMap.containsKey(variableName.toString()) && variablesMap.get(variableName.toString()).isValueAssigned()){
            numbers.push(variablesMap.get(variableName.toString()).getValue());
            LOGGER.debug(MessageFormat.format(NUMBER_STACK_PUSH,numbers.peek()));
            i++;
        } else if (numbers.size() == 1 && variableAssignedBalance.intValue() > 0){
            variablesMap.put(variableName.toString(), VariableExpression.of(numbers.peek(), true));
            if (!expressions.isEmpty()) {
                expressions.pop();
            }
            variableAssignedBalance.decrement();
            LOGGER.debug(MessageFormat.format(VARIABLE_ASSIGNED,variableName.toString(),variablesMap.get(variableName.toString()).isValueAssigned()));
        } else {
            variablesMap.put(variableName.toString(), VariableExpression.of(0,false));
            variableAssignedBalance.increment();
            LOGGER.debug(MessageFormat.format(VARIABLE_NOT_ASSIGNED_PUSHED_TO_EXPRESSIONS_STACK,variableName.toString()));
        }
        return i;
    }

    /**
     * Process the LET operator. This will create the variable name, and if the LET expression has a value assigned to it immediately (without intermediate operations) it will assign it
     * @param i The current string character position
     * @param s The input string
     * @param operators The operators stack to push any operators in the let expression
     * @param expressions The expressions stack to store the variable name to resolve
     * @param variablesMap The variables map to store variable names and possible values
     * @param variableUnnasignedBalance The counter to check all variable names have been assigned
     * @return The position of the string to continue parsing.
     */
    protected int processLetOperator(int i, String s, Stack<Character> operators, Stack<String>expressions, HashMap<String, VariableExpression> variablesMap, MutableInt variableUnnasignedBalance, MutableBoolean complexLet) {
        variableUnnasignedBalance.increment();
        LOGGER.debug(LET_OPERATOR_TO_ASSIGN_VALUE_TO_VARIABLE_BEGINS);// The LET operator is present, a LET expression is parsed
        i++;
        if(i>4){
            complexLet.setTrue();
        }
        char calculatorChar = s.charAt(i);
        if (calculatorChar == '(') {
            operators.push(calculatorChar);
            LOGGER.debug(MessageFormat.format(OPERATORS_STACK_PUSH,operators.peek()));
            i++;
            calculatorChar = s.charAt(i);
        }
        StringBuilder variableName = new StringBuilder(String.valueOf(calculatorChar));
        if (Character.isAlphabetic(calculatorChar)){ //Build the variable name. Only alphabetic characters are allowed. It will cut off the variable name on the first non-alphabetic character
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
        int negate = 1; //Check to see if its a negative number
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
            variablesMap.put(variableName.toString(), VariableExpression.of(possibleNumber, true));
            if (!expressions.isEmpty()){
                expressions.pop();
            }
            variableUnnasignedBalance.decrement();  //variable names balance decrements since variable has value assigned
            LOGGER.debug(MessageFormat.format(VARIABLE_ASSIGNED,variableName.toString(),possibleNumber));
            i++;
        }else {
            variablesMap.put(variableName.toString(), VariableExpression.of(0L, false));

            expressions.push(variableName.toString());
            complexLet.setTrue();
            LOGGER.debug(MessageFormat.format(VARIABLE_NOT_ASSIGNED_PUSHED_TO_EXPRESSIONS_STACK, variableName.toString()));
            i--; //reset counter
        }
        return i;
    }

    /**
     * Execute the operation.
     * A division will only result in using Javas default of rounding down to zero.
     * Any arithmetic exception will be allowed to float up to main method
     * @param operation  Operation to execute
     * @param secondOperand Second operand
     * @param firstOperand First operand
     * @return the result of the operation.
     */
    protected long operation(char operation, long secondOperand, long firstOperand) {
        LOGGER.debug(MessageFormat.format("OPERATION:{0}, FIRST OPERAND:{1}, SECOND OPERAND:{2}", operation, firstOperand,secondOperand));
        switch (operation) {
            case '+': return firstOperand + secondOperand;
            case '_': return firstOperand - secondOperand;
            case '*': return firstOperand * secondOperand;
            case '/': return firstOperand / secondOperand; // assume secondOperand is not 0
        }
        return 0;
    }

}
