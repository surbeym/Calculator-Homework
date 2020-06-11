package org.juancampos

import spock.lang.Specification
import spock.lang.Unroll


class ValidatorSpec extends Specification {
    @Unroll
    def "Test the Validator to make sure parenthesis are well formed. When input = #inputOperation then valid = #valid"() {
        given:"A new instance of a validator"
        def validator = new Validator()

        when:"The validParenthesis is called with input operation"
        def actualValue = validator.validateParenthesis(inputOperation);
        then:
        actualValue == valid
        where:"The parameterized table that contains input values"
        inputOperation | valid
        ""             | true
        "add(1,2)"     | true
        "add(1,2,3))"  | false
    }

    @Unroll
    def "Test the validator to make sure command input is not empty. When input = #inputOperation then validateEmptyCommand = #valid"() {
        given:"A new instance of validator"
        def validator = new Validator()
        when:"Validate for empty input"
        def actualValue = validator.validateEmptyCommand(inputOperation)
        then:"The validator returns the expected result"
        actualValue == valid
        where:
        inputOperation | valid
    }


}