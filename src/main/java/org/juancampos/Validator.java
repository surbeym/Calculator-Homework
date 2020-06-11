package org.juancampos;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.logging.Logger;

public class Validator {
    public static final Logger logger = Logger.getLogger(Validator.class.getName());
    public static final String VALID_INPUT = "VALID INPUT";
    public static final String INVALID_INPUT_EMPTY_COMMAND = "Invalid Input. Empty command";
    public static final String INVALID_INPUT_MAL_FORMED_PARENTHESIS = "Invalid Input. Mal formed parenthesis";

    public Pair<Boolean,String> validate(String input){
        Pair<Boolean,String> validationResult = Pair.of(true, VALID_INPUT);
        if (validateEmptyCommand(input)){
            return Pair.of(false, INVALID_INPUT_EMPTY_COMMAND);
        }
        if (!validateParenthesis(input)) {
            return Pair.of(false, INVALID_INPUT_MAL_FORMED_PARENTHESIS);
        }
        return validationResult;
    }

    boolean validateEmptyCommand(String input) {
       return StringUtils.isEmpty(input);
    }

    public boolean validateParenthesis(String input) {
        int balance = 0;
        StringBuilder sbInput = new StringBuilder(input);
        for (int i = 0; i < sbInput.length(); i++) {
            char c = sbInput.charAt(i);
            if (c == '(') balance++;
            if (c == ')') balance--;
            if (balance < 0) break;
        }
        return balance == 0;
    }
}
