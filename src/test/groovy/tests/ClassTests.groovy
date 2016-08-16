package tests

import spock.lang.Specification

class ClassTests extends Specification {
    def 'check implicit conversions'() {
        given:
            String.addImplicitConversion(String) { it.size().toString() }
        expect:
            "foo" as String == '3'
    }
}
