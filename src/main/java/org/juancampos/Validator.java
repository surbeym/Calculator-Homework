package org.juancampos;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.juancampos.enums.Operators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validator class for executing validations for the command input.
 * The validator will execute checks for well formed parenthesis,
 * invalid characters or empty command. It will also validate that
 * the command at least starts with a valid operation, as a short circuit
 * to not even process something that might be invalid right away.
 */
public class Validator implements IValidator {
    private static final Logger LOGGER = LogManager.getLogger(Validator.class.getName());
    public static final String VALID_INPUT = "VALID INPUT";
    public static final String INVALID_INPUT_EMPTY_COMMAND = "Invalid Input. Empty command";
    public static final String INVALID_INPUT_MAL_FORMED_PARENTHESIS = "Invalid Input. Mal formed parenthesis";
    public static final int MINIMAL_OPERATION = 6;
    public static final String SUCCESSFUL_VALIDATION = "Successful validation";
    public static final String INVALID_FIRST_OPERATION_MUST_BE_ADD_SUB_MULT_DIV_OR_LET = "Invalid First Operation. Must be ADD, SUB, MULT, DIV or LET";
    public static final String INVALID_CHARACTERS_IN_INPUT = "Invalid characters in input";
    public static final String INPUT_IS_EMPTY_COMMAND = "Input is empty command";
    public static final String INPUT_COMMAND_HAS_INVALID_PARENTHESIS = "Input command has invalid parenthesis";
    public static final String INPUT_COMMAND_HAS_INVALID_FIRST_OPERATION = "Input command has invalid first operation";
    public static final String INPUT_COMMAND_HAS_INVALID_CHARACTERS = "Input command has invalid characters";

    /**
     * Main validator method. It will call subroutines to validate each scenario
     * @param input The command input string to validate.
     * @return A tuple containing the validation result (true or false) and a validation message.
     */
    @Override
    public Pair<Boolean,String> validate(String input){
        Pair<Boolean,String> validationResult = ImmutablePair.of(true, VALID_INPUT);
        if (isEmptyCommand(input)){
            LOGGER.debug(INPUT_IS_EMPTY_COMMAND);
            return ImmutablePair.of(false, INVALID_INPUT_EMPTY_COMMAND);
        }
        String trimmedInput = input.trim();
        if (isInvalidParenthesis(trimmedInput)) {
            LOGGER.debug(INPUT_COMMAND_HAS_INVALID_PARENTHESIS);
            return ImmutablePair.of(false, INVALID_INPUT_MAL_FORMED_PARENTHESIS);
        }
        if (isInvalidFirstOperation(trimmedInput)) {
            LOGGER.debug(INPUT_COMMAND_HAS_INVALID_FIRST_OPERATION);
            return ImmutablePair.of(false, INVALID_FIRST_OPERATION_MUST_BE_ADD_SUB_MULT_DIV_OR_LET);
        }
        if (invalidCharacters(trimmedInput)) {
            LOGGER.debug(INPUT_COMMAND_HAS_INVALID_CHARACTERS);
            return ImmutablePair.of(false, INVALID_CHARACTERS_IN_INPUT);
        }
        LOGGER.debug(SUCCESSFUL_VALIDATION);
        return validationResult;
    }

    /**
     * Validate the command is not null or empty spaces
     * @param input The command input string
     * @return True of the command is empty
     */
    boolean isEmptyCommand(String input) {
       return input == null || StringUtils.isEmpty(input.trim());
    }

    /**
     * Validates the parenthesis balance (a closing parenthesis corresponds to an opening parenthesis)
     *
     * @param input The command input string
     * @return True if the input command string contains invalid number of parenthesis
     */
    @Override
    public boolean isInvalidParenthesis(String input) {
        int balance = 0;
        StringBuilder sbInput = new StringBuilder(input);
        for (int i = 0; i < sbInput.length(); i++) {
            char c = sbInput.charAt(i);
            if (c == '(') balance++;
            if (c == ')') balance--;
            if (balance < 0) break;
        }
        return !(balance == 0);
    }

    /**
     * The input command string may be incorrect from the first few characters.
     * This validation checks that the command may start correctly, and there
     * are only a limited number of scenarios. This method will validate those
     * scenarios.
     * @param input The input command string.
     * @return True of the command starts incorrectly, false otherwise.
     */
    @Override
    public boolean isInvalidFirstOperation(String input){

        if (input.length() < MINIMAL_OPERATION){
            return true;
        }
        String firstOperation = input.substring(0,2);
        return !firstOperation.equals(Operators.ADD.getSymbol() + "(") &&
                !firstOperation.equals(Operators.SUB.getSymbol() + "(") &&
                !firstOperation.equals(Operators.MULT.getSymbol() + "(") &&
                !firstOperation.equals(Operators.DIV.getSymbol() + "(") &&
                !firstOperation.equals(Operators.LET.getSymbol() + "(");


    }

    /**
     * The input command string may only contain valid characters to do the operation.
     * These include the operators, which internally have been transformed to single characters.
     * This allows a regular expression string to be used to validate for it.
     * The regular expression just validates for presence,it does not validate format nor order.
     * The operators are:
     * ADD = +
     * SUB = _ (underscore, this was to allow - to be for negative numbers)
     * MULT = *
     * DIV = /
     * LET = #
     * Alphanumerical characters
     * @param input The input command string
     * @return True if there are invalid charactes in the string, false otherwise.
     */
    @Override
    public boolean invalidCharacters(String input){
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile("^[a-zA-Z0-9()#_*+-/]*$");
        matcher = pattern.matcher(input);
        return !matcher.matches();
    }
}
