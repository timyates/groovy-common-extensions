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

class TakeTests extends Specification {
  @Unroll( 'Taking #num from #list should give #expected')
  def 'check take with various inputs'() {
    expect:
      expected == list.take( num )
    where:
      list = [ 1, 2, 3, 4, 5 ]
      num | expected
      -6  | [ 1, 2, 3, 4, 5 ]
      -5  | [ 1, 2, 3, 4, 5 ]
      -4  | [    2, 3, 4, 5 ]
      -3  | [       3, 4, 5 ]
      -2  | [          4, 5 ]
      -1  | [             5 ]
       0  | [               ]
      -0  | [               ]
       1  | [ 1             ]
       2  | [ 1, 2          ]
       3  | [ 1, 2, 3       ]
       4  | [ 1, 2, 3, 4    ]
       5  | [ 1, 2, 3, 4, 5 ]
       6  | [ 1, 2, 3, 4, 5 ]
  }
}