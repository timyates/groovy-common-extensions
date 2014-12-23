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

/**
 * @author Matt Burgess
 */
class StringExtensionMethods {

    static XmlSlurper xmlSlurper = new XmlSlurper()
    static ConfigSlurper configSlurper = new ConfigSlurper()
    static JsonSlurper jsonSlurper = new JsonSlurper()

    static GPathResult toXml( String self ) {
        xmlSlurper.parseText( self )
    }

    static ConfigObject toConfig( String self ) {
        configSlurper.parse( self )
    }

    static Object toJson( String self ) {
        jsonSlurper.parseText( self )
    }
}
