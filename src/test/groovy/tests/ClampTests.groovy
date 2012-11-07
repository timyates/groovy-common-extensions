package tests

import spock.lang.Specification
import spock.lang.Unroll

public class ClampTests extends Specification {
  @Unroll( 'Clamping #input between #lower and #upper should result in #expected' )
  def 'check clamping'() {
    expect:
      expected == input.clamp( lower, upper )
    where:
      input | lower | upper | expected
       1   | 3   | 5   | 3
       2   | 3   | 5   | 3
       3   | 3   | 5   | 3
       4   | 3   | 5   | 4
       5   | 3   | 5   | 5
       6   | 3   | 5   | 5
       2.0 | 2.1 | 2.4 | 2.1
       2.3 | 2.1 | 2.4 | 2.3
       2.5 | 2.1 | 2.5 | 2.5
       'a' | 'c' | 'e' | 'c'
       'd' | 'c' | 'e' | 'd'
       'f' | 'c' | 'e' | 'e'
  }
}
