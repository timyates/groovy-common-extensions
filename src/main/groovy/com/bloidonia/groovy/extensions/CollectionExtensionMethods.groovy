package com.bloidonia.groovy.extensions

import org.codehaus.groovy.runtime.DefaultGroovyMethods
import static org.codehaus.groovy.runtime.DefaultGroovyMethodsSupport.createSimilarList

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
      self[new Random().nextInt(self.size())]
  }
}