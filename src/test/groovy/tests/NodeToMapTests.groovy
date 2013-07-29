/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tests

import spock.lang.Specification

/**
 * @author Dan Woods
 * @author Tim Yates
 */
class NodeToMapTests extends Specification {
    def 'check xml string to node to map coercion with children'() {
        given:
        def xml = '''<dan id="1234" attr="attrValue">
                    |  <key value="val" />
                    |  <key value="val2" />
                    |</dan>'''.stripMargin()
        def map = _getMap( xml, '_children' )

        expect:
        map instanceof Map
        map.dan._children.size() == 2
        map.dan._children.key[1].value == 'val2'
    }

    def 'check xml to map coercion with children'() {
        given:
        def xml = '''<dan id="1234" attr="attrValue">
                    |  <key value="val" />
                    |  <key value="val2" />
                    |</dan>'''.stripMargin()
        def map = _getMap(xml)

        expect:
        map instanceof Map
        map.dan.key.size() == 2
        map.dan.key[1].value == 'val2'
    }

    def 'check xml to map coercion'() {
        given:
        def xml = '<dan value="a"><subnode><item value="a"/></subnode></dan>'
        def map = _getMap(xml)

        expect:
        map == [dan: [value: 'a', subnode: [item: [value: 'a']]]]
    }

    def 'check nested without attrs'() {
        given:
        def xml = '<dan><cool>maybe</cool></dan>'
        def map = _getMap(xml)

        expect:
        map == [dan: [ cool: "maybe" ] ]
    }

    def 'test angry schema design'() {
        given:
        def xml = '''<dan cool="yes">
                     |  <cool>maybe</cool>
                     |</dan>
                  '''.stripMargin()
        def map = _getMap(xml)

        expect:
        map == [dan: [cool: ['yes', 'maybe']]]
    }

    def _getMap(xml, childPrefix=null) {
        new XmlSlurper().parseText(xml).toMap( childPrefix )
    }
}
