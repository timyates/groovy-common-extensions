package com.bloidonia.groovy.extensions

class NumericExtensionMethods {
  static <T extends Comparable> T clamp( T self, T lower, T upper ) {
    self < lower ? lower : self > upper ? upper : self
  }

  static <T extends Comparable> T clamp( T self, Range range ) {
    clamp( self, range.from, range.to )
  }

  static Range clamp( Range self, Range range ) {
    self.class.newInstance( clamp( self.from, range.from, range.to ),
                            clamp( self.to, range.from, range.to ),
                            self.reverse )
  }
}