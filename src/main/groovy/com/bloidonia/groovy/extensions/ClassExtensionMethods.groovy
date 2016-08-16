package com.bloidonia.groovy.extensions


class ClassExtensionMethods {
    /**
     * Adds an implicit conversion to a class's meta class to another type.
     *
     * @param self The source class type
     * @param to When this conversion should be used
     * @param conversion The mechanism used to do the conversion
     * @example
     * <pre>
     *    // add an implicit conversion from String to Integer that uses the string length
     *    addImplicitConversion(String, Integer) { it.length }
     *    assert ("foo" as Integer) == 3
     * </pre>
     */
    static void addImplicitConversion(Class self, Class to, Closure conversion) {
        def oldAsType = self.metaClass.getMetaMethod('asType', [] as Class[])
        self.metaClass.asType = { Class clazz ->
            (clazz == to) ?
                conversion(delegate) :
                oldAsType.doMethodInvoke(delegate, clazz)
        }
    }
}
