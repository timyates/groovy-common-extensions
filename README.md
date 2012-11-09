# groovy-common-extensions

Lets you add things I find commonly useful to the Groovy language via the extension
system.

Obviously requires Groovy 2.0.5 (so that the extension system exists)

Usage:

    git clone git://github.com/timyates/groovy-common-extensions.git
    cd groovy-common-extensions
    ./gradlew jar

Then include the `build/lib/groovy-common-extensions-0.1.jar` in your classpath
when starting groovy and the following methods will be available to you:

**Currently a WIP to see how useful I find it...**

# Current extensions:

## `clamp`

    static <T extends Comparable> T clamp( T self, T lower, T upper )

Lets you do:

    println 10.clamp(  1, 15 )    // 10
    println 10.clamp(  1,  5 )    // 5
    println 10.clamp( 12, 20 )    // 12

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

## `zip` and `unzip`

    static File zip ( File self, File destination = null )
    static Collection<File> unzip ( File self, File destination = null )

Zips/unzips a single file or directory tree. If no destination is given, the target directory for the generated
 zip or extracted file(s) will be relative to the current file location.

Examples:

    // zips the directory tree and creates a 'tmp.zip' file in '/var'
    File zipFile = new File('/var/tmp/').zip()

    // extracts the files to '/var/'
    Collection<File> extractedFiles = new File('/var/tmp.zip').unzip()

    // zips the directory content and moves 'tmp.zip' to '/home/bill'
    File zipFile = new File('/var/tmp/').zip(new File('/home/bill/tmp.zip')

    // extracts the files to '/home/bill/'
    Collection<File> extractedFiles = new File('/var/tmp.zip').unzip(new File('/home/bill'))