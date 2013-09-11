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

import org.codehaus.groovy.runtime.DefaultGroovyMethods
import static org.codehaus.groovy.runtime.DefaultGroovyMethodsSupport.createSimilarList

/**
 * @author Tim Yates
 * @author dwoods
 * @author Andrew Taylor
 */
class CollectionExtensionMethods {
  /**
   * Sort a list based on a collection of Closures.  If the first closure fails to differentiate the objects in the
   * list, it moves to the second, and continues until all Closures have been executed.
   *
   * @param self      The collection to sort
   * @param mutate    Should the original list be mutated
   * @param closures  The list of Closures used to sort the Collection
   * @return          A sorted collection
   */
  static Collection sort( Collection self, boolean mutate, Closure... closures ) {
    self.sort( mutate ) { a, b ->
      closures.findResult { cls -> cls( a ) <=> cls( b ) ?: null }
    }
  }

  /**
   * Take up to n items from a List.  If n is negative, take off the end of the list, else delegate to the existing
   * Groovy take method.
   *
   * @param self  The collection to take items from.
   * @param n     The number of elements to take.
   * @return      A list of elements up to n long.
   */
  static List take( List self, int n ) {
    int absn = Math.abs( n )
    if( n >= 0 || absn == self.size() ) {
      // Shouldn't be doing this, but I can't think of another way to delegate back to DGM
      DefaultGroovyMethods.take( self, absn )
    }
    else {
      createSimilarList( self, NumericExtensionMethods.clamp( absn, 0, self.size() ) ).with { List ret ->
        ret.addAll( self[ (n.clamp(-self.size(),0))..-1 ] )
        ret
      }
    }
  }

  /**
   * Select a single random element from a List
   *
   * @param self The List to select from
   * @return     A random element from the List
   */
  static <T> T rand( List<T> self ) {
    rand( self, 1, true, new Random() )[0]
  }

  /**
   * Select a list of n unique items from the List
   *
   * @param self The List to select from
   * @param n    The number of items to select
   * @return     A List containing the random elements
   */
  static <T> List<T> rand( List<T> self, int n ) {
    rand( self, n, false, new Random() )
  }

  /**
   * Select a list of n items from the List
   *
   * @param self             The List to select from
   * @param n                The number of items to select
   * @param allowDuplicates  If true, the same element can be selected more than once.
   * @return                 A List containing the random elements
   */
  static <T> List<T> rand( List<T> self, int n, boolean allowDuplicates ) {
    rand( self, n, allowDuplicates, new Random() )
  }

  /**
   * Select a list of n items from the List
   *
   * @param self             The List to select from
   * @param n                The number of items to select
   * @param allowDuplicates  If true, the same element can be selected more than once.
   * @param r                An instance of Random so we can set a seed to get reproducible random results
   * @return
   */
  static <T> List<T> rand( List<T> self, int n, boolean allowDuplicates, Random r ) {
    List<T> ret = createSimilarList( self, 0 )
    if( n <= 0 ) {
      return ret
    }
    if( allowDuplicates ) {
      (1..n).each {
        ret << self[ r.nextInt( self.size() ) ]
      }
    }
    else {
      if( n > self.size() ) {
        throw new IllegalArgumentException( "Cannot have $n unique items from a list containing only ${self.size()}" )
      }
      int remain = self.size()
      int i = 0
      while( n > 0 ) {
        if( r.nextInt( remain ) < n ) {
          ret << self[ i ]
          n--
        }
        remain--
        i++
      }
    }
    ret
  }

  /**
   * Select a single random element from an Iterator
   *
   * @param self The Iterator to select from
   * @return     A random element from the List
   */
  static <T> T rand( Iterator<T> self ) {
    rand( self, 1, true, new Random() )[0]
  }

  /**
   * Select a list of n unique items from the Iterator
   *
   * @param self The Iterator to select from
   * @param n    The number of items to select
   * @return     A List containing the random elements
   */
  static <T> List<T> rand( Iterator<T> self, int n ) {
    rand( self, n, false, new Random() )
  }

  /**
   * Select a list of n items from the Iterator
   *
   * @param self             The Iterator to select from
   * @param n                The number of items to select
   * @param allowDuplicates  If true, the same element can be selected more than once.
   * @return                 A List containing the random elements
   */
  static <T> List<T> rand( Iterator<T> self, int n, boolean allowDuplicates ) {
    rand( self, n, allowDuplicates, new Random() )
  }

  /**
   * Select a list of n random items from the Iterator
   *
   * @param self             The Iterator to select from
   * @param n                The number of items to select
   * @param allowDuplicates  If true, the same element can be selected more than once.
   * @param r                An instance of Random so we can set a seed to get reproducible random results
   * @return                 A List containing the random elements
   */
  static <T> List<T> rand( Iterator<T> self, int n, boolean allowDuplicates, Random rnd ) {
    List<T> result

    if (allowDuplicates) {

      int i = 2
      result = [self.next()] * n 
      while (self.hasNext()) {
        T item = self.next()
        for (int j = 0; j < n; j++) {
          if (rnd.nextInt(i) == 0) {
            result[j] = item
          }
        }
        i++
      }

    } else {

      result = self.take(n).toList()
      Collections.shuffle(result)
      int i = n + 1
      while( self.hasNext() ) {
        int j = rnd.nextInt(i)
        T item = self.next()
        if( j < n ) {
          result[j] = item
        }
        i++
      }
    }

    result
  }

  static class TransposingIterator<T> implements Iterator<T> {
    private int idx = 0
    private List<Iterator<T>> iterators
    private List<Integer> weights
   
    TransposingIterator( List<List<T>> lists, List<Integer> amounts ) {
      iterators = lists*.iterator()
      weights = [amounts,0..<amounts.size()].transpose()
                                            .collect { List<Integer> it ->
        [ it[ 1 ] ] * it[ 0 ]
      }.flatten()
    }
   
    private void moveIdx() {
      idx = ++idx % weights.size()
    }
   
    @Override boolean hasNext() {
      iterators*.hasNext().any()
    }
   
    @Override T next() {
      if( !hasNext() ) { throw new NoSuchElementException() }
      while( !iterators[ weights[ idx ] ].hasNext() ) { moveIdx() }
      T ret = iterators[ weights[ idx ] ].next()
      moveIdx()
      ret
    }
   
    @Override void remove() {
      throw new UnsupportedOperationException()
    }
  }

  /**
   * Return a TransposingIterator that returns an element from each list in turn.
   *
   * <pre class="groovyTestCase">
   *   def left  = [   1,   2,   3      ]
   *   def right = [ 'a', 'b', 'c', 'd' ]
   *
   *   def result = [left,right].transposedIterator().collect()
   *
   *   assert result == [ 1, 'a', 2, 'b', 3, 'c', 'd' ]
   * </pre>
   *
   * @param lists A List of Lists to cycle over
   * @return      An Iterator that can be used to fetch an item from each list in turn
   */
  static <T> Iterator<T> transposedIterator( List<List<T>> lists ) {
    transposedIterator( lists, [ 1 ] * lists.size() )
  }

  /**
   * Return a TransposingIterator that returns an element from each list in turn.
   *
   * <pre class="groovyTestCase">
   *   def left  = [   1,   2,   3      ]
   *   def right = [ 'a', 'b', 'c', 'd' ]
   *
   *   def result = [left,right].transposedIterator( [ 1, 2 ] ).collect()
   *
   *   assert result == [ 1, 'a', 'b', 2, 'c', 'd', 3 ]
   * </pre>
   *
   * @param lists    A List of Lists to cycle over
   * @param amounts  The number to take from each list
   * @return         An Iterator that can be used to fetch an item from each list in turn
   */
  static <T> Iterator<T> transposedIterator( List<List<T>> lists, List<Integer> amounts ) {
    new TransposingIterator( lists, amounts )
  }
}