package com.bloidonia.groovy.extensions

import groovy.util.slurpersupport.NodeChild

/**
 * User: dan-woods
 * Date: 11/20/12
 */
class NodeChildExtensionMethods {
    static Map toMap( NodeChild self ) {
        toMap( self, '_children' )
    }

    static Map toMap( NodeChild self, String childKey ) {
        [
            (self.name()): [ *:self.attributes() ] <<
                            ( self.children().size() ? [ (childKey): self.children().collect { it.toMap( childKey ) } ]
                                                     : [:] )
        ]
    }
}
