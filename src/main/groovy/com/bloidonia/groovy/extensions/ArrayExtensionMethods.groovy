/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bloidonia.groovy.extensions

/**
 * @author Tim Yates
 */
class ArrayExtensionMethods {
    /**
     * Generate a hex-dump of a byte array.
     *
     * <pre class="groovyTestCase">
     *   byte[] bytes = "Hello and welcome to ★ Groovy".getBytes('UTF-8')
     *
     *   String output = new StringWriter().with { w ->
     *      bytes.hexdump( w )
     *      w.toString()
     *   }
     *
     *   String expected = '''            +--------------------------------------------------+
     *                       |            | 0  1  2  3  4  5  6  7   8  9  a  b  c  d  e  f  |
     *                       | +----------+--------------------------------------------------+------------------+
     *                       | | 00000000 | 48 65 6c 6c 6f 20 61 6e  64 20 77 65 6c 63 6f 6d | Hello and welcom |
     *                       | | 00000010 | 65 20 74 6f 20 e2 98 85  20 47 72 6f 6f 76 79    | e to ... Groovy  |
     *                       | +----------+--------------------------------------------------+------------------+
     *                       |'''.stripMargin()
     *   assert output == expected
     * </pre>
     *
     * @param self     the byte[] to dump
     * @param writer   the writer to dump to
     * @param idx      the index into self to start dumping from
     * @param len      the number of bytes to dump
     *
     * @since 0.5.7
     */
    static void hexdump( byte[] self, Writer writer, int idx, int len ) {
        writer.write '''            +--------------------------------------------------+
                       |            | 0  1  2  3  4  5  6  7   8  9  a  b  c  d  e  f  |
                       | +----------+--------------------------------------------------+------------------+\n'''.stripMargin()
        self[ idx..<(idx+len) ].with { bfr ->
            def bytes = bfr.collect { String.format( '%02x', it ) }
                           .collate( 8 )
                           .collate( 2 )
                           .collect { a, b -> ( a + [ '' ] + b ).join( ' ' ).padRight( 48, ' ' ) }
            def ascii = bfr.collect { it > 0x1f && it < 0x7f ? (char)it : '.' }
                           .collate( 16 )
                           .collect { it.join().padRight( 16, ' ' ) }
            int offs = idx
            [bytes,ascii].transpose().each { b, a ->
                writer.write " | ${String.format( '%08x', offs )} | ${b} | ${a} |\n"
                offs += 16 
            }
        }
        writer.write   ' +----------+--------------------------------------------------+------------------+\n'
        writer.flush()
    }

    /**
     * Generate a hex-dump of a byte array.
     *
     * @param self     the byte[] to dump
     * @param idx      the index into self to start dumping from
     * @param len      the number of bytes to dump
     *
     * @since 0.5.7
     */
    static void hexdump( byte[] self, int idx, int len ) {
        hexdump( self, new PrintWriter( System.out ), idx, len )
    }

    /**
     * Generate a hex-dump of a byte array.
     *
     * @param self     the byte[] to dump
     * @param writer   the writer to dump to
     * @param idx      the index into self to start dumping from
     *
     * @since 0.5.7
     */
    static void hexdump( byte[] self, Writer writer, int idx ) {
        hexdump( self, writer, idx, self.length )
    }

    /**
     * Generate a hex-dump of a byte array.
     *
     * @param self     the byte[] to dump
     * @param idx      the index into self to start dumping from
     *
     * @since 0.5.7
     */
    static void hexdump( byte[] self, int idx ) {
        hexdump( self, new PrintWriter( System.out ), idx, self.length )
    }

    /**
     * Generate a hex-dump of a byte array.
     *
     * @param self     the byte[] to dump
     * @param writer   the writer to dump to
     *
     * @since 0.5.7
     */
    static void hexdump( byte[] self, Writer writer ) {
        hexdump( self, writer, 0, self.length )
    }

    /**
     * Generate a hex-dump of a byte array.
     *
     * @param self     the byte[] to dump
     *
     * @since 0.5.7
     */
    static void hexdump( byte[] self ) {
        hexdump( self, new PrintWriter( System.out ), 0, self.length )
    }
}