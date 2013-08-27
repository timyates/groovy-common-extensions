# groovy-common-extensions

[![Build Status](https://travis-ci.org/timyates/groovy-common-extensions.png)](https://travis-ci.org/timyates/groovy-common-extensions)

Lets you add things I find commonly useful to the Groovy language via the extension
system.

Obviously requires at least Groovy 2.0.5 (so that the extension system exists)

Usage:

    @Grab( 'com.bloidonia:groovy-common-extensions:0.5.1' )

and the following methods will be available to you:

# Current extensions:

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

## `rand` functionality for `List`

    static <T> T rand( List<T> self )
    static <T> List<T> rand( List<T> self, int n ) {
    static <T> List<T> rand( List<T> self, int n, boolean allowDuplicates ) {
    static <T> List<T> rand( List<T> self, int n, boolean allowDuplicates, Random r ) {

Randomly select an element from a list.

- The first form returns a single random element from the List.
- The second form returns a `List` of `n` random elements from the List (duplicates allowed)
- The third form allows you to specify no duplicates (by passing `false` as the third parameter) and returns a `List`
- The fourth form also returns a `List` and also allows you to set the Random object to be used in the processing.  This allows tests to specify a seed so reproducability is assured.

The returned list is of the same class as the input.

If you ask for `0` items, you get an empty list returned.

If you ask for more unique elements than there are items in the list, this throws an `IllegalArgumentException`

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
