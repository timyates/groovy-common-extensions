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

package com.bloidonia.groovy.extensions

/**
 * @author Tim Yates
 */
class ObjectExtensionMethods {

  /**
   * Will execute closure passing 'self' as a parameter to the closure.
   *
   * When the closure has run, self will be flattened (if it is a list)
   * and close will be called on every item in it (if it responds to a
   * zero-arg version of 'close').
   *
   * <pre class="groovyTestCase">
   *   // A class that responds to close()
   *   class A {
   *     boolean isClosed = false
   *     def close() {
   *       isClosed = true
   *     }
   *   }
   *
   *   // Make an instance and test
   *   def obj = new A()
   *   def result = obj.withClosable {
   *     'cool'
   *   }
   *
   *   // Check the result
   *   assert result == 'cool'
   *   // Assert obj is closed
   *   assert obj.isClosed
   *
   *   def list = [ new A(), 'tim', new A() ]
   *   result = list.withClosable {
   *     'also cool'
   *   }
   *
   *   // Check the result
   *   assert result == 'also cool'
   *   // Assert all A instances are closed
   *   assert list.grep( A )*.isClosed.every()
   * </pre>
   *
   * @param self the object to call 'close' on after the closure has run
   * @param c the closure to run
   * @return the result of calling the closure
   */
  static Object withClosable( Object self, Closure c ) {
    try {
      c( self )
    }
    finally {
      [ self ].flatten().each {
        if( it.respondsTo( 'close' ) ) {
          it.close()
        }
      }
    }
  }
}