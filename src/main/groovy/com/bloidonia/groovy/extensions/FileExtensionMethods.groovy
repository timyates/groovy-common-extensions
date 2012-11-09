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
    static File zip ( File self, File destination = null )  {

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
                    addToZipOutput(it, (it.absolutePath - it.name) - root)
                }
            } else {
                addToZipOutput(self, "")
            }
        }

        destination
    }

    private static void checkZipDestination( File file ) {
        if (file && !file.exists()) file.createNewFile()
        if (file && !file.isFile()) throw new IllegalArgumentException("'destination' has to be a *.zip file.")
        if (file && !file.name.toLowerCase().endsWith(ZIP_EXTENSION)) throw new IllegalArgumentException("'destination' has to be a *.zip file.")
    }

    /**
     * Unzips this file. As a precondition, this file has to refer to a *.zip file. If the <tt>destination</tt>
     * directory is not provided, it will fall back to this file's parent directory.
     *
     * @param self
     * @param destination (optional), the destination directory where this file's content will be unzipped to.
     * @return a {@link java.util.Collection} of unzipped {@link java.io.File} objects.
     */
    static Collection<File> unzip ( File self, File destination = null ) {
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
                else {
                    final dir = new File(destination, entry.name)
                    dir.mkdir()

                    unzippedFiles << dir
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
