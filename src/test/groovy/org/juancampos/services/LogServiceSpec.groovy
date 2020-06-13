package org.juancampos.services

import org.apache.logging.log4j.Level
import spock.lang.Specification
import spock.lang.Unroll


class LogServiceSpec extends Specification {
    @Unroll
    def "Test method to set custom logging level. When input level = #inputLevel then expected log4j2 level = #log4j2level"() {
        given:"An instance of the log service"
        def logService = new LogService()
        when:"Log service is called to set custom level"
        def actual = logService.getLogLevel(inputLevel)
        then:"Log service returns the expected log4j2 level"
        actual == log4j2level
        where:"Parameterized values for the test"
        inputLevel    | log4j2level
        null          | Level.INFO
        ""            | Level.INFO
        "   "         | Level.INFO
        "debug"       | Level.DEBUG
        "DEBUG"       | Level.DEBUG
        "eRRoR"       | Level.ERROR
        "WARN"        | Level.INFO
    }

}