package org.juancampos.utils

import spock.lang.Specification
import spock.lang.Unroll


class VariableExpressionSpec extends Specification {
    @Unroll
    def "Test Setting Variable Expression values when VariableExpression.of(#value,#isValueAssigned) then getValue() = #value and isAssigned() = #isValueAssigned"() {
        given:""

        when:"A variable expression is created"
        VariableExpression variableExpression = VariableExpression.of(value,isValueAssigned)
        then:"The internal values match the constructor"
        variableExpression.getValue() == value
        variableExpression.isValueAssigned() == isValueAssigned
        where:""
        value | isValueAssigned
        1     | true
        0     | false
    }

    @Unroll
    def "Test Setting Variable Expression setValue when setValue(#value) then getValue() = #value"() {
        given:""

        when:"A variable expression is created"
        VariableExpression variableExpression = VariableExpression.of(0,false)
        variableExpression.setValue(value)
        then:"The internal values match the constructor"
        variableExpression.getValue() == value
        !variableExpression.isValueAssigned()
        where:""
        value | _
        5     | _
        10    | _
    }

    @Unroll
    def "Test Setting Variable Expression setIsAssigned when setIsValueAssigned(#value) then isValueAssigned() = #value"() {
        given:""

        when:"A variable expression is created"
        VariableExpression variableExpression = VariableExpression.of(0,false)
        variableExpression.setIsValueAssigned(value)
        then:"The internal values match the constructor"
        variableExpression.isValueAssigned() == value
        where:""
        value | _
        true  | _
        false | _
    }

}