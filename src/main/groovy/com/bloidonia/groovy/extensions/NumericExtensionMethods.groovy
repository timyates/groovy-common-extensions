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
   * Clamp a Comparable between a lower and upper bound
   *
   * <pre class="groovyTestCase">
   *   assert 1.clamp( 3, 6 ) == 3
   * </pre>
   *
   * @param self   The Comparable to clamp
   * @param lower  The lower bound
   * @param upper  The upper bound
   * @return       The comparable clamped to a value between lower and upper
   */
  static <T extends Comparable> T clamp( T self, T lower, T upper ) {
    self < lower ? lower : self > upper ? upper : self
  }

  /**
   * Clamp a Comparable between the lower and upper bound of a Range
   *
   * <pre class="groovyTestCase">
   *   assert 1.clamp( 3..6 ) == 3
   * </pre>
   *
   * @param self   The Comparable to clamp
   * @param range  The range to clamp to
   * @return       The comparable clamped to a value between the lower and upper bounds of the Range
   */
  static <T extends Comparable> T clamp( T self, Range<T> range ) {
    clamp( self, (T)range.from, (T)range.to )
  }

  /**
   * Clamp a Range so that it falls within the bounds of another Range
   *
   * <pre class="groovyTestCase">
   *   assert (1..5).clamp( 3..6 ) == 3..5
   *   assert (1..8).clamp( 3..6 ) == 3..6
   *   assert (5..8).clamp( 3..6 ) == 5..6
   * </pre>
   *
   * @param self   The Range to clamp
   * @param range  The Range to clamp to
   * @return       The Range with its upper and lower bounds clamped to those of the clamp range
   */
  static <T extends Comparable> Range<T> clamp( Range<T> self, Range<T> range ) {
    T from = clamp( self.from, range.from, range.to )
    T to = clamp( self.to, range.from, range.to )
    self.class.newInstance( self.reverse ? to : from,
                            self.reverse ? from : to )
  }
}