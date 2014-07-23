package de.unioninvestment.crud2go.testing.tests

import de.unioninvestment.crud2go.testing.spock.CrudConfigSpec
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptCurrentUser

/**
 * Created by cmj on 17.07.14.
 */
class CrudConfigSpecSpec extends CrudConfigSpec {

    def 'should load a crud2go configuration from classpath by relative path'() {
        when:
        load 'testingSimpleConfig.xml'

        then:
        instance != null
        instance.mainScript != null
        instance.portlet != null
        instance.config != null
    }

    def 'should load a config from classpath by absolute path'() {
        when:
        load '/de/unioninvestment/crud2go/testing/tests/testingSimpleConfig.xml'

        then:
        instance != null
    }

    def 'should load a config from file by path relative to maven project root'() {
        when:
        load {
            fromFile 'src/test/resources/de/unioninvestment/crud2go/testing/tests/testingSimpleConfig.xml'
        }

        then:
        instance != null
    }

    def 'should allow direct access to the mainScript via delegation'() {
        given:
        load 'testingSimpleConfig.xml'

        when:
        def data = backend.read()

        then:
        data.size() == 4000
    }

    def 'should allow direct access to the script context'() {
        when:
        load 'testingSimpleConfig.xml'

        then:
        portlet != null
        portlet == instance.portlet
    }

    def 'should use anonymous user by default'() {
        when:
        load 'testingSimpleConfig.xml'
        ScriptCurrentUser currentUser = instance.mainScript.currentUser

        then:
        currentUser.name == null
        currentUser.roles == ['all', 'anonymous'] as Set
        currentUser.authenticated == false
        portlet.elements.adminComponent == null
    }

    def 'should allow modification of current user'() {
    	when:
		load {
			 fromClasspath 'testingSimpleConfig.xml'
			 currentUserName 'carsten'
			 currentUserRoles(['admin'])
		}
        ScriptCurrentUser currentUser = instance.mainScript.currentUser

		then:
		currentUser.name == 'carsten'
		currentUser.roles == ['all', 'admin', 'authenticated'] as Set
        currentUser.authenticated == true
		portlet.elements.adminComponent != null
    }

    def 'should allow optional additional validation'() {
        when:
        load {
            fromClasspath 'testingSimpleConfig.xml'
            validate()
        }
        ScriptCurrentUser currentUser = instance.mainScript.currentUser

        then:
        currentUser.name == 'carsten'
        currentUser.roles == ['all', 'admin', 'authenticated'] as Set
        currentUser.authenticated == true
        portlet.elements.adminComponent != null
    }

}
