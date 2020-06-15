package org.juancampos

import org.juancampos.services.ValidatorService
import spock.lang.Specification
import spock.lang.Unroll


class ValidatorSpec extends Specification {
    @Unroll
    def "Test the Validator to make sure parenthesis are well formed. When input = #inputOperation then valid = #invalid"() {
        given:"A new instance of a validator"
        def validator = new ValidatorService()

        when:"The validParenthesis is called with input operation"
        def actualValue = validator.isInvalidParenthesis(inputOperation)
        then:"Actual value matches expected value"
        actualValue == invalid
        where:"The parameterized table that contains input values"
        inputOperation | invalid
        ""             | true
        "ADD(1,2)"     | false
        "ADD(1,2,3))"  | true
        "ADD 1,2"      | true
        "ADD (1,2"     | true
        "ADD 1,2)"     | true
    }

    @Unroll
    def "Test the validator to make sure command input is not empty. When input = #inputOperation then validateEmptyCommand = #invalid"() {
        given:"A new instance of validator"
        def validator = new ValidatorService()
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
        def validator = new ValidatorService()
        when:"Validate for empty input"
        def actualValue = validator.isInvalidFirstOperation(inputOperation)
        then:"The validator returns the expected result"
        actualValue == invalid
        where:"Parameterized values for inputs and expected results"
        inputOperation | invalid
        "*(1,2)"      | false
        "+(1,2)"      | false
        "/(1,2)"      | false
        "_(-1,2)"     | false
        "#(-1,2)"     | false
        "*)1,2)"      | true
        "+)1,2)"      | true
        "/)1,2)"      | true
        "_)-1,2)"     | true
        "#)-1,2)"     | true
        "-)1,2)"      | true
        "ADD(1,2)"    | true
        "MOLT(1,2)"   | true
        "MULT(1,2)"   | true
        "RES(1,2)"    | true
        "%(1,2)"      | true
        "+(1,2"       | true
    }

    @Unroll
    def "Test the validator to make sure it only contains valid characters. When input = #inputOperation then invalidCharacters = #valid"() {
        given:"A new instance of validator"
        def validator = new ValidatorService()
        when:"Validate for empty input"
        def actualValue = validator.validateCharacters(inputOperation)
        then:"The validator returns the expected result"
        actualValue == valid
        where:""
        inputOperation | valid
        "AD(1,2)"      | true
        "ADD(1,2)"     | true
        "MOLT(1,2)"    | true
        "MULT(1,2)"    | true
        "RES(1,2)"     | true
        "%RES(1,2"     | false
        "#(a,5,+(a,a))"| false
    }

    @Unroll
    def "Test main validator method. When Input = '#input' then validate result = '#expectedOutput'"() {
        given:"A new instance of validator"
        def validator = new ValidatorService()
        when:"Validate for input"
        def actualValue = validator.validate(input)
        then:"The validator returns the expected result"
        actualValue.equals(expectedOutput)
        where:""
        input | expectedOutput
        ""    | false
        "(("  | false
        "AD(1,2)" | false
        "+(1,1)"  | true
    }




}