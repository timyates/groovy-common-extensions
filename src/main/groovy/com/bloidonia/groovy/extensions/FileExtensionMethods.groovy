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

package com.bloidonia.groovy.extensions

import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import groovy.io.FileType
import java.util.zip.ZipEntry

/**
 * Extends {@link File} with common extension methods not found in Groovy core (currently ;)).
 *
 * @see java.io.File
 */
class FileExtensionMethods {

    public static final String ZIP_EXTENSION = ".zip"

    static File zip ( File self )  {
        zip( self, null, null )
    }

    static File zip ( File self, File destination )  {
        zip( self, destination, null )
    }

    static File zip ( File self, Closure<Boolean> filter )  {
        zip( self, null, filter )
    }

    /**
     * Zips this {@link java.io.File} and places it in the optional <tt>destination</tt> directory. If the
     * <tt>destination</tt> directory is not provided, the resulting ZIP file will be placed in this file's
     * parent directory.
     *
     * @param self
     * @param destination (optional), the ZIP file as the outcome of calling this method. If not provided,
     *      destination will be created from this file's parent and file name (appending the *.zip extension).
     *
     * @return the zipped {@link java.io.File}
     */
    static File zip ( File self, File destination, Closure<Boolean> filter )  {

        checkZipDestination(destination)

        if (destination == null) {
            destination = new File(self.parent, self.name + ZIP_EXTENSION)
        }

        def zipOutput = new ZipOutputStream(new FileOutputStream(destination))
        final root = self.absolutePath - self.name

        def addToZipOutput = { File f, String path ->
            zipOutput.putNextEntry(new ZipEntry(path ? path + File.separator + f.name : f.name))
            zipOutput.write(f.bytes)
            zipOutput.closeEntry()
        }

        zipOutput.withStream {
            if (self.isDirectory())  {
                self.eachFileRecurse(FileType.FILES) {
                    if( filter == null || filter( it ) ) {
                        addToZipOutput(it, (it.absolutePath - it.name) - root)
                    }
                }
            } else {
                if( filter == null || filter( it ) ) {
                    addToZipOutput(self, "")
                }
            }
        }

        destination
    }

    private static void checkZipDestination( File file ) {
        boolean created = false
        if (file && !file.exists()) {
            file.createNewFile()
            created = true
        }
        try {
            if (file && !file.isFile()) throw new IllegalArgumentException("'destination' has to be a *.zip file.")
            if (file && !file.name.toLowerCase().endsWith(ZIP_EXTENSION)) throw new IllegalArgumentException("'destination' has to be a *.zip file.")
        }
        catch( e ) {
            if( created ) {
                file.delete()
            }
            throw e
        }
    }

    static Collection<File> unzip ( File self ) {
        unzip( self, null, null )
    }

    static Collection<File> unzip ( File self, File destination ) {
        unzip( self, destination, null )
    }

    static Collection<File> unzip ( File self, Closure<Boolean> filter ) {
        unzip( self, null, filter )
    }

    /**
     * Unzips this file. As a precondition, this file has to refer to a *.zip file. If the <tt>destination</tt>
     * directory is not provided, it will fall back to this file's parent directory.
     *
     * @param self
     * @param destination (optional), the destination directory where this file's content will be unzipped to.
     * @return a {@link java.util.Collection} of unzipped {@link java.io.File} objects.
     */
    static Collection<File> unzip ( File self, File destination, Closure<Boolean> filter ) {
        checkUnzipFileType(self)
        checkUnzipDestination(destination)

        // if destination directory is not given, we'll fall back to the parent directory of 'self'
        if (destination == null) destination = new File(self.parent)

        def unzippedFiles = []

        final zipInput = new ZipInputStream(new FileInputStream(self))
        zipInput.withStream {
            def entry
            while(entry = zipInput.nextEntry)  {
                if (!entry.isDirectory())  {
                    final file = new File(destination, entry.name)
                    if( filter == null || filter( file ) ) {
                        file.parentFile?.mkdirs()

                        def output = new FileOutputStream(file)
                        output.withStream {
                            int len = 0;
                            byte[] buffer = new byte[4096]
                            while ((len = zipInput.read(buffer)) > 0){
                                output.write(buffer, 0, len);
                            }
                        }

                        unzippedFiles << file
                    }
                }
                else {
                    final dir = new File(destination, entry.name)
                    if( filter == null || filter( file ) ) {
                        dir.mkdir()

                        unzippedFiles << dir
                    }
                }
            }
        }

        unzippedFiles
    }

    private static void checkUnzipFileType(File self) {
        if (!self.isFile()) throw new IllegalArgumentException("File#unzip() has to be called on a *.zip file.")

        def filename = self.name
        if (!filename.toLowerCase().endsWith(ZIP_EXTENSION)) throw new IllegalArgumentException("File#unzip() has to be called on a *.zip file.")
    }

    private static void checkUnzipDestination(File file) {
        if (file && !file.isDirectory()) throw new IllegalArgumentException("'destination' has to be a directory.")
    }
}
