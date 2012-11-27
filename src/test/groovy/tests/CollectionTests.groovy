package tests

import spock.lang.Specification

/**
 * @author dwoods
 * @date 11/27/12
 */
class CollectionTests extends Specification {
    def 'check random list element'() {
        given:
        def list = (0..99).collect{it}
        expect:
        list.rand() in list
    }
}
