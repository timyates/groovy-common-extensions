package tests

import spock.lang.Specification

import java.nio.file.Files

class FileTests extends Specification {

    def "check for ZipSlip"() {
        given:
        def dest = Files.createTempDirectory("zipslip")
        def zip = new File(FileTests.class.getResource("/zip-slip.zip").toURI())

        when:
        zip.unzip(dest.toFile())

        then:
        def ex = thrown(IllegalArgumentException)
        ex.printStackTrace()
    }

}
