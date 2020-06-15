package org.juancampos.services;

import org.apache.commons.lang3.StringUtils;
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
public class ValidatorService implements IValidatorService {
    private static final Logger LOGGER = LogManager.getLogger(ValidatorService.class.getName());
    public static final String VALID_INPUT = "VALID INPUT";
    public static final int MINIMAL_OPERATION = 6;
    public static final String SUCCESSFUL_VALIDATION = "Successful validation";
    public static final String INVALID_CHARACTERS_IN_INPUT = "Invalid characters in input";
    public static final String INPUT_IS_EMPTY_COMMAND = "Input is empty command";
    public static final String INPUT_COMMAND_HAS_INVALID_PARENTHESIS = "Input command has invalid parenthesis";
    public static final String INPUT_COMMAND_HAS_INVALID_FIRST_OPERATION = "Input command has invalid first operation";


    private ValidatorService()
    {
        // private constructor
    }

    // Inner class to provide instance of class
    private static class ValidatorSinglenton
    {
        private static final IValidatorService INSTANCE = new ValidatorService();
    }

    public static IValidatorService getInstance()
    {
        return ValidatorSinglenton.INSTANCE;
    }
    /**
     * Main validator method. It will call subroutines to validate each scenario
     * @param input The command input string to validate.
     * @return True if is valid false otherwise.
     */
    @Override
    public boolean validate(String input){
        if (isEmptyCommand(input)){
            LOGGER.error(INPUT_IS_EMPTY_COMMAND);
            return false;
        }
        String trimmedInput = input.trim();
        if (isInvalidParenthesis(trimmedInput)) {
            LOGGER.error(INPUT_COMMAND_HAS_INVALID_PARENTHESIS);
            return false;
        }
        if (isInvalidFirstOperation(trimmedInput)) {
            LOGGER.error(INPUT_COMMAND_HAS_INVALID_FIRST_OPERATION);
            return false;
        }
        LOGGER.debug(SUCCESSFUL_VALIDATION);
        return true;
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
        if (!StringUtils.contains(input,"(") && !StringUtils.contains(input,")")) {
            return true;
        }
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
    public boolean validateCharacters(String input){
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9,()-]*$");
        Matcher matcher = pattern.matcher(input);
        if(matcher.matches()) {
            LOGGER.debug(VALID_INPUT);
            return true;
        }
        LOGGER.error(INVALID_CHARACTERS_IN_INPUT);
        return false;
    }
}
