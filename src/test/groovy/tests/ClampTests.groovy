package tests

import spock.lang.Specification
import spock.lang.Unroll

public class ClampTests extends Specification {
  @Unroll( 'Clamping #input between #lower and #upper should result in #expected' )
  def 'check clamping'() {
    expect:
      expected == input.clamp( lower, upper )
    where:
      input | lower | upper || expected
       1    |  3    |  5    ||   3
       2    |  3    |  5    ||   3
       3    |  3    |  5    ||   3
       4    |  3    |  5    ||   4
       5    |  3    |  5    ||   5
       6    |  3    |  5    ||   5
       2.0  |  2.1  |  2.4  ||   2.1
       2.3  |  2.1  |  2.4  ||   2.3
       2.5  |  2.1  |  2.5  ||   2.5
       'a'  |  'c'  |  'e'  ||   'c'
       'd'  |  'c'  |  'e'  ||   'd'
       'f'  |  'c'  |  'e'  ||   'e'
  }

  @Unroll( 'Range clamping #input to #range should result in #expected' )
  def 'check clamping'() {
    expect:
      expected == input.clamp( range )
    where:
      input | range      || expected
       1    |  3..5      ||   3
       2    |  3..5      ||   3
       3    |  3..5      ||   3
       4    |  3..5      ||   4
       5    |  3..5      ||   5
       6    |  3..5      ||   5
       1    |  5..3      ||   3
       2    |  5..3      ||   3
       3    |  5..3      ||   3
       4    |  5..3      ||   4
       5    |  5..3      ||   5
       6    |  5..3      ||   5
       2.0  |  2.1..2.4  ||   2.1
       2.3  |  2.1..2.4  ||   2.3
       2.5  |  2.1..2.5  ||   2.5
       'a'  |  'c'..'e'  ||   'c'
       'd'  |  'c'..'e'  ||   'd'
       'f'  |  'c'..'e'  ||   'e'
  }

  @Unroll( 'Range clamping #input to #range should result in #expected' )
  def 'check clamping'() {
    expect:
      expected == input.clamp( range )
    where:
      input      | range      || expected
       1..6      |  3..5      ||   3..5
       2..6      |  3..5      ||   3..5
       3..6      |  3..5      ||   3..5
       4..6      |  3..5      ||   4..5
       5..6      |  3..5      ||   5..5
       6..6      |  3..5      ||   5..5
       1..6      |  5..3      ||   3..5
       2..6      |  5..3      ||   3..5
       3..6      |  5..3      ||   3..5
       4..6      |  5..3      ||   4..5
       5..6      |  5..3      ||   5..5
       6..6      |  5..3      ||   5..5
       6..1      |  3..5      ||   5..3
       6..2      |  3..5      ||   5..3
       6..3      |  3..5      ||   5..3
       6..4      |  3..5      ||   5..4
       6..5      |  3..5      ||   5..5
       6..6      |  3..5      ||   5..5
       6..1      |  5..3      ||   5..3
       6..2      |  5..3      ||   5..3
       6..3      |  5..3      ||   5..3
       6..4      |  5..3      ||   5..4
       6..5      |  5..3      ||   5..5
       6..6      |  5..3      ||   5..5
       2.0..2.5  |  2.1..2.4  ||   2.1..2.4
       2.3..2.5  |  2.1..2.4  ||   2.3..2.4
       2.5..2.8  |  2.1..2.5  ||   2.5..2.5
       'a'..'f'  |  'c'..'e'  ||   'c'..'e'
       'd'..'f'  |  'c'..'e'  ||   'd'..'e'
       'f'..'b'  |  'c'..'e'  ||   'e'..'c'
  }
}
