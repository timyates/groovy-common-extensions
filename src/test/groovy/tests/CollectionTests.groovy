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

/**
 * @author dwoods
 * @author Tim Yates
 * @date 11/27/12
 */
class CollectionTests extends Specification {
  def 'check random list element'() {
    given:
      def list = 0..99
    expect:
      list.rand() in list
  }

  def 'check multiple random list elements'() {
    given:
      def list = 0..99
    expect:
      list.rand( 3 ).every { it in list }
  }

  def 'check n=size actually does something'() {
    given:
      def list = 0..9
      def results = (1..10).collect {
        list.rand( list.size(), false, new Random( it ) )
      }
    expect:
      results.each { println it }
      results.size() == 10
      results.unique().size() != 1
  }

  def 'check reproducability'() {
    given:
      def list = 0..99

      // No duplicates
      def randomsA = list.rand( 3, false, new Random( 4 ) )
      def randomsB = list.rand( 3, false, new Random( 4 ) )

      // With duplicates
      def randomsC = list.rand( 3, true, new Random( 4 ) )
      def randomsD = list.rand( 3, true, new Random( 4 ) )
    expect:
      randomsA == randomsB
      randomsC == randomsD
  }

  def 'cannot get more unique results than there are results'() {
    setup:
      def list = 0..5
    when:
      list.rand( 7, false )
    then:
      IllegalArgumentException ex = thrown()
      ex.message == 'Cannot have 7 unique items from a list containing only 6'
  }

  def 'check unique results are unique'() {
    when:
      def size = list.size()
      def rand = list.rand( size, false )
      def uniq = rand.unique()

    then:
      uniq.class == clazz

    and:
      uniq.size() == size

    where:
      list                | clazz
      1..10               | ArrayList
      1..10 as LinkedList | LinkedList
      1..10 as Vector     | Vector
  }

  def 'check random itertor elements'() {
    given:
      def range = 1..10
      def iter = range.iterator()
    expect:
      iter.rand() in range
  }

  def 'check multiple random iterator elements'() {
    given:
      def list = 0..99
    expect:
      list.iterator().rand( 3 ).every { it in list }
  }

  def 'check uniqueness of random iterator elements'() {
    given:
      def list = 0..99
      def size = list.size()
      def rand = list.iterator().rand( size, false )
    expect:
      rand.unique()
  }

  def 'check integer averages'() {
    given:
      def avg = [ 332, 42, 100 ].average()
    expect:
      avg.mean     == 158
      avg.median   == 100
      String.format( '%.7g', avg.variance ) == '15698.67'
      String.format( '%.6g', avg.stdDev )   == '125.294'
  }

  def 'check integer averages with even number of elements'() {
    given:
      def avg = [ 332, 998, 42, 100 ].average()
    expect:
      avg.mean     == 368
      avg.median   == 216
      String.format( '%.7g', avg.variance ) == '144074.0'
      String.format( '%.6g', avg.stdDev )   == '379.571'
  }

  def 'check BigInteger averages'() {
    given:
      def avg = [ 332G, 42G, 100G ].average()
    expect:
      avg.mean     == 158
      avg.median   == 100
      String.format( '%.7g', avg.variance ) == '15698.67'
      String.format( '%.6g', avg.stdDev )   == '125.294'
  }

  def 'check BigDecimal averages with even number of elements'() {
    given:
      def avg = [ 332.0G, 998.0G, 42.0G, 100.0G ].average()
    expect:
      avg.mean     == 368
      avg.median   == 216
      String.format( '%.7g', avg.variance ) == '144074.0'
      String.format( '%.6g', avg.stdDev )   == '379.571'
  }

  def 'check zero length averages'() {
    given:
      def avg = [].average()
    expect:
      avg.mean     == 0
      avg.median   == 0
      avg.variance == 0
      avg.stdDev   == 0
  }
}