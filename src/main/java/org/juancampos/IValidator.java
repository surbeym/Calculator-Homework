package org.juancampos;

import org.apache.commons.lang3.tuple.Pair;

public interface IValidator {
    Pair<Boolean,String> validate(String input);

    boolean isInvalidParenthesis(String input);

    boolean isInvalidFirstOperation(String input);

    boolean invalidCharacters(String input);
}
