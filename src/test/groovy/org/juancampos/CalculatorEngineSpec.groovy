package org.juancampos

import org.apache.commons.lang3.mutable.MutableInt
import org.apache.commons.lang3.tuple.Pair
import spock.lang.Specification
import spock.lang.Unroll


class CalculatorEngineSpec extends Specification {
    @Unroll
    def "Test resolve variable "() {
        given: "A variables map"
        HashMap<String, Pair<Long,Boolean>> variablesMap = new HashMap<>()
        final String variableName = "BAR"
        if (variableName == inputString)
            variablesMap = mapValues

        when:"The calculator engine resolve variable name method is called"
        def actualCounter = CalculatorEngine.resolveVariableName(inputString,numberStack,variablesMap,0 as int ,inputCharacter as char,new MutableInt(0))
        then: "The counter and variablesMap sizes are as expected"
        actualCounter == expectedCounter
        variablesMap.size() == expectedMapSize
        variablesMap.get(inputString).right == resolvedValue
        where:"Parameterized Values"
        inputString   | inputCharacter | numberStack            | mapValues              || expectedCounter | expectedMapSize | resolvedValue
        "FOO"         | 'F' as char    | [1L] as Stack<Long>     |[]                      || 2               | 1               | false
        "BAR"         | 'B' as char    | [2L] as Stack<Long>     |[BAR:Pair.of(1L,true)]  || 3               | 1               | true
        "BAR"         | 'B' as char    | [2L,3L] as Stack<Long>   |[BOO:Pair.of(1L,true)]  || 2               | 2               | false
    }

}