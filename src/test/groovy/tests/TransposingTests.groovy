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
import spock.lang.Unroll

/**
 * @author Tim Yates
 * @author dwoods
 */
public class TransposingTests extends Specification {
  @Unroll( 'Transposing over #input with weights #amount should result in #expected' )
  def 'Check weighted transposing iterator'() {
    expect:
      expected == input.transposedIterator( amount ).collect()
    where:
      input           | amount || expected
      [1..4,'a'..'e'] | [1,1]  || [ 1,'a',2,'b',3,'c',4,'d','e' ]
      [1..4,'a'..'e'] | [1,2]  || [ 1,'a','b',2,'c','d',3,'e',4 ]
      [1..4,'a'..'e'] | [2,1]  || [ 1,2,'a',3,4,'b','c','d','e' ]
      [1..4,'a'..'e'] | [2,2]  || [ 1,2,'a','b',3,4,'c','d','e' ]
  }

  def 'Check default transposing iterator'() {
    expect:
      expected == input.transposedIterator().collect()
    where:
      input           || expected
      [1..4,'a'..'e'] || [ 1,'a',2,'b',3,'c',4,'d','e' ]
  }
}
