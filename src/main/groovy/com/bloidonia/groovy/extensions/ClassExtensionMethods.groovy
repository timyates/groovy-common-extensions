/*
 * Copyright 2016 the original author or authors.
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

/**
 * @author MartyNeal
 */
class ClassExtensionMethods {
    /**
     * Adds an implicit conversion to a class's meta class to another type.
     *
     * @param self The source class type
     * @param to When this conversion should be used
     * @param conversion The mechanism used to do the conversion
     * @since 0.7.0
     * @example
     * <pre class="groovyTestCase">
     *    // add an implicit conversion from String to Integer that uses the string length
     *    String.addImplicitConversion(Integer) { it.length() }
     *    assert ("foo" as Integer) == 3
     * </pre>
     */
    static void addImplicitConversion(Class self, Class to, Closure conversion) {
        def oldAsType = self.metaClass.getMetaMethod('asType', [] as Class[])
        self.metaClass.asType = { Class clazz ->
            (clazz == to) ?
                conversion(delegate) :
                oldAsType.doMethodInvoke(delegate, clazz)
        }
    }
}
