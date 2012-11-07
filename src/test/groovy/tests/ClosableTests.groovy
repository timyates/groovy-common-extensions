package tests

import spock.lang.Specification

public class ClosableTests extends Specification {
  def 'check withClosable with test class'() {
    given:
      def a = new TestClass()
      def b = a.withClosable {
        "hello"
      }
    expect:
      b == 'hello'
      a.isClosed == true
  }
}

class TestClass {
  boolean isClosed = false
  void close() {
    isClosed = true
  }
}