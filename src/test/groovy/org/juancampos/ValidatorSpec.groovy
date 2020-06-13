package org.juancampos

import org.apache.commons.lang3.tuple.Pair
import spock.lang.Specification
import spock.lang.Unroll


class ValidatorSpec extends Specification {
    @Unroll
    def "Test the Validator to make sure parenthesis are well formed. When input = #inputOperation then valid = #invalid"() {
        given:"A new instance of a validator"
        def validator = new Validator()

        when:"The validParenthesis is called with input operation"
        def actualValue = validator.isInvalidParenthesis(inputOperation)
        then:"Actual value matches expected value"
        actualValue == invalid
        where:"The parameterized table that contains input values"
        inputOperation | invalid
        ""             | false
        "ADD(1,2)"     | false
        "ADD(1,2,3))"  | true
    }

    @Unroll
    def "Test the validator to make sure command input is not empty. When input = #inputOperation then validateEmptyCommand = #invalid"() {
        given:"A new instance of validator"
        def validator = new Validator()
        when:"Validate for empty input"
        def actualValue = validator.isEmptyCommand(inputOperation)
        then:"The validator returns the expected result"
        actualValue == invalid
        where:"parameterized values for inputs and expected results"
        inputOperation | invalid
        ""             | true
        null           | true
        " "            | true
        "      "       | true
    }

    @Unroll
    def "Test the validator to make sure first operation is valid. When input = #inputOperation then isInvalidFirstOperation = #invalid"() {
        given:"A new instance of validator"
        def validator = new Validator()
        when:"Validate for empty input"
        def actualValue = validator.isInvalidFirstOperation(inputOperation)
        then:"The validator returns the expected result"
        actualValue == invalid
        where:"Parameterized values for inputs and expected results"
        inputOperation | invalid
        "*(1,2)"      | false
        "+(1,2)"      | false
        "/(1,2)"      | false
        "_(-1,2)"    | false
        "#(-1,2)"    | false
        "*)1,2)"      | true
        "+)1,2)"      | true
        "/)1,2)"      | true
        "_)-1,2)"    | true
        "#)-1,2)"    | true
        "-)1,2)"      | true
        "ADD(1,2)"     | true
        "MOLT(1,2)"    | true
        "MULT(1,2)"    | true
        "RES(1,2)"     | true
        "%(1,2)"       |true
    }

    @Unroll
    def "Test the validator to make sure it only contains valid characters. When input = #inputOperation then invalidCharacters = #invalid"() {
        given:"A new instance of validator"
        def validator = new Validator()
        when:"Validate for empty input"
        def actualValue = validator.invalidCharacters(inputOperation)
        then:"The validator returns the expected result"
        actualValue == invalid
        where:""
        inputOperation | invalid
        "AD(1,2)"      | false
        "ADD(1,2)"     | false
        "MOLT(1,2)"    | false
        "MULT(1,2)"    | false
        "RES(1,2)"     | false
        "%RES(1,2"     | true
        "#(a,5,+(a,a))"| false
    }

    @Unroll
    def "Test main validator method. When Input = '#input' then validate result = '#expectedOutput'"() {
        given:"A new instance of validator"
        def validator = new Validator()
        when:"Validate for input"
        def actualValue = validator.validate(input)
        then:"The validator returns the expected result"
        actualValue.equals(expectedOutput)
        where:""
        input | expectedOutput
        ""    | Pair.of(false,Validator.INVALID_INPUT_EMPTY_COMMAND)
        "(("  | Pair.of(false,Validator.INVALID_INPUT_MAL_FORMED_PARENTHESIS)
        "AD(1,2)" | Pair.of(false,Validator.INVALID_FIRST_OPERATION_MUST_BE_ADD_SUB_MULT_DIV_OR_LET)
        "+(1,2)!" | Pair.of(false,Validator.INVALID_CHARACTERS_IN_INPUT)
        "+(1,1)"  | Pair.of(true,Validator.VALID_INPUT)
    }




}