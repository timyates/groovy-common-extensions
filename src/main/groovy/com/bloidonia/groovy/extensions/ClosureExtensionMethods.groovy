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

/**
 * @author Tim Yates
 */
class ClosureExtensionMethods {

    /**
     * Set a Closure's delegate
     * <pre class="groovyTestCase">
     *   def testClosure = { num ->
     *       num + val
     *   }
     *
     *   def value = testClosure.withDelegate( [ val: 4 ] )( 3 )
     *   assert value == 7
     * </pre>
     *
     * @param self     the Closure
     * @param delegate the object to delegate to
     * @return         the closure but with its delegate set
     */
    static <T> Closure<T> withDelegate( Closure<T> self, Object delegate ) {
        self.delegate = delegate
        self
    }

    /**
     * Set a Closure's delegate and resolveStrategy
     * @param self     the Closure
     * @param delegate the object to delegate to
     * @param strategy the resolveStrategy to use
     * @return         the closure but with its delegate and strategy set
     */
    static <T> Closure<T> withDelegate( Closure<T> self, Object delegate, int strategy ) {
        self.delegate = delegate
        self.resolveStrategy = strategy
        self
    }
}