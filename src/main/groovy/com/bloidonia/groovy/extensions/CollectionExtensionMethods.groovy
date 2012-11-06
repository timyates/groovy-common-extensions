package com.bloidonia.groovy.extensions

class CollectionExtensionMethods {
  static Collection sort( Collection self, boolean mutate, Closure... closures ) {
    self.sort( mutate ) { a, b ->
      closures.findResult { cls -> cls( a ) <=> cls( b ) ?: null }
    }
  }
}