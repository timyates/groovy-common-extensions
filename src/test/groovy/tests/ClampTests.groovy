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

class ClampTests extends Specification {
  @Unroll( 'Clamping #input between #lower and #upper should result in #expected' )
  def 'check clamping'() {
    expect:
      expected == input.clamp( lower, upper )
    where:
      input | lower | upper || expected
       1    |  3    |  5    ||   3
       2    |  3    |  5    ||   3
       3    |  3    |  5    ||   3
       4    |  3    |  5    ||   4
       5    |  3    |  5    ||   5
       6    |  3    |  5    ||   5
       2.0  |  2.1  |  2.4  ||   2.1
       2.3  |  2.1  |  2.4  ||   2.3
       2.5  |  2.1  |  2.5  ||   2.5
       'a'  |  'c'  |  'e'  ||   'c'
       'd'  |  'c'  |  'e'  ||   'd'
       'f'  |  'c'  |  'e'  ||   'e'
  }

  @Unroll( 'Range clamping #input to #range should result in #expected' )
  def 'check clamping by range'() {
    expect:
      expected == input.clamp( range )
    where:
      input | range      || expected
       1    |  3..5      ||   3
       2    |  3..5      ||   3
       3    |  3..5      ||   3
       4    |  3..5      ||   4
       5    |  3..5      ||   5
       6    |  3..5      ||   5
       1    |  5..3      ||   3
       2    |  5..3      ||   3
       3    |  5..3      ||   3
       4    |  5..3      ||   4
       5    |  5..3      ||   5
       6    |  5..3      ||   5
       2.0  |  2.1..2.4  ||   2.1
       2.3  |  2.1..2.4  ||   2.3
       2.5  |  2.1..2.5  ||   2.5
       'a'  |  'c'..'e'  ||   'c'
       'd'  |  'c'..'e'  ||   'd'
       'f'  |  'c'..'e'  ||   'e'
  }

  @Unroll( 'Range clamping #input range to #range should result in #expected' )
  def 'check range clamping by range'() {
    expect:
      expected == input.clamp( range )
    where:
      input      | range      || expected
       1..6      |  3..5      ||   3..5
       2..6      |  3..5      ||   3..5
       3..6      |  3..5      ||   3..5
       4..6      |  3..5      ||   4..5
       5..6      |  3..5      ||   5..5
       6..6      |  3..5      ||   5..5
       1..6      |  5..3      ||   3..5
       2..6      |  5..3      ||   3..5
       3..6      |  5..3      ||   3..5
       4..6      |  5..3      ||   4..5
       5..6      |  5..3      ||   5..5
       6..6      |  5..3      ||   5..5
       6..1      |  3..5      ||   5..3
       6..2      |  3..5      ||   5..3
       6..3      |  3..5      ||   5..3
       6..4      |  3..5      ||   5..4
       6..5      |  3..5      ||   5..5
       6..6      |  3..5      ||   5..5
       6..1      |  5..3      ||   5..3
       6..2      |  5..3      ||   5..3
       6..3      |  5..3      ||   5..3
       6..4      |  5..3      ||   5..4
       6..5      |  5..3      ||   5..5
       6..6      |  5..3      ||   5..5
       2.0..2.5  |  2.1..2.4  ||   2.1..2.4
       2.3..2.5  |  2.1..2.4  ||   2.3..2.4
       2.5..2.8  |  2.1..2.5  ||   2.5..2.5
       'a'..'f'  |  'c'..'e'  ||   'c'..'e'
       'd'..'f'  |  'c'..'e'  ||   'd'..'e'
       'f'..'b'  |  'c'..'e'  ||   'e'..'c'
  }
}
