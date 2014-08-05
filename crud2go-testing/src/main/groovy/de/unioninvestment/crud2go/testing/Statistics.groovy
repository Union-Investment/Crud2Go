package de.unioninvestment.crud2go.testing

import groovy.transform.CompileStatic

/**
 * Created by cmj on 23.07.14.
 */

@CompileStatic
class Statistics {
    /**
     * the compile time. if the config was cached, this is the last compile time
     */
    long compileTime

    /**
     * the actual initialization time after compilation
     */
    long postCompileTime

    /**
     * the actual build time for this test, which may or may not include compilation
     */
    long buildTime
}
