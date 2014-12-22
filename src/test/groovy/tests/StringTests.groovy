/*
 * Copyright 2012 the original author or authors.
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
 * Created by mburgess on 12/22/14.
 */
class StringTests extends Specification {

    def 'check XML parsing'() {
        def xmlString = """
          <root>
            <config>
              <a>Hello</a>
              <b>World!</b>
              <c>3</c>
            </config>
          </root>
        """

        when:
        def xmlObject = xmlString.toXml()

        then:
        assert xmlObject.config.a.equals('Hello')
        assert xmlObject.config.b == 'World!'
        assert xmlObject.config.c == 3

    }

    def 'check config parsing'() {
        def configString = """
                  config {
                    a = 1
                    b = 2
                    c = 3
                  }
    """

        when:
        def configObject = configString.toConfig()

        then:
        assert configObject.config.a == 1
        assert configObject.config.b == 2
        assert configObject.config.c == 3
    }

    def 'check JSON parsing'() {
        def jsonString = """
            {
               "config": {
                 "a": "Hello",
                 "b": "World!",
                 "c": 3
               }
            }
    """

        when:
        def jsonObject = jsonString.toJson()

        then:
        assert jsonObject.config.a == 'Hello'
        assert jsonObject.config.b == 'World!'
        assert jsonObject.config.c == 3

    }
}
