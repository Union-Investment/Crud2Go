package de.unioninvestment.crud2go.testing.tests

import de.unioninvestment.crud2go.testing.spock.CrudConfigSpec

/**
 * Created by cmj on 17.07.14.
 */
class CrudConfigSimpleSpec extends CrudConfigSpec {

    def 'should load a crud2go configuration'() {
        when:
        load 'testingSimpleConfig.xml'

        then:
        instance != null
        instance.mainScript != null
        instance.portlet != null
        instance.config != null
    }

    def 'should allow access to the mainScript via delegation'() {
        given:
        load 'testingSimpleConfig.xml'

        when:
        def data = backend.read()

        then:
        data.size() == 4000
    }

}
