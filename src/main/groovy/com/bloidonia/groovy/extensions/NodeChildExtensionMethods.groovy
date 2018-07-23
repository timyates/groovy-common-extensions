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

import groovy.util.slurpersupport.NodeChild

/**
 * User: dan-woods
 * Date: 11/20/12
 */
class NodeChildExtensionMethods {
    static Map toMap( NodeChild self, def m=[:] ) {
        m[self.name()] = self.attributes() ? [*:self.attributes()] : self.text()
        if (self.children().size()) {
            self.children().each { c ->
                c.toMap().inject(m[self.name()]) { r, k, v ->
                    if (r instanceof Map) {
                        if (r[k] && (r[k] instanceof List)) {
                            m[self.name()][k] << v
                        } else if (r[k] && !(r[k] instanceof List)) {
                            m[self.name()][k] = [r[k]] << v
                        } else {
                            m[self.name()][k] = v
                        }
                    }
                    else  { m[self.name()]=[:].with{it[k]=v;it} }
                }
            }
        }
        m
    }
}