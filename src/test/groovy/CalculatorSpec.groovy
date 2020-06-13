import spock.lang.Specification
import spock.lang.Unroll


class CalculatorSpec extends Specification {
    @Unroll
    def "Test getCommandInput = #commandInput when the input string is = #inputValues"() {
        given:"An instance of calculator initialized to the command argument"
        def calculator = new Calculator()
        when:"The calculator is called to format the command argument to make keywords into characters"
        def actual = calculator.getCommandString(inputValues)

        then:"The formatted command input string matches the expected value"
        actual.equals(commandInput)
        where:"Parameterized Values"
        inputValues | commandInput
        [new StringBuilder("ADD(1,ADD(1,2))")] as List<StringBuilder> | "+(1,+(1,2))"
        [new StringBuilder("let(a, let(b, 10, add(b, b)), let(b, 20, add(a, b))")] as List<StringBuilder> | "#(A,#(B,10,+(B,B)),#(B,20,+(A,B))"
    }

    @Unroll
    def "test calculator call when input command is #inputOperations then expectedResult = #expectedResult"() {
        given:"A calculator instance initialized with the command line argument"
        def calculator = new Calculator(operations: inputOperations)

        when:"The calculator is called to execute the command and return the value"
        def actualResult = calculator.call()
        then:"The actual result matches the expected result from the table"
        actualResult == expectedResult
        where:
        inputOperations                                                 | expectedResult
        [new StringBuilder("sub!@(5, mult(-2, 3))")]as List<StringBuilder> | 0
        [new StringBuilder("sub(5, mult(-2, 3))")]as List<StringBuilder> | 11
        [new StringBuilder("add(1, mult(2, 3))")]as List<StringBuilder> | 7
        [new StringBuilder("mult(add(2, 2), div(9, 3))")]as List<StringBuilder> | 12
        [new StringBuilder("add(1, mult(-2, 3))")]as List<StringBuilder> | -5
        [new StringBuilder("mult(add(2, 2), div(-9, 3))")]as List<StringBuilder> | -12
        [new StringBuilder("sub(-1,mult(4,div(3,div(3,3))))")] as List<StringBuilder> | -13
        [new StringBuilder("let(zones,sub(-1,mult(4,div(3,div(3,3)))),div(39,zones))")] as List<StringBuilder> | -3
        [new StringBuilder("let(az, 5, add(az, az))")] as List<StringBuilder> | 10
        [new StringBuilder("mult(5,let(ans, 5, add(ans, ans)))")] as List<StringBuilder> | 50
        [new StringBuilder("mult(let(ans, 5, add(ans, ans)),4)")] as List<StringBuilder> | 40
        [new StringBuilder("let(ans, 5, let(bus, mult(ans, 10), add(bus, ans)))")] as List<StringBuilder> | 55
        [new StringBuilder("let(az, add(1, mult(2, 3)), add(az, az))")] as List<StringBuilder> | 14
        [new StringBuilder("let(az, div(9,1), add(az, az))")] as List<StringBuilder> | 18
        [new StringBuilder("let(a, let(b, 10, add(b, b)), let(b, 20, add(a, b)))")] as List<StringBuilder> | 40
        [new StringBuilder("let(a, let(b, 10, add(b, 5)), let(b, 20, add(a, b)))")] as List<StringBuilder> | 35
        [new StringBuilder("let(foo, let(bar, 10, add(bar, bar)), let(bar, 20, add(foo, bar)))")] as List<StringBuilder> | 40
    }
}