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
 */
class CollectionExtensionMethods {
  static Collection sort( Collection self, boolean mutate, Closure... closures ) {
    self.sort( mutate ) { a, b ->
      closures.findResult { cls -> cls( a ) <=> cls( b ) ?: null }
    }
  }

  static List take( List self, int n ) {
    int absn = Math.abs( n )
    if( n >= 0 || absn == self.size() ) {
      // Shouldn't be doing this, but I can't think of another way to delegate back to DGM
      DefaultGroovyMethods.take( self, absn )
    }
    else {
      createSimilarList( self, absn.clamp( 0, self.size() ) ).with { ret ->
        ret.addAll( self[ (n.clamp(-self.size(),0))..-1 ] )
        ret
      }
    }
  }

  static <T> T rand( List<T> self ) {
    rand( self, 1, true, new Random() )[0]
  }

  static <T> List<T> rand( List<T> self, int n ) {
    rand( self, n, false, new Random() )
  }

  static <T> List<T> rand( List<T> self, int n, boolean allowDuplicates ) {
    rand( self, n, allowDuplicates, new Random() )
  }

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

  static class TransposingIterator<T> implements Iterator<T> {
    private int idx = 0
    private List<Iterator> iter
    private List<Integer>  amts
   
    TransposingIterator( List<List> lists, List<Integer> amounts ) {
      iter = lists*.iterator()
      int i = 0
      amts = amounts.collectMany { [ i++ ] * it }
    }
   
    private void moveIdx() {
      idx = ++idx % amts.size()
    }
   
    @Override boolean hasNext() {
      iter*.hasNext().any()
    }
   
    @Override T next() {
      if( !hasNext() ) { throw new NoSuchElementException() }
      while( !iter[ amts[ idx ] ].hasNext() ) { moveIdx() }
      T ret = iter[ amts[ idx ] ].next()
      moveIdx()
      ret
    }
   
    @Override void remove() {
      throw new UnsupportedOperationException()
    }
  }

  static <T> Iterator<T> transposedIterator( List<List<T>> lists ) {
    transposedIterator( lists, [ 1 ] * lists.size() )
  }

  static <T> Iterator<T> transposedIterator( List<List<T>> lists, List<Integer> amounts ) {
    new TransposingIterator( lists, amounts )
  }
}