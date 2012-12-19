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
class NumericExtensionMethods {
  /**
   * <pre class="groovyTestCase">
   *   assert 1.clamp( 3, 6 ) == 3
   * </pre>
   */
  static <T extends Comparable> T clamp( T self, T lower, T upper ) {
    self < lower ? lower : self > upper ? upper : self
  }

  static <T extends Comparable> T clamp( T self, Range range ) {
    clamp( self, range.from, range.to )
  }

  static Range clamp( Range self, Range range ) {
    self.class.newInstance( clamp( self.from, range.from, range.to ),
                            clamp( self.to, range.from, range.to ),
                            self.reverse )
  }
}