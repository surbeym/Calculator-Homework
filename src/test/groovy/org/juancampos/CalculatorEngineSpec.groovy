package org.juancampos

import org.apache.commons.lang3.mutable.MutableInt
import org.apache.commons.lang3.tuple.Pair
import org.juancampos.exceptions.CalculatorException
import org.juancampos.services.CalculatorService
import org.juancampos.services.ICalculatorService
import org.juancampos.utils.VariableExpression
import spock.lang.Specification
import spock.lang.Unroll


class CalculatorEngineSpec extends Specification {
    @Unroll
    def "Test resolve variable "() {
        given: "A variables map"
        HashMap<String, VariableExpression<Long,Boolean>> variablesMap = new HashMap<>()
        final String variableName = "BAR"
        if (variableName == inputString)
            variablesMap = mapValues
        ICalculatorService calculatorEngine = CalculatorService.getInstance()
        when:"The calculator engine resolve variable name method is called"
        def actualCounter = calculatorEngine.resolveVariableName(inputString,numberStack,variablesMap,0 as int ,inputCharacter as char,new MutableInt(0))
        then: "The counter and variablesMap sizes are as expected"
        actualCounter == expectedCounter
        variablesMap.size() == expectedMapSize
        variablesMap.get(inputString).isValueAssigned() == resolvedValue
        where:"Parameterized Values"
        inputString   | inputCharacter | numberStack            | mapValues                            || expectedCounter | expectedMapSize | resolvedValue
        "FOO"         | 'F' as char    | [1L] as Stack<Long>    |[]                                    || 2               | 1               | false
        "BAR"         | 'B' as char    | [2L] as Stack<Long>    |[BAR: VariableExpression.of(1L,true)] || 3               | 1               | true
        "BAR"         | 'B' as char    | [2L,3L] as Stack<Long> |[BOO:VariableExpression.of(1L,true)]  || 2               | 2               | false
    }

    @Unroll
    def "Test resolve variable name will throw invalid argument exception when input string is : #inputString "() {
        given: "A variables map"
        HashMap<String, Pair<Long,Boolean>> variablesMap = new HashMap<>()
        ICalculatorService calculatorEngine = CalculatorService.getInstance()
        when:"The calculator engine resolve variable name method is called"
        calculatorEngine.resolveVariableName(inputString, [1L] as Stack<Long>,variablesMap,0 as int ,'F'as char,new MutableInt(0))
        then: "The counter and variablesMap sizes are as expected"
        thrown CalculatorException
        where:"Parameterized Values"
        inputString   | _
        "FOO("         | _
        "FOO5"         | _
        "BAR("         | _
        "BAR6"         | _
    }

    @Unroll
    def "Test the operation method. Operation: #operand1 #operation #operand2 = #expectedResult"() {
        given:"A calculator engine"
        def engine = CalculatorService.getInstance() as CalculatorService
        when:"Operation method is called"

        then:"result is as expected"
        engine.operation(operation as char,operand2 as long, operand1 as long) == expectedResult
        where: "Parameterized table"
        operand1    | operation  | operand2  |  expectedResult
        1           | '+'        | 1         |  2
        100         | '+'        | -15       |  85
        100         | '_'        | 15        |  85
        100         | '_'        | -15       |  115
        3           | '*'        | 4         |  12
        3           | '*'        |-4         |  -12
        13          | '/'        |-4         |  -3
        13          | '/'        | 4         |   3
        20          | '/'        | 5         |   4
        5           | '-'        | 1         |   0 //invalid operation
    }

    def "Test the operation method. When division by zero it will throw arithmetic exception"() {
        given:"A calculator engine"
        def engine = CalculatorService.getInstance() as CalculatorService
        when:"Operation method is called"
        engine.operation('/' as char,0 as long, 10 as long)
        then:"result is as expected"
        thrown ArithmeticException

    }

    @Unroll
    def "Test the precedence method. #operator1 has precedence over #operator2 = #expectedResult"() {
        given:"A calculator engine"
        def engine = CalculatorService.getInstance() as CalculatorService
        when:"Operation method is called"

        then:"result is as expected"
        engine.precedence(operator1 as char,operator2 as char) == expectedResult
        where: "Parameterized table"
        operator1    | operator2  |   expectedResult
        '+'         | '+'         |   true
        '+'         | '_'         |   true
        '+'         | '*'         |   true
        '+'         | '/'         |   true
        '+'         | '('         |   false
        '+'         | '('         |   false
        '+'         | ')'         |   false
        '+'         | ')'         |   false
        '_'         | '+'         |   true
        '_'         | '_'         |   true
        '_'         | '*'         |   true
        '_'         | '/'         |   true
        '*'         | '+'         |   false
        '*'         | '_'         |   false
        '*'         | '*'         |   true
        '*'         | '/'         |   true
        '/'         | '+'         |   false
        '/'         | '_'         |   false
        '/'         | '*'         |   true
        '/'         | '/'         |   true
    }

}