package com.bloidonia.groovy.extensions

/**
 * User: ddcdwoods
 * Date: 11/26/12
 */
class ConfigObjectExtensionMethods {
    static Map merge(Map self, Map other, Boolean sourcePrecedence) {
        for(entry in other) {
            def configEntry = self[entry.key]
            if(configEntry == null) {
                self[entry.key] = entry.value
                continue
            } else {
                if(configEntry instanceof Map && configEntry.size() > 0 && entry.value instanceof Map) {
                    configEntry.merge(entry.value, sourcePrecedence)
                }
                else {
                    if (!sourcePrecedence || !self[entry.key]) {
                        self[entry.key] = entry.value
                    }
                }
            }
        }
        return self
    }
}
