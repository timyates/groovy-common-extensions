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

package com.bloidonia.groovy.extensions

import groovy.json.JsonSlurper
import groovy.util.slurpersupport.GPathResult
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParser;


/**
 * @author Matt Burgess
 */
class StringExtensionMethods {

    static ConfigSlurper configSlurper = new ConfigSlurper()
    static JsonSlurper jsonSlurper = new JsonSlurper()

    /**
     * Parse a String as XML via XmlSlurper
     *
     * <pre class="groovyTestCase">
     *   assert '<root><name>Matt</name></root>'.toXml().name.text() == 'Matt'
     * </pre>
     *
     * @param self   The String to parse
     * @return       The GPathResult of the call to parseText
     * @see groovy.util.XmlSlurper
     */
    static GPathResult toXml( String self ) {
        toXml(self, false, true, false)
    }

    static GPathResult toXml( String self,
                              boolean validating,
                              boolean namespaceAware) {
        toXml(self, validating, namespaceAware, false)
    }

    static GPathResult toXml( String self,
                              boolean validating,
                              boolean namespaceAware,
                              boolean allowDocTypeDeclaration) {
        new XmlSlurper(validating, namespaceAware, allowDocTypeDeclaration).parseText(self)
    }

    /**
     * Parse a String as XML via XmlSlurper with a specified SAXParser.
     *
     * <pre class="groovyTestCase">
     *  // Given some unhinged HTML
     *  def text="""<div>
     *             |    <h2>Test</h2>
     *             |    <div>
     *             |        <span>Hi
     *             |    </div>
     *             |</div>""".stripMargin()
     *
     *  // Pick your HTML cleaner of choice, and pass it to toXml
     *  def parsed = text.toXml(new org.cyberneko.html.parsers.SAXParser())
     *
     *  assert parsed.BODY.DIV.DIV.SPAN.text().trim() == 'Hi'
     * </pre>
     *
     * @param self   The String to parse
     * @param parser The SAXParser to use
     * @return       The GPathResult of the call to parseText
     */
    static GPathResult toXml( String self,
                              SAXParser parser) {
        toXml(self, parser.getXMLReader())
    }

    static GPathResult toXml( String self,
                              XMLReader reader) {
        new XmlSlurper(reader).parseText(self)
    }

    static ConfigObject toConfig( String self ) {
        configSlurper.parse( self )
    }

    static Object toJson( String self ) {
        jsonSlurper.parseText( self )
    }
}
