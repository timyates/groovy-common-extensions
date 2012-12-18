package tests

import spock.lang.Specification

/**
 * @author dwoods
 * @author timyates
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
}
