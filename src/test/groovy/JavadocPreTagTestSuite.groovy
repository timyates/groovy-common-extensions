import groovy.util.JavadocAssertionTestSuite
import junit.framework.Test
import junit.framework.TestCase
import junit.framework.TestSuite

class JavadocPreTagTestSuite extends TestCase {
  public static Test suite() {
    new TestSuite().with { suite ->
      suite.addTest( JavadocAssertionTestSuite.suite( 'src/main' ) )
      suite
    }
  }
}