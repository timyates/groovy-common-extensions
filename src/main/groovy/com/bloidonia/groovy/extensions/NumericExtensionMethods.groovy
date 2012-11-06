package com.bloidonia.groovy.extensions

class NumericExtensionMethods {
  static <T extends Comparable> T clamp( T self, T lower, T upper ) {
    self < lower ? lower : self > upper ? upper : self
  }
}