package de.unioninvestment.crud2go.testing.tests.scripting.model
import de.unioninvestment.crud2go.testing.spock.CrudConfigSpec
/**
 * Created by cmj on 23.07.14.
 */
class ScriptPortletConfigSpec extends CrudConfigSpec {

    def setup() {
        load '../../testingSimpleConfig.xml'
    }

    def 'should set a testing flag inside the portlet'() {
        expect:
        portlet.inTest == true
    }


    def 'should allow replacement of components in id map for mocking'() {
        given:
        portlet.elements.table1 = [test: { 1 }]

        expect:
        portlet.elements.table1.test() == 1
    }

    def 'should allow replacement of components before script execution'() {
        given:
        load {
            from '../../testingSimpleConfig.xml'
            dontRunMainScript()
        }

        when:
        portlet.elements.table1 = [test: { 1 }]
        instance.runMainScript()

        then:
        scriptPortletConfigSpecMock.test() == 1
    }
}
