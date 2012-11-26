package tests

import spock.lang.Specification

/**
 * User: ddcdwoods
 * Date: 11/26/12
 */
class ConfigObjectTests extends Specification {
    def 'check source precedence in merge'() {
        given:
        def config1 = """
            config {
                a = 1
                b = 2
                c = 3
            }
        """
        def config2 = """
            config {
                a = 2
                b = 2
            }
        """
        def configObject1 = new ConfigSlurper().parse(config1)
        def configObject2 = new ConfigSlurper().parse(config2)
        def merge = configObject2.merge(configObject1)

        def configObject3 = new ConfigSlurper().parse(config1)
        def configObject4 = new ConfigSlurper().parse(config2)
        def mergeWithSourcePrecedence = configObject4.merge(configObject3,true)

        expect:
        // overwrote config2's value
        assert merge.config.a == 1
        // preserved config2's value
        assert mergeWithSourcePrecedence.config.a == 2

        // config2 inherited value from config1
        assert merge.config.c == 3
        // config2 inherited from config1
        assert mergeWithSourcePrecedence.config.c == merge.config.c
    }
}
