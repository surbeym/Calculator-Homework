import org.juancampos.exceptions.CalculatorException
import spock.lang.Specification
import spock.lang.Unroll


class CalculatorSpec extends Specification {
    @Unroll
    def "Test getCommandInput = #commandInput when the input string is = #inputValues"() {
        given: "An instance of calculator initialized to the command argument"
        def calculator = new Calculator()
        when: "The calculator is called to format the command argument to make keywords into characters"
        def actual = calculator.getCommandString(inputValues)

        then: "The formatted command input string matches the expected value"
        actual.equals(commandInput)
        where: "Parameterized Values"
        inputValues                                                   | commandInput
        [new StringBuilder("ADD   (1,A   DD(1,   2))")] as List<StringBuilder> | "ADD(1,ADD(1,2))"
        [new StringBuilder("let(a, let(b, 10, add(b, b)), let(b, 20, add(a, b))")] as List<StringBuilder> | "LET(A,LET(B,10,ADD(B,B)),LET(B,20,ADD(A,B))"
    }

    @Unroll
    def "test calculator call when input command is #inputOperations then expectedResult = #expectedResult"() {
        given: "A calculator instance initialized with the command line argument"
        def calculator = new Calculator(operations: inputOperations)

        when: "The calculator is called to execute the command and return the value"
        def actualResult = calculator.call()
        then: "The actual result matches the expected result from the table"
        actualResult == expectedResult
        where:
        inputOperations                                                                            | expectedResult
        [new StringBuilder("sub!@(5, mult(-2, 3))")]as List<StringBuilder>                         | 0
        [new StringBuilder("add(2,mult(let(A,1,add(2,A)),6))")]as List<StringBuilder>              | 20
        [new StringBuilder("sub(5, mult(-2, 3))")]as List<StringBuilder>                           | 11
        [new StringBuilder("add(1, mult(2, 3))")]as List<StringBuilder>                            | 7
        [new StringBuilder("mult(add(2, 2), div(9, 3))")]as List<StringBuilder>                    | 12
        [new StringBuilder("add(1, mult(-2, 3))")]as List<StringBuilder>                           | -5
        [new StringBuilder("mult(add(2, 2), div(-9, 3))")]as List<StringBuilder>                   | -12
        [new StringBuilder("sub(-1,mult(4,div(3,div(3,3))))")] as List<StringBuilder>              | -13
        [new StringBuilder("let(zones,sub(-1,mult(4,div(3,div(3,3)))),div(39,zones))")] as List<StringBuilder>  | -3
        [new StringBuilder("let(az, 5, add(az, az))")] as List<StringBuilder>                      | 10
        [new StringBuilder("mult(5,let(ans, 5, add(ans, ans)))")] as List<StringBuilder>           | 50
        [new StringBuilder("mult(let(ans, 5, add(ans, ans)),4)")] as List<StringBuilder>           | 40
        [new StringBuilder("mult(4,let(ans, 5, add(ans, ans)))")] as List<StringBuilder>           | 40
        [new StringBuilder("let(ans, 5, let(bus, mult(ans, 10), add(bus, ans)))")] as List<StringBuilder>   | 55
        [new StringBuilder("let(az, add(1, mult(2, 3)), add(az, az))")] as List<StringBuilder>              | 14
        [new StringBuilder("let(az, add(1, 6), add(az, az))")] as List<StringBuilder>                       | 14
        [new StringBuilder("let(az, div(9,1), add(az, az))")] as List<StringBuilder>                        | 18
        [new StringBuilder("let(a, let(b, 10, add(b, b)), let(b, 20, add(a, b)))")] as List<StringBuilder>  | 40
        [new StringBuilder("let(a, let(b, 10, add(b, 5)), let(b, 20, add(a, b)))")] as List<StringBuilder>  | 35
        [new StringBuilder("let(foo, let(bar, 10, add(bar, bar)), let(bar, 20, add(foo, bar)))")] as List<StringBuilder>      | 40
        [new StringBuilder("add(let(col,add(2,3),mult(2,col)),mult(let(bar,2,sub(10,bar)),add(1,let(foo,2,add(3,foo)))))")] as List<StringBuilder>    | 58
    }

    @Unroll
    def "test calculator call when input command is #inputOperations with an unassigned variable then throws CalculatorException"() {
        given: "A calculator instance initialized with the command line argument"
        def calculator = new Calculator(operations: inputOperations)

        when: "The calculator is called to execute the command and return the value"
        calculator.call()
        then: "A calculator exception is thrown"
        thrown CalculatorException
        where:
        inputOperations                                                          | expectedResult
        [new StringBuilder("let(FOO,5, mult(FOO, BAR))")] as List<StringBuilder> | 0
        [new StringBuilder("let(foo, let(bar, 10, add(bar, bar)), let(bar, 20, add(foo, ber)))")] as List<StringBuilder> | 40
    }

    @Unroll
    def "Test substituting operation strings for characters in the command line input string."() {
        Calculator calculator = new Calculator()

        when: "The calculator is called to execute the command and return the value"
        def actual = calculator.substituteOperators(inputOperations)
        then: "The actual result matches the expected result from the table"
        actual.equals(expectedResult)
        where:
        inputOperations                  | expectedResult
        "ADD(1,2)"                         | "+(1,2)"
        "MULT(1,2)"                         | "*(1,2)"
        "SUB(1,2)"                         | "_(1,2)"
        "DIV(1,2)"                         | "/(1,2)"
        "LET(1,2)"                         | "#(1,2)"
        "ADD(1,2) MULT(1,2) SUB(1,2) DIV(1,2) ADD(3,4) LET(1,2)" | "+(1,2) *(1,2) _(1,2) /(1,2) +(3,4) #(1,2)"

    }
}