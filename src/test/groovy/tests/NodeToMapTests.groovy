package tests

import spock.lang.Specification

/**
 * User: dan-woods
 * Date: 11/20/12
 */
class NodeToMapTests extends Specification {
    def 'check xml string to node to map coercion with children'() {
        given:
        def xml = '''<dan id="1234" attr="attrValue">
                    |  <key value="val" />
                    |  <key value="val2" />
                    |</dan>'''.stripMargin()
        def map = new XmlSlurper().parseText(xml).toMap()

        expect:
        map instanceof Map
        map.dan._children.size() == 2
    }
}
