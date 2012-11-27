# groovy-common-extensions

Lets you add things I find commonly useful to the Groovy language via the extension
system.

Obviously requires Groovy 2.0.5 (so that the extension system exists)

Usage:

    git clone git://github.com/timyates/groovy-common-extensions.git
    cd groovy-common-extensions
    ./gradlew jar

Then include the `build/lib/groovy-common-extensions-0.2.jar` in your classpath
when starting groovy and the following methods will be available to you:

**Currently a WIP to see how useful I find it...**

# Current extensions:

## `clamp`

    static <T extends Comparable> T clamp( T self, T lower, T upper )
    static <T extends Comparable> T clamp( T self, Range range ) {
    static Range clamp( Range self, Range range ) {

Lets you do:

<code>
```groovy
    println 10.clamp(  1, 15 )    // 10
    println 10.clamp(  1,  5 )    // 5
    println 10.clamp( 12, 20 )    // 12
    println 10.clamp( 12..20 )    // 12
    println (3..20).clamp( 2..9 ) // 3..9
```
</code>

Works with any comparable:

<code>
```groovy
    println 'a'.clamp( 'b', 'z' ) // b
```
</code>

## Multi closure `sort`

    static Collection sort( Collection self, boolean mutate, Closure... closures )

Lets you do ([example from here](https://gist.github.com/3314416)):

<code>
```groovy
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
```
</code>

## Negative index `take` (with Lists)

    static List take( List self, int n )

Lets you safely grab the end of a list, as you can with `take` for the front

<code>
```groovy
    println [1,2,3,4].take( -2 ) // [3,4]
```
</code>

If you pass a positive number, it delegates to the original `DGM.take` method

<code>
```groovy
    println [1,2,3,4].take( 2 ) // [1,2]
```
</code>

## `withClosable`

    static Object withClosable( Object self, Closure c ) {

Executes the closure (passing the delegate), and when finished it calls
`close` on the delegate if the method exists.  If it doesn't exist, it does
nothing.

Examples:

<code>
```groovy
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
```
</code>

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

<code>
```groovy
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
```
</code>

## `toMap` functionality for `NodeChild`

    static Map toMap( NodeChild self )
    static Map toMap( NodeChild self, String childKey )

Converts a `NodeChild` and its children to a `Map`.  Child nodes are by default put inside a
key `_children`, though this can be changed using the optional `childKey` parameter (if your
xml contains `_children` nodes for example)

Examples:

<code>
```groovy
    def xmlstr = '<dan value="a"><subnode><item value="a"/></subnode></dan>'
    def xml = new XmlSlurper().parseText( xmlstr )
    def map = xml.toMap()

    assert map == [dan:[value:'a',_children:[[subnode:[_children:[[item:[value:'a']]]]]]]]
```
</code>

And with a `childKey`:

<code>
```groovy
    def xmlstr = '<dan value="a"><subnode><item value="a"/></subnode></dan>'
    def xml = new XmlSlurper().parseText( xmlstr )
    def map = xml.toMap( 'kids' )

    assert map == [dan:[value:'a',kids:[[subnode:[kids:[[item:[value:'a']]]]]]]]
```
</code>

## Extended `merge` functionality for `ConfigObject`

    static ConfigObject merge( Map self, Map other, Boolean sourcePrecedence )

Boolean `sourcePrecedence` specifies that the source `ConfigObject` should not have it's existing key/value
pair overwritten by a merge with another `ConfigObject`

Example:

<code>
```groovy
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
 ```
 </code>

## `rand` functionality for `List`

    static <T> T rand( List<T> self )

Randomly select an element from a list.

Example:

```groovy
    def list = [1, 2, 3, 4, 5]
    def randomInt = list.rand()
    assert randomInt in list
```