package com.bloidonia.groovy.extensions

class ObjectExtensionMethods {

  static Object withClosable( Object self, Closure c ) {
    try {
      c( self )
    }
    finally {
      [ self ].flatten().each {
        if( it.respondsTo( 'close' ) ) {
          it.close()
        }
      }
    }
  }
}