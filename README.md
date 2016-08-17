# groovy-common-extensions

[![Build Status](https://travis-ci.org/timyates/groovy-common-extensions.png)](https://travis-ci.org/timyates/groovy-common-extensions)

Lets you add things I find commonly useful to the Groovy language via the extension
system.

Obviously requires at least Groovy 2.0.5 (so that the extension system exists)

Usage:

    @Grab( 'com.bloidonia:groovy-common-extensions:0.7.0' )

and the following methods will be available to you:

# Current extensions:

## `addImplicitConversion` (thanks MartyNeal)

    static void addImplicitConversion(Class self, Class to, Closure conversion)

Lets you overload the `asType` call in Groovy, so you can do:

    String.addImplicitConversion(Integer) { it.length() }
    assert ("foo" as Integer) == 3

## `clamp`

    static <T extends Comparable> T clamp( T self, T lower, T upper )
    static <T extends Comparable> T clamp( T self, Range range ) {
    static Range clamp( Range self, Range range ) {

Lets you do:

    println 10.clamp(  1, 15 )    // 10
    println 10.clamp(  1,  5 )    // 5
    println 10.clamp( 12, 20 )    // 12
    println 10.clamp( 12..20 )    // 12
    println (3..20).clamp( 2..9 ) // 3..9

Works with any comparable:

    println 'a'.clamp( 'b', 'z' ) // b

## Multi closure `sort`

    static Collection sort( Collection self, boolean mutate, Closure... closures )

Lets you do ([example from here](https://gist.github.com/3314416)):

    List list = [
      [id:0, firstName: 'Sachin', lastName: 'Tendulkar', age: 40 ],
      [id:1, firstName: 'Sachin', lastName: 'Tendulkar', age: 103 ],
      [id:2, firstName: 'Ajay', lastName: 'Tendulkar', age: 48 ],
      [id:3, firstName: 'Virendra', lastName: 'Sehwag', age: 5 ],
      [id:4, firstName: 'Virendra', lastName: 'Sehwag', age: 50 ],
      [id:5, firstName: 'Sachin', lastName: 'Nayyar', age: 15 ]
    ]

    // returns [2, 5, 0, 1, 3, 4]
    list.sort( false, { it.firstName }, { it.lastName }, { it.age } )*.id

## Negative index `take` (with Lists)

    static List take( List self, int n )

Lets you safely grab the end of a list, as you can with `take` for the front

    println [1,2,3,4].take( -2 ) // [3,4]

If you pass a positive number, it delegates to the original `DGM.take` method

    println [1,2,3,4].take( 2 ) // [1,2]

## `withClosable`

    static Object withClosable( Object self, Closure c ) {

Executes the closure (passing the delegate), and when finished it calls
`close` on the delegate if the method exists.  If it doesn't exist, it does
nothing.

Examples:

    // Create a FileWriter, close it when finished
    new FileWriter( '/tmp/d.txt' ).withClosable {
      it.println 'd'
    }

    // Create 3 FileWriters, close them all when done, and return
    // Closure result ('tim') to the rslt var
    def rslt = [ '/tmp/a.txt', '/tmp/b.txt', '/tmp/c.txt' ].collect {
      new FileWriter( it )
    }.withClosable { a, b, c ->
      a.println 'a'
      b.println 'b'
      c.println 'c'
      'tim'
    }
    assert rslt == 'tim'

    // This works, but does nothing
    'tim'.withClosable {
      println it
    }

## `tap`

    static Object tap( Object self, Closure c ) {

Taken [from Ruby](http://ruby-doc.org/core-2.0/Object.html#method-i-tap), the `tap` method executes the closure using the object as the
delegate - internally, it just calls `self.with c` and then it returns `self`.

This allows you to `tap` into a method chain:

    def m = (1..10)                         .tap { println "original ${it}" }
                   .findAll { it % 2 == 0 } .tap { println "evens    ${it}" }
                   .collect { it * it }     .tap { println "squares  ${it}" }
    // prints:
    //    original [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
    //    evens    [2, 4, 6, 8, 10]
    //    squares  [4, 16, 36, 64, 100]

    // and returns:
    assert m == [4, 16, 36, 64, 100]

## `zip` and `unzip`

    static File zip ( File self )
    static File zip ( File self, File destination )
    static File zip ( File self, Closure<Boolean> filter )
    static File zip ( File self, File destination, Closure<Boolean> filter )

    static Collection<File> unzip ( File self )
    static Collection<File> unzip ( File self, File destination )
    static Collection<File> unzip ( File self, Closure<Boolean> filter )
    static Collection<File> unzip ( File self, File destination, Closure<Boolean> filter )

Zips/unzips a single file or directory tree. If no destination is given, the target directory for the generated
 zip or extracted file(s) will be relative to the current file location.  If a filter is specified, then each file is passed to the closure before it is handled.  If the closure returns `false`, the file is skipped.

Examples:

    // zips the directory tree and creates a 'tmp.zip' file in '/var'
    File zipFile = new File('/var/tmp/').zip()

    // zips all *.txt files in the directory tree and creates a 'tmp.zip' file in '/var'
    File zipFile = new File('/var/tmp/').zip {
      it.name.endsWith '.txt'
    }

    // extracts the files to '/var/'
    Collection<File> extractedFiles = new File('/var/tmp.zip').unzip()

    // zips the directory content and moves 'tmp.zip' to '/home/bill'
    File zipFile = new File('/var/tmp/').zip(new File('/home/bill/tmp.zip')

    // extracts the files to '/home/bill/'
    Collection<File> extractedFiles = new File('/var/tmp.zip').unzip(new File('/home/bill'))

## `toMap` functionality for `NodeChild`

    static Map toMap( NodeChild self )
    static Map toMap( NodeChild self, String childKey )

Converts a `NodeChild` and its children to a `Map`.  Child nodes are by default put inside a
key `_children`, though this can be changed using the optional `childKey` parameter (if your
xml contains `_children` nodes for example)

Examples:

    def xmlstr = '<dan value="a"><subnode><item value="a"/></subnode></dan>'
    def xml = new XmlSlurper().parseText( xmlstr )
    def map = xml.toMap()

    assert map == [dan:[value:'a',_children:[[subnode:[_children:[[item:[value:'a']]]]]]]]

And with a `childKey`:

    def xmlstr = '<dan value="a"><subnode><item value="a"/></subnode></dan>'
    def xml = new XmlSlurper().parseText( xmlstr )
    def map = xml.toMap( 'kids' )

    assert map == [dan:[value:'a',kids:[[subnode:[kids:[[item:[value:'a']]]]]]]]

## `rand` functionality for `List` and `Iterator`

    static <T> T rand( List<T> self )
    static <T> List<T> rand( List<T> self, int n ) {
    static <T> List<T> rand( List<T> self, int n, boolean allowDuplicates ) {
    static <T> List<T> rand( List<T> self, int n, boolean allowDuplicates, Random r ) {

    static <T> T rand( Iterator<T> self )
    static <T> List<T> rand( Iterator<T> self, int n )
    static <T> List<T> rand( Iterator<T> self, int n, boolean allowDuplicates )
    static <T> List<T> rand( Iterator<T> self, int n, boolean allowDuplicates, Random r )

Randomly select an element from a list or iterator.

- The first form returns a single random element from the List or Iterator.
- The second form returns a `List` of `n` random elements from the List or Iterator (duplicates allowed)
- The third form allows you to specify no duplicates (by passing `false` as the third parameter) and returns a `List`
- The fourth form also returns a `List` and also allows you to set the Random object to be used in the processing.  This allows tests to specify a seed so reproducability is assured.

The returned list is of the same class as the input.

If you ask for `0` items, you get an empty list returned.

If you ask for more unique elements than there are items in the list, this throws an `IllegalArgumentException`

When `rand` is called on an Iterator, the entire Iterator is consumed until hasNext() is false and the returned elements are chosen from all the items iterated over with uniform probability.

Example:

    def list = [1, 2, 3, 4, 5]
    def randomInt = list.rand()
    assert randomInt in list

## Extended `merge` functionality for `ConfigObject`

    static ConfigObject merge( Map self, Map other, Boolean sourcePrecedence )

Boolean `sourcePrecedence` specifies that the source `ConfigObject` should not have it's existing key/value
pair overwritten by a merge with another `ConfigObject`

Example:

        def config1 = """
            config {
                a = 1
                b = 2
                c = 3
            }
        """
        def config2 = """
            config {
                a = 2
                b = 2
            }
        """

        def configObject1 = new ConfigSlurper().parse(config1)
        def configObject2 = new ConfigSlurper().parse(config2)
        def merge = configObject2.merge(configObject1)

        def configObject3 = new ConfigSlurper().parse(config1)
        def configObject4 = new ConfigSlurper().parse(config2)
        def mergeWithSourcePrecedence = configObject4.merge(configObject3,true)

        // overwrote config2's value
        assert merge.config.a == 1
        // preserved config2's value
        assert mergeWithSourcePrecedence.config.a == 2

        // config2 inherited value from config1
        assert merge.config.c == 3
        // config2 inherited from config1
        assert mergeWithSourcePrecedence.config.c == merge.config.c

## Transposed Iterator for List of Lists

    static <T> Iterator<T> transposedIterator( List<List<T>> lists )
    static <T> Iterator<T> transposedIterator( List<List<T>> lists, List<Integer> amounts )

Given a list of lists, ie:

    def a = [ 1..4, 'a'..'e' ]

you can (with Groovy) call `a.transpose()` ie:

    assert a.transpose() == [ [ 1,'a' ], [ 2,'b' ], [ 3,'c' ], [ 4,'d' ] ]

This new functionality returns an iterator, and you can specify how many reseults of each list to expect. So:

    // We need the collect as we want the values from the Iterator
    def b = a.transposedIterator().collect()

    // Note we get the extraneous 'e' that is lost by transpose()
    assert b = [ 1, 'a', 2, 'b', 3, 'c', 4, 'd', 'e' ]

And (passing a list of amounts):

    // So we want 1 element of list 1 followed by 2 of list 2 (repeated till exhaustion)
    def c = a.transposedIterator( [ 1, 2 ] ).collect()

    // Note we run out of list 2 after the 'e', so just get the last 4 from list 1
    assert c = [ 1, 'a', 'b', 2, 'c', 'd', 3, 'e', 4 ]

## Averages for collection of Numbers

    static <V extends Number> AverageStats<V> average( Collection<V> collection )

Given a collection of Numbers, ie:

    def a = 1..10

We can get the `mean`, `median`, `variance` and `stdDev` wrapped in an Immutable `AverageStats` class by simply calling:

    def stats = a.average()

    assert stats.mean     == 5.5
    assert stats.median   == 5.5
    assert stats.variance == 8.25
    assert String.format( '%.5g', avg.stdDev ) == '2.8723'

This is useful when we are assuming a collection of numbers is the same based on it's superficial criteria, ie given:

    def a = [1] * 50 + [100] * 50
    def b = 1..100

It is easy using simple (bogus) stats to say they are very similar:

    // Both print: "MIN 1 MAX 100 SIZE 100 MEAN 50.5"
    println "MIN ${a.min()} MAX ${a.max()} SIZE ${a.size()} MEAN ${a.sum() / 100}"
    println "MIN ${b.min()} MAX ${b.max()} SIZE ${b.size()} MEAN ${b.sum() / 100}"

However, the standard deviation shows that they are very different sequences of numbers:

    // prints: "AverageStats( mean:50.5, median:50.5, variance:2450.25, stdDev:49.5 )"
    println a.average()
    // prints: "AverageStats( mean:50.5, median:50.5, variance:833.25, stdDev:28.86607004772212 )"
    println b.average()

## byte[].hexdump()

    static void hexdump( byte[] self, Writer writer, int idx, int len )
    static void hexdump( byte[] self, int idx, int len )
    static void hexdump( byte[] self, Writer writer, int idx )
    static void hexdump( byte[] self, int idx )
    static void hexdump( byte[] self, Writer writer )
    static void hexdump( byte[] self )

Dumps a byte array out as hex in a readable form. You can also pass a `Writer` to print to this rather
than `System.out` (the default)

ie: calling:

    String output = new StringWriter().with { w ->
        bytes.hexdump( w )
        w.toString()
    }

Writes the following to `output`:

                +--------------------------------------------------+
                | 0  1  2  3  4  5  6  7   8  9  a  b  c  d  e  f  |
     +----------+--------------------------------------------------+------------------+
     | 00000000 | 48 65 6c 6c 6f 20 61 6e  64 20 77 65 6c 63 6f 6d | Hello and welcom |
     | 00000010 | 65 20 74 6f 20 e2 98 85  20 47 72 6f 6f 76 79    | e to ... Groovy  |
     +----------+--------------------------------------------------+------------------+

## String.toXml

    static GPathResult toXml( String self ) {
    static GPathResult toXml( String self, boolean validating, boolean namespaceAware)
    static GPathResult toXml( String self, boolean validating, boolean namespaceAware, boolean allowDocTypeDeclaration)
    static GPathResult toXml( String self, SAXParser parser)
    static GPathResult toXml( String self, XMLReader reader)

Will pass any String through `XmlSlurper` with the specified constructor parameters, ie:

    assert '<xml><name>Tim</name></xml>'.toXml().name == 'Tim'

## String.toConfig

    static ConfigObject toConfig( String self ) {

## String.toJson

    static Object toJson( String self ) {
