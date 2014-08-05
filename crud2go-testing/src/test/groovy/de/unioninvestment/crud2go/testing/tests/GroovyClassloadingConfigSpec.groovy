package de.unioninvestment.crud2go.testing.tests

import de.unioninvestment.crud2go.testing.spock.CrudConfigSpec

/**
 * Created by cmj on 17.07.14.
 */
class GroovyClassloadingConfigSpec extends CrudConfigSpec {


    def 'classes within different scripts should not interfere'() {
        given:
        // please note: do not test multiple configs yourself in parallel, this is not supported
        load 'testingGroovyClassloading1Config.xml'
        def instance1 = instance
        load 'testingGroovyClassloading2Config.xml'
        def instance2 = instance

        expect:
        instance1.mainScript.newHello() == 'Hello 1'
        instance2.mainScript.newHello() == 'Hello 2'
    }
}
