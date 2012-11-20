package com.bloidonia.groovy.extensions

import groovy.util.slurpersupport.NodeChild

/**
 * User: dan-woods
 * Date: 11/20/12
 */
class NodeChildExtensionMethods {
    static Map toMap(NodeChild self) {
        [
                (self.name()): [*: self.attributes()] <<
                        (self.children().size()
                        ? [_children: self.children().collect { toMap(it) }]
                        : [:])
        ]
    }
}
