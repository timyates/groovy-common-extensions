package tests

import spock.lang.Specification
import spock.lang.Unroll

public class TakeTests extends Specification {
  @Unroll( 'Taking #num from #list should give #expected')
  def 'check take with various inputs'() {
    expect:
      expected == list.take( num )
    where:
      list = [ 1, 2, 3, 4, 5 ]
      num | expected
      -6  | [ 1, 2, 3, 4, 5 ]
      -5  | [ 1, 2, 3, 4, 5 ]
      -4  | [    2, 3, 4, 5 ]
      -3  | [       3, 4, 5 ]
      -2  | [          4, 5 ]
      -1  | [             5 ]
       0  | [               ]
      -0  | [               ]
       1  | [ 1             ]
       2  | [ 1, 2          ]
       3  | [ 1, 2, 3       ]
       4  | [ 1, 2, 3, 4    ]
       5  | [ 1, 2, 3, 4, 5 ]
       6  | [ 1, 2, 3, 4, 5 ]
  }
}