/*
 * Copyright 2012 the original author or authors.
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

package tests

import spock.lang.Specification
import spock.lang.Unroll

public class ZipFileTests extends Specification {

    File dir, subdir
    File txt1, txt2, txt3

    def setup() {
        dir = new File(System.properties['java.io.tmpdir'] as String, 'test/')
        dir.mkdirs()

        txt1 = new File(dir, 'test1.txt')
        txt1.text = 'Test 1'

        txt2 = new File(dir, 'test2.txt')
        txt2.text = 'Test 2'

        subdir = new File(dir, 'sub')
        subdir.mkdirs()

        txt3 = new File(subdir, 'test3.txt')
        txt3.text = 'Test 3'
    }

    def cleanup() {
        dir.deleteDir()
    }

    def 'zip all files in the given directory'() {
        given:
            File result = dir.zip()

        expect:
            result == new File(dir.parent, "test.zip")
    }

    def 'zip specified files in the given directory'() {
        given:
            File result = dir.zip {
                it.name in [ 'test1.txt', 'test3.txt' ]
            }
            def files = result.unzip()

        expect:
            result == new File(dir.parent, "test.zip")
            files.size() == 2
            files*.name.sort() == [ 'test1.txt', 'test3.txt' ]
    }

    def 'zip and unzip filtered files in the given directory'() {
        given:
            File result = dir.zip {
                it.name in [ 'test1.txt', 'test3.txt' ]
            }
            def files = result.unzip {
                it.name == 'test3.txt'
            }

        expect:
            result == new File(dir.parent, "test.zip")
            files.size() == 1
            files*.name == [ 'test3.txt' ]
    }

    def 'zip a single file'() {
        given:
            File result = txt1.zip()

        expect:
            result == new File(dir, "test1.txt.zip")
    }

    def 'throw IllegalArgumentException if given destination is not a file'() {
        when:
            dir.zip(new File('xxx.txt'))

        then:
            IllegalArgumentException ex = thrown()
            ex.message == "'destination' has to be a *.zip file."
    }

    def 'put zip file to the given destination'() {
        when:
            def zippedFile = dir.zip(new File(dir, 'test.zip'))

        then:
            zippedFile == new File(dir, 'test.zip')
    }

    def 'unzip the file to the given directory'() {
        given:
            File zippedDir = dir.zip()
            def files = zippedDir.unzip()

        expect:
            files.size() == 3

            files[0].name.endsWith('test3.txt') && files[0].parent =~ "/test/sub"
            files[1].name.endsWith('test1.txt') && files[1].parent =~ "/test"
            files[2].name.endsWith('test2.txt') && files[2].parent =~ "/test"
    }

    def 'throw IllegalArgumentException if the file to be unzipped is not a zip file'() {
        when:
            txt1.unzip()

        then:
            IllegalArgumentException ex = thrown()
            ex.message == "File#unzip() has to be called on a *.zip file."
    }

    def 'unzip single file'() {
        given:
        File zippedFile = txt1.zip()
        def files = zippedFile.unzip()

        expect:
        files.size() == 1

        files[0].name.endsWith('test1.txt')
    }
}