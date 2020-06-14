package org.juancampos.services;

import org.apache.commons.lang3.tuple.Pair;

public interface IValidatorService {
    boolean validate(String input);

    boolean isInvalidParenthesis(String input);

    boolean isInvalidFirstOperation(String input);

    boolean validateCharacters(String input);
}
